package at.metalab.m68k.soup.resource.posts;

/**
 * https://github.com/soup/clients/tree/master/v1.1#quotes
 * 
 * @author m68k
 * 
 */
public class Quote {

	private String source;

	private String quote;

	private String tags;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
}
