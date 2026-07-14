package com.openmarket.farmersapi;

import java.time.LocalDateTime;

public class CartItem {

    private int id;


	private int productId;

    private String productName;
    private int price;
    private int quantity;


	private int totalPrice;

    private LocalDateTime addedAt;


	private String image_url;

    // Default Constructor
    public CartItem() {}

    // Parameterized Constructor
	public CartItem(int id, int productId, String productName, int price, int quantity, int totalPrice,
			String image_url,
			LocalDateTime addedAt) {
        this.id = id;
		this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.image_url = image_url;
        this.addedAt = addedAt;
    }

	// 🔥 Auto calculate total price
	public int getTotalPrice() {
		return price * quantity;
	}

	// Getters & Setters

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice; // optional override
	}

	public LocalDateTime getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(LocalDateTime addedAt) {
		this.addedAt = addedAt;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
}