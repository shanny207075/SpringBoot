package com.example.linebot.carousel.model;

public class Carousel {
	private String type;

	private String altText;

	private Template template;

	public Carousel() {
		this.type = "template";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAltText() {
		return altText;
	}

	public void setAltText(String altText) {
		this.altText = altText;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

}