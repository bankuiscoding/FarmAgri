package com.openmarket.farmersapi.registration;

public class ActivationToken {
	Integer id;
	String username;
	String status;
	String activation_token;
	String activation_expiry;
	String created_at;
	String updated_at;

	public ActivationToken(Integer id, String username, String status, String activation_token,
			String activation_expiry, String created_at, String updated_at) {
		this.id = id;
		this.username = username;
		this.status = status;
		this.activation_token = activation_token;
		this.activation_expiry = activation_expiry;
		this.created_at = created_at;
		this.updated_at = updated_at;

	}

	public ActivationToken() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getActivation_token() {
		return activation_token;
	}

	public void setActivation_token(String activation_token) {
		this.activation_token = activation_token;
	}

	public String getActivation_expiry() {
		return activation_expiry;
	}

	public void setActivation_expiry(String activation_expiry) {
		this.activation_expiry = activation_expiry;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}


}

