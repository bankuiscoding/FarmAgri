package com.openmarket.farmersapi;

// Update package name to match yours

public class WishlistRequest {
	private String username;
	private String productName;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
}