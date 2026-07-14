package com.openmarket.farmersapi.registration;

import java.time.LocalDate;


public class WeatherCurrent {
	    private int id;
	    private LocalDate date;
	    private int hour;
	    private double locationLat;
	    private double locationLong;
	    private String locationRegion;
	    private String locationCountry;
	    private String locationCity;
	    private String locationTz;
	    private String dataJson;

		public WeatherCurrent() {
	    }

	    public WeatherCurrent(int id, LocalDate date, int hour, double locationLat, double locationLong,
	                          String locationRegion, String locationCountry, String locationCity,
	                          String locationTz, String dataJson) {
	        this.id = id;
	        this.date = date;
	        this.hour = hour;
	        this.locationLat = locationLat;
	        this.locationLong = locationLong;
	        this.locationRegion = locationRegion;
	        this.locationCountry = locationCountry;
	        this.locationCity = locationCity;
	        this.locationTz = locationTz;
	        this.dataJson = dataJson;
	    }

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public LocalDate getDate() {
			return date;
		}

		public void setDate(LocalDate date) {
			this.date = date;
		}

		public int getHour() {
			return hour;
		}

		public void setHour(int hour) {
			this.hour = hour;
		}

		public double getLocationLat() {
			return locationLat;
		}

		public void setLocationLat(double locationLat) {
			this.locationLat = locationLat;
		}

		public double getLocationLong() {
			return locationLong;
		}

		public void setLocationLong(double locationLong) {
			this.locationLong = locationLong;
		}

		public String getLocationRegion() {
			return locationRegion;
		}

		public void setLocationRegion(String locationRegion) {
			this.locationRegion = locationRegion;
		}

		public String getLocationCountry() {
			return locationCountry;
		}

		public void setLocationCountry(String locationCountry) {
			this.locationCountry = locationCountry;
		}

		public String getLocationCity() {
			return locationCity;
		}

		public void setLocationCity(String locationCity) {
			this.locationCity = locationCity;
		}

		public String getLocationTz() {
			return locationTz;
		}

		public void setLocationTz(String locationTz) {
			this.locationTz = locationTz;
		}

		public String getDataJson() {
			return dataJson;
		}

		public void setDataJson(String dataJson) {
			this.dataJson = dataJson;
		}

	}



