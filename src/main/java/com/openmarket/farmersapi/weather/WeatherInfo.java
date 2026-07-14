package com.openmarket.farmersapi.weather;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmarket.farmersapi.weather.dto.WeatherResponse;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

@CrossOrigin(origins = "*")
@RequestMapping("/weather/search")
@RestController
public class WeatherInfo {

	final String DB_URL = "jdbc:mysql://localhost/weathercurrent";

	@Value("${weather.api.key}")

	String apiKey;
	OkHttpClient client = new OkHttpClient();
	ObjectMapper mapper = new ObjectMapper();

	Logger LOGGER = LoggerFactory.getLogger(getClass());


	
	@GetMapping("/currenttime/place")
	public ResponseEntity<CurrentWeatherResponse> searchCurrentWeather(@RequestParam String latitude,
			@RequestParam String longitude) {
		
		LOGGER.info(String.format("lat:'%s' , lon: '%s'", latitude, longitude));
		
		GeoCoordinate geoCoord = getGeoCoordinates(latitude, longitude);
		
		CurrentWeatherResponse currentWeatherResponse = checkDBForCurrentWeatherInfo(geoCoord);

		
		if (currentWeatherResponse == null) { // weather info is not present in database. Call weather api.
			LOGGER.info(" Weather info is not present in database. Call weather API");

			WeatherResponse weatherAPIResponse = invokeWeatherAPI(latitude, longitude);
			currentWeatherResponse = convertWeatherAPIResponseToInternalResponse(weatherAPIResponse);

			currentWeatherResponse.setLatitude(latitude);
			currentWeatherResponse.setLongitude(longitude);

			currentWeatherResponse = convertWeatherAPIResponseToInternalResponse(weatherAPIResponse);

			// since weather info was not in database, add here.
			insertWeatherInfoIntoDB(currentWeatherResponse, geoCoord);

		}

		return ResponseEntity.ok().body(currentWeatherResponse);

	}
	
	
	private GeoCoordinate getGeoCoordinates(String latitude, String longitude) {
		
		String[] latSplit = latitude.split("\\.");
		String[] lonSplit = longitude.split("\\.");
		
		Integer lat = 0;
		Integer latDecimals = 0;
		Integer lon = 0;
		Integer lonDecimals = 0;
		
		// Parse Latitude safely
		if(latSplit.length == 1) {
			lat = Integer.valueOf(latSplit[0]);
		} else if (latSplit.length == 2) {
			lat = Integer.valueOf(latSplit[0]);
			String decPart = latSplit[1];
			// Prevent NumberFormatException by keeping only the first 6 digits
			if (decPart.length() > 6) {
				decPart = decPart.substring(0, 6);
			}
			latDecimals = Integer.valueOf(decPart);
		}
		
		// Parse Longitude safely
		if(lonSplit.length == 1) {
			lon = Integer.valueOf(lonSplit[0]);
		} else if (lonSplit.length == 2) {
			lon = Integer.valueOf(lonSplit[0]);
			String decPart = lonSplit[1];
			// Prevent NumberFormatException by keeping only the first 6 digits
			if (decPart.length() > 6) {
				decPart = decPart.substring(0, 6);
			}
			lonDecimals = Integer.valueOf(decPart);
		}
		
		LOGGER.info(String.format(
				"After splitting: lat:'%d' , latDecimals:'%d', lon: '%d' , lonDecimals: '%d' ;",
				lat, latDecimals, lon, lonDecimals
					)
				);
		
		GeoCoordinate geoCoord = new GeoCoordinate(lat, latDecimals, lon, lonDecimals);
		
		return geoCoord;
		
	}
	private CurrentWeatherResponse convertWeatherAPIResponseToInternalResponse(WeatherResponse weatherAPIResponse) {

		CurrentWeatherResponse currWeatherResponse = new CurrentWeatherResponse();

		

		currWeatherResponse.setCloud(weatherAPIResponse.getCurrent().getCloud());
		currWeatherResponse.setCountry(weatherAPIResponse.getLocation().getCountry());
		currWeatherResponse.setHumidity(weatherAPIResponse.getCurrent().getHumidity());
		currWeatherResponse.setLastUpdatedEpoch(weatherAPIResponse.getCurrent().getLast_updated_epoch());
		currWeatherResponse.setName(weatherAPIResponse.getLocation().getName());
		currWeatherResponse.setTimeZoneId(weatherAPIResponse.getLocation().getTz_id());
		currWeatherResponse.setWindMPH(weatherAPIResponse.getCurrent().getWind_mph());
		currWeatherResponse.setWindKPH(weatherAPIResponse.getCurrent().getWind_kph());
		currWeatherResponse.setWindDegree(weatherAPIResponse.getCurrent().getWind_degree());
		currWeatherResponse.setWindDirection(weatherAPIResponse.getCurrent().getWind_dir());
		currWeatherResponse.setHumidity(weatherAPIResponse.getCurrent().getHumidity());
		currWeatherResponse.setTempCelcius(weatherAPIResponse.getCurrent().getTemp_c());
		currWeatherResponse.setRegion(weatherAPIResponse.getLocation().getRegion());
		currWeatherResponse.setWeatherShortInfo(weatherAPIResponse.getCurrent().getCondition().getText());
		currWeatherResponse.setLocaltime(weatherAPIResponse.getLocation().getLocaltime());
		String localtime = weatherAPIResponse.getLocation().getLocaltime();
		LocalDateTime dateTime = LocalDateTime.parse(localtime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		currWeatherResponse.setYear(dateTime.getYear());
		currWeatherResponse.setMonth(dateTime.getMonthValue());
		currWeatherResponse.setDay(dateTime.getDayOfMonth());
		currWeatherResponse.setHour(dateTime.getHour());




		return currWeatherResponse;
	}

	private WeatherResponse invokeWeatherAPI(String latitude, String longitude) {

		Response response;
		String weatherinfo;


		try {


			Request request = new Request.Builder()
					.url("http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + latitude + "&q="
							+ longitude)
					.get().build();

			response = client.newCall(request).execute();
			weatherinfo = response.body().string();
			LOGGER.debug(weatherinfo);

		} catch (IOException e) {

			throw new RuntimeException(e);

		}

		// Convert incoming response into internal java object
		WeatherResponse weatherAPIResponse;


		try {
			weatherAPIResponse = mapper.readValue(weatherinfo, WeatherResponse.class);
			LOGGER.info("User input lat/lon: {}, {}", latitude, longitude);
			LOGGER.info("Parsed WeatherAPI lat/lon: " + weatherAPIResponse.getLocation().getLat() + ", "
					+ weatherAPIResponse.getLocation().getLon());
		} catch (JsonProcessingException e) {

			throw new RuntimeException("Couldn't parse reponse json string to WeatherRespnse java object.", e);

		}

		return weatherAPIResponse;
	}

	private CurrentWeatherResponse checkDBForCurrentWeatherInfo(GeoCoordinate geoCoord) {

		LocalTime dateTime = LocalTime.now();
		LocalDate date = LocalDate.now();

		int year = date.getYear();
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = dateTime.getHour();
		int minute = dateTime.getMinute();

		int finalHour = getFinalHourRoundOffHour(hour, minute);

		CurrentWeatherResponse wRespone = checkDBForWeatherInfo(day, month, year, finalHour, geoCoord);

		return wRespone;

	}

	private CurrentWeatherResponse checkDBForWeatherInfo(int day, int month, int year, int hour, GeoCoordinate geoCoord) {

		// execute this query.

		// if there is at least one record in result set that means weather data is
		// already present in db.
		// in this case, extract data from this DB record and populate in a
		// CurrentWeatherResponse object.

		// if there are no record, return null, indicating that no record was found.
		LOGGER.info(String.format(
				"Executing query in database. day:%d, month:%d, year:%d, hour:%d, lat:%d.%d , lon:%d.%d", day, month,
				year, hour, geoCoord.getLat(), geoCoord.getLatdecimal(), geoCoord.getLon(), geoCoord.getLonDecimal()));

		CurrentWeatherResponse weatherResponse = null;
		final String DB_URL = "jdbc:mysql://localhost/weatherCurrent";

		String selectQuery = "SELECT * FROM curr_weather WHERE day = ? AND month = ? AND year = ? AND hour = ? AND lat = ? AND latDecimal = ? AND lon = ? AND lonDecimal = ?";

		try {

			Connection connection = DriverManager.getConnection(DB_URL, "root", "root");

			PreparedStatement stmt = connection.prepareStatement(selectQuery);
			
			stmt.setInt(1, day);
			stmt.setInt(2, month);
			stmt.setInt(3, year);
			stmt.setInt(4, hour);
			stmt.setInt(5, geoCoord.getLat());
			stmt.setInt(6, geoCoord.getLatdecimal());
			stmt.setInt(7, geoCoord.getLon());
			stmt.setInt(8, geoCoord.getLonDecimal());
			
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				weatherResponse = new CurrentWeatherResponse();

				weatherResponse.setDay(rs.getInt("day"));
				weatherResponse.setYear(rs.getInt("Year"));
				weatherResponse.setRegion(rs.getString("region"));
				weatherResponse.setMonth(rs.getInt("month"));
				weatherResponse.setHour(rs.getInt("hour"));
				weatherResponse.setLatitude(rs.getInt("lat") + "." + rs.getInt("latDecimal"));
				weatherResponse.setLongitude(rs.getInt("lon") + "." + rs.getInt("lonDecimal"));
				weatherResponse.setCloud(rs.getDouble("cloud"));
				weatherResponse.setCountry(rs.getString("country"));
				weatherResponse.setHumidity(rs.getDouble("humidity"));

				weatherResponse.setLastUpdatedEpoch(rs.getLong("last_updated_timestamp"));
				weatherResponse.setCreatedTimeStamp(rs.getInt("created_time_stamp"));

				weatherResponse.setLocaltimeEpoch(rs.getLong("localtime_epoch"));
				weatherResponse.setName(rs.getString("name"));
				weatherResponse.setLocaltime(rs.getString("local_time"));

				weatherResponse.setTempCelcius(rs.getDouble("temp_celcius"));
				weatherResponse.setTimeZoneId(rs.getString("time_zone_id"));
				weatherResponse.setWeatherShortInfo(rs.getString("weather_short_info"));
				weatherResponse.setLastUpdatedEpoch(rs.getLong("last_updated_epoch"));
				weatherResponse.setLocaltimeEpoch(rs.getLong("localtime_epoch"));

				weatherResponse.setWindDegree(rs.getDouble("wind_degree"));
				weatherResponse.setWindMPH(rs.getDouble("wind_mph"));
				weatherResponse.setWindKPH(rs.getDouble("wind_kph"));
				weatherResponse.setWindDirection(rs.getString("wind_direction"));


			}

		}

		catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return weatherResponse;

	}

