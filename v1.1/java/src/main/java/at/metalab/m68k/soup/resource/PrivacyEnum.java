package at.metalab.m68k.soup.resource;

public enum PrivacyEnum {

	OPEN("open"), APPROVAL("approval"), INVITE("invite");

	private String propertyValue;

	PrivacyEnum(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public static PrivacyEnum getByPropertyValue(String propertyValue) {
		for (PrivacyEnum privacyEnum : values()) {
			if (privacyEnum.getPropertyValue().equals(propertyValue)) {
				return privacyEnum;
			}
		}
		return null;
	}
}