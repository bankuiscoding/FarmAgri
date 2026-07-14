package com.openmarket.farmersapi;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

@Service
public class RazorpayService {

	private static final String KEY = "rzp_test_SXnWcv7EnPWZFu";
	private static final String SECRET = "avWARBU2QT0UtDYIOxXek7Ch";

	public com.razorpay.Order createOrder(int amount) throws Exception {

		RazorpayClient client = new RazorpayClient(KEY, SECRET);

		JSONObject options = new JSONObject();
		options.put("amount", amount * 100); // paisa
		options.put("currency", "INR");
		options.put("receipt", "txn_123");

		return client.orders.create(options);
	}

	@PostMapping("/verify")
	public String verifyPayment(@RequestBody Map<String, String> data) {

		try {
			String orderId = data.get("razorpay_order_id");
			String paymentId = data.get("razorpay_payment_id");
			String signature = data.get("razorpay_signature");

			String payload = orderId + "|" + paymentId;

			boolean isValid = Utils.verifySignature(payload, signature, "avWARBU2QT0UtDYIOxXek7Ch");

			if (isValid) {
				return "Payment SUCCESS";
			} else {
				return "Payment FAILED";
			}

		} catch (Exception e) {
			return "Verification Error";
		}
	}
}