	private void insertWeatherInfoIntoDB(CurrentWeatherResponse currWeatherResponse, GeoCoordinate geoCoord) {

		LOGGER.info("Inserting weather info into database");

		try {

			Connection connection = DriverManager.getConnection(DB_URL, "root", "root");

			String insertQuery = "INSERT INTO curr_weather ("
					+ "name, region, country, lat, latDecimal, lon, lonDecimal , time_zone_id, local_time, localtime_epoch, "
					+ "temp_celcius, last_updated_epoch, weather_short_info, wind_mph, wind_kph, "
					+ "wind_degree, wind_direction, humidity, cloud, created_time_stamp,last_updated_timestamp ,"
					+ "day, month, year, hour) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

			PreparedStatement ps = connection.prepareStatement(insertQuery);

			LocalTime dateTime = LocalTime.now();
			LocalDate date = LocalDate.now();

			int year = date.getYear();
			int month = date.getMonthValue();
			int day = date.getDayOfMonth();
			int hour = dateTime.getHour();
			int minute = dateTime.getMinute();

			int finalHour = getFinalHourRoundOffHour(hour, minute);

			ps.setString(1, currWeatherResponse.getName());
			ps.setString(2, currWeatherResponse.getRegion());
			ps.setString(3, currWeatherResponse.getCountry());
			
			ps.setInt(4, geoCoord.getLat());
			ps.setInt(5, geoCoord.getLatdecimal());
			ps.setInt(6, geoCoord.getLon());
			ps.setInt(7, geoCoord.getLonDecimal());
			ps.setString(8, currWeatherResponse.getTimeZoneId());
			ps.setString(9, currWeatherResponse.getLocaltime());
			ps.setLong(10, currWeatherResponse.getLocaltimeEpoch());
			ps.setDouble(11, currWeatherResponse.getTempCelcius());
			ps.setLong(12, currWeatherResponse.getLastUpdatedEpoch());
			ps.setString(13, currWeatherResponse.getWeatherShortInfo());
			ps.setDouble(14, currWeatherResponse.getWindMPH());
			ps.setDouble(15, currWeatherResponse.getWindKPH());
			ps.setDouble(16, currWeatherResponse.getWindDegree());
			ps.setString(17, currWeatherResponse.getWindDirection());
			ps.setDouble(18, currWeatherResponse.getHumidity());
			ps.setDouble(19, currWeatherResponse.getCloud());
			ps.setInt(20, currWeatherResponse.getCreatedTimeStamp());
			ps.setString(21, currWeatherResponse.getLastUpdatedTimeStamp());
			ps.setInt(22, day);
			ps.setInt(23, month);
			ps.setInt(24, year);
			ps.setInt(25, finalHour);

			ps.executeUpdate();
			ps.close();
			connection.close();

		} catch (SQLException e) {

			throw new RuntimeException("Failed to insert records into database", e);

		}

	}

	private int getCurrentRoundOffHour() {

		LocalTime dateTime = LocalTime.now();
		LocalDate date = LocalDate.now();

		int year = date.getYear();
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = dateTime.getHour();
		int minute = dateTime.getMinute();

		int finalHour = hour;

		if (minute == 0 || minute <= 30) {

		} else {

			if (finalHour + 1 == 24) {

			} else {

				finalHour = hour + 1;
			}

		}

		return finalHour;

	}

	private int getFinalHourRoundOffHour(int hour, int minute) {

		int finalHour = hour;

		if (minute == 0 || minute <= 30) {

		} else {

			if (finalHour + 1 == 24) {

			} else {

				finalHour = hour + 1;
			}

		}

		return finalHour;

	}

	@GetMapping("/forecasttime/place")
	public ResponseEntity<String> searchForecastWeather(@RequestParam double latitude, @RequestParam double longitude,
			@RequestParam(defaultValue = "3") int days) {

		OkHttpClient client = new OkHttpClient();
		String location = latitude + "," + longitude;

		Request request = new Request.Builder().url("http://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q="
				+ latitude + "," + longitude + "&days=" + days).get().build();

		try {
			Response response = client.newCall(request).execute();
			String weatherinfo = response.body().string();
			return ResponseEntity.ok().body(weatherinfo);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);

		}
	}
}
