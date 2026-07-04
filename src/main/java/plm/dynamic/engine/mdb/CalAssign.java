package plm.dynamic.engine.mdb;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import plm.dynamic.OptionMode;
import plm.dynamic.engine.type.ExtendType;
import com.flame.type.XBaseType;
import com.flame.util.XException;

/**
 * 本类主要被CalParam使用，用存放CalParam被分配的值，这个类主要是对真实值的包装
 * 因为Value被到处使用，因此取了这个特殊的名字，CalAssign中存放数据可能包括：String、Long、Integer、Collection、Object
 * 
 * @author hujin
 */
public class CalAssign implements Serializable {
	private static final long serialVersionUID = 1L;
	private static ObjectMapper objectMapper = new ObjectMapper();
	private Object value;

	public static CalAssign toCalAssign(CalParam param, Object input) {
		CalAssign assign = new CalAssign();

		Object data = convertType(param, input);
		if (data instanceof String) {
			if (OptionMode.LIST.equals(param.getOptionMode())) {
				if (param.isMultiSelect() && !StringUtils.isEmpty(data)) {
					Collection<String> values = new HashSet<>();
					Collections.addAll(values, ((String) data).split(","));
					assign.value = values;
				} else {
					assign.value = data;
				}
			} else {
				assign.value = data;
			}
		} else {
			assign.value = data;
		}

		return assign;
	}

	public static Object convertType(CalParam param, Object object) {
		if (object == null)
			return null;

		XBaseType baseType = param.getBaseType();
		Class<?> valueType = param.getBaseType().getPrototype();
		if (object instanceof String && !String.class.equals(valueType)) {
			if ("".equals(object.toString().trim())) {
				return null;
			}
			if (XBaseType.LONG.equals(baseType)) {
				return Long.parseLong((String) object);
			} else if (XBaseType.BOOLEAN.equals(baseType)) {
				return Boolean.parseBoolean((String) object);
			} else if (XBaseType.NUMBER.equals(baseType)) {
				return Double.parseDouble((String) object);
			} else if (ExtendType.class.isAssignableFrom(valueType)) {
				try {
					return objectMapper.readValue((String) object, valueType);
				} catch (IOException e) {
					throw new XException(e.getMessage(), e);
				}
			}
		} else {
			return object;
		}

		return object;
	}

	public Object value() {
		return this.value;
	}

	public boolean isNull() {
		if (this.value == null || "".equals(this.value))
			return true;

		if (this.value instanceof Collection) {
			return ((Collection<?>) this.value).isEmpty();
		} else if (this.value instanceof ExtendType) {
			return false;
		}

		return false;
	}

	public boolean hasValue(Object object) {
		if (object == null || this.isNull()) {
			return false;
		}

		if (this.value instanceof Collection) {
			Collection<?> values = (Collection<?>) this.value;

			if (object instanceof Collection) {
				return !CollectionUtils.intersection(values, (Collection<?>) object).isEmpty();
			}

			return values.contains(object);
		} else {
			return this.value.equals(object);
		}
	}

	public String toString() {
		return this.value == null ? null : this.value.toString();
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return this.isNull();
		}

		if (this.isNull()) {
			if (object instanceof CalAssign) {
				return ((CalAssign) object).isNull();
			} else {
				return false;
			}
		} else {
			if (object instanceof CalAssign) {
				CalAssign assign = (CalAssign) object;
				if (this.value instanceof Collection && assign.value instanceof Collection) {
					Collection<?> lColl = (Collection<?>) this.value;
					Collection<?> rColl = (Collection<?>) assign.value;
					if (lColl.size() != rColl.size())
						return false;

					return lColl.containsAll(rColl) && rColl.containsAll(lColl);
				} else {
					return this.value.equals(assign.value);
				}
			} else {
				if (this.value instanceof Collection && object instanceof Collection) {
					Collection<?> lColl = (Collection<?>) this.value;
					Collection<?> rColl = (Collection<?>) object;
					if (lColl.size() != rColl.size()) {
						return false;
					} else {
						return lColl.containsAll(rColl) && rColl.containsAll(lColl);
					}
				} else {
					return this.value.equals(object);
				}
			}
		}
	}
}
