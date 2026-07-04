package plm.dynamic;

public enum OptionMode {
	NONE("None"), RANGE("Range"), LIST("List"), DLIST("DList");

	private String display;

	private OptionMode(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return this.display;
	}

	public String getName() {
		return this.name();
	}
}
