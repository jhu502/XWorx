package plm.dynamic.bean;

import com.flame.orm.XJsonType;

public class XChoice extends XJsonType<XChoice> {
	private static final long serialVersionUID = 1L;
	private Object value;
	private String description;
	private char type = 'N'; // V:Value;R:Range;D:Dynamic
	private String statement = "";

	public static XChoice newXChoice(String value, String description) {
		XChoice xchoice = new XChoice();
		xchoice.setValue(value);
		xchoice.setDescription(description);

		return xchoice;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}
}
