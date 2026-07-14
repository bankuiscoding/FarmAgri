package com.openmarket.farmersapi;

import java.time.LocalDateTime;

public class Order {
	private int orderId;
	private String username;
	private int totalAmount;
	private String shippingAddress;
	private String paymentMethod;
	private String status;
	private String razorpayOrderId; // NEW FIELD
	private LocalDateTime orderDate;

	public Order() {
	}

	public Order(int orderId, String username, int totalAmount, String shippingAddress, String paymentMethod,
			String status, String razorpayOrderId, LocalDateTime orderDate) {
		this.orderId = orderId;
		this.username = username;
		this.totalAmount = totalAmount;
		this.shippingAddress = shippingAddress;
		this.paymentMethod = paymentMethod;
		this.status = status;
		this.razorpayOrderId = razorpayOrderId;
		this.orderDate = orderDate;
	}

	// Getters and Setters
	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}
}