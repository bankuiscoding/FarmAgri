package com.openmarket.farmersapi;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

@RequestMapping("/search/weather")
@RestController
public class WeatherAPI {


	@Value("${weather.api.key}")
	String apiKey;

	@GetMapping("/current/place")
	public ResponseEntity searchCurrentWeather(@RequestParam String placeName) {

		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("text/plain");
		RequestBody body = RequestBody.create(mediaType, "");

		// sdsddd
		Request request = new Request.Builder()
				.url("http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=London")
				.get().build();

		try {
			Response response = client.newCall(request).execute();

			String weatherinfo = response.body().string();

			return ResponseEntity.ok().body(weatherinfo);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			throw new RuntimeException(e);
		}

	}

}
