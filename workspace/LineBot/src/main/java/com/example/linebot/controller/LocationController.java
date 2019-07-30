package com.example.linebot.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.example.linebot.carousel.model.Action;
import com.example.linebot.carousel.model.Carousel;
import com.example.linebot.carousel.model.Column;
import com.example.linebot.carousel.model.DefaultAction;
import com.example.linebot.carousel.model.Template;
import com.example.linebot.model.PlaceInfoOriginal;
import com.example.linebot.model.PlaceInfoView;

@Component
public class LocationController {

	private final Logger log = Logger.getLogger("tesglog");

	private String apiKey = "";

	/**
	 * 依據經緯度的位置，透過google api尋找附近美食
	 * 
	 * @param replyToken
	 *            -> channel token
	 * @param key
	 *            -> google api key
	 * @param lat
	 *            -> 緯度
	 * @param lon
	 *            -> 經度
	 * 
	 */
	public String buildPlace(String replyToken, String key, String lat, String lon) {

		apiKey = key;

		String googleMapUrl = buildLocationUrl(lat, lon);

		String content = createConnectionAndGetContent(googleMapUrl);

		List<PlaceInfoOriginal> placeList = parsingJsonToList(content);

		List<PlaceInfoView> randomRankList = randomRankList(placeList);

		String responseMsg = buildCarousel(randomRankList);
		
		log.info(responseMsg);

		return "{\"replyToken\":\"" + replyToken + "\",\"messages\":[" + responseMsg + "]}";
	}

	/**
	 * 
	 * 組成Carousel所需資料
	 * 
	 * */
	public String buildCarousel(List<PlaceInfoView> randomRankList) {
		Carousel carousel = new Carousel();
		Template template = new Template();

		List<Column> columns = new ArrayList<>();
		for (PlaceInfoView info : randomRankList) {
			Column column = new Column();

			column.setTitle(info.getName());
			column.setText(info.getAddress());

			// 點及圖片要做的事
			DefaultAction defaultAction = new DefaultAction();
			defaultAction.setType("uri");
			defaultAction.setLabel("查詢店家地址");
			defaultAction.setUri(info.getMapUrl());

			// 點及按鈕要做的事
			List<Action> actions = new ArrayList<>();
			Action action = new Action();
			action.setLabel("推薦指數: " + info.getRating());
			action.setType("uri");

			action.setUri(info.getMapUrl());
			actions.add(action);

			column.setThumbnailImageUrl(info.getPhotoUrl());

			column.setDefaultAction(defaultAction);
			column.setActions(actions);
			columns.add(column);
		}

		template.setType("carousel");
		template.setColumns(columns);

		carousel.setAltText("這是Google所推薦附近的美食～～");
		carousel.setTemplate(template);

		JSONObject jsonObject = new JSONObject(carousel);

		return jsonObject.toString();
	}

	/**
	 * 根據經緯度及placeId資訊，組查詢地址的URL
	 * 
	 * @param placeId
	 *            -> google api place_id
	 * @param lat
	 *            -> 緯度
	 * @param lon
	 *            -> 經度
	 * 
	 */
	public String buildGoogleMapUrl(Double lat, Double lon, String placeId) {
		String googleMapUrl = "https://www.google.com/maps/search/?api=1&query={0},{1}&query_place_id={2}";
		MessageFormat message = new MessageFormat(googleMapUrl);
		Object[] array = new Object[] { lat, lon, placeId };
		return message.format(array);
	}

	/**
	 * 根據photo reference及api key資訊，組URL
	 * 
	 * @param key
	 *            -> google api key
	 * @param photoRef
	 *            -> google photo reference
	 * 
	 */
	public String buildPhotoUrl(String photoRef) {
		String googleMapUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference={0}&key={1}";
		MessageFormat message = new MessageFormat(googleMapUrl);
		Object[] array = new Object[] { photoRef, apiKey };
		return message.format(array);
	}

