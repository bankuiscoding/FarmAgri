package com.openmarket.farmersapi.registration;

public class SendEmail {
	String username;

	String email;

	String subject;
	String description;
	String message;


	String content;

	public SendEmail(String username, String email, String subject, String description, String message,
			String content) {
		super();
		this.username = username;
		this.email = email;
		this.message = message;
		this.subject = subject;
		this.description = description;
		this.content = content;

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}



	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	}

