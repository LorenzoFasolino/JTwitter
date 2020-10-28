package utilities;

import com.google.gson.JsonArray;

public interface ThreadCompleteListener {
    void notifyOfThreadComplete(final JsonArray tweets);
    
}