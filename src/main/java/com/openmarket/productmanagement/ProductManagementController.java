package com.openmarket.productmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/product")
public class ProductManagementController {

	Logger LOGGER = LoggerFactory.getLogger(ProductManagementController.class);

	private static final String DB_URL = "jdbc:mysql://localhost:3306/PRODUCTMANAGEMENT";
	private static final String DB_USER = "root";
	private static final String DB_PASS = "root"; // Update if your password is different!

	// 1. GET ALL PRODUCTS (Now pulls farmer details from DB)
	@GetMapping("/all")
	public ResponseEntity<List<ProductManagementResponse>> getAllProducts() {
		LOGGER.info("Fetching all products for the shop...");
		List<ProductManagementResponse> list = new ArrayList<>();

		String query = """
				SELECT
				    c.category_id, c.name AS category_name,
				    p.product_id, p.name AS product_name, p.description, p.image_url, p.price_range,
				    p.farmer_name, p.farmer_mobile, p.unit_type, p.harvest_date,
				    v.variety_id, v.group_label, v.variety_name
				FROM categories c
				JOIN products p ON c.category_id = p.category_id
				LEFT JOIN product_varieties v ON p.product_id = v.product_id
				ORDER BY c.category_id, p.product_id
				""";

		try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
				PreparedStatement ps = con.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				ProductManagementResponse dto = new ProductManagementResponse(rs.getInt("category_id"),
						rs.getString("category_name"), rs.getInt("product_id"), rs.getString("product_name"),
						rs.getString("description"), rs.getString("image_url"), rs.getString("price_range"),
						rs.getInt("variety_id"), rs.getString("group_label"), rs.getString("variety_name"),
						rs.getString("farmer_name"), rs.getString("farmer_mobile"), rs.getString("unit_type"),
						rs.getString("harvest_date"));
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
		return ResponseEntity.ok(list);
	}

	// 2. ADD NEW PRODUCT (Fully supports the Seller Form)
	@PostMapping("/add")
	public ResponseEntity<String> addProduct(@RequestParam int categoryId, @RequestParam String name,
			@RequestParam String description, @RequestParam String priceRange, @RequestParam String imageUrl,
			@RequestParam String farmerName, @RequestParam String farmerMobile, @RequestParam String unitType,
			@RequestParam String harvestDate) {

		String query = """
				INSERT INTO products
				(category_id, name, description, price_range, image_url, farmer_name, farmer_mobile, unit_type, harvest_date)
				VALUES(?,?,?,?,?,?,?,?,?)
				""";

		try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
				PreparedStatement ps = con.prepareStatement(query)) {

			ps.setInt(1, categoryId);
			ps.setString(2, name);
			ps.setString(3, description);
			ps.setString(4, priceRange);
			ps.setString(5, imageUrl);
			ps.setString(6, farmerName);
			ps.setString(7, farmerMobile);
			ps.setString(8, unitType);
			ps.setString(9, harvestDate);

			ps.executeUpdate();
			return ResponseEntity.ok("Crop successfully added to database!");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Failed to save crop to database.");
		}
	}

	// 3. DELETE PRODUCT (For Seller Dashboard)
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable int id) {
		String query = "DELETE FROM products WHERE product_id=?";
		try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
				PreparedStatement ps = con.prepareStatement(query)) {
			ps.setInt(1, id);
			int count = ps.executeUpdate();
			if (count > 0) {
				return ResponseEntity.ok("Product deleted");
			} else {
				return ResponseEntity.badRequest().body("Product not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Delete failed");
		}
	}
}