package com.openmarket.farmersapi.weather.dto;

public class WeatherResponse {
	
	private String userMessage;
	
    private WeatherResponseLocation location;
    private WeatherResponseCurrentWeather current;


	// no constructor argument:NECESSARY
    public WeatherResponse() {}
    
    
    
    
    // Getters and Setters
    public WeatherResponseLocation getLocation() { return location; }
    public void setLocation(WeatherResponseLocation location) { this.location = location; }

    public WeatherResponseCurrentWeather getCurrent() { return current; }
    public void setCurrent(WeatherResponseCurrentWeather current) { this.current = current; }




	public String getUserMessage() {
		return userMessage;
	}




	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}
    
    
}
