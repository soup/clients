package at.metalab.m68k.soup.resource;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;

/**
 * https://github.com/soup/clients/tree/master/v1.1#return-value-1
 * 
 * @author m68k
 * 
 */
public class Group extends Resource {

	private int id;

	private String title;

	private String name;

	private String imageUrl;

	private String url;

	private PrivacyEnum privacy;

	private List<String> permissions = new ArrayList<String>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setPrivacy(PrivacyEnum privacy) {
		this.privacy = privacy;
	}

	public PrivacyEnum getPrivacy() {
		return privacy;
	}

	public static Group create(JsonNode groupNode) {
		Group group = new Group();
		group.setId(groupNode.get("id").getIntValue());
		group.setName(groupNode.get("name").getTextValue());
		group.setTitle(groupNode.get("title").getTextValue());
		group.setUrl(groupNode.get("url").getTextValue());
		group.setImageUrl(groupNode.get("image_url").getTextValue());
		if (groupNode.has("privacy")) {
			group.setPrivacy(PrivacyEnum.getByPropertyValue(groupNode.get(
					"privacy").getTextValue()));
		}

		return group;
	}

}
