package com.openmarket.farmersapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MandiPriceService {

	private final String RESOURCE_ID = "35985678-0d79-46b4-9ed6-6f13308a1d24";
	private final String BASE_URL = "https://api.data.gov.in/resource/" + RESOURCE_ID;

	private final String API_KEY = "579b464db66ec23bdd000001ae7baf8b92e44f3374e963c29f6ac97a";

	public Map<String, Object> getRealTimePrice(String state, String crop, String season, String quality,
			String district, String market) {
		RestTemplate restTemplate = new RestTemplate();

		try {
			// 1. ATTEMPT 1: Specific Search (Location + Quality + Commodity)
			String url = buildUrl(state, crop, quality, district, market);
			System.out.println("🌐 [Attempt 1] Filtering by: "
					+ (market != null ? market : (district != null ? district : state)));
			Map<String, Object> response = restTemplate.getForObject(url, Map.class);

			// 2. ATTEMPT 2: Fallback to District if Market search fails
			if (isResponseEmpty(response) && market != null && !market.isEmpty()) {
				url = buildUrl(state, crop, quality, district, null);
				System.out.println(
						"⚠️ [Attempt 2] No data for market " + market + ". Falling back to District: " + district);
				response = restTemplate.getForObject(url, Map.class);
			}

			// 3. ATTEMPT 3: Broad Search (State + Commodity ONLY) if everything else fails
			if (isResponseEmpty(response)) {
				url = buildUrl(state, crop, null, null, null);
				System.out.println("⚠️ [Attempt 3] No location match. Trying broad State search: " + url);
				response = restTemplate.getForObject(url, Map.class);
			}

			return response;

		} catch (ResourceAccessException e) {
			System.err.println("❌ [TIMEOUT] Government API took too long: " + e.getMessage());
			return createErrorResponse("Government server connection timed out. Please try again.");
		} catch (Exception e) {
			System.err.println("❌ [ERROR] API Fetch Error: " + e.getMessage());
			return null;
		}
	}

	private String buildUrl(String state, String crop, String quality, String district, String market) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).queryParam("api-key", API_KEY)
				.queryParam("format", "json").queryParam("filters[state]", state)
				.queryParam("filters[commodity]", crop);

		if (quality != null && !quality.isEmpty()) {
			builder.queryParam("filters[grade]", quality);
		}
		if (district != null && !district.isEmpty()) {
			builder.queryParam("filters[district]", district);
		}
		if (market != null && !market.isEmpty()) {
			builder.queryParam("filters[market]", market);
		}

		return builder.toUriString();
	}

	private boolean isResponseEmpty(Map<String, Object> response) {
		if (response == null || !response.containsKey("records")) {
			return true;
		}
		List<?> records = (List<?>) response.get("records");
		return records == null || records.isEmpty();
	}

	private Map<String, Object> createErrorResponse(String message) {
		Map<String, Object> error = new HashMap<>();
		error.put("error", message);
		return error;
	}
}