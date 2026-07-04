package plm.dynamic.engine.mdb;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CalChoice implements Comparable<CalChoice>, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private Object value;
	private String description;
	private String longDescription;
	private String expression;
	private Status status = Status.ENABLED;
	private Set<String> prompts = new HashSet<String>();

	public enum Status {
		ENABLED("Enabled"), DISABLED("Disabled");

		private String value;

		Status(String value) {
			this.value = value;
		}

		public String toString() {
			return this.value;
		}
	}

	public CalChoice(Object value) {
		this.value = value;
	}

	public CalChoice(Object value, String description) {
		this.value = value;
		this.description = description;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getDescription() {
		return this.description;
	}

	public String getLongDescription() {
		return this.longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getExpression() {
		return this.expression;
	}

	public void setExpression(String exp) {
		this.expression = exp;
	}

	public String getPrompt() {
		if (prompts.isEmpty())
			return "";

		StringBuffer strbuf = new StringBuffer();
		for (String str : this.prompts) {
			if (strbuf.length() > 0)
				strbuf.append("\n").append(str);
			else
				strbuf.append(str);
		}
		return strbuf.toString();
	}

	public void addPrompt(String prompt) {
		if (prompt != null && !prompt.trim().isEmpty())
			this.prompts.add(prompt);
	}

	public void cleanPrompt() {
		this.prompts.clear();
	}

	public Object value() {
		return this.value;
	}

	public int hashCode() {
		return this.value.hashCode();
	}

	public String toString() {
		return this.value.toString();
	}

	public boolean precondition() {
		return true;
	}

	public CalChoice clone() throws CloneNotSupportedException {
		CalChoice calvalue = (CalChoice) super.clone();
		calvalue.value = this.value;
		calvalue.prompts = new HashSet<String>();
		return calvalue;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;

		if (!(object instanceof CalChoice))
			return false;

		CalChoice choice = (CalChoice) object;

		if (this.value != null) {
			return this.value.equals(choice.value);
		} else {
			return this.value == choice.value;
		}
	}

	@Override
	public int compareTo(CalChoice o) {
		if (this.value instanceof String) {
			return ((String) this.value()).compareTo((String) o.value());
		} else if (this.value instanceof Long) {
			return ((Long) this.value()).compareTo((Long) o.value());
		} else {
			return 0;
		}
	}
}
