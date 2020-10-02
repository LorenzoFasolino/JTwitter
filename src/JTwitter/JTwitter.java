package JTwitter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.*;

import config.Config;

//import org.json.simple.*;

public class JTwitter {

	private final static String getTokenURL = "https://api.twitter.com/oauth2/token";
	private static String bearerToken = "";
	private static String APIKEY = Config.APIKEY;
	private static String APISECRET = Config.APISECRET;
	
	

	/**
	 * @param apiKey apiKey
	 * @param apiSecret apiSecret
	 * Con questo costruttore si forza ad utilizzare le apikey e apisecret passate come parametri
	 */
	public JTwitter(String apiKey, String apiSecret) {

		APIKEY = apiKey;
		APISECRET = apiSecret;

	}
	
	

	/**
	 * Costruttore vuoto
	 */
	public JTwitter() {

	}
	
	

	/**
	 * @param screenName Nome dell'utente
	 * @param count Il numero di tweet che si vogliono avere, max: 200
	 * @return Ritorna i primi count tweets dell' utente con il nome screenName
	 */
	public JsonArray getUserTimeLine(String screenName, int count) {

		try {
			if (bearerToken.equals("")) {
				bearerToken = requestBearerToken(getTokenURL);
			}

			return fetchTimelineTweet("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name="
					+ screenName + "&count=" + count + "&exclude_replies=true&tweet_mode=extended");
		} catch (IOException e) {
			System.out.println("\n Errore per l'utente: " + screenName);
			// e.printStackTrace();
			return new JsonArray();
		}

	}
	
	

	/**
	 * @param userId ID dell'utente
	 * @param count Il numero di tweet che si vogliono avere, max: 200
	 * @return Ritorna i primi count tweets dell' utente con l'id userId
	 */
	public JsonArray getUserTimeLineWithId(String userId, int count) {

		try {
			if (bearerToken.equals("")) {
				bearerToken = requestBearerToken(getTokenURL);

			}

			return fetchTimelineTweet("https://api.twitter.com/1.1/statuses/user_timeline.json?user_id=" + userId
					+ "&count=" + count + "&exclude_replies=true&tweet_mode=extended");
		} catch (IOException e) {
			System.out.println("IOException e");
			e.printStackTrace();
			return null;
		}

	}
	
	

	/**
	 * @param tweets Un array Json contenente diversi tweets
	 * @return Una lista di Hashtag ordinati per numero di occorrenze
	 */
	public ArrayList<Hashtag> hashtagCounter(JsonArray tweets) {

		ArrayList<Hashtag> hashtags = new ArrayList<>();

		for (int i = 0; i < tweets.size(); i++) {

			JsonObject tweet = tweets.get(i).getAsJsonObject();

			JsonObject entities = (JsonObject) tweet.get("entities");

			JsonArray tweetHashtags = entities.getAsJsonArray("hashtags");

			for (int j = 0; j < tweetHashtags.size(); j++) {

				boolean flag = false;

				String hashtag = tweetHashtags.get(j).getAsJsonObject().get("text").getAsString();

				for (Hashtag h : hashtags) {

					if (h.getName().equals(hashtag)) {

						h.setCount(h.getCount() + 1);
						flag = true;
						break;
					}

				}

				if (!flag) {
					Hashtag newHashtag = new Hashtag(hashtag);
					newHashtag.setCount(newHashtag.getCount() + 1);
					hashtags.add(newHashtag);
					flag = false;
				}

			}

		}

		hashtags.sort(new Comparator<Hashtag>() {

			@Override
			public int compare(Hashtag o1, Hashtag o2) {
				// TODO Auto-generated method stub
				return o2.getCount() - o1.getCount();
			}

		});

		return hashtags;
	}
	
	
	

	/**
	 * @param screenName Nome dell'utente
	 * @param count Il numero di tweets che si vogliono avere, max: 200
	 * @param maxId L'id massimo tra i tweets che si vuole cercare  
	 * @return Ritorna i tweet dell'utente dove l'id massimo dei tweet è maxId
	 */
	public JsonArray getUserTimeLineMaxId(String screenName, int count, String maxId) {

		try {
			if (bearerToken.equals("")) {
				bearerToken = requestBearerToken(getTokenURL);
			}

			return fetchTimelineTweet(
					"https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + screenName + "&count="
							+ count + "&max_id=" + maxId + "&exclude_replies=true&tweet_mode=extended");
		} catch (IOException e) {
			return null;
		}

	}
	
	
	

