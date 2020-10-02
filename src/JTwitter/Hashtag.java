package JTwitter;


/**
 * Rappresenta un hashtag costituito dal nome e dal numero di accorenza
 *
 */
public class Hashtag {
	
	/**
	 * Costruttore
	 * @param name Il nome dell'hashtag
	 */
	public Hashtag(String name){
		this.name = name;
		this.count = 0;
	}
	
	private String name;
	private int count;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

}
