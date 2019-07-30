package com.example.linebot.controller;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.linebot.carousel.model.Action;
import com.example.linebot.carousel.model.Carousel;
import com.example.linebot.carousel.model.Column;
import com.example.linebot.carousel.model.DefaultAction;
import com.example.linebot.carousel.model.Template;
import com.example.linebot.model.Image;
import com.example.linebot.model.Movie;

@Component
public class TextController {

	private final Logger log = Logger.getLogger("tesglog");

	// 打招呼
	private List<String> GREETINGS = Arrays.asList("hi", "hello", "你好", "哈囉", "安", "嗨", "您好");
	// 回復打招呼
	private List<String> GREETINGS_RESPONSE = Arrays.asList("你好呀(^o^)/", "哈囉～ヾ(・ω・ｏ)", "安安呀( ･ω･)ﾉ", "嗨～( ﾟ▽ﾟ)ﾉ");
	// 近期電影
	private List<String> MOVIE = Arrays.asList("電影", "movie");
	// 近期新聞
	private List<String> NEWS = Arrays.asList("新聞", "news");
	// 無法提供服務訊息
	private String serviceError = "很抱歉，目前無法提供''{0}''的服務...(ಥ﹏ಥ)";
	// 提供服務標題
	private String serviceTitle = ":*:･ ''{0}'' ･:*:  (๑˙❥˙๑)";
	// 需要選單
	private List<String> HELP = Arrays.asList("help", "menu", "選單");
	// 避開關鍵字
	private String locationKeyWord = "請給我你的「位置資訊」";
	// 155公車路線圖
	private final String BUS_155 = "155公車路線圖";
	// 去程(秋虹谷 => 后里)
	private final String GOING = "去程";
	// 回程(后里 => 秋虹谷)
	private final String RETURN = "回程";

	private final String MESSAGE = "message";

	/**
	 * 組回傳的response body json 格式訊息
	 */
	public String buildMessage(String replyToken, String requestMsg) {
		String greetingMsg = "";
		String responseMsg = "";

		responseMsg = replyRecommendMovie(requestMsg);

		if (!StringUtils.isEmpty(responseMsg)) {
			return returnMessage(replyToken, responseMsg);
		}

		responseMsg = replyHelp(requestMsg);

		if (!StringUtils.isEmpty(responseMsg)) {
			return returnMessage(replyToken, responseMsg);
		}

		responseMsg = replyBus(requestMsg);

		if (!StringUtils.isEmpty(responseMsg)) {
			return returnMessage(replyToken, responseMsg);
		}

		greetingMsg = replyGreeting(requestMsg, greetingMsg);

		responseMsg = replyNews(requestMsg, responseMsg);

		// 是否顯示menu
		boolean isShowMenu = false;

		// 無法辨識的詞彙
		if (unidentifiedKeyWord(responseMsg, greetingMsg, requestMsg)) {
			responseMsg = "不好意思，我不太了解「" + requestMsg + "」的意思...(´-ω-`)";
			isShowMenu = true;
		}

		// 打招呼後
		if (!StringUtils.isEmpty(greetingMsg)) {
			responseMsg = greetingMsg;
			isShowMenu = true;
		}

		String menuCarousel = isShowMenu ? "," + buildMenuCarousel() : "";

		responseMsg = "{\"type\":\"text\",\"text\":\"" + responseMsg + "\"}" + menuCarousel;

		return returnMessage(replyToken, responseMsg);
	}

	/**
	 * 回復「去程」
	 */
	private String replyGoing(String requestMsg) {
		if (!GOING.equals(requestMsg)) {
			return "";
		}

		return "{\"type\":\"text\",\"text\":\"" + "\"}";
	}

	/**
	 * 回復「155公車路線圖」
	 */
	private String replyBus(String requestMsg) {
		if (!BUS_155.equals(requestMsg)) {
			return "";
		}

		return "{\"type\":\"text\",\"text\":\"https://www.traffic.taichung.gov.tw/df_ufiles/df_pics/155%E8%B7%AF(108.03.18).jpg\"}";
	}

	/**
	 * 無法辨識的詞彙
	 */
	private boolean unidentifiedKeyWord(String responseMsg, String greetingMsg, String requestMsg) {
		return StringUtils.isEmpty(responseMsg) && StringUtils.isEmpty(greetingMsg) && !locationKeyWord.equals(requestMsg);
	}

	/**
	 * 回傳類型為carousel template
	 */
	private String returnMessage(String replyToken, String responseMsg) {
		return "{\"replyToken\":\"" + replyToken + "\",\"messages\":[" + responseMsg + "]}";
	}

