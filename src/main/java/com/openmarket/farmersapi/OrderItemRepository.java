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
public class OrderItemRepository {

	private final String url = "jdbc:mysql://localhost:3306/PRODUCTMANAGEMENT";
	private final String dbUsername = "root";
	private final String dbPassword = "root";

	// 1. Save an individual item from a cart
	public void saveItem(OrderItem item) {
		String query = "INSERT INTO order_items (order_id, product_name, seller_username, buyer_username, quantity, price_per_unit, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, item.getOrderId());
			stmt.setString(2, item.getProductName());
			stmt.setString(3, item.getSellerUsername());
			stmt.setString(4, item.getBuyerUsername());
			stmt.setInt(5, item.getQuantity());
			stmt.setInt(6, item.getPricePerUnit());
			stmt.setString(7, "Pending");

			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 2. Fetch orders FOR THE SELLER DASHBOARD
	public List<OrderItem> getOrdersBySeller(String sellerUsername) {
		List<OrderItem> items = new ArrayList<>();
		String query = "SELECT * FROM order_items WHERE seller_username = ? ORDER BY item_id DESC";

		try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, sellerUsername);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				OrderItem item = new OrderItem();
				item.setItemId(rs.getInt("item_id"));
				item.setOrderId(rs.getInt("order_id"));
				item.setProductName(rs.getString("product_name"));
				item.setSellerUsername(rs.getString("seller_username"));
				item.setBuyerUsername(rs.getString("buyer_username"));
				item.setQuantity(rs.getInt("quantity"));
				item.setPricePerUnit(rs.getInt("price_per_unit"));
				item.setStatus(rs.getString("status"));
				items.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return items;
	}

	// 3. Update Status (Seller changes to 'Shipped' or 'Delivered')
	public boolean updateStatus(int itemId, String newStatus) {
		String query = "UPDATE order_items SET status = ? WHERE item_id = ?";
		try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, newStatus);
			stmt.setInt(2, itemId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