	/**
	 * 根據經緯度及api key資訊，組URL
	 * 
	 * @param key
	 *            -> google api key
	 * @param lat
	 *            -> 緯度
	 * @param lon
	 *            -> 經度
	 * 
	 */
	public String buildLocationUrl(String lat, String lon) {
		String googleMapUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key={0}&location={1},{2}&rankby=distance&type=restaurant&language=zh-TW";
		MessageFormat message = new MessageFormat(googleMapUrl);
		Object[] array = new Object[] { apiKey, lat, lon };
		return message.format(array);
	}

	/**
	 * 建立https連線，並取得內容
	 *
	 * @param lon
	 *            -> 組好的google url
	 * 
	 */
	public String createConnectionAndGetContent(String googleMapUrl) {
		StringBuilder responseData = new StringBuilder();
		try {
			// google map的網址
			URL url = new URL(googleMapUrl);
			URLConnection con = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				responseData.append(inputLine);
			}
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return responseData.toString();
	}

	/**
	 * 解析json組成List<PlaceInfo>
	 * 
	 * @param lon
	 *            -> 組好的google url
	 */
	public static List<PlaceInfoOriginal> parsingJsonToList(String content) {
		JSONObject jObj;
		List<PlaceInfoOriginal> placeList = null;
		try {
			placeList = new ArrayList<>();
			jObj = new JSONObject(content);
			JSONArray array = jObj.getJSONArray("results");
			for (int i = 0; i < array.length(); i++) {
				JSONObject record = (JSONObject) array.get(i);
				String address = record.getString("vicinity");
				String name = record.getString("name");
				String placeId = record.getString("place_id");
				JSONArray photos = record.getJSONArray("photos");
				JSONObject photo = (JSONObject) photos.get(0);
				String photoRef = photo.getString("photo_reference");
				JSONObject geometry = (JSONObject) record.get("geometry");
				JSONObject location = (JSONObject) geometry.get("location");
				Double lat = location.getDouble("lat");
				Double lng = location.getDouble("lng");

				Double rating = null;
				try {
					rating = record.getDouble("rating");
				}
				catch (Exception e) {

				}

				if (rating == null || rating < 3.6) {
					continue;
				}

				PlaceInfoOriginal info = new PlaceInfoOriginal();
				info.setAddress(address);
				info.setName(name);
				info.setRating(rating);
				info.setPhoneRef(photoRef);
				info.setPlaceId(placeId);
				info.setLat(lat);
				info.setLng(lng);
				placeList.add(info);
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return placeList;
	}

	/**
	 * 隨意抽取五家推薦店家資訊
	 * 
	 * @param placeList
	 *            -> 店家資訊 *
	 */
	public List<PlaceInfoView> randomRankList(List<PlaceInfoOriginal> placeList) {
		int count = 0;
		List<Integer> randomTmp = new ArrayList<>();
		List<PlaceInfoView> newList = new ArrayList<>();

		for (int i = 0; i < placeList.size(); i++) {
			if (count == 5) {
				break;
			}

			int random = getRandomNum(placeList.size(), randomTmp);
			randomTmp.add(random);
			PlaceInfoOriginal info = placeList.get(random);

			PlaceInfoView newInfo = new PlaceInfoView();
			newInfo.setAddress(info.getAddress());
			newInfo.setName(info.getName());
			newInfo.setRating(info.getRating());

			// 查詢店家地址
			String mapUrl = buildGoogleMapUrl(info.getLat(), info.getLng(), info.getPlaceId());
			newInfo.setMapUrl(mapUrl);

			// 店家照片
			String phoneRef = buildPhotoUrl(info.getPhoneRef());
			newInfo.setPhotoUrl(phoneRef);

			newList.add(newInfo);
			count++;
		}
		return newList;
	}

	/**
	 * 取得亂數
	 * 
	 * @param placeTotal
	 *            -> 店家總數量
	 * @param randomTmp
	 *            -> 出現過的亂數
	 */
	public Integer getRandomNum(int placeTotal, List<Integer> randomTmp) {
		int random = new Random().nextInt(placeTotal);
		if (randomTmp == null || randomTmp.size() == 0) {
			return random;
		}

		while (randomTmp.contains(random)) {
			random = new Random().nextInt(placeTotal);
		}

		return random;
	}

}
