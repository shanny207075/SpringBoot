package com.example.linebot.carousel.model;

// 點按消息中的按鈕或圖像時要採取的操作類型
public class Action {
	private String type;

	private String label;

	private String data;
	// 當type為uri，才須給值
	private String uri;
	// 當type為message，才須給值
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}