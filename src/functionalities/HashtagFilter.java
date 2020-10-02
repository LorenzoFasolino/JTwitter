package functionalities;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Date;
//import java.util.Random;
import java.util.Scanner;

//import org.json.simple.parser.ParseException;

import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import JTwitter.JTwitter;

public class HashtagFilter {

	/**
	 * 
	 * Data un file json sontenente dei tweet, questo metodo filtra i tweet in base alla parole e agli hashtags che verrando inseriti a runtime. In quando ha terminato il filtraggio, se writeFile è a true, scrive due file json, uno contenente i tweets filtrati, un altro contenent solanto i testi dei tweets filtrati
	 * 
	 * @param filePathName Il path del file dove sono presenti i tweet da filtrare
	 * @param writeFile Se true verranno scritti i file di output
	 * @param outFolderPath Il path della cartella dove verranno salvati i tweets filtrati
	 * @param outTextFolderPath Il path della cartella dove verrano salvati soltanto i testi dei tweets filtrati
	 * @param outputName Il nome del file in output
	 * @return I tweets filtrati in forma di json array
	 * @throws IOException 
	 */
	public static JsonArray filterHashtags(String filePathName, boolean writeFile, String outFolderPath, String outTextFolderPath, String outputName) throws IOException {

		JTwitter twitter = new JTwitter();

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		String contents = new String(Files.readAllBytes(Paths.get(filePathName)));

		JsonParser parser = new JsonParser();
		JsonArray tweets = parser.parse(contents).getAsJsonArray();

		ArrayList<String> words = new ArrayList<>();
		ArrayList<String> hashtags = new ArrayList<>();

		System.out.print("Inserisci le parole separate da uno spazio: ");
		String s = scanner.nextLine();

		String[] wordstwo = s.split(" ");
		for (String str : wordstwo) {
			if (!str.equals(" ")) {
				words.add(str);
				hashtags.add(str);
			}
		}

		System.out.println("****WORDS****");
		for (String st : words) {

			System.out.println(st);
		}

		System.out.println("****HASHTAGS****");
		for (String st : hashtags) {

			System.out.println(st);
		}

		tweets = twitter.tweetFilter(tweets, words, hashtags);

		JsonArray tweetsText = twitter.tweetsText(tweets);

		try (FileWriter file = new FileWriter(outTextFolderPath+"/TextTweets" + outputName)) {

			file.write(tweetsText.toString());
			System.out.println(outTextFolderPath+"/TextTweets" + outputName);
			file.flush();

		} catch (IOException e) {
			System.out.println("Errore assicurati che la cartella dove deve essere salvato il file esiste. FilaPath: "+outTextFolderPath);
		}

		try (FileWriter file = new FileWriter(outFolderPath+"/FiltredTweets_" + outputName)) {

			file.write(tweets.toString());
			System.out.println(outFolderPath+"/FiltredTweets_" + outputName);
			file.flush();

		} catch (IOException e) {
			System.out.println("Errore assicurati che la cartella dove deve essere salvato il file esiste. FilaPath: "+outFolderPath);
		}

		return tweets;

	}

}
