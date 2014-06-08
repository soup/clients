package at.metalab.m68k.soup.resource.posts;

import java.io.InputStream;
import java.util.Calendar;

class PostProperties {

	protected String tags;

	protected String title;

	protected String body;

	protected String source;

	protected String url;

	protected String description;

	protected String caption;

	protected String quote;

	protected String embedCode;

	protected String filename;

	protected String review;

	protected int rating;

	protected String location;

	protected Calendar startDate;

	protected Calendar endDate;

	// not an actual json property (used for uploading data via multipart)
	protected InputStream data;
}
