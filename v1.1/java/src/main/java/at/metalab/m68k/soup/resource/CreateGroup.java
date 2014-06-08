package at.metalab.m68k.soup.resource;

public class CreateGroup {

	private PrivacyEnum privacy = PrivacyEnum.OPEN;

	private Group group;

	public void setGroup(Group group) {
		this.group = group;
	}

	public Group getGroup() {
		return group;
	}

	public void setPrivacy(PrivacyEnum privacy) {
		this.privacy = privacy;
	}

	public PrivacyEnum getPrivacy() {
		return privacy;
	}

}
