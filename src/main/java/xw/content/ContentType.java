package xw.content;

public enum ContentType {
	PRIMARY("Primary"), SECONDARY("Secondary"), THUMBNAIL3D("Thumbnail3D");

	private String display;

	ContentType(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return display;
	}
}