	/**
	 * 回復「求救、選單」
	 */
	private String replyHelp(String requestMsg) {
		boolean isHelp = false;
		for (String s : HELP) {
			if (requestMsg.toLowerCase().indexOf(s) > -1) {
				isHelp = true;
				break;
			}
		}

		if (!isHelp) {
			return "";
		}
		return buildMenuCarousel();
	}

	/**
	 * 組成選單
	 */
	private String buildMenuCarousel() {
		Carousel carousel = new Carousel();
		Template template = new Template();

		List<Column> columns = new ArrayList<>();

		Column common = getCommonColumn();
		columns.add(common);

		Column mom = getMomColumn();
		columns.add(mom);

		template.setType("carousel");
		template.setColumns(columns);

		carousel.setAltText("這是目前有提供的服務～～");
		carousel.setTemplate(template);

		JSONObject jsonObject = new JSONObject(carousel);

		return jsonObject.toString();
	}

	/**
	 * 一般服務選單內容
	 */
	private Column getMomColumn() {
		// 媽媽服務選單
		Column mom = new Column();
		mom.setTitle("媽媽選單");
		mom.setText("請選擇服務項目");

		// 點及按鈕要做的事
		List<Action> momActions = new ArrayList<>();
		Action bus = new Action();
		bus.setLabel("155公車路線圖");
		bus.setType(MESSAGE);
		bus.setText("155公車路線圖");
		momActions.add(bus);

		Action going = new Action();
		going.setLabel("去程(秋虹谷 → 后里區公所)");
		going.setType(MESSAGE);
		going.setText("去程(秋虹谷 → 后里區公所)");
		momActions.add(going);

		Action bus2 = new Action();
		bus2.setLabel("155公車路線圖");
		bus2.setType(MESSAGE);
		bus2.setText("155公車路線圖");
		momActions.add(bus2);

		mom.setActions(momActions);
		mom.setThumbnailImageUrl("https://image.flaticon.com/icons/svg/1725/1725447.svg");
		return mom;
	}

	/**
	 * 一般服務選單內容
	 */
	private Column getCommonColumn() {
		// 一般服務選單
		Column common = new Column();

		common.setTitle("一般選單");
		common.setText("請選擇服務項目");

		// 點及按鈕要做的事
		List<Action> actions = new ArrayList<>();
		Action movie = new Action();
		movie.setLabel("電影排行榜");
		movie.setType(MESSAGE);
		movie.setText("電影排行榜");
		actions.add(movie);

		Action news = new Action();
		news.setLabel("今日頭條新聞");
		news.setType(MESSAGE);
		news.setText("今日頭條新聞");
		actions.add(news);

		Action eating = new Action();
		eating.setLabel("附近美食");
		eating.setType(MESSAGE);
		eating.setText(locationKeyWord);
		actions.add(eating);

		common.setActions(actions);

		common.setThumbnailImageUrl("https://image.flaticon.com/icons/svg/168/168319.svg");
		return common;
	}

	/**
	 * 回復「打招呼」
	 */
	private String replyGreeting(String requestMsg, String greetingMsg) {
		int random = new Random().nextInt(GREETINGS_RESPONSE.size());
		for (String s : GREETINGS) {
			if (requestMsg.toLowerCase().indexOf(s) > -1) {
				greetingMsg = GREETINGS_RESPONSE.get(random);
				break;
			}
		}
		return greetingMsg;
	}

	/**
	 * 回復「電影」
	 */
	private String replyRecommendMovie(String requestMsg) {
		boolean isMovie = false;
		for (String s : MOVIE) {
			if (requestMsg.toLowerCase().indexOf(s) > -1) {
				isMovie = true;
				break;
			}
		}

		if (!isMovie) {
			return "";
		}

		String serviceType = "近期電影排行榜";
		List<Movie> moviesList = parsingMovie("https://movies.yahoo.com.tw/chart.html");

		if (CollectionUtils.isEmpty(moviesList)) {
			return MessageFormat.format(serviceError, serviceType);
		}

		return buildCarousel(moviesList);
	}

