package plm.dynamic.engine.mdb;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;

import plm.dynamic.engine.cvm.AbstractRule;
import plm.dynamic.engine.cvm.Simulator;
import plm.dynamic.engine.mdb.CalChoice.Status;

public class CalParam extends CalCharacter {
	private static final long serialVersionUID = 1L;
	private transient CalCharacter prototype;
	private CalAssign assign; //记录当前选中的值;
	private Simulator simulator;
	private Source source = Source.UNKNOWN;
	private Boolean redraw = false;	//标识是否有更新

	public enum Source {
		INPUT("输入"), DRIVEN("驱出"), CONFIG("配置"), CASCADE("级联"), DEFAULT("默认"), ERROR("错误"), UNKNOWN("未知");

		private String display = "";

		Source(String display) {
			this.display = display;
		}

		public String toDisplay() {
			return this.display;
		}
	}

	public CalParam(CalCharacter calOption) {
		this.prototype = calOption;
		this.setUUID(this.prototype.getUUID()); // CalParam 和 CalOption的UUID是一样的
		this.setName(this.prototype.getName());
		this.setDisplayName(this.prototype.getDisplayName());
		this.setPartNumber(this.prototype.getPartNumber());
		this.setDetailName(this.prototype.getDetailName());
		this.setDisplay(this.prototype.isDisplay());
		this.setReadonly(this.prototype.isReadonly());
		this.setRequired(this.prototype.isRequired());
		this.setGlobalId(this.prototype.getGlobalId());
		this.setMultiSelect(this.prototype.isMultiSelect());
		this.setBaseType(this.prototype.getBaseType());
		this.setOptionMode(this.prototype.getOptionMode());
		this.setEmulatorUUID(this.prototype.getEmulatorUUID());
		this.setConstValue(this.prototype.getConstValue());

		for (Object key : this.prototype.toChoices()) {
			try {
				CalChoice calvalue = this.prototype.getChoice(key);
				CalChoice choice = calvalue.clone();
				this.choices.put(key, choice);
				if (Status.ENABLED.equals(choice.getStatus())) {
					this.enabled.put(key, choice);
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
	}

	public Simulator getSimulator() {
		return simulator;
	}

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
	}

	public CalCharacter getCalCharact() {
		return this.prototype;
	}

	@Override
	public String getVariableName() {
		return this.prototype.getVariableName();
	}

	@Override
	public String getDescription() {
		return this.prototype.getDescription();
	}

	@Override
	public String getSortNo() {
		return this.prototype.getSortNo();
	}

	@Override
	public String getSelectConditionNo() {
		return this.prototype.getSelectConditionNo();
	}

	@Override
	public List<AbstractRule> getRelatedRule() {
		return this.prototype.getRelatedRule();
	}

	@Override
	public String getPreConditionNo() {
		return this.prototype.getPreConditionNo();
	}

	public CalAssign getAssign() {
		return this.assign;
	}

	@Override
	public String getMapAttribute() {
		return this.prototype.getMapAttribute();
	}

	@Override
	public String getDefaultValue() {
		return this.prototype.getDefaultValue();
	}

	public Collection<CalChoice> getAllOptions() {
		Set<CalChoice> result = new TreeSet<CalChoice>();
		result.addAll(this.getChoices());
		result.addAll(this.prototype.getChoices());
		return result;
	}

	public boolean hasValue() {
		if (this.assign == null) {
			return false;
		} else {
			return !this.assign.isNull();
		}
	}

	public boolean hasValue(Object object) {
		if (this.assign == null) {
			return false;
		} else {
			return this.assign.hasValue(object);
		}
	}

	@Override
	public boolean hasChoice(Object value) {
		if (this.prototype.getChoice(value) != null)
			return true;
		else
			return false;
	}
	
	public boolean isValue(Object value) {
		if (this.assign == null) {
			if (value == null) {
				return true;
			} else {
				return value.equals(this.assign);
			}
		} else {
			return this.assign.equals(value);
		}
	}

	public CalParam setValue(Object value) {
		if (value instanceof CalAssign) {
			this.assign = (CalAssign) value;
		} else if (value instanceof CalChoice) {
			this.assign = CalAssign.toCalAssign(this, ((CalChoice) value).value());
		} else {
			this.assign = CalAssign.toCalAssign(this, value);
		}
		return this;
	}

	public Object getValue() {
		if (this.assign == null) {
			return null;
		} else {
			return this.assign.value();
		}
	}

	public Source getSource() {
		return this.source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public void clearValue() {
		this.setValue(null).setSource(Source.UNKNOWN);
	}
	
	public boolean getRedraw() {
		return this.redraw;
	}
	
	public void setRedraw(boolean redraw) {
		this.redraw = redraw;
	}

	@Override
	public void enableAllChoice() {
		for (Entry<Object, CalChoice> entry : this.choices.entrySet()) {
			Object key = entry.getKey();
			CalChoice choice = entry.getValue();
			CalChoice _option = this.prototype.getChoice(key);
			if (Status.ENABLED.equals(_option.getStatus())) {
				this.enabled.put(choice.value(), choice);
				choice.setStatus(Status.ENABLED);
				choice.cleanPrompt();
			}
		}
	}

	public boolean isEnabledOption(Object option) {
		return this.enabled.containsKey(option);
	}

	public Collection<?> crossOptions(Set<?> options) {
		if (options == null || options.isEmpty())
			return new HashSet<>();
		return CollectionUtils.intersection(this.getEnabledChoices(), options);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		
		if (this == obj) 
			return true;
		
		if (obj instanceof CalParam) {
			CalParam param = (CalParam) obj;
			return this.getUUID().equals(param.getUUID());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.prototype.hashCode();
	}

	@Override
	public String toString() {
		if (this.hasValue()) {
			return this.getDetailName() + "^(" + (this.prototype.isRequired() ? "Y" : "N") + "," + (this.prototype.isDisplay() ? "Y" : "N") + ")='" + this.getAssign() + "'  "
					+ this.getSource().toString();
		} else {
			return this.getDetailName() + "^(" + (this.prototype.isRequired() ? "Y" : "N") + "," + (this.prototype.isDisplay() ? "Y" : "N") + ")=" + this.getEnabledChoices() + "  "
					+ this.getSource().toString();
		}
	}
}
