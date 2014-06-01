package at.metalab.m68k.soup.resource.posts;

import java.util.Calendar;

/**
 * https://github.com/soup/clients/tree/master/v1.1#events
 * 
 * @author m68k
 * 
 */
public class Event {

	private String title;

	private String location;

	private String description;

	private Calendar startDate;

	private Calendar endDate;

	private String tags;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTags() {
		return tags;
	}
}
