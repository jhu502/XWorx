package plm.dynamic;

public enum CaseType {
	FLATTABLE("FlatTable"), BLOCKTYPE("BlockType");

	private String display;

	private CaseType(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return this.display;
	}

	public String getName() {
		return this.name();
	}
}