	/**
	 * @param tweets Un insieme di tweets
	 * @param until La data fino alla quale si vogliono tagliare i tweets
	 * @return Tutti i tweets che vengono prima della data until
	 */
	public JsonArray cutDataUntil(JsonArray tweets, Date until) {

		JsonArray tweets2 = new JsonArray();

		for (int i = 0; i < tweets.size(); i++) {

			JsonObject tweet = tweets.get(i).getAsJsonObject();

			String date = tweet.get("created_at").getAsString();

			Date tweetDate = JTwitter.getTwitterDate(date);

			if (tweetDate.before(until) || tweetDate.equals(until)) {

				tweets2.add(tweet);

			}

		}

		return tweets2;

	}
	
	
	

	/**
	 * @param screenName Il nome dell'utente
	 * @param since La data fino dalla quale si vogliono raccogliere i tweets
	 * @return Ritorna 200 i tweets dell'utente che partono dalla data since
	 */
	public JsonArray getUserTimeLineSince(String screenName, Date since) {

		JsonArray tweets = getUserTimeLine(screenName, 200);

		if (tweets.size() == 0) {
			return null;
		}
		JsonObject lastTweet = tweets.get(tweets.size() - 1).getAsJsonObject();

		String date = lastTweet.get("created_at").getAsString();
		String id = lastTweet.get("id").getAsString();

		Date tweetDate = JTwitter.getTwitterDate(date);

		JsonArray tweets2 = new JsonArray();

		int j = 0;

		while (since.before(tweetDate) && since.compareTo(tweetDate) != 0 && j < 15) {

			tweets2 = getUserTimeLineMaxId(screenName, 200, id);

			for (int i = 0; i < tweets2.size(); i++) {
				JsonObject obj = tweets2.get(i).getAsJsonObject();
				tweets.add(obj);
			}

			lastTweet = tweets.get(tweets.size() - 1).getAsJsonObject();

			date = lastTweet.get("created_at").getAsString();
			id = lastTweet.get("id").getAsString();

			tweetDate = JTwitter.getTwitterDate(date);

			System.out.print(".");

			j++;

		}

		JsonObject tweet1 = tweets.get(tweets.size() - 1).getAsJsonObject();

		String tDate1 = tweet1.get("created_at").getAsString();

		Date tweetDate1 = JTwitter.getTwitterDate(tDate1);

		if (since.before(tweetDate1)) {

			System.out.println(
					"\nNon sono riuscito ad arrivare alla data desiderata con 3200 tweets. Utente: " + screenName);
			return null;

		}

		for (int i = tweets.size() - 1; i >= 0; i--) {

			JsonObject tweet = tweets.get(i).getAsJsonObject();

			String tDate = tweet.get("created_at").getAsString();

			Date tweetDate2 = JTwitter.getTwitterDate(tDate);

			if (tweetDate2.before(since)) {
				tweets.remove(i);
			} else {
				break;
			}

		}

		return tweets;

	}
	
	
	
	

	/**
	 * @param tweets Un insieme di tweets 
	 * @return Un array Json contenente dove per ogni tweet in input è memorizzato soltanto il testo
	 */
	public JsonArray tweetsText(JsonArray tweets) {

		JsonArray result = new JsonArray();

		for (int i = 0; i < tweets.size(); i++) {

			JsonObject obj = tweets.get(i).getAsJsonObject();

			String text = "";
			if (obj.get("full_text") == null) {
				text = obj.get("text").getAsString();
			} else {
				text = obj.get("full_text").getAsString();
				if (text.substring(0, 2).equals("RT")) {
					text = obj.get("retweeted_status").getAsJsonObject().get("full_text").getAsString();
				}
			}

			JsonObject newObj = new JsonObject();

			newObj.addProperty("text", text);

			result.add(newObj);

		}

		return result;

	}
	
	
	
	

	/**
	 * @param tweets Un insieme di tweets
	 * @param words Una lista di parola
	 * @param hashtags Una lista di hashtags
	 * @return Un array json dove sono presenti soltanto i tweets che contengono almeno una parola presa in input o almeno un hashtag preso un input
	 */
	public JsonArray tweetFilter(JsonArray tweets, ArrayList<String> words, ArrayList<String> hashtags) {

		JsonArray result = new JsonArray();

		for (int i = 0; i < tweets.size(); i++) {

			JsonObject iObject = (JsonObject) tweets.get(i);
			String text = "";
			if (iObject.get("full_text") == null)
				text = iObject.get("text").toString();
			else
				text = iObject.get("full_text").toString();

			if (stringContainWords(text, words)) {

				result.add(iObject);

			} else if (hashtags.size() > 0) {

				JsonObject ob = (JsonObject) tweets.get(i);

				JsonObject entities = (JsonObject) ob.get("entities");

				JsonArray tweetHashtags = entities.getAsJsonArray("hashtags");

				for (int k = 0; k < tweetHashtags.size(); k++) {

					JsonObject obj = tweetHashtags.get(k).getAsJsonObject();

					String hashtag = obj.get("text").getAsString();

					if (hashtags.contains(hashtag)) {

						result.add(iObject);

						break;

					}

				}

			}

		}

		return result;

	}
	
	
	
	

