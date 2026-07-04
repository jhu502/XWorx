package plm.dynamic.engine.cvm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.engine.type.ExtendType;
import com.flame.type.XBaseType;

public abstract class AbstractRule extends UuidObject {
	private static final long serialVersionUID = 1L;
	protected String name;
	protected String detailName;
	protected String sentence = ""; //记录转换后的表达式
	protected String expression = ""; //记录原始的表达式
	protected Class<?> resultType = boolean.class;
	protected XBaseType[] charactTypes = null;
	private Map<String, CalCharacter> charactMap = new HashMap<>(); //Map<VariableName, CalOption>

	public String getName() {
		return this.name;
	}

	public String getDetailName() {
		return this.detailName;
	}

	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}

	public String[] getVariables() {
		return this.charactMap.keySet().toArray(new String[0]);
	}

	public String getSentence() {
		return this.sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getExpression() {
		return this.expression;
	}

	public Class<?> getResultType() {
		return this.resultType;
	}

	public void addCalCharact(CalCharacter calCharact) {
		this.charactMap.put(calCharact.getVariableName(), calCharact);
		this.charactTypes = null;
	}

	public void setCharactMap(Map<String, CalCharacter> charactMap) {
		this.charactMap.putAll(charactMap);
		this.charactTypes = null;
	}

	public XBaseType[] getCharactType() {
		if (this.charactTypes == null) {
			this.charactTypes = new XBaseType[this.charactMap.size()];
			CalCharacter[] options = this.charactMap.values().toArray(new CalCharacter[0]);

			for (int i = 0; i < options.length; i++) {
				this.charactTypes[i] = options[i].getBaseType();
			}
		}
		return this.charactTypes;
	}

	public CalCharacter[] calCharacts() {
		return this.charactMap.values().toArray(new CalCharacter[0]);
	}

	public Collection<CalCharacter> getCalCharact() {
		return this.charactMap.values();
	}

	public Map<String, CalCharacter> getCharactMap() {
		return this.charactMap;
	}

	public Set<CalParam> performRule(Map<String, CalParam> metabolic, CalParam inParam) {
		Set<CalParam> result = new HashSet<>();

		return result;
	}

	public String showParamInfo(CalParam calParam) {
		if (calParam == null)
			return "";
		
		if (ExtendType.class.isAssignableFrom(calParam.getBaseType().getPrototype())) {
			return "参数" + calParam.getDetailName() + "被赋值";
		} else {
			return "“" + calParam.getDetailName() + "=" + calParam.getValue() + "”";
		}
	}

	public String toString() {
		return this.detailName;
	}
}
