package com.openmarket.farmersapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class CartItemRepository {

	// 🔥 FIXED: Now pointing to the same database as your Orders!
	private final String url = "jdbc:mysql://localhost:3306/PRODUCTMANAGEMENT";
    private final String username = "root";
	private final String password = "root";

    public void addItem(CartItem item) {
		System.out.println("🛒 [CART REPO] Attempting to add item to cart: " + item.getProductName());
		String query = "INSERT INTO add_to_cart (product_id, product_name, price, quantity, image_url) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, item.getProductId());
			stmt.setString(2, item.getProductName());
			stmt.setInt(3, item.getPrice());
			stmt.setInt(4, item.getQuantity());
			stmt.setString(5, item.getImage_url());

            stmt.executeUpdate();
			System.out.println("✅ [CART REPO] Item added successfully.");

        } catch (SQLException e) {
			System.out.println("❌ [CART REPO ERROR] Failed to add item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<CartItem> getAllItems() {
		System.out.println("🛒 [CART REPO] Fetching all cart items...");
        List<CartItem> items = new ArrayList<>();
        String query = "SELECT * FROM add_to_cart";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                CartItem item = new CartItem(
						rs.getInt("id"), rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("price"),
						rs.getInt("quantity"), rs.getInt("price") * rs.getInt("quantity"), rs.getString("image_url"),
						rs.getTimestamp("added_at").toLocalDateTime()
                );
                items.add(item);
            }
			System.out.println("✅ [CART REPO] Fetched " + items.size() + " items.");

        } catch (SQLException e) {
			System.out.println("❌ [CART REPO ERROR] Failed to fetch items: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    public void deleteItem(int id) {
        String query = "DELETE FROM add_to_cart WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateQuantity(int id, int quantity) {
		String query = "UPDATE add_to_cart SET quantity = ? WHERE id = ?";
		try (Connection conn = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, quantity);
			stmt.setInt(2, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clearCart() {
		String query = "DELETE FROM add_to_cart";
		try (Connection conn = DriverManager.getConnection(url, username, password);
				Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}