package com.openmarket.farmersapi.weather;



public class CurrentWeatherResponse {
	
	
	
    private String name;
    private String region;
    private String country;

    private String timeZoneId;
    private long localtimeEpoch;
    private String localtime;
    
    private double tempCelcius;
    private long lastUpdatedEpoch;
    private String weatherShortInfo;
    private double windMPH;
    private double windKPH;
    private double windDegree;
	private String windDirection;
    private double humidity;
    private double cloud;
	private int Day;
	private int Month;
	private int Year;
	private int Hour;
	private String latitude;
	private String longitude;
	public int createdTimeStamp;
	public String lastUpdatedTimeStamp;
	
	
	public String getLastUpdatedTimeStamp() {
		return lastUpdatedTimeStamp;
	}

	public void setLastUpdatedTimeStamp(String string) {
		this.lastUpdatedTimeStamp = lastUpdatedTimeStamp;
	}

	public int getCreatedTimeStamp() {
		return createdTimeStamp;
	}

	public void setCreatedTimeStamp(int createdTimeStamp) {
		this.createdTimeStamp = createdTimeStamp;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getTimeZoneId() {
		return timeZoneId;
	}
	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}
	public long getLocaltimeEpoch() {
		return localtimeEpoch;
	}
	public void setLocaltimeEpoch(long localtimeEpoch) {
		this.localtimeEpoch = localtimeEpoch;
	}
	public String getLocaltime() {
		return localtime;
	}
	public void setLocaltime(String localtime) {
		this.localtime = localtime;
	}
	public double getTempCelcius() {
		return tempCelcius;
	}
	public void setTempCelcius(double tempCelcius) {
		this.tempCelcius = tempCelcius;
	}
	public long getLastUpdatedEpoch() {
		return lastUpdatedEpoch;
	}
	public void setLastUpdatedEpoch(long lastUpdatedEpoch) {
		this.lastUpdatedEpoch = lastUpdatedEpoch;
	}
	public String getWeatherShortInfo() {
		return weatherShortInfo;
	}
	public void setWeatherShortInfo(String weatherShortInfo) {
		this.weatherShortInfo = weatherShortInfo;
	}
	public double getWindMPH() {
		return windMPH;
	}
	public void setWindMPH(double windMPH) {
		this.windMPH = windMPH;
	}
	public double getWindKPH() {
		return windKPH;
	}
	public void setWindKPH(double windKPH) {
		this.windKPH = windKPH;
	}
	public double getWindDegree() {
		return windDegree;
	}
	public void setWindDegree(double windDegree) {
		this.windDegree = windDegree;
	}
	public String getWindDirection() {
		return windDirection;
	}
	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}
	public double getHumidity() {
		return humidity;
	}
	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}
	public double getCloud() {
		return cloud;
	}
	public void setCloud(double cloud) {
		this.cloud = cloud;
	}
	public int getDay() {
		return Day;
	}
	public void setDay(int day) {
		Day = day;
	}
	public int getMonth() {
		return Month;
	}
	public void setMonth(int month) {
		Month = month;
	}
	public int getYear() {
		return Year;
	}
	public void setYear(int year) {
		Year = year;
	}
	public int getHour() {
		return Hour;
	}
	public void setHour(int hour) {
		Hour = hour;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}


	
	

    
    
    
}
