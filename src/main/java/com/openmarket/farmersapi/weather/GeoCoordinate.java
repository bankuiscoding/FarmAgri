package com.openmarket.farmersapi.weather;

public class GeoCoordinate {
	
	Integer lat;
	Integer latdecimal;
	Integer lon;
	Integer lonDecimal;
	
	public GeoCoordinate(Integer lat, Integer latdecimal, Integer lon, Integer lonDecimal) {
		super();
		this.lat = lat;
		this.latdecimal = latdecimal;
		this.lon = lon;
		this.lonDecimal = lonDecimal;
	}

	Integer getLat() {
		return lat;
	}
	public void setLat(Integer lat) {
		this.lat = lat;
	}
	public Integer getLatdecimal() {
		return latdecimal;
	}
	public void setLatdecimal(Integer latdecimal) {
		this.latdecimal = latdecimal;
	}
	public Integer getLon() {
		return lon;
	}
	public void setLon(Integer lon) {
		this.lon = lon;
	}
	public Integer getLonDecimal() {
		return lonDecimal;
	}
	public void setLonDecimal(Integer lonDecimal) {
		this.lonDecimal = lonDecimal;
	}

	
	
}
