package plm.dynamic.engine.mdb;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.cvm.UuidObject;
import plm.dynamic.engine.exprs.Expression;
import com.flame.util.XException;

public class CalSelection extends UuidObject {
	private static final long serialVersionUID = 1L;
	private String name = null;
	private String detailName = "";
	private String methodName = "";
	private Method method = null;
	private String sentence = ""; //记录转换后的表达式
	private String expression = "false"; //记录原始的表达式
	private Class<?> resultType;
	private Class<?>[] optionTypes = null;
	private Map<String, CalCharacter> optionMap = new LinkedHashMap<String, CalCharacter>();

	public CalSelection(Emulator emulator, String name, String expression, Class<?> resultType) {
		this.name = name;
		this.detailName = emulator.getNumber() + ":" + name;
		this.methodName = "exec" + emulator.getNumber().replaceAll("-", "") + "_" + name.replaceAll("-", "") + "_" + this.getUUID().replaceAll("-", "_");
		this.expression = expression;
		this.resultType = resultType;
	}

	public String getName() {
		return this.name;
	}

	public String getDetailName() {
		return this.detailName;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public Method getMethod() {
		return this.method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Class<?> getResultType() {
		return this.resultType;
	}

	public Class<?>[] getOptionType() {
		return this.optionTypes;
	}

	public Set<String> getVariables() {
		return this.optionMap.keySet();
	}

	public String getSentence() {
		return this.sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getExpression() {
		return expression;
	}

	public Map<String, CalCharacter> getOptionMap() {
		return this.optionMap;
	}

	public void setOptionMap(Map<String, CalCharacter> optionMap) {
		this.optionMap.putAll(optionMap);
		this.optionTypes = new Class<?>[this.optionMap.size()];
		CalCharacter[] options = this.optionMap.values().toArray(new CalCharacter[0]);

		for (int i = 0; i < options.length; i++) {
			this.optionTypes[i] = options[i].getBaseType().getPrototype();
		}
	}

	public String toString() {
		return this.expression;
	}

	public Object perform(Expression expression) {
		try {
			return method.invoke(expression, new Object[0]);
		} catch (Exception e) {
			throw new XException(e.getMessage(), e);
		}
	}
}
