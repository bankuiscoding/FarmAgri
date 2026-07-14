package com.openmarket.farmersapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

	private final String url = "jdbc:mysql://localhost:3306/PRODUCTMANAGEMENT";
	private final String dbUsername = "root";
	private final String dbPassword = "root";

	// 1. Save a new order to the database
	public void save(Order order) {
		System.out.println("  🗄️ [REPO] Attempting to save order to DB for user: " + order.getUsername());
		String query = "INSERT INTO orders (username, total_amount, shipping_address, payment_method, status, razorpay_order_id) VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			System.out.println("  🗄️ [REPO] DB Connection Successful! Executing INSERT...");
			stmt.setString(1, order.getUsername());
			stmt.setInt(2, order.getTotalAmount());
			stmt.setString(3, order.getShippingAddress());
			stmt.setString(4, order.getPaymentMethod());
			stmt.setString(5, "Order Placed");
			stmt.setString(6, order.getRazorpayOrderId());

			int rowsAffected = stmt.executeUpdate();
			System.out.println("  ✅ [REPO] Save complete. Rows affected: " + rowsAffected);

		} catch (SQLException e) {
			System.out.println("  ❌ [REPO CRITICAL ERROR] Failed to save order!");
			System.out.println("     Reason: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// 2. Fetch history for the frontend
	public List<Order> getOrdersByUsername(String username) {
		System.out.println("  🗄️ [REPO] Attempting to fetch orders from DB for: [" + username + "]");
		List<Order> userOrders = new ArrayList<>();
		String query = "SELECT * FROM orders WHERE username = ? ORDER BY order_date DESC";

		try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			System.out.println("  🗄️ [REPO] DB Connection Successful! Executing SELECT...");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();

			int count = 0;
			while (rs.next()) {
				count++;
				Order order = new Order();
				order.setOrderId(rs.getInt("order_id"));
				order.setUsername(rs.getString("username"));
				order.setTotalAmount(rs.getInt("total_amount"));
				order.setShippingAddress(rs.getString("shipping_address"));
				order.setPaymentMethod(rs.getString("payment_method"));
				order.setStatus(rs.getString("status"));
				order.setRazorpayOrderId(rs.getString("razorpay_order_id"));

				// Added a try-catch specifically for the date mapping, as it's a common silent
				// crasher
				try {
					order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
				} catch (Exception dateEx) {
					System.out.println(
							"  ⚠️ [REPO WARNING] Could not parse order_date for order ID: " + order.getOrderId());
				}

				userOrders.add(order);
			}
			System.out.println("  ✅ [REPO] Fetch complete. Found " + count + " matching rows in the database.");

		} catch (SQLException e) {
			System.out.println("  ❌ [REPO CRITICAL ERROR] Failed to fetch orders!");
			System.out.println("     Reason: " + e.getMessage());
			e.printStackTrace();
		}
		return userOrders;
	}

	// 3. Delete order
	public boolean deleteOrderById(int orderId) {
		System.out.println("  🗄️ [REPO] Attempting to delete order ID: " + orderId);
		String query = "DELETE FROM orders WHERE order_id = ?";

		try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, orderId);
			int rowsDeleted = ps.executeUpdate();
			System.out.println("  ✅ [REPO] Delete complete. Rows removed: " + rowsDeleted);
			return rowsDeleted > 0;

		} catch (SQLException e) {
			System.out.println("  ❌ [REPO CRITICAL ERROR] Failed to delete order!");
			System.out.println("     Reason: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	// Add this method to your existing OrderRepository.java
	public int saveAndReturnId(Order order) {
		System.out.println("  🗄️ [REPO] Saving order and getting ID for: " + order.getUsername());
		String query = "INSERT INTO orders (username, total_amount, shipping_address, payment_method, status, razorpay_order_id) VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
				// Notice Statement.RETURN_GENERATED_KEYS here
				PreparedStatement stmt = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, order.getUsername());
			stmt.setInt(2, order.getTotalAmount());
			stmt.setString(3, order.getShippingAddress());
			stmt.setString(4, order.getPaymentMethod());
			stmt.setString(5, "Order Placed");
			stmt.setString(6, order.getRazorpayOrderId());

			stmt.executeUpdate();

			// Fetch the generated ID
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int newOrderId = rs.getInt(1);
				System.out.println("  ✅ [REPO] Order saved with ID: " + newOrderId);
				return newOrderId;
			}

		} catch (SQLException e) {
			System.out.println("  ❌ [REPO ERROR] Failed to save order and get ID!");
			e.printStackTrace();
		}
		return -1; // Indicates failure
	}
}