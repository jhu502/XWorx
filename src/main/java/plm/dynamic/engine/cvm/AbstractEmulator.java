package plm.dynamic.engine.cvm;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import plm.dynamic.OptionMode;
import plm.dynamic.engine.exprs.Expression;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalSelection;
import plm.dynamic.engine.rule.CaseTableRule;
import plm.dynamic.engine.rule.DriverTableRule;
import plm.dynamic.engine.rule.EquivalentRule;
import plm.dynamic.engine.rule.EvaluationRule;
import plm.dynamic.engine.rule.ExpressionRule;
import plm.dynamic.engine.rule.PreConditionRule;
import plm.dynamic.engine.rule.UsageLinkRule;
import com.flame.util.XException;

public abstract class AbstractEmulator extends UuidObject implements Emulator {
	private static final long serialVersionUID = 1L;
	protected EmulatorType emulatorType = EmulatorType.BRANCH;
	private String oid = "";
	private String number = "";
	private String detailName = "";
	private String globalId = "";
	private String referenceId;
	private double quantity = 1;
	private boolean endItem = false;
	private boolean collapse = false;
	private boolean enabled = true;
	private Map<String, CalCharacter> PV_MAPPING = new LinkedHashMap<>(); // Parameter Number : CalCharact
	private Map<String, CaseTableRule> CT_MAPPING = new LinkedHashMap<>(); // CaseTable Number : CaseTableRule
	private Map<String, EquivalentRule> EQ_MAPPING = new LinkedHashMap<>(); // Equivalent Number : EquivalentRule
	private Map<String, EvaluationRule> EV_MAPPING = new LinkedHashMap<>(); // Evaluation Number : EvaluationRule
	private Map<String, ExpressionRule> CC_MAPPING = new LinkedHashMap<>(); // Expression Number : ExpressionRule
	private Map<String, PreConditionRule> PC_MAPPING = new LinkedHashMap<>(); // PreCondition Number : PreConditionRule
	private Map<String, DriverTableRule> DT_MAPPING = new LinkedHashMap<>(); // DriverTableRule Number : DriverTableRule
	private Map<String, CalSelection> OD_MAPPING = new LinkedHashMap<>(); // Expression Name : Expression
	private Map<String, Emulator> subLayerEmulator = new LinkedHashMap<>(); // ReferenceId : Emulator
	private Map<String, ModuleCascadeMapping> MCM_MAPPING = new LinkedHashMap<>(); // Module Number : ModuleCascadeMapping
	private Map<String, Object> FIELD_MAPPING = new LinkedHashMap<>(); // Field Name : Field / Attribute Value
	private Class<Expression> ruleExpressClass = null;
	private UsageLinkRule usageRule;
	private CalSelection evalRule;

	public EmulatorType getEmulatorType() {
		return this.emulatorType;
	}

	public String getOid() {
		return this.oid;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
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

	public String getReferenceId() {
		return this.referenceId;
	}

	public void setReferenceId(String referId) {
		this.referenceId = referId;
	}

	public double getQuantity() {
		return this.quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public boolean isCollapse() {
		return this.collapse;
	}

	public void setCollapse(boolean collapse) {
		this.collapse = collapse;
	}

	public boolean isEndItem() {
		return this.endItem;
	}

	public void setEndItem(boolean endItem) {
		this.endItem = endItem;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public UsageLinkRule getUsageRule() {
		return usageRule;
	}

	public void setUsageRule(UsageLinkRule usageRule) {
		this.usageRule = usageRule;
	}

	public CalSelection getCalSelection() {
		return this.evalRule;
	}

	public void setEvaluationRule(CalSelection evalRule) {
		this.evalRule = evalRule;
	}

	public void addSubLayerEmulator(Emulator emulator) {
		String referId = emulator.getReferenceId();

		this.subLayerEmulator.put(referId, emulator);
	}

	public Emulator getSubLayerEmulator(String refId) {
		return this.subLayerEmulator.get(refId);
	}

	@JsonIgnore
	public Collection<Emulator> getSubLayerEmulators() {
		return this.subLayerEmulator.values();
	}

	@JsonIgnore
	public Map<String, CalSelection> getOdExpressions() {
		return this.OD_MAPPING;
	}

	@JsonIgnore
	public Map<String, CalCharacter> getCharactChoices() {
		return this.PV_MAPPING;
	}

	@JsonIgnore
	public Collection<CalCharacter> getCalCharacts() {
		return this.PV_MAPPING.values();
	}

	public Collection<CalCharacter> getDListCharacts() {
		Map<String, CalCharacter> DL_MAPPING = new LinkedHashMap<>();

		for (CalCharacter charact : this.getCalCharacts()) {
			if (OptionMode.DLIST.equals(charact.getOptionMode())) {
				DL_MAPPING.put(charact.getName(), charact);
			}
		}

		return DL_MAPPING.values();
	}

	public CalCharacter getCalCharact(String referName) {
		String[] paths = referName.split("\\.");
		if (paths.length == 0)
			return null;

		if (paths.length == 1) {
			return this.getCalCharact(null, referName);
		}

		String[] referPath = java.util.Arrays.copyOf(paths, paths.length - 2);
		String charactName = paths[paths.length - 2] + "." + paths[paths.length - 1];
		CalCharacter calCharact = this.getCalCharact(referPath, charactName);

		if (calCharact == null) {
			referPath = java.util.Arrays.copyOf(paths, paths.length - 1);
			charactName = paths[paths.length - 1];
			calCharact = this.getCalCharact(referPath, charactName);
		}

		return calCharact;
	}

	public CalCharacter getCalCharact(String[] path, String name) {
		if (path == null || path.length == 0) {
			return this.PV_MAPPING.get(name);
		} else {
			Emulator emulator = this;
			for (String refId : path) {
				emulator = emulator.getSubLayerEmulator(refId);
				if (emulator == null) {
					throw new XException("位置号：" + refId + "在BOM不存在.");
				}
			}
			return emulator.getCalCharact(null, name);
		}
	}

	@Override
	public Map<String, CaseTableRule> getCaseTableRules() {
		return this.CT_MAPPING;
	}

	@Override
	public Map<String, EquivalentRule> getEquivalentRules() {
		return this.EQ_MAPPING;
	}

	@Override
	public Map<String, ExpressionRule> getExpressionRules() {
		return this.CC_MAPPING;
	}

	@Override
	public Map<String, EvaluationRule> getEvaluationRules() {
		return this.EV_MAPPING;
	}

	@Override
	public Map<String, PreConditionRule> getPreConditionRules() {
		return this.PC_MAPPING;
	}

	@Override
	public Map<String, DriverTableRule> getDriverTableRules() {
		return this.DT_MAPPING;
	}

	public Map<String, ModuleCascadeMapping> getModuleCascadeMapping() {
		return this.MCM_MAPPING;
	}

	public Map<String, Object> getFieldValueMapping() {
		return this.FIELD_MAPPING;
	}

	public Expression getExpressionObject() {
		try {
			Constructor<Expression> constructor = ruleExpressClass.getConstructor();
			return constructor.newInstance();
		} catch (XException e) {
			throw e;
		} catch (Exception e) {
			throw new XException(e.getMessage(), e);
		}
	}

	public Class<Expression> getRuleExpressionClass() {
		return this.ruleExpressClass;
	}

	public void setRuleExpressionClass(Class<Expression> clazz) {
		this.ruleExpressClass = clazz;
	}

	public String toString() {
		return this.detailName;
	}
}
