package plm.dynamic;

public enum InputType {
	INPUT("Input"), REQUIRED("Required"), READONLY("Readonly"), HIDDEN("Hidden");

	private String display;

	private InputType(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return this.display;
	}

	public String getName() {
		return this.name();
	}
}
