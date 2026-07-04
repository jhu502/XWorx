package plm.dynamic.engine.cvm;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import plm.dynamic.engine.exprs.Expression;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.engine.mdb.CalSelection;
import plm.dynamic.engine.rule.CaseTableRule;
import plm.dynamic.engine.rule.DriverTableRule;
import plm.dynamic.engine.rule.EquivalentRule;
import plm.dynamic.engine.rule.EvaluationRule;
import plm.dynamic.engine.rule.ExpressionRule;
import plm.dynamic.engine.rule.PreConditionRule;
import plm.dynamic.engine.rule.UsageLinkRule;

/**
 * 
 * @author hujin
 * @version 1.1
 * 
 */
public interface Emulator {
	public enum EmulatorType {
		TOP, BRANCH, LEAF
	}
	
	public static class ModuleCascadeMapping {
		private boolean defaultMapping = true;
		private Map<String, Object> paramMapping = new LinkedHashMap<>();
		
		public boolean isDefaultMapping() {
			return this.defaultMapping;
		}
		
		public void setDefaultMapping(boolean bool) {
			this.defaultMapping = bool;
		}
		
		public boolean containsMapping(String name) {
			return this.paramMapping.containsKey(name);
		}
		
		public Map<String, Object> getParamMapping() {
			return this.paramMapping;
		}
		
		public void addParamMapping(String param, Object object) {
			this.paramMapping.put(param, object);
		}
	}
	
	public String getOid();

	public String getUUID();

	public EmulatorType getEmulatorType();

	public String getNumber();

	public void setNumber(String number);

	public String getDetailName();

	public void setDetailName(String detailName);

	public String getGlobalId();

	public void setGlobalId(String globalName);

	public String getReferenceId();

	public void setReferenceId(String referId);
	
	public double getQuantity();
	
	public void setQuantity(double quantity);

	public boolean isEndItem();

	public boolean isEnabled();

	public void setEnabled(boolean enabled);

	public Collection<CalCharacter> getCalCharacts();

	public CalCharacter getCalCharact(String refName);

	public CalCharacter getCalCharact(String[] path, String name);

	public CalSelection getCalSelection();
	
	public UsageLinkRule getUsageRule();

	public void setEvaluationRule(CalSelection selection);

	public Emulator getSubLayerEmulator(String refId);

	public Collection<Emulator> getSubLayerEmulators();

	public void addSubLayerEmulator(Emulator emulator);

	public Class<Expression> getRuleExpressionClass();

	public void setRuleExpressionClass(Class<Expression> cls);

	public Map<String, CalSelection> getOdExpressions();

	public Map<String, CalCharacter> getCharactChoices();

	public Map<String, CaseTableRule> getCaseTableRules();

	public Map<String, EquivalentRule> getEquivalentRules();

	public Map<String, ExpressionRule> getExpressionRules();

	public Map<String, EvaluationRule> getEvaluationRules();

	public Map<String, PreConditionRule> getPreConditionRules();

	public Map<String, DriverTableRule> getDriverTableRules();
	
	public Map<String, ModuleCascadeMapping> getModuleCascadeMapping();

	public void preliminaryAnalysis();

	public void simulateConfiguration(Simulator simulator, CalParam inParam);

	public void calculateEmulatorStatus(Simulator simulator);

	public void calculateCharactStatus(Simulator simulator);
}
