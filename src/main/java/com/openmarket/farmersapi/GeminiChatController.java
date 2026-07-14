package com.openmarket.farmersapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class GeminiChatController {

	private final ChatClient chatClient;

	// In-memory stores: userId → list of "Role: message" strings
	private final Map<String, List<String>> memory = new ConcurrentHashMap<>();
	private final Map<String, List<String>> userCrops = new ConcurrentHashMap<>();

	public GeminiChatController(ChatClient.Builder builder) {
		this.chatClient = builder.build();
	}

	// ─────────────────────────────────────────────────────────────
	// 1. CHAT (Memory + Hindi/English, strictly farming only)
	// ─────────────────────────────────────────────────────────────
	@GetMapping("/chat")
	public String chat(@RequestParam String message, @RequestParam(defaultValue = "guest") String user,
			@RequestParam(defaultValue = "en") String lang) {

		List<String> history = memory.computeIfAbsent(user, k -> new ArrayList<>());
		List<String> crops = userCrops.getOrDefault(user, new ArrayList<>());

		String context = String.join("\n", history);
		String cropInfo = crops.isEmpty() ? "Not specified" : String.join(", ", crops);
		String langRule = lang.equalsIgnoreCase("hi")
				? "Always reply in simple Hindi (Devanagari script). Do NOT use English."
				: "Always reply in clear, simple English.";

		String systemPrompt = """
				You are Kisan AI 🌾 — an expert assistant ONLY for Indian agriculture.

				=== STRICT RULES (never break these) ===
				1. %s
				2. ONLY answer questions about: farming, crops, seeds, fertilizers, pesticides,
				   irrigation, soil, weather impact on crops, crop diseases, mandi prices,
				   government farm schemes, and post-harvest storage.
				3. If the user asks about ANYTHING outside farming (clothes, travel, movies,
				   shopping, politics, etc.), reply ONLY with this message (in the correct language):
				   "मैं केवल खेती से जुड़े सवालों का जवाब दे सकता हूँ। कृपया खेती के बारे में पूछें। 🌾"
				   (English version: "I can only help with farming-related questions. Please ask about agriculture. 🌾")
				4. Keep answers focused, practical, and under 200 words unless the farmer
				   explicitly asks for detailed information.
				5. Never invent crop data. If uncertain, say so.

				=== USER CONTEXT ===
				Farmer name : %s
				Crops grown : %s

				=== RECENT CONVERSATION (last 3 turns) ===
				%s
				""".formatted(langRule, user, cropInfo, context);

		String reply = chatClient.prompt().system(systemPrompt).user(message).call().content();

		// Store last 3 turns (6 lines) only
		history.add("Farmer: " + message);
		history.add("Kisan AI: " + reply);
		if (history.size() > 6) {
			history.subList(0, history.size() - 6).clear();
		}

		return reply;
	}

	// ─────────────────────────────────────────────────────────────
	// 2. ADD CROP
	// ─────────────────────────────────────────────────────────────
	@PostMapping("/add-crop")
	public String addCrop(@RequestParam String user, @RequestParam String crop) {
		userCrops.computeIfAbsent(user, k -> new ArrayList<>()).add(crop.trim());
		return "✅ Crop added: " + crop;
	}

	// ─────────────────────────────────────────────────────────────
	// 3. GET CROPS
	// ─────────────────────────────────────────────────────────────
	@GetMapping("/my-crops")
	public List<String> getCrops(@RequestParam String user) {
		return userCrops.getOrDefault(user, new ArrayList<>());
	}

	// ─────────────────────────────────────────────────────────────
	// 4. PRICE PREDICTION — strict JSON, no markdown
	// ─────────────────────────────────────────────────────────────
	@GetMapping("/price")
	public String predictPrice(@RequestParam String crop, @RequestParam String season, @RequestParam String state,
			@RequestParam String quality) {

		String prompt = """
				You are an Indian agricultural price analyst.
				Return ONLY a raw JSON object — no markdown, no explanation, no extra text.

				Fields required:
				{
				  "crop": "<crop name>",
				  "state": "<state>",
				  "season": "<season>",
				  "quality": "<quality>",
				  "price_per_quintal_inr": <number>,
				  "range_low_inr": <number>,
				  "range_high_inr": <number>,
				  "market_trend": "Rising | Stable | Falling",
				  "advice": "<one practical selling tip for the farmer in 1-2 sentences>"
				}

				Input:
				  Crop    : %s
				  Season  : %s
				  State   : %s
				  Quality : %s

				Respond with ONLY the JSON. Start your response with { and end with }.
				""".formatted(crop, season, state, quality);

		return chatClient.prompt().system(prompt).call().content();
	}

	@PostMapping(value = "/disease", consumes = "multipart/form-data")
	public ResponseEntity<String> detectDisease(@RequestParam("image") MultipartFile file) {

		try {
			// Validate empty image
			if (file == null || file.isEmpty()) {
				return ResponseEntity.badRequest().body("{\"error\":\"Please upload a crop image.\"}");
			}

			// Validate file type
			String contentType = file.getContentType();

			if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png")
					|| contentType.equals("image/webp"))) {

				return ResponseEntity.badRequest().body("{\"error\":\"Only JPG, PNG and WEBP images are allowed.\"}");
			}

			// Maximum 5 MB
			if (file.getSize() > 5 * 1024 * 1024) {
				return ResponseEntity.badRequest().body("{\"error\":\"Image size must be below 5 MB.\"}");
			}

			byte[] imageBytes = file.getBytes();

			Media imageMedia = new Media(MimeTypeUtils.parseMimeType(contentType), new ByteArrayResource(imageBytes));

			String systemPrompt = """
					You are a crop disease detection expert for Indian agriculture.

					Carefully analyze the uploaded crop image.

					Return ONLY one valid raw JSON object.

					Rules:
					- Do not use markdown.
					- Do not use ```json code blocks.
					- Start the response with { and end with }.
					- Do not add text before or after the JSON.
					- Treatment must be written in simple Hinglish.
					- Use <br> for line breaks in treatment.
					- Do not invent a disease when the image is unclear.
					- If this is not a crop image, return disease as "Not a crop image".

					Return this exact structure:

					{
					  "disease": "<disease name>",
					  "affected_part": "<leaf | stem | fruit | root | whole plant>",
					  "severity": "<Low | Medium | High | N/A>",
					  "cause": "<fungal | bacterial | viral | pest | nutrient deficiency | unknown>",
					  "treatment": "🌱 <b>Ilaj ke Steps:</b><br><br>1. <step one><br>2. <step two><br>3. <medicine and dosage>",
					  "prevention": "<one prevention tip in simple Hinglish>"
					}
					""";

			String response = chatClient.prompt()

					// This model is used only for this request
					.options(OpenAiChatOptions.builder().model("meta-llama/llama-4-scout-17b-16e-instruct")
							.temperature(0.2).build())

					.system(systemPrompt).user(userMessage -> userMessage
							.text("Analyze this crop image and identify any visible disease.").media(imageMedia))
					.call().content();

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			String safeMessage = e.getMessage() == null ? "Unknown image analysis error"
					: e.getMessage().replace("\"", "'");

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\":\"Image processing failed: " + safeMessage + "\"}");
		}
	}

	// ─────────────────────────────────────────────────────────────
	// 6. CLEAR MEMORY
	// ─────────────────────────────────────────────────────────────
	@DeleteMapping("/clear")
	public String clear(@RequestParam String user) {
		memory.remove(user);
		return "✅ Chat memory cleared for user: " + user;
	}

	// ─────────────────────────────────────────────────────────────
	// 7. WEATHER-BASED ADVICE
	// ─────────────────────────────────────────────────────────────
	@GetMapping("/weather-advice")
	public String getWeatherAdvice(@RequestParam(defaultValue = "guest") String user, @RequestParam String location,
			@RequestParam String temperature, @RequestParam String condition) {

		List<String> crops = userCrops.getOrDefault(user, new ArrayList<>());
		String cropInfo = crops.isEmpty() ? "General crops (no crops registered)" : String.join(", ", crops);

		String systemPrompt = """
				You are Kisan AI 🌾, an Indian agricultural weather advisor.

				Current weather data:
				  Location    : %s
				  Temperature : %s°C
				  Condition   : %s
				  Farmer crops: %s

				Give exactly 3 short, actionable farming tips based on this weather.
				Each tip must:
				- Be specific to the crops listed (or general if no crops listed)
				- Be 1-2 sentences maximum
				- Start with a relevant emoji

				Weather-specific guidance to include if applicable:
				- Rain / humidity  → drainage, fungal disease prevention, spraying schedule
				- High heat (>35°C) → irrigation timing, mulching, heat stress
				- Cold (<10°C)      → frost protection, delayed sowing advice
				- Cloudy / overcast → photosynthesis impact, pest monitoring

				Format as a numbered list (1. 2. 3.). No markdown headers. No JSON.
				""".formatted(location, temperature, condition, cropInfo);

		return chatClient.prompt().system(systemPrompt).call().content();
	}

	// ─────────────────────────────────────────────────────────────
	// 8. GLOBAL EXCEPTION HANDLER
	// ─────────────────────────────────────────────────────────────
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleErrors(RuntimeException ex) {
		String msg = ex.getMessage();
		if (msg != null && msg.contains("429")) {
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
					.body("🌾 Kisan AI bahut busy hai abhi. 60 seconds baad dobara try karein.");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Server error: " + msg);
	}
}