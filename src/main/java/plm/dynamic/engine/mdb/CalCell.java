package plm.dynamic.engine.mdb;

import java.io.Serializable;

/**
 * 
 * @author hujin
 * @version 1.1
 * 
 */
public class CalCell implements Serializable {
	private static final long serialVersionUID = 1L;
	public Object value;	//String / Set

	public CalCell(Object value) {
		this.value = value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return this.value;
	}
	
	public String toString() {
		return this.value.toString();
	}
}
