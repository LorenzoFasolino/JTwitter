package utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonCounter {
	
	public static void main(String[] args) throws IOException {
		
		
		String contents = new String(Files.readAllBytes(Paths.get("Tweets/03_06_2020.json")));
		
		JsonParser parser = new JsonParser();
	    JsonArray users = parser.parse(contents).getAsJsonArray();
	    System.out.println(users.size());
		
	}

	
}
