package utilities;

import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;

public class Responder implements ThreadCompleteListener  {
	
	private JsonArray t;
	
	public Responder(){
		
		t = new JsonArray();
	}

	@Override
	public synchronized void notifyOfThreadComplete(JsonArray tweets) {
		
	
		for(int i=0; i<tweets.size(); i++){
			
			
			JsonObject obj = tweets.get(i).getAsJsonObject();
			t.add(obj);
			
		}
		
	}

	public JsonArray getT() {
		return t;
	}

	public void setT(JsonArray t) {
		this.t = t;
	}
	
	

}
