package functionalities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
//import java.nio.file.Path;
import java.nio.file.Paths;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Date;
//import java.util.stream.Stream;

import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserFinder {
	
	
public static JsonObject findUser(String folderPathName, boolean writeFile ) throws ParseException, java.text.ParseException, IOException {

		
		//String result = "";
		
		JsonObject jsonResult = new JsonObject();
		
		ArrayList<String> users = new ArrayList<>();
		
		
	
		//File folder = new File("TweetsForFindUsers");
		File folder = new File(folderPathName);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {
					//System.out.println(file.getName());
					String contents = new String(Files.readAllBytes(Paths.get(folderPathName+"/"+file.getName())));
					
					JsonParser parser = new JsonParser();
			        JsonObject obj  =   parser.parse(contents).getAsJsonObject();
			        
			        JsonArray tweets = obj.get("statuses").getAsJsonArray();
			        
			        //System.out.println(tweets.size());
			        
			        for(int i=0; i<tweets.size(); i++){
			        	
			        	String userId = tweets.get(i).getAsJsonObject().get("user").getAsJsonObject().get("screen_name").getAsString();
			        	
			        	if(!users.contains(userId)){
			        		users.add(userId);
			        	}
			        	
			        }
				}
		    }
		
		Gson gson = new GsonBuilder().create();
		JsonArray jsonUsers = gson.toJsonTree(users).getAsJsonArray();
		jsonResult.add("users", jsonUsers);
		
		if(writeFile) {
			try (FileWriter file = new FileWriter("Users2.json")) {
				 
	            file.write(jsonResult.toString());
	            file.flush();
	 
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		
		return jsonResult;
	    
		
	}

}
