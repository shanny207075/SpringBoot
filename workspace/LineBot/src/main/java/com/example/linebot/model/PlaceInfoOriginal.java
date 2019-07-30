package com.example.linebot.model;

public class PlaceInfoOriginal {

	// 店家地址
	private String address;

	// 網友推薦指數
	private Double rating;

	// 店家店名
	private String name;

	// 店家圖片參考位置
	private String phoneRef;

	private String placeId;

	// 店家緯度
	private Double lat;

	// 店家經度
	private Double lng;

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public String getPhoneRef() {
		return phoneRef;
	}

	public void setPhoneRef(String phoneRef) {
		this.phoneRef = phoneRef;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

}