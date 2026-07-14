package com.openmarket.farmersapi.registration;

public class NewPassword {
	String username;
	String email;
	String newpassword;

	public NewPassword() {

	}

	public NewPassword(String username, String email, String newpassword) {
		this.username = username;
		this.email = email;
		this.newpassword = newpassword;

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

}
