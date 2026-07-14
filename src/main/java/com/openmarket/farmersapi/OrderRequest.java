package com.openmarket.farmersapi;

import java.util.List;

public class OrderRequest {
	private String username;
	private int totalAmount;
	private String shippingAddress;
	private String paymentMethod;
	private List<CartItem> items; // This catches the frontend array correctly now!

	// Getters and Setters
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	// This nested class perfectly matches the JavaScript JSON names!
	public static class CartItem {
		private String product;
		private String seller;
		private int price;
		private int quantity;

		public String getProduct() {
			return product;
		}

		public void setProduct(String product) {
			this.product = product;
		}

		public String getSeller() {
			return seller;
		}

		public void setSeller(String seller) {
			this.seller = seller;
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
	}
}