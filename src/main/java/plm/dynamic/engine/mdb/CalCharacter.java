package plm.dynamic.engine.mdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import plm.dynamic.OptionMode;
import plm.dynamic.engine.cvm.AbstractRule;
import plm.dynamic.engine.cvm.UuidObject;
import plm.dynamic.engine.mdb.CalChoice.Status;
import plm.dynamic.engine.rule.EvaluationRule;
import com.flame.type.XBaseType;

public class CalCharacter extends UuidObject {
	private static final long serialVersionUID = 1L;
	public static final String MIN = "min";
	public static final String MAX = "max";
	protected String name;
	protected String displayName;
	protected String partNumber;
	protected String detailName;
	protected String globalId;
	protected String varName;
	protected String description;
	protected boolean display = true;
	protected boolean readonly = false;
	protected boolean required = false;
	protected boolean multiSelect = false;
	protected String defaultValue;
	protected Object constValue;
	protected String attributeMapping;
	protected String dynamicMapping;
	protected String prompt = "";
	protected String sortNo = "";
	protected String selCondition = "";
	protected String preCondition = "";
	protected String emulatorUuid = "";
	protected XBaseType baseType;
	protected OptionMode optionMode = OptionMode.NONE;
	protected Map<Object, CalChoice> choices = new LinkedHashMap<>();
	protected Map<Object, CalChoice> enabled = new LinkedHashMap<>();
	private List<AbstractRule> relatedRules = new ArrayList<>();
	private List<CalCharacter> childCharacts = new ArrayList<>();
	private EvaluationRule evaluationRule;

	public CalCharacter() {
	}

