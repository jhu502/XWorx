package plm.dynamic.engine.type;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.util.XException;

public class MatrixType extends ExtendType {
	private static final long serialVersionUID = 1L;
	protected Map<String, Map<String, Object>> fields = new LinkedHashMap<String, Map<String, Object>>();
	protected List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
	
	public MatrixType() {
	}

	public static MatrixType valueOf(String jsonStr) {
		try {
			return mapper.readValue(jsonStr, MatrixType.class);
		} catch (Exception e) {
			throw new XException(e.getMessage(), e);
		}
	}

	public Map<String, Object> getField(String name) {
		return this.fields.get(name);
	}

	public Map<String, Map<String, Object>> getFields() {
		return fields;
	}

	public void addField(String property, Map<String, Object> info) {
		this.fields.put(property, info);
	}

	public Object getCell(int row, String field) {
		if (row > this.getRowNum()) {
			return null;
		}
		return this.rows.get(row).get(field);
	}

	public void addRow(Map<String, Object> row) {
		this.rows.add(row);
	}

	public List<Map<String, Object>> getRows() {
		return rows;
	}

	public Map<String, Object> getRow(int index) {
		return this.rows.get(index);
	}

	public Map<String, Object> getRow(String field, Object value) {
		if (isBlank(field)) {
			return null;
		}

		for (Map<String, Object> row : this.rows) {
			if (value == null && row.get(field) == null) {
				return row;
			} else if (value.equals(row.get(field))) {
				return row;
			}
		}
		return null;
	}

	@JsonIgnore
	public int getRowNum() {
		return this.rows.size();
	}
	
	public boolean hasRow(int index) {
		if (index >= rows.size()) {
			return false;
		}
		
		Map<String, Object> row = this.getRow(index);
		boolean bool = false;
		for (Object object : row.values()) {
			if (object != null && !"".equals(object)) {
				bool = true;
			}
		}
		
		return bool;
	}

	public int getCount(String field) {
		int count = 0;
		for (Map<String, Object> row : rows) {
			if (!isBlank(row.get(field))) {
				count = count + 1;
			}
		}
		return count;
	}

	public int getCount(String field, Object value) {
		int count = 0;
		for (Map<String, Object> row : rows) {
			if (value == null && row.get(field) == null) {
				count = count + 1;
			} else if (value != null && value.equals(row.get(field))) {
				count = count + 1;
			}
		}
		return count;
	}
	
	public double getSum(String field) {
		double sum = 0;
		for (Map<String, Object> row : rows) {
			Object value = row.get(field);
			if (value != null && !"".equals(value)) {
				sum = sum + Double.parseDouble((String) value);
			}
		}
		return sum;
	}
	
	public static String getDefaultValue() {
		return "{}";
	}
	
	@JsonIgnore
	public void clear() {
		this.rows.clear();
	}

	@JsonIgnore
	public MatrixType clone() {
		MatrixType mtype = new MatrixType();
		mtype.fields.putAll(this.fields);

		for (Map<String, Object> row : rows) {
			mtype.rows.add(new LinkedHashMap<String, Object>(row));
		}

		return mtype;
	}
}