	/**
	 * parsing奇摩電影排行
	 */
	private List<Movie> parsingMovie(String urlStr) {
		Document doc = null;
		try {
			doc = Jsoup.connect(urlStr).get();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		if (doc == null) {
			return new ArrayList<>();
		}

		List<Movie> movies = new ArrayList<>();

		Elements movieRankAll = doc.select("ul.ranking_list_r");
		Elements a = movieRankAll.select("a");
		for (Element content : a) {
			String movieLink = content.attr("href");
			String movieName = content.getElementsByTag("span").text();
			String movieRank = content.getElementsByTag("div").text();
			String rankType = content.attr("data-ga");
			if (rankType.indexOf("台北票房榜") < -1 || Integer.valueOf(movieRank) > 7) {
				break;
			}

			Document thisMovieDoc = createConnectMovieDoc(movieLink);
			String photoUrl = getMoviePhoto(thisMovieDoc);
			String score = getMovieScore(thisMovieDoc);

			Movie movie = new Movie();
			movie.setLink(movieLink);
			movie.setName(movieName);
			movie.setRank("第" + movieRank + "名");
			movie.setRankType(rankType);
			movie.setPhotoUrl(photoUrl);
			movie.setScore("網友滿意度:" + score);

			movies.add(movie);

		}
		return movies;
	}

	/**
	 * 取得電影分數
	 */
	public String getMovieScore(Document thisMovieDoc) {
		Elements movieScore = thisMovieDoc.select("div.score");
		return movieScore.select("div").attr("data-num");
	}

	/**
	 * 取得電影照片
	 */
	public String getMoviePhoto(Document thisMovieDoc) {
		Elements moviePhoto = thisMovieDoc.select("div.movie_intro_foto");
		return moviePhoto.select("img").attr("src");
	}

	/**
	 * 依據該電影url，建立連線
	 */
	public Document createConnectMovieDoc(String movieUrl) {
		Document doc = null;
		try {
			doc = Jsoup.connect(movieUrl).get();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return doc;
	}

	/**
	 * 
	 * 組成Carousel所需資料
	 * 
	 */
	public String buildCarousel(List<Movie> moviesList) {
		Carousel carousel = new Carousel();
		Template template = new Template();

		List<Column> columns = new ArrayList<>();
		for (Movie info : moviesList) {
			Column column = new Column();

			column.setTitle(info.getName());
			column.setText(info.getScore());

			// 點及圖片要做的事
			DefaultAction defaultAction = new DefaultAction();
			defaultAction.setType("uri");
			defaultAction.setLabel("查看詳情");
			defaultAction.setUri(info.getLink());

			// 點及按鈕要做的事
			List<Action> actions = new ArrayList<>();
			Action action = new Action();
			action.setLabel(info.getRank());
			action.setType("uri");
			action.setUri(info.getLink());
			actions.add(action);

			column.setThumbnailImageUrl(info.getPhotoUrl());
			column.setDefaultAction(defaultAction);
			column.setActions(actions);
			columns.add(column);
		}

		template.setType("carousel");
		template.setColumns(columns);

		carousel.setAltText("這是Yahoo近期的電影排行～～");
		carousel.setTemplate(template);

		JSONObject jsonObject = new JSONObject(carousel);

		return jsonObject.toString();
	}

	/**
	 * 回復「新聞」
	 */
	private String replyNews(String requestMsg, String responseMsg) {
		for (String s : NEWS) {
			if (requestMsg.toLowerCase().indexOf(s) > -1) {
				responseMsg = parsingNews("https://news.google.com/?hl=zh-TW&gl=TW&ceid=TW:zh-Hant");
				break;
			}
		}
		return responseMsg;
	}

	/**
	 * parsing Google頭條新聞
	 */
	private String parsingNews(String urlStr) {
		Document doc = null;
		try {
			doc = Jsoup.connect(urlStr).get();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		String serviceType = "Google頭條新聞";

		if (doc == null) {
			return MessageFormat.format(serviceError, serviceType);
		}

		StringBuilder str = new StringBuilder();
		str.append(MessageFormat.format(serviceTitle, serviceType));
		str.append("\\n\\n");
		Elements newsRankAll = doc.select("article.MQsxIb.xTewfe.R7GTQ.keNKEd.j7vNaf.Cc0Z5d.EjqUne");
		Elements a = newsRankAll.select("a.DY5T1d");
		int count = 0;
		for (Element content : a) {
			if (count > 4) {
				break;
			}

			String newsLink = buildLink(content.attr("href"));
			String newsName = content.text();

			if (newsLink.length() > 200) {
				continue;
			}

			if (!"".equals(newsName) && null != newsName) {
				// 處理跳脫字元
				str.append(newsName.replace("\"", "＂"));
				str.append("\\n");
				str.append(newsLink);
				str.append("\\n\\n");
			}

			count++;
		}
		return str.toString();
	}

	public String buildLink(String link) {
		return "https://news.google.com" + link.replace(".", "");
	}
}