	/**
	 * @param s Una stringa
	 * @param words Una lista di parola
	 * @return true se la string contiene almeno una parola presa in input
	 */
	private boolean stringContainWords(String s, ArrayList<String> words) {

		for (String w : words) {

			if (s.contains(w)) {
				return true;
			} else {

				return false;

			}

		}

		return false;

	}


	/**
	 * @param consumerKey apiKey
	 * @param consumerSecret apiSecret
	 * @return La codifica in Base 64 della concatenazione di consumerKey e consumerSecret sotto forma di stringa
	 */
	private static String encodeKeys(String consumerKey, String consumerSecret) {
		try {
			String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
			String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");

			String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
			byte[] encodedBytes = Base64.getEncoder().encode(fullKey.getBytes());

			return new String(encodedBytes);
		} catch (UnsupportedEncodingException e) {
			System.out.println(e);
			return new String();
		}
	}

	
	/**
	 * @param endPointUrl Un endpoint di Twitter
	 * @return Il token di tipo bearer sotto forma di string
	 * @throws IOException Endpoint non corretto
	 */
	private static String requestBearerToken(String endPointUrl) throws IOException {
		HttpsURLConnection connection = null;
		String encodedCredentials = encodeKeys(APIKEY, APISECRET);

		// System.out.println("encodedCredentials "+encodedCredentials);
		try {
			URL url = new URL(endPointUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Host", "api.twitter.com");
			connection.setRequestProperty("User-Agent", "anyApplication");
			connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			connection.setRequestProperty("Content-Length", "29");
			connection.setUseCaches(false);

			writeRequest(connection, "grant_type=client_credentials");

			// Parse the JSON response into a JSON mapped object to fetch fields
			// from.
			JsonParser parser = new JsonParser();

			String response = readResponse(connection);

			JsonObject obj = parser.parse(response).getAsJsonObject();

			if (obj != null) {
				String tokenType = obj.get("token_type").getAsString();
				String token = obj.get("access_token").getAsString();

				return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
			}
			return new String();
		} catch (MalformedURLException e) {
			System.out.println(e);
			throw new IOException("Invalid endpoint URL specified.", e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	
	
	/**
	 * @param endPointUrl Un endpoint di Twitter
	 * @return Ritorna un insieme di tweets sotto forma di array Json
	 * @throws IOException Può essere causata da un errore della risposta o da un endpoint errato
	 */
	private static JsonArray fetchTimelineTweet(String endPointUrl) throws IOException {
		HttpsURLConnection connection = null;

		try {
			URL url = new URL(endPointUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Host", "api.twitter.com");
			connection.setRequestProperty("User-Agent", "anyApplication");
			connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
			connection.setUseCaches(false);

			// Parse the JSON response into a JSON mapped object to fetch fields
			// from.

			JsonParser parser = new JsonParser();

			String response = readResponse(connection);

			JsonArray obj = parser.parse(response).getAsJsonArray();
			return obj;

		} catch (MalformedURLException e) {
			throw new IOException("Invalid endpoint URL specified.", e);
		} catch (Exception e) {
			if (connection != null) {
				connection.disconnect();
			}
			throw new IOException("Errore con la risposta", e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	
	
	

	// Writes a request to a connection
	/**
	 * @param connection Una connesione di tipo HttpURLConnection
	 * @param textBody Il body della richiesta
	 * @return true se la richiesta è stata effettuata correttamente
	 */
	private static boolean writeRequest(HttpURLConnection connection, String textBody) {
		try {
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			wr.write(textBody);
			wr.flush();
			wr.close();

			return true;
		} catch (IOException e) {
			System.out.println(e);
			return false;
		}
	}
	
	
	
	

	// Reads a response for a given connection and returns it as a string.
	/**
	 * @param connection Una connesione di tipo HttpURLConnection
	 * @return Il contenuto della risposta sotto forma di stringa
	 */
	private static String readResponse(HttpURLConnection connection) {
		try {
			StringBuilder str = new StringBuilder();

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				str.append(line + System.getProperty("line.separator"));
			}
			return str.toString();
		} catch (IOException e) {
			return new String();
		}
	}
	
	
	
	

	/**
	 * @param date Una data sotto forma di string a in formato "EEE MMM dd HH:mm:ss Z yyyy"
	 * @return Un ogetto di tipo Date relativo alla data passata in input
	 */
	public static Date getTwitterDate(String date) {

		final String TWITTER = "EEE MMM dd HH:mm:ss Z yyyy";

		SimpleDateFormat sf = new SimpleDateFormat(TWITTER, Locale.ENGLISH);
		try {
			return sf.parse(date);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

}
