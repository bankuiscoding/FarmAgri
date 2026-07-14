package com.openmarket.farmersapi.registration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")

@RestController
public class UserRegistration {

	Logger LOGGER = LoggerFactory.getLogger(UserRegistration.class);

	private static final String UPLOAD_DIR = "uploads/";

	@PostMapping("/user/register")
	public ResponseEntity registerUser(@RequestBody UserDetail usf) {

		LOGGER.info("User registration request recieved");

		// Insert a record for user. Status is inactive until activateed.
		String uesrDBId = insertUserIntoDB(usf);
		LOGGER.info("User record inserted into database");

		String uniqueId = UUID.randomUUID().toString();

		// Insert an activation token for same user id.
		insertActivationToken(uniqueId, usf.getUsername());
		LOGGER.info("User record activation token inserted into database");

		LOGGER.info("Send an activation email.");
		sendActivationEmail(usf.getEmail(), uniqueId);

		return ResponseEntity.ok().body("Registration Successful. User Record Id: " + uesrDBId);



	}

	private String insertUserIntoDB(UserDetail user) {
		final String DB_URL = "jdbc:mysql://localhost/USER";
		Connection connection;
		try {

			String passwordHash = SHA256Hashing.hashWithSHA256(user.getPassword());

			LOGGER.info("Password hash created successfully");

			connection = DriverManager.getConnection(DB_URL, "root", "root");

			String query = "INSERT INTO users (username, email, password_hash, status, created_at, updated_at) VALUES (?,?,?,?,?,?) ";

			PreparedStatement PS = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			PS.setString(1, user.getUsername());
			PS.setString(2, user.getEmail());
			PS.setString(3, passwordHash);
			PS.setString(4, "inactive");

			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentDateTime = format.format(date);

			PS.setString(5, currentDateTime);
			PS.setString(6, currentDateTime);

			LOGGER.debug(query);
			LOGGER.debug(PS.toString());

			int count = PS.executeUpdate();

			Integer id = null;

			ResultSet rs = PS.getGeneratedKeys();

			if (rs.next()) {
				id = rs.getInt(1);
			}

			return "" + id;

		} catch (SQLException e) {
			throw new RuntimeException(e);

		}

	}

	private void insertActivationToken(String activationToken, String userName) {
		final String DB_URL = "jdbc:mysql://localhost/USER";
		Connection connection;

		String query = "INSERT INTO activation_token(username,activation_token,activation_expiry,created_at,updated_at) VALUES(?,?,?,?,?)";

		try {
			connection = DriverManager.getConnection(DB_URL, "root", "root");
			PreparedStatement PS = connection.prepareStatement(query);
			PS.setString(1, userName);
			PS.setString(2, activationToken);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date expriy = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
			String expiryFormmatedDate = formatter.format(expriy);

			PS.setString(3, expiryFormmatedDate);

			Date date = new Date();
			String currentDateTime = formatter.format(date);

			PS.setString(4, currentDateTime);
			PS.setString(5, currentDateTime);

			PS.executeUpdate();

		} catch (SQLException e) {

			throw new RuntimeException(e);
		}

	}

