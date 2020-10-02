package utilities;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import JTwitter.JTwitter;

public class Worker implements Runnable {  
    private String userID;  
    private JTwitter twitter;
    private Date since;
    private Date until;
    private JsonArray tweets;
    
    private final Set<ThreadCompleteListener> listeners = new CopyOnWriteArraySet<ThreadCompleteListener>();
    
    public Worker(String userID, String apiKey,String  apiSecret, Date since, Date until){  
        this.userID=userID;  
        twitter = new JTwitter(apiKey,apiSecret);
        this.since = since;
        this.until = until;
        this.tweets = new JsonArray();
    }  
    
    
     public void run() {  
    	 
     	JsonArray tweets1 = twitter.getUserTimeLineSince(userID,since);
     	
     	if(tweets1!=null){
     		
     		tweets1 = twitter.cutDataUntil(tweets1, until);
         	
         	for (int j = 0; j < tweets1.size(); j++) {
 		        JsonObject tweet = tweets1.get(j).getAsJsonObject();
 		        tweets.add(tweet);
 			}
         	
         	notifyListeners();
     		
     	}
     	
    }  
     
     public final void addListener(final ThreadCompleteListener listener) {
    	    listeners.add(listener);
    	  }
    	  public final void removeListener(final ThreadCompleteListener listener) {
    	    listeners.remove(listener);
    	  }
    	  private final void notifyListeners() {
    	    for (ThreadCompleteListener listener : listeners) {
    	      listener.notifyOfThreadComplete(tweets);
    	    }
    	  }
     
     public JsonArray getTweets(){
  		return tweets;
  	}
     
     
}
