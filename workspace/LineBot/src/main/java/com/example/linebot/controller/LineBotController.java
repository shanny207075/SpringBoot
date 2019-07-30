package com.example.linebot.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.linebot.text.model.Event;
import com.example.linebot.text.model.EventWrapper;

@RestController
@RequestMapping("/linebot")
public class LineBotController {

	private final Logger log = Logger.getLogger("tesglog");
	// line access token
	private String  accessToken = System.getenv("ACCESS_TOKEN");
	// google map api key
	private String  apiKey = System.getenv("API_KEY");

	@Autowired
	TextController textCtrl;

	@Autowired
	StickerController stickerCtrl;
	
	@Autowired
	LocationController locationCtrl;

	@RequestMapping(value = "/test")
	public String justTest() {
		return "@@@ justTest  @@@";
	}

	@RequestMapping(value = "/callback")
	public void callback(@RequestBody EventWrapper events) {
		
		log.setLevel(Level.ALL);
		for (Event event : events.getEvents()) {
			switch (event.getType()) {
			case "message":
				// 當event為message時進入此case執行，其他event(如follow、unfollow、leave等)的case在此省略，您可自己增加
				log.info("@@@ This is a message event!  @@@");
				
				String replyToken = event.getReplyToken();
				String rspMessage = "";
				
				switch (event.getMessage().getType()) {

				case "text": 
					String requestMsg = event.getMessage().getText();
					log.info("@@@ text message  @@@" + requestMsg);
					
					rspMessage = textCtrl.buildMessage(replyToken, requestMsg);
					log.info("## response body json message ##" + rspMessage);
										
					break;
				case "image":
					break;
				case "audio":
					break;
				case "video":
					break;
				case "file":
					break;
				case "sticker":
					String stickerId = event.getMessage().getStickerId();
					log.info("@@@ sticker message  @@@" + stickerId);
					
					rspMessage = stickerCtrl.buildSticker(replyToken);
					log.info("## response body json message ##" + rspMessage);
					
					break;
				case "location":
					log.info("@@@ Longitude @@@" + event.getMessage().getLongitude());
					log.info("@@@ Latitude @@@" + event.getMessage().getLatitude());
					rspMessage = locationCtrl.buildPlace(replyToken, apiKey, event.getMessage().getLatitude(), event.getMessage().getLongitude());
					break;
				}
				
				sendResponseMessages(rspMessage);
				
				break;
			}
		}
	}

	/**
	 * 回傳訊息
	 */
	private void sendResponseMessages(String rspMessage) {

		// 建立連線
		HttpsURLConnection con = createConnection();

		if (con == null) {
			return;
		}

		// 將「回傳訊息」資料輸出
		writeOutPut(con, rspMessage);

		// 顯示回傳結果
		showResult(con);

	}

	/**
	 * 建立連線
	 */
	private HttpsURLConnection createConnection() {
		HttpsURLConnection con = null;
		try {

			// 回傳的網址
			URL myurl = new URL("https://api.line.me/v2/bot/message/reply");
			// 使用HttpsURLConnection建立https連線
			con = (HttpsURLConnection) myurl.openConnection();
			// 設定post方法
			con.setRequestMethod("POST");
			// 設定Content-Type為json
			con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			// 設定Authorization
			con.setRequestProperty("Authorization", "Bearer " + this.accessToken);
			// 允許輸入流，即允許下載
			con.setDoOutput(true);
			// 允許輸出流，即允許上傳
			con.setDoInput(true);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	/**
	 * 將「回傳訊息」資料輸出
	 */
	private void writeOutPut(HttpsURLConnection con, String messagne) {

		try {
			// 開啟HttpsURLConnection的連線
			DataOutputStream output = new DataOutputStream(con.getOutputStream());

			// 回傳訊息編碼為utf-8
			output.write(messagne.getBytes(Charset.forName("utf-8")));
			output.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 顯示回傳結果
	 */
	private void showResult(HttpsURLConnection con) {

		try {
			int rspCode = con.getResponseCode();
			// 顯示回傳的結果，若code為200代表回傳成功
			log.info("Resp Code:" + rspCode + "; Resp Message:" + con.getResponseMessage());
			printErrorMessage(rspCode, con.getErrorStream());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 顯示回傳的錯誤訊息
	 * 
	 * @throws Exception
	 */
	private void printErrorMessage(int rspCode, InputStream errorStream) throws Exception {
		if (rspCode != 200) {
			String response = readStreamConvertToString(errorStream);
			log.info("### response  ### : " + response);
		}
	}

	/**
	 * 將 InputStream 轉換為字串
	 */
	private String readStreamConvertToString(InputStream stream) throws Exception {
		InputStream inputStream = new BufferedInputStream(stream);
		StringBuilder builder = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = in.readLine()) != null) {
				builder.append(line);
			}
			in.close();
		}
		return builder.toString();
	}
}