	@CrossOrigin(origins = "*")
	@PostMapping("/login")
	private ResponseEntity ok(@RequestBody LoginData lgf) {

		final String DB_URL = "jdbc:mysql://localhost/USER";

		Connection connection;
		String loginUnsuccessfullMessage = null;

		try {

			String password_hash = SHA256Hashing.hashWithSHA256(lgf.getPassword());

			connection = DriverManager.getConnection(DB_URL, "root", "root");
			// SELECT * FROM company.employee WHERE name = "prashant" OR age >= 45 OR salary
			// < 10000;

			String query = "SELECT * FROM users  WHERE username = ?";

			PreparedStatement PS = connection.prepareStatement(query);

			PS.setString(1, lgf.getUsername());

			ResultSet rs = PS.executeQuery();

			if (rs.next()) {

				LOGGER.debug("User found: " + lgf.getUsername());

				String status = rs.getString("status");

				if ("active".equals(status)) {

					String pwh = rs.getString("password_hash");

					if (password_hash.equals(pwh)) {

						return ResponseEntity.ok("User login successfull");

					} else {

						loginUnsuccessfullMessage = "Password Incorrect";

					}

				} else {

					loginUnsuccessfullMessage = "User is not active";

				}

			} else {

				loginUnsuccessfullMessage = "User doesn't exists";
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		return ResponseEntity.badRequest().body(loginUnsuccessfullMessage);

	}

	@CrossOrigin(origins = "*")
	@GetMapping("/user/activate/{token}")
	public ResponseEntity activateUser(@PathVariable String token) {

		// 1. search activation token table and check if there is a record that has
		// above token in activation_id column and status is active.
		final String DB_URL = "jdbc:mysql://localhost/USER";

		LOGGER.info("Activating user related to token: " + token);
		
		Connection connection;
		try {
			connection = DriverManager.getConnection(DB_URL, "root", "root");
			String query = "SELECT * FROM  activation_token WHERE activation_token=?";
			PreparedStatement PS = connection.prepareStatement(query);
			PS.setString(1, token);
			ResultSet rs = PS.executeQuery();

			String username = null;
			String status = null;
			String actRecordId = null;
			String newPwdHash = null;

			if (rs.next()) {
				username = rs.getString("username");
				status = rs.getString("status");
				actRecordId = rs.getString("id");
				newPwdHash = rs.getString("new_password");

			} else {
				// Bad request. invalid token.
				return ResponseEntity.badRequest().body("Invalid token or this link no longer exists");
			}

			LOGGER.debug(
					"activation record values: id: " + actRecordId + " , username: " + username + " status: " + status);

			// update user status as active if user is not null.
			if ("active".equals(status)) {

				String updateQuery = null;
				PreparedStatement updatePS = null;

				if (newPwdHash == null) {

					updateQuery = "UPDATE users set status = ? WHERE username = ?";
					updatePS = connection.prepareStatement(updateQuery);
					updatePS.setString(1, "active");
					updatePS.setString(2, username);

				} else {

					updateQuery = "UPDATE users set status = ?, password_hash = ? WHERE username = ?";
					updatePS = connection.prepareStatement(updateQuery);
					updatePS.setString(1, "active");
					updatePS.setString(2, newPwdHash);
					updatePS.setString(3, username);

				}

				int count = updatePS.executeUpdate();

				if (count > 0) {

					LOGGER.debug("PAssword reset successful");

				} else {

				}

			} else {
				return ResponseEntity.badRequest().body("You are not activated");
			}

		} catch (SQLException e) {

			throw new RuntimeException(e);
		}

		// 2. Get that record and get username from that record.

		// 3. Update record in user table to make user active.

		return ResponseEntity.ok().body("User activated");
	}




	private void sendActivationEmail(String toEmail, String activationToken) {

		HtmlEmail email = new HtmlEmail();
		email.setHostName("smtp.googlemail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("anditisankit@gmail.com", "qttq viee mect ugfu"));
		email.setSSLOnConnect(true);
		String content = "Please click the link to verify your email: " + "http://localhost/user/activate/"
				+ activationToken;

		try {


			email.setFrom("anditisankit@gmail.com");

			email.addTo(toEmail);
			email.setSubject("Account Activation");
			email.setMsg(content);

			email.send();
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}


	}

	@PostMapping("/newpassword")
	public ResponseEntity createNewPassword(@RequestBody NewPassword nwf) {
		final String DB_URL = "jdbc:mysql://localhost/USER";

		Connection connection;

		String querry = "SELECT * FROM  users WHERE username=? AND email=? ";
		try {

			connection = DriverManager.getConnection(DB_URL, "root", "root");
			PreparedStatement PS = connection.prepareStatement(querry);
			PS.setString(1, nwf.getUsername());
			PS.setString(2, nwf.getEmail());

			ResultSet rs = PS.executeQuery();

			Boolean recordExists = rs.next();
			final String userEmailId = rs.getString("email");

			if (!recordExists) {
				return ResponseEntity.badRequest().body("You are not registered");
			}

			LOGGER.info("User username and email found");

			// ----------------------------------------------------------------------------------

			String actTokenQuery = "SELECT * FROM activation_token WHERE username = ?";
			PreparedStatement actTokenPS = connection.prepareStatement(actTokenQuery);
			actTokenPS.setString(1, nwf.getUsername());
			ResultSet actTokenRs = actTokenPS.executeQuery();
			Boolean actRecordExists = actTokenRs.next();

			if (actRecordExists) {
				
				LOGGER.info("Activation record found. Updating token details");
				
				// update newactivation table record column new_password with hash of incoming
				// password in API body
				String newPassWordHash = SHA256Hashing.hashWithSHA256(nwf.getNewpassword());
				String newActivationToken = UUID.randomUUID().toString();

				Date expriy = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String activation_expiry = formatter.format(expriy);
				
				String passwordUpdate = "UPDATE activation_token SET status = ?,activation_token = ? , activation_expiry = ? , new_password=?  WHERE username=?";
				PreparedStatement updateActPS = connection.prepareStatement(passwordUpdate);
				updateActPS.setString(1, "Active");
				updateActPS.setString(2, newActivationToken);
				updateActPS.setString(3, activation_expiry);
				updateActPS.setString(4, newPassWordHash);
				updateActPS.setString(5, nwf.getUsername());
				
				int count = updateActPS.executeUpdate();
				
				if(count > 0) {
					
					sendActivationEmail(userEmailId, newActivationToken);

//					String password_hash = SHA256Hashing.hashWithSHA256(nwf.getNewpassword());
//					Date date = new Date();
//					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					String updated_at = format.format(date);
//
//					String updateQuerry = "UPDATE users SET password_hash = ? , updated_at = ? WHERE username = ?";
//					PreparedStatement updateUserPS = connection.prepareStatement(updateQuerry);
//					updateUserPS.setString(1, password_hash);
//
//					updateUserPS.setString(2, updated_at);
//					updateUserPS.setString(3, nwf.getUsername());
//					int counter = updateUserPS.executeUpdate();

				}

			} else {

				LOGGER.info("NO activation record found. creating a new one");
				// Insert new record in activation_table
				String newPassword = SHA256Hashing.hashWithSHA256(nwf.getNewpassword());
				String activation_token = UUID.randomUUID().toString();


				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				String updated_at = formatter.format(date);
				Date expiry = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));

				String activation_expiry = formatter.format(expiry);

				String insertDataQuerry = "INSERT INTO activation_token (username , activation_token , status , activation_expiry, updated_at,new_password  )VALUES(?,?,?,?,?,?)";

				PreparedStatement rePS = connection.prepareStatement(insertDataQuerry);

				rePS.setString(1, nwf.getUsername());
				rePS.setString(2, activation_token);
				rePS.setString(3, "Active");

				rePS.setString(4, activation_expiry);
				rePS.setString(5, updated_at);
				rePS.setString(6, newPassword);

				int counter = rePS.executeUpdate();
				
				if (counter > 0) {

					sendActivationEmail(userEmailId, activation_token);

				}


			}
			return ResponseEntity.ok().body("Password reset successfully");



		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

	}

	@CrossOrigin(origins = "*")
	@PostMapping("/image_upload")
	public ResponseEntity<String> upload(@RequestParam("cropName") String cropName,
			@RequestParam("cropImage") MultipartFile file) {



		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("No image selected.");

		}
		try {

			File uploadDir = new File(UPLOAD_DIR);
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
			}


			String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
			Path filePath = Paths.get(UPLOAD_DIR + fileName);
			Files.write(filePath, file.getBytes());

			return ResponseEntity.ok("Crops uploaded successfully! Saved as:" + fileName);

		} catch (IOException e) {
			throw new RuntimeException(e);

		}


			

		}

		@GetMapping
		public ResponseEntity weatherInfo(@RequestBody WeatherCurrent wtf) {
			final String DB_URL = "jdbc:mysql://localhost/ weatherCurrent";
			Connection connection;
			
			String querry = "INSERT INTO weatherData(id,date,hour,location_lat,location_long,location_region,location_country,location_city,location_tz,data_json)VALUES(?,?,?,?,?,?,?,?,?,?)";
			
			return null;
					

		}

		@RequestMapping("/cart")
		@CrossOrigin(origins = "*")
		public class CartController {

			@PostMapping("/add")
			public String addToCart(@RequestBody Map<String, Object> data) {
				System.out.println(data);
				return "Added to cart successfully!";
			}
		}
	}





