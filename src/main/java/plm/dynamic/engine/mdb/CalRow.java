package plm.dynamic.engine.mdb;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author hujin
 * @version 1.1
 * 
 */
public class CalRow implements Serializable {
	private static final long serialVersionUID = 1L;
	private CalCell[] cell;

	public CalRow() {
	}

	public CalRow(Object[] objects) {
		int _length = objects.length;
		this.cell = new CalCell[_length];
		for (int i = 0; i < _length; i++) {
			this.cell[i] = new CalCell(objects[i]);
		}
	}

	public CalRow(List<Object> list) {
		int _length = list.size();
		this.cell = new CalCell[_length];
		int i = 0;
		for (Object obj : list) {
			this.cell[i++] = new CalCell(obj);
		}
	}

	public CalCell[] getCells() {
		return this.cell;
	}

	public CalCell getCell(int col) {
		return this.cell[col];
	}
}
