package com.openmarket.productmanagement;

public class ProductManagementResponse {
	private int categoryId;
	private String categoryName;
	private int productId;
	private String productName;
	private String description;
	private String imageUrl;
	private String priceRange;
	private int varietyId;
	private String groupLabel;
	private String varietyName;

	// NEW SELLER FIELDS
	private String farmerName;
	private String farmerMobile;
	private String unitType;
	private String harvestDate;

	public ProductManagementResponse() {

	}

	// Updated Constructor
	public ProductManagementResponse(int categoryId, String categoryName, int productId, String productName,
			String description, String imageUrl, String priceRange, int varietyId, String groupLabel,
			String varietyName, String farmerName, String farmerMobile, String unitType, String harvestDate) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.productId = productId;
		this.productName = productName;
		this.description = description;
		this.imageUrl = imageUrl;
		this.priceRange = priceRange;
		this.varietyId = varietyId;
		this.groupLabel = groupLabel;
		this.varietyName = varietyName;
		this.farmerName = farmerName;
		this.farmerMobile = farmerMobile;
		this.unitType = unitType;
		this.harvestDate = harvestDate;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getPriceRange() {
		return priceRange;
	}

	public void setPriceRange(String priceRange) {
		this.priceRange = priceRange;
	}

	public int getVarietyId() {
		return varietyId;
	}

	public void setVarietyId(int varietyId) {
		this.varietyId = varietyId;
	}

	public String getGroupLabel() {
		return groupLabel;
	}

	public void setGroupLabel(String groupLabel) {
		this.groupLabel = groupLabel;
	}

	public String getVarietyName() {
		return varietyName;
	}

	public void setVarietyName(String varietyName) {
		this.varietyName = varietyName;
	}

	public String getFarmerName() {
		return farmerName;
	}

	public void setFarmerName(String farmerName) {
		this.farmerName = farmerName;
	}

	public String getFarmerMobile() {
		return farmerMobile;
	}

	public void setFarmerMobile(String farmerMobile) {
		this.farmerMobile = farmerMobile;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public String getHarvestDate() {
		return harvestDate;
	}

	public void setHarvestDate(String harvestDate) {
		this.harvestDate = harvestDate;
	}

}