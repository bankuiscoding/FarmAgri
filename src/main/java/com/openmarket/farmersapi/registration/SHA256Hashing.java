package com.openmarket.farmersapi.registration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Hashing {

	public static String hashWithSHA256(String input) {
        // Create MessageDigest instance for SHA-256
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			// Perform the hashing (input gets converted to bytes)
			byte[] hashedBytes = digest.digest(input.getBytes());

			// Convert the hashed bytes to a hexadecimal string
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashedBytes) {
				hexString.append(String.format("%02x", b)); // Convert byte to hex and append
			}

			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {

			throw new RuntimeException(e);

		}
        

    }


}