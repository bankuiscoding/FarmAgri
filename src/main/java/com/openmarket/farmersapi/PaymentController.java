package com.openmarket.farmersapi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {
	@Value("${app.razorpay.key-id:}")
	private String keyId;

	@Value("${app.razorpay.key-secret:}")
	private String keySecret;

	@PostMapping("/create-order")
	public Map<String, Object> createOrder(@RequestBody Map<String, Object> data) {

		try {
			int amount = Integer.parseInt(data.get("amount").toString());

			RazorpayClient client = new RazorpayClient(keyId, keySecret);

			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", amount * 100); // convert to paise
			orderRequest.put("currency", "INR");

			Order order = client.orders.create(orderRequest);

			Map<String, Object> response = new HashMap<>();
			response.put("id", order.get("id"));
			response.put("amount", order.get("amount"));
			response.put("keyId", keyId);

			return response;

		} catch (Exception e) {
			e.printStackTrace();
			return Map.of("error", "Something went wrong");
		}
	}

	@PostMapping("/verify")
	public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, String> data) {
		try {
			String payload = data.get("razorpay_order_id") + "|" + data.get("razorpay_payment_id");
			boolean verified = Utils.verifySignature(payload, data.get("razorpay_signature"), keySecret);
			return verified ? ResponseEntity.ok(Map.of("verified", true))
					: ResponseEntity.badRequest().body(Map.of("verified", false));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("verified", false));
		}
	}
}
