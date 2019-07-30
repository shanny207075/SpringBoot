package com.example.linebot.carousel.model;

import java.util.List;

public class Template {
	private String type;

	private List<Column> columns;

	// 圖片寬高比
	private String imageAspectRatio;
	// 圖片尺寸 cover:填滿整個框框/contain:顯示完整圖片
	private String imageSize;
	
	public Template(){
		this.imageAspectRatio ="rectangle";
		this.imageSize ="cover";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public String getImageAspectRatio() {
		return imageAspectRatio;
	}

	public void setImageAspectRatio(String imageAspectRatio) {
		this.imageAspectRatio = imageAspectRatio;
	}

	public String getImageSize() {
		return imageSize;
	}

	public void setImageSize(String imageSize) {
		this.imageSize = imageSize;
	}

}