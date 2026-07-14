package com.openmarket.farmersapi;

import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openmarket.farmersapi.OrderRequest.CartItem;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	// ===============================================
	// 1. PLACE ORDER (Split-Cart Logic)
	// ===============================================
	// ===============================================
	// 1. PLACE ORDER (Split-Cart Logic with X-RAY DEBUGGING)
	// ===============================================
	@PostMapping("/place")
	public ResponseEntity<String> placeOrder(@RequestBody OrderRequest request) {
		System.out.println("\n=================================================");
		System.out.println("➡️ [CHECKOUT START] Buyer: " + request.getUsername());
		System.out.println("➡️ Total Amount: ₹" + request.getTotalAmount());

		// 1. Save the main receipt
		Order newOrder = new Order();
		newOrder.setUsername(request.getUsername());
		newOrder.setTotalAmount(request.getTotalAmount());
		newOrder.setShippingAddress(request.getShippingAddress());
		newOrder.setPaymentMethod(request.getPaymentMethod());

		int generatedOrderId = orderRepository.saveAndReturnId(newOrder);

		if (generatedOrderId == -1) {
			System.out.println("❌ [ERROR] Main order failed to save!");
			return ResponseEntity.internalServerError().body("Failed to generate order ID");
		}

		System.out.println("✅ Main order saved. ID: " + generatedOrderId);

		// 2. CHECK THE CART ITEMS
		if (request.getItems() != null && !request.getItems().isEmpty()) {
			System.out.println("📦 Cart contains " + request.getItems().size() + " items. Processing now...");

			for (OrderRequest.CartItem cartItem : request.getItems()) {
				System.out.println("   -> Crop: " + cartItem.getProduct() + " | Seller: " + cartItem.getSeller()
						+ " | Qty: " + cartItem.getQuantity());

				OrderItem dbItem = new OrderItem();
				dbItem.setOrderId(generatedOrderId);
				dbItem.setBuyerUsername(request.getUsername());
				dbItem.setProductName(cartItem.getProduct());
				dbItem.setSellerUsername(cartItem.getSeller());
				dbItem.setPricePerUnit(cartItem.getPrice());
				dbItem.setQuantity(cartItem.getQuantity());

				orderItemRepository.saveItem(dbItem);
			}
		} else {
			System.out.println("❌ [CRITICAL ERROR] Java received an EMPTY cart from JavaScript!");
			System.out.println(
					"❌ Make sure your JS cart array matches the OrderRequest.CartItem variable names perfectly.");
			}

		System.out.println("=================================================\n");
		return ResponseEntity.ok("Order placed successfully with ID: " + generatedOrderId);
		}

	// ===============================================
	// 2. SELLER API: Fetch Orders to fulfill
	// ===============================================
	@GetMapping("/seller/{sellerUsername}")
	public List<OrderItem> getSellerOrders(@PathVariable String sellerUsername) {
		System.out.println("➡️ [CONTROLLER] Fetching dashboard orders for SELLER: [" + sellerUsername + "]");
		return orderItemRepository.getOrdersBySeller(sellerUsername);
	}

	// ===============================================
	// 3. SELLER API: Update Shipping Status
	// ===============================================
	@PutMapping("/seller/update-status/{itemId}")
	public ResponseEntity<String> updateItemStatus(@PathVariable int itemId, @RequestParam String newStatus) {
		System.out.println("➡️ [CONTROLLER] Updating item ID: " + itemId + " to status: " + newStatus);
		boolean updated = orderItemRepository.updateStatus(itemId, newStatus);
		if (updated) {
			return ResponseEntity.ok("Status updated successfully!");
		} else {
			return ResponseEntity.badRequest().body("Failed to update status.");
		}
	}

	// ===============================================
	// 4. GET: Fetches history for a specific BUYER
	// ===============================================
	@GetMapping("/history/{username}")
	public List<Order> getHistory(@PathVariable String username) {
		System.out.println("➡️ [CONTROLLER] Received request to FETCH orders for username: [" + username + "]");
		List<Order> orders = orderRepository.getOrdersByUsername(username);
		System.out.println("⬅️ [CONTROLLER] Returning " + orders.size() + " orders to the frontend.");
		return orders;
	}

	// ===============================================
	// 5. DELETE: Cancels/Deletes an order by its ID
	// ===============================================
	@DeleteMapping("/delete/{orderId}")
	public ResponseEntity<String> deleteOrder(@PathVariable int orderId) {
		System.out.println("➡️ [CONTROLLER] Received request to DELETE order ID: [" + orderId + "]");
		try {
			boolean isDeleted = orderRepository.deleteOrderById(orderId);
			if (isDeleted) {
				System.out.println("✅ [CONTROLLER] Order " + orderId + " deleted successfully.");
				return ResponseEntity.ok("Order deleted successfully!");
			} else {
				System.out.println("⚠️ [CONTROLLER] Order " + orderId + " not found to delete.");
				return ResponseEntity.badRequest().body("Order not found or already deleted.");
			}
		} catch (Exception e) {
			System.out.println("❌ [CONTROLLER] Error deleting order: " + e.getMessage());
			return ResponseEntity.internalServerError().body("Error deleting order: " + e.getMessage());
		}
	}

	// ===============================================
	// 6. EMAIL NOTIFICATIONS
	// ===============================================
	@PostMapping("/sendAttachment")
	public ResponseEntity<String> sendAttachment(@RequestBody Email emf) {
		System.out.println("➡️ [CONTROLLER] Received request to SEND EMAIL to: [" + emf.getEmail() + "]");

		HtmlEmail email = new HtmlEmail();
		email.setHostName("smtp.googlemail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("anditisankit@gmail.com", "qttq viee mect ugfu"));
		email.setSSLOnConnect(true);
		email.setCharset("UTF-8");

		try {
			email.addTo(emf.getEmail());
			email.setFrom("anditisankit@gmail.com", "FARM AGRI Support");

			String titleColor;
			String actionHeading;
			String actionMessage;

			if ("BUY".equalsIgnoreCase(emf.getType())) {
				email.setSubject("Order Confirmed: " + emf.getSubject());
				titleColor = "#2e7d32";
				actionHeading = "ऑर्डर की पुष्टि (Order Confirmed)";
				actionMessage = "आपका ऑर्डर सफलतापूर्वक स्वीकार कर लिया गया है। हम जल्द ही आपके सामान की डिलीवरी करेंगे।";
			} else {
				email.setSubject("Crop Listed Successfully: " + emf.getSubject());
				titleColor = "#8B4513";
				actionHeading = "फसल बिक्री जानकारी (Crop Sale Listed)";
				actionMessage = "आपकी फसल बिक्री के लिए FARM AGRI पर लिस्ट हो गई है। खरीदार आपसे जल्द ही संपर्क करेंगे।";
			}

			String htmlContent = "<html><body style='font-family: Arial; line-height: 1.6;'>"
					+ "<div style='background-color: " + titleColor
					+ "; padding: 15px; color: white; text-align: center;'>" + "<h1>FARM AGRI</h1>" + "</div>"
					+ "<div style='padding: 20px; border: 1px solid #ddd;'>" + "<h3>नमस्ते " + emf.getName() + ",</h3>"
					+ "<p style='font-weight: bold; color: " + titleColor + ";'>" + actionHeading + "</p>" + "<p>"
					+ actionMessage + "</p>" + "<p><b>विवरण (Details):</b> " + emf.getDescription() + "</p>" + "<hr>"
					+ "<p>" + emf.getMessage() + "</p>" + "<br><p>धन्यवाद,<br><b>टीम फार्म एग्री</b></p>" + "</div>"
					+ "</body></html>";

			email.setHtmlMsg(htmlContent);
			email.send();
			System.out.println("✅ [CONTROLLER] Email sent successfully!");
			return ResponseEntity.ok("Email sent for " + emf.getType() + " transaction.");

		} catch (EmailException e) {
			System.out.println("❌ [CONTROLLER] Failed to send email: " + e.getMessage());
			return ResponseEntity.status(500).body("Error: " + e.getMessage());
		}
	}
}