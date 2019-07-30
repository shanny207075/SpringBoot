package com.example.linebot.controller;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class StickerController {

	/**
	 * 組回傳的response body json 格式訊息
	 */
	public  String buildSticker(String replyToken) {
		int stickerId = 52002734 + new Random().nextInt(45);
		return "{\"replyToken\":\"" + replyToken + "\",\"messages\":[{\"type\":\"sticker\",\"packageId\":11537,\"stickerId\":\"" + stickerId + "\"}]}";
	}

}
