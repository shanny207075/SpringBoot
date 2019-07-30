package com.example.linebot.carousel.model;

import java.util.List;

public class Column {
	private String thumbnailImageUrl;

	// 圖片背景顏色
	private String imageBackgroundColor;

	private DefaultAction defaultAction;

	private List<Action> actions;

	// 標題
	private String title;

	// 文字說明
	private String text;

	public Column() {
		this.imageBackgroundColor = "#E8DDA0";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getThumbnailImageUrl() {
		return thumbnailImageUrl;
	}

	public void setThumbnailImageUrl(String thumbnailImageUrl) {
		this.thumbnailImageUrl = thumbnailImageUrl;
	}

	public String getImageBackgroundColor() {
		return imageBackgroundColor;
	}

	public void setImageBackgroundColor(String imageBackgroundColor) {
		this.imageBackgroundColor = imageBackgroundColor;
	}

	public DefaultAction getDefaultAction() {
		return defaultAction;
	}

	public void setDefaultAction(DefaultAction defaultAction) {
		this.defaultAction = defaultAction;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

}