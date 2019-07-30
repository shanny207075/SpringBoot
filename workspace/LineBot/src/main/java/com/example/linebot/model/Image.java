package com.example.linebot.model;

public class Image {

	// 回傳類型
	private String type;
	// 圖片原始url
	private String originalContentUrl;
	// 圖片預覽url
	private String previewImageUrl;

	public Image() {
		this.type = "image";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOriginalContentUrl() {
		return originalContentUrl;
	}

	public void setOriginalContentUrl(String originalContentUrl) {
		this.originalContentUrl = originalContentUrl;
	}

	public String getPreviewImageUrl() {
		return previewImageUrl;
	}

	public void setPreviewImageUrl(String previewImageUrl) {
		this.previewImageUrl = previewImageUrl;
	}

}