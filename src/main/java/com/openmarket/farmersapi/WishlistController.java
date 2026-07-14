package com.openmarket.farmersapi;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin // Allows your frontend to talk to this backend
public class WishlistController {

	private final String DB_URL = "jdbc:mysql://localhost/USER";
	private final String DB_USER = "root";
	private final String DB_PASS = "root"; // Change to your DB password if different

	@PostMapping("/toggle")
	public ResponseEntity<String> toggleWishlist(@RequestBody WishlistRequest request) {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

			// 1. Check if the item is already in the wishlist
			String checkQuery = "SELECT * FROM wishlist WHERE username = ? AND product_name = ?";
			PreparedStatement checkPS = connection.prepareStatement(checkQuery);
			checkPS.setString(1, request.getUsername());
			checkPS.setString(2, request.getProductName());
			ResultSet rs = checkPS.executeQuery();

			if (rs.next()) {
				// 2. If it exists, REMOVE IT
				String deleteQuery = "DELETE FROM wishlist WHERE username = ? AND product_name = ?";
				PreparedStatement deletePS = connection.prepareStatement(deleteQuery);
				deletePS.setString(1, request.getUsername());
				deletePS.setString(2, request.getProductName());
				deletePS.executeUpdate();
				return ResponseEntity.ok().body("Removed");
			} else {
				// 3. If it doesn't exist, ADD IT
				String insertQuery = "INSERT INTO wishlist (username, product_name) VALUES (?, ?)";
				PreparedStatement insertPS = connection.prepareStatement(insertQuery);
				insertPS.setString(1, request.getUsername());
				insertPS.setString(2, request.getProductName());
				insertPS.executeUpdate();
				return ResponseEntity.ok().body("Added");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Database error");
		}
	}

	@GetMapping("/get/{username}")
	public ResponseEntity<List<String>> getWishlist(@PathVariable String username) {
		List<String> userWishlist = new ArrayList<>();

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
			String query = "SELECT product_name FROM wishlist WHERE username = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				userWishlist.add(rs.getString("product_name"));
			}
			return ResponseEntity.ok(userWishlist);

		} catch (SQLException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}
}