	public CalCharacter(String name, XBaseType baseType, Boolean required, Boolean readonly, String displayName) {
		this.name = name;
		this.baseType = baseType;
		this.required = required;
		this.readonly = readonly;
		this.displayName = displayName == null ? "" : displayName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPartNumber() {
		return this.partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getDetailName() {
		return this.detailName;
	}

	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}

	public String getGlobalId() {
		return this.globalId;
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	public String getVariableName() {
		return this.varName;
	}

	public void setVariableName(String varName) {
		this.varName = varName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDecription(String description) {
		this.description = description;
	}

	public XBaseType getBaseType() {
		return baseType;
	}

	public void setBaseType(XBaseType baseType) {
		this.baseType = baseType;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(Boolean display) {
		this.display = display;
	}

	public boolean isReadonly() {
		return this.readonly;
	}

	public void setReadonly(Boolean readOnly) {
		this.readonly = readOnly;
	}

	public boolean isMultiSelect() {
		return this.multiSelect;
	}

	public void setMultiSelect(Boolean mselect) {
		this.multiSelect = mselect;
	}

	public boolean isRequired() {
		return required;
	}

	public Boolean isAttribute() {
		return false;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public OptionMode getOptionMode() {
		return this.optionMode;
	}

	public void setOptionMode(OptionMode mode) {
		this.optionMode = mode;
	}

	public String getSortNo() {
		return this.sortNo;
	}

	public void setSortNo(String sortNo) {
		if (sortNo == null)
			this.sortNo = "";
		else
			this.sortNo = sortNo;
	}

	public CalChoice getMin() {
		return this.choices.get(MIN);
	}

	public void setMin(CalChoice choice) {
		this.choices.put(MIN, choice);
	}

	public CalChoice getMax() {
		return this.choices.get(MAX);
	}

	public void setMax(CalChoice choice) {
		this.choices.put(MAX, choice);
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String value) {
		this.defaultValue = value;
	}

	public Object getConstValue() {
		return constValue;
	}

	public void setConstValue(Object value) {
		this.constValue = value;
	}

	public String getMapAttribute() {
		return attributeMapping;
	}

	public void setMapAttribute(String mapAttribute) {
		this.attributeMapping = mapAttribute;
	}

	public String getDynamicMapping() {
		return dynamicMapping;
	}

	public void setDynamicMapping(String dynamicMapping) {
		this.dynamicMapping = dynamicMapping;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getPrompt() {
		return this.prompt;
	}

	public String getSelectConditionNo() {
		return this.selCondition;
	}

	public void setSelectConditionNo(String selCondition) {
		this.selCondition = selCondition;
	}

	public String getPreConditionNo() {
		return this.preCondition;
	}

	public void setPreConditionNo(String preCondition) {
		this.preCondition = preCondition;
	}

	public void addChoice(Object value) {
		CalChoice calvalue = this.choices.get(value);
		if (calvalue == null) {
			calvalue = this.enabled.get(value);
			if (calvalue == null) {
				calvalue = new CalChoice(value);
				calvalue.setStatus(Status.ENABLED);
				this.choices.put(value, calvalue);
				this.enabled.put(value, calvalue);
			} else {
				this.choices.put(value, calvalue);
				calvalue.setStatus(Status.ENABLED);
				calvalue.cleanPrompt();
			}
		}
	}

	public void addChoice(CalChoice calchoice) {
		calchoice.setStatus(Status.ENABLED);
		this.choices.put(calchoice.value(), calchoice);
		this.enabled.put(calchoice.value(), calchoice);
	}

	public CalChoice getChoice(Object choice) {
		return this.choices.get(choice);
	}

	public Collection<CalChoice> getChoices() {
		return this.choices.values();
	}

	public Collection<Object> toChoices() {
		return this.choices.keySet();
	}

	public EvaluationRule getEvaluationRule() {
		return this.evaluationRule;
	}

	public void setEvaluationRule(EvaluationRule evalRule) {
		this.evaluationRule = evalRule;
	}

	public Collection<Object> getEnabledChoiceVals() {
		return this.enabled.keySet();
	}

	public Collection<CalChoice> getEnabledChoices() {
		return this.enabled.values();
	}

	public boolean hasChoice(Object value) {
		if (this.getChoice(value) != null)
			return true;
		else
			return false;
	}

	public boolean hasEnabledChoice(Object object) {
		if (object instanceof CalAssign) {
			CalAssign assign = (CalAssign) object;
			Object value = assign.value();
			if (value == null) {
				return false;
			} else {
				if (value instanceof Collection) {
					for (Object obj : (Collection<?>) value) {
						if (this.enabled.containsKey(obj)) {
							return true;
						}
					}
					return false;
				} else {
					return this.enabled.containsKey(value);
				}
			}
		} else if (object instanceof Collection) {
			for (Object obj : (Collection<?>) object) {
				if (this.enabled.containsKey(obj)) {
					return true;
				}
			}
			return false;
		} else {
			return this.enabled.containsKey(object);
		}
	}

	public void enableChoice(String value) {
		CalChoice calvalue = this.choices.get(value);
		if (calvalue != null) {
			this.enabled.put(value, calvalue);
			calvalue.setStatus(Status.ENABLED);
			calvalue.cleanPrompt();
		}
	}

	public void enableAllChoice() {
		this.enabled.putAll(this.choices);
		for (CalChoice calvalue : this.choices.values()) {
			calvalue.setStatus(Status.ENABLED);
			calvalue.cleanPrompt();
		}
	}

	public void disableChoice(Object value, String prompt) {
		CalChoice calvalue = null;
		if (value instanceof CalChoice) {
			calvalue = (CalChoice) value;
		} else {
			calvalue = this.choices.get(value);
		}
		if (calvalue != null) {
			calvalue.setStatus(Status.DISABLED);
			calvalue.addPrompt(prompt);
			this.enabled.remove(calvalue.value());
		}
	}

	public void disableChoice(CalChoice choice) {
		if (choice != null) {
			choice.setStatus(Status.DISABLED);
			choice.addPrompt(prompt);
			this.enabled.remove(choice.value());
		}
	}

	public List<AbstractRule> getRelatedRule() {
		return this.relatedRules;
	}

	public void addRelatedRule(AbstractRule calTable) {
		this.relatedRules.add(calTable);
	}

	public List<CalCharacter> getChildCharacts() {
		return childCharacts;
	}

	public void addChildCharacts(CalCharacter charact) {
		this.childCharacts.add(charact);
	}

	public void setChildCharacts(List<CalCharacter> childCharacts) {
		this.childCharacts = childCharacts;
	}

	public String getEmulatorUUID() {
		return this.emulatorUuid;
	}

	public void setEmulatorUUID(String uuid) {
		this.emulatorUuid = uuid;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (obj instanceof CalCharacter) {
			CalCharacter option = (CalCharacter) obj;
			return this.getUUID().equals(option.getUUID());
		}
		return false;
	}

	public int hashCode() {
		return this.getDetailName().hashCode();
	}

	public String toString() {
		return this.getDetailName() + ":" + this.getEnabledChoiceVals().toString();
	}
}
