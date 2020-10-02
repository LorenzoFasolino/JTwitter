package functionalities;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
//import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.parser.ParseException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import JTwitter.Hashtag;
import JTwitter.JTwitter;
import config.Config;
import utilities.Responder;
import utilities.Worker;

public class HashtagFinderMultiT {
	
	// private  static String APIKEY ="nmRfqyEGW8f0DnQpKtGQYk6KL";
	// private  static String APISECRET ="b3qdocNejtTTiTIDFUy58uAEdGRgdgeXa2NQrwoGkwLXgmrMaT";
	 private static int NUMUTENTI = 614;
	
	
	/**
	 * @param args
	 * @throws java.text.ParseException 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static String findHashtags(String sinceString, String untilString, String filePath, String foldrPathTweet, String folderPathHashtag, boolean writeFile  ) throws ParseException, java.text.ParseException, IOException, InterruptedException  {

		
		JTwitter twitter = new JTwitter();
		
		Responder responder = new Responder();
		
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy Z");
		
		
		Date since = sf.parse(sinceString+" +0000");
		
		Date until = sf.parse(untilString+" +0000");
		
		String contents = new String(Files.readAllBytes(Paths.get(filePath)));
		
		JsonParser parser = new JsonParser();
        JsonObject obj  =   parser.parse(contents).getAsJsonObject();
        JsonArray users = obj.get("users").getAsJsonArray();
        
        JsonArray tweets;
        
        ArrayList<Integer> nums = new ArrayList<>();
        
        for(int i =0; i< 100; i++){
        	
        	Random random = new Random();
        	int userNum = random.nextInt(NUMUTENTI);
        	
        	while(nums.contains((Integer)userNum)){
        		
        		 userNum = random.nextInt(NUMUTENTI);
        		
        	}
        	
        	nums.add((Integer) userNum);
        	
        }
        
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        
        
        for (int i = 0; i <100; i++) { 
        	
        	int num = nums.get(i);
        	
        	String userId = users.get(num).getAsString();
        	
        	Worker worker = new Worker(userId,Config.APIKEY,Config.APISECRET,since,until);  
            worker.addListener(responder);
            executor.execute(worker);//calling execute method of ExecutorService  
          }  
        executor.shutdown();  
        while (!executor.isTerminated()) {   } 
        
        tweets = responder.getT();
        
        System.out.println("Tweet trovati: "+tweets.size());
     
        
       ArrayList<Hashtag> HashtagCounted = twitter.hashtagCounter(tweets);
        
        String hashtagResults  = "";
		
		 
		 
		 for(Hashtag h : HashtagCounted){
			 
			 hashtagResults = hashtagResults+"Hashtag: "+h.getName()+" num: "+h.getCount()+"\n";
			 
		 }
		 
		 if(writeFile) {
		 
		 try (FileWriter file = new FileWriter(folderPathHashtag+"/Hashtags_"+sinceString.replaceAll("/", "_")+".txt")) {
			 
	            file.write(hashtagResults);
	            System.out.println("\nScrivo in: "+folderPathHashtag+"/Hashtags"+sinceString.replaceAll("/", "_")+".txt");
	            file.flush();
	 
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		 }
		 
		 
		
		 try (FileWriter file = new FileWriter(foldrPathTweet+"/"+sinceString.replaceAll("/", "_")+".json")) {
			 
	            file.write(tweets.toString());
	            System.out.println("Scrivo in: "+foldrPathTweet+"/"+sinceString.replaceAll("/", "_")+".json");
	            file.flush();
	 
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		 
		 return hashtagResults;
		
		
	    
		
	}
	




}
