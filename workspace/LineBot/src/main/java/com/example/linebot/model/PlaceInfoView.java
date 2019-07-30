package com.example.linebot.model;

public class PlaceInfoView {

	// 店家地址
	private String address;

	// 網友推薦指數
	private Double rating;

	// 店家店名
	private String name;

	// 店家地址url
	private String  mapUrl;
	
	// 店家照片url
	private String photoUrl;
	
	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getMapUrl() {
		return mapUrl;
	}

	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}