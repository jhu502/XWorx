package plm.dynamic.engine.load;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.flame.util.FlameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plm.dynamic.InputType;
import plm.dynamic.OptionMode;
import plm.dynamic.bean.XCaseRows;
import plm.dynamic.XCaseTable;
import plm.dynamic.XCharacteristic;
import plm.dynamic.bean.XChoice;
import plm.dynamic.XExpression;
import plm.dynamic.engine.cvm.AbstractEmulator;
import plm.dynamic.engine.cvm.DefaultEmulator;
import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.cvm.Emulator.EmulatorType;
import plm.dynamic.engine.exprs.RuleExpressionUtility;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalChoice;
import plm.dynamic.engine.mdb.CalRow;
import plm.dynamic.engine.mdb.CalSelection;
import plm.dynamic.engine.rule.CaseTableRule;
import plm.dynamic.engine.rule.DriverTableRule;
import plm.dynamic.engine.rule.EquivalentRule;
import plm.dynamic.engine.rule.EvaluationRule;
import plm.dynamic.engine.rule.ExpressionRule;
import plm.dynamic.engine.rule.UsageLinkRule;
import plm.dynamic.service.DynamicServiceHelper;
import plm.part.XPart;
import plm.part.XPartUsageLink;
import plm.part.service.XPartServiceHelper;
import com.flame.type.XBaseType;
import com.flame.util.XException;

/**
 * 
 * @author hujin
 * @version 1.1
 * 
 */
public class XWorxDataLoader extends AbstractLoader {
	private static final Logger logger = LoggerFactory.getLogger(XWorxDataLoader.class);
	private XPartUsageLink link;
	private XPart xpart;

	public static XWorxDataLoader newDataLoader(XPart part, XPartUsageLink link) {
		XWorxDataLoader loader = new XWorxDataLoader();
		loader.xpart = part;
		loader.link = link;

		return loader;
	}

	public static XWorxDataLoader newDataLoader(XPart part) {
		XWorxDataLoader loader = new XWorxDataLoader();
		loader.xpart = part;

		return loader;
	}

	/**
	 * Load parameters and Case Table into cto analyzer from PLM.
	 * 
	 * @param emulator
	 * @throws XException
	 */
	private void loadCharacteristics(Emulator emulator) throws XException {
		List<XCharacteristic> list = DynamicServiceHelper.repository().getSortedXCharacteristic(xpart);
		for (XCharacteristic xcharact : list) {
			boolean required = InputType.REQUIRED.equals(xcharact.getInputType());
			boolean readonly = InputType.READONLY.equals(xcharact.getInputType());
			CalCharacter calCharact = new CalCharacter(xcharact.getNumber(), xcharact.getBaseType(), required, readonly, xcharact.getName());
			emulator.getCharactChoices().put(calCharact.getName(), calCharact);
			calCharact.setOptionMode(xcharact.getOptionMode());
			calCharact.setVariableName(this.genParameterAlias());
			calCharact.setEmulatorUUID(emulator.getUUID());
			if (isBlank(emulator.getGlobalId()))
				calCharact.setGlobalId(calCharact.getName());
			else
				calCharact.setGlobalId(emulator.getGlobalId() + "." + calCharact.getName());

			String referId = emulator.getReferenceId();
			if (FlameUtils.isNotBlank(referId)) {
				calCharact.setPartNumber(this.xpart.getNumber());
				calCharact.setDetailName(this.xpart.getNumber() + ":" + calCharact.getName());
			} else {
				calCharact.setPartNumber(this.xpart.getNumber());
				calCharact.setDetailName(this.xpart.getNumber() + "(" + referId + "):" + calCharact.getName());
			}

			if (OptionMode.DLIST.equals(xcharact.getOptionMode())) {
				calCharact.setDynamicMapping(xcharact.getDynamicMapping());
				/**
				 * 加载类型为InfoTable选型方式为DList的动态参数
				 */
				if (XBaseType.INFOTABLE.equals(xcharact.getBaseType())) {
					for (XChoice xchoice : xcharact.getChoices()) {
						String childName = xcharact.getNumber() + "." + xchoice.getValue();
						CalCharacter childCharact = new CalCharacter(childName, XBaseType.STRING, false, true, xchoice.getDescription());
						emulator.getCharactChoices().put(childCharact.getName(), childCharact);
						childCharact.setDisplay(false);
						childCharact.setOptionMode(OptionMode.DLIST);
						childCharact.setVariableName(this.genParameterAlias());
						childCharact.setEmulatorUUID(emulator.getUUID());
						childCharact.setDynamicMapping(xchoice.getValue().toString());
						if (isBlank(emulator.getGlobalId()))
							childCharact.setGlobalId(xcharact.getNumber());
						else
							childCharact.setGlobalId(emulator.getGlobalId() + "." + childCharact.getName());

						if (FlameUtils.isNotBlank(referId)) {
							childCharact.setPartNumber(this.xpart.getNumber());
							childCharact.setDetailName(this.xpart.getNumber() + ":" + childCharact.getName());
						} else {
							childCharact.setPartNumber(this.xpart.getNumber());
							childCharact.setDetailName(this.xpart.getNumber() + "(" + referId + "):" + childCharact.getName());
						}

						calCharact.addChildCharacts(childCharact);
					}

					/**
					 * 为Emulator设置UsageRule
					 */
					UsageLinkRule usageRule = UsageLinkRule.newInstance(emulator, calCharact);
					calCharact.addRelatedRule(usageRule);
					for (CalCharacter character : calCharact.getChildCharacts()) {
						character.addRelatedRule(usageRule);
					}
					((AbstractEmulator) emulator).setUsageRule(usageRule);
				}
			} else if (OptionMode.LIST.equals(xcharact.getOptionMode())) {
				for (XChoice xchoice : xcharact.getChoices()) {
					CalChoice choice = new CalChoice(xchoice.getValue(), xchoice.getDescription());
					calCharact.addChoice(choice);
				}
			}
		}

		logger.debug("All parameters have been loaded into Emulator successfully.");
	}

	/**
	 * 加载参数型态
	 * 
	 * @param emulator
	 * @throws XException
	 */
	private void loadParameterModel(Emulator emulator) throws XException {

	}

	/**
	 * 
	 * @param emulator
	 * @throws XException
	 */
	private void loadParameterSetting(Emulator emulator) throws XException {

	}

	/**
	 * Load object dependency and bom into lenovo cto db.
	 * 
	 * @param emulator
	 * @throws XException
	 */
	private void loadBomStructure(Emulator emulator) throws XException {
		Collection<CalCharacter> dlistColl = ((AbstractEmulator) emulator).getDListCharacts();
		List<?> list = XPartServiceHelper.repository().getUsedbyXPart(xpart);
		for (Object o : list) {
			Object[] objs = (Object[]) o;
			XPartUsageLink link = (XPartUsageLink) objs[0];
			XPart child = (XPart) objs[1];
			/**
			 * 用直接下层部件信息填充DList参数的可选值；
			 */
			for (CalCharacter charact : dlistColl) {
				String dyMapping = charact.getDynamicMapping();
				if (StringUtils.isNoneEmpty(dyMapping)) {
					if (dyMapping.startsWith("Class:")) {

					} else {
						Object value = this.dynamicMappingValue(child, link, dyMapping);
						charact.addChoice(new CalChoice(value, value.toString()));
					}
				}
			}

			UsageLinkRule usageRule = emulator.getUsageRule();
			if (usageRule != null) {
				List<Object> row = new ArrayList<>();
				for (CalCharacter column : usageRule.getColumns()) {
					String dyMapping = column.getDynamicMapping();
					if (StringUtils.isNoneEmpty(dyMapping)) {
						Object value = this.dynamicMappingValue(child, link, dyMapping);
						row.add(value);
					}
				}
				usageRule.addRow(new CalRow(row));
			}

			Emulator subEmulator = XWorxDataLoader.newDataLoader(child, link).loadData2Emulator(emulator);
			logger.debug("Emulator:" + subEmulator.getDetailName() + " has loaded successfully.");
		}
	}

	/**
	 * 将案例表&表达式约束加载进Emulator
	 * 
	 * @param emulator
	 * @throws XException
	 */
	private void loadCaseTableRules(Emulator emulator) throws XException {
		List<XCaseTable> list = DynamicServiceHelper.repository().getRelatedXCaseTable(this.xpart);
		for (XCaseTable casetable : list) {
			CaseTableRule casetableRule = transformCaseTable(casetable, emulator);
			emulator.getCaseTableRules().put(casetableRule.getName(), casetableRule);
			for (CalCharacter calOption : casetableRule.getColumns()) {
				calOption.addRelatedRule(casetableRule);
			}
		}
	}

	private void loadExpressionRules(Emulator emulator) throws XException {
		List<XExpression> list = DynamicServiceHelper.repository().getRelatedXExpression(this.xpart);
		for (XExpression xexpress : list) {
			ExpressionRule expressionRule = new ExpressionRule(emulator, xexpress.getName(), xexpress.getExpression());
			expressionRule.setDescription(xexpress.getDescription());
			RuleExpressionUtility utility = RuleExpressionUtility.parserExpressionRule(emulator, xexpress.getExpression());
			emulator.getExpressionRules().put(expressionRule.getName(), expressionRule);
			for (CalCharacter charact : utility.getOptionMap().values()) {
				charact.addRelatedRule(expressionRule);
			}
		}
	}

	/**
	 * 加载Equivalent约束进Emulator
	 * @param emulator
	 * @throws XException
	 */
	private void loadEquivalentRules(Emulator emulator) throws XException {
		Map<String, EquivalentRule> EQ_Mapping = emulator.getEquivalentRules();
	}

	private void loadDriverTableRules(Emulator emulator) throws XException {
		Map<String, DriverTableRule> DT_Mapping = emulator.getDriverTableRules();
	}

	public void handleAllExpression(Emulator emulator) {
		Map<String, ExpressionRule> ruleExpressMap = emulator.getExpressionRules();
		for (ExpressionRule ruleExpress : ruleExpressMap.values()) {
			RuleExpressionUtility ruleUtility = RuleExpressionUtility.parserExpressionRule(emulator, ruleExpress.getExpression());
			ruleExpress.setSentence(ruleUtility.getExpression());
			ruleExpress.setCharactMap(ruleUtility.getOptionMap());

			for (CalCharacter calOption : ruleExpress.getCharactMap().values()) {
				calOption.addRelatedRule(ruleExpress);
			}
		}

		Map<String, EvaluationRule> evaluationMap = emulator.getEvaluationRules();
		for (EvaluationRule evaluation : evaluationMap.values()) {
			RuleExpressionUtility ruleUtility = RuleExpressionUtility.parserExpressionRule(emulator, evaluation.getExpression());
			evaluation.setSentence(ruleUtility.getExpression());
			evaluation.setCharactMap(ruleUtility.getOptionMap());

			for (CalCharacter calOption : evaluation.getCharactMap().values()) {
				calOption.addRelatedRule(evaluation);
			}
		}

		Map<String, CalSelection> dependencyMap = emulator.getOdExpressions();
		for (CalSelection selection : dependencyMap.values()) {
			RuleExpressionUtility ruleUtility = RuleExpressionUtility.parserExpressionRule(emulator, selection.getExpression());
			selection.setSentence(ruleUtility.getExpression());
			selection.setOptionMap(ruleUtility.getOptionMap());
		}
	}

	@Override
	public Emulator loadData2Emulator() {
		DefaultEmulator emulator = new DefaultEmulator(EmulatorType.TOP);
		emulator.setNumber(this.xpart.getNumber());
		emulator.setGlobalId(emulator.getReferenceId());

		try {
			emulator.setNumber(this.xpart.getNumber());
			emulator.setDetailName(this.xpart.getDisplay());
			emulator.setCollapse(this.xpart.isCollapsible());
			emulator.setEndItem(this.xpart.isEndItem());
			emulator.setGlobalId(emulator.getGlobalId() == null ? "" : emulator.getGlobalId());

			this.loadCharacteristics(emulator);
			this.loadParameterModel(emulator);
			this.loadParameterSetting(emulator);
			this.loadBomStructure(emulator);
			this.loadCaseTableRules(emulator);
			this.loadExpressionRules(emulator);
			this.loadEquivalentRules(emulator);
			this.loadDriverTableRules(emulator);
			this.handleAllExpression(emulator);
		} finally {
			//	threadLocal.remove();
		}

		return emulator;
	}

	@Override
	public Emulator loadData2Emulator(Emulator parent) {
		DefaultEmulator emulator = new DefaultEmulator();

		emulator.setNumber(this.xpart.getNumber());
		emulator.setReferenceId(link.getComponentId());
		emulator.setQuantity(link.getQuantity());
		if (parent == null) {
			emulator.setGlobalId(emulator.getReferenceId());
		} else {
			parent.addSubLayerEmulator(emulator);
			if (StringUtils.isEmpty(parent.getGlobalId())) {
				emulator.setGlobalId(emulator.getReferenceId());
			} else {
				emulator.setGlobalId(parent.getGlobalId() + "." + emulator.getReferenceId());
			}
		}

		try {
			emulator.setNumber(this.xpart.getNumber());
			emulator.setDetailName(this.xpart.getDisplay());
			if (isBlank(emulator.getGlobalId())) {
				emulator.setGlobalId("");
			}

			if (emulator instanceof DefaultEmulator) {
				DefaultEmulator xemulator = (DefaultEmulator) emulator;
				xemulator.setCollapse(this.xpart.isCollapsible());
				xemulator.setEndItem(this.xpart.isEndItem());
			}

			this.loadCharacteristics(emulator);
			this.loadParameterModel(emulator);
			this.loadParameterSetting(emulator);
			this.loadBomStructure(emulator);
			this.loadCaseTableRules(emulator);
			this.loadExpressionRules(emulator);
			this.loadEquivalentRules(emulator);
			this.loadDriverTableRules(emulator);
			this.handleAllExpression(emulator);
		} finally {
			//	threadLocal.remove();
		}

		return emulator;
	}

	protected CaseTableRule transformCaseTable(XCaseTable casetable, Emulator emulator) {
		int length = casetable.getHead().length();
		CalCharacter[] columns = new CalCharacter[length];

		int j = 0;
		for (String column : casetable.getHead().getData()) {
			columns[j++] = emulator.getCalCharact(column);
		}

		CaseTableRule calTable = new CaseTableRule(casetable.getName(), columns);
		calTable.setDetailName(emulator.getNumber() + ":" + casetable.getName());
		XCaseRows caseRows = casetable.getRows();
		for (List<Object> row : caseRows.getData()) {
			Object[] objects = new Object[length];
			int i = 0;
			for (Object value : row) {
				/**
				CalCharacter character = columns[i];
				if (!isBlank(value)) {
					OptionMode omode = charact.getOptionMode();
					if (OptionMode.LIST.equals(omode)) {
						character.addChoice(value);
					} else if (OptionMode.NONE.equals(omode)) {
						character.setOptionMode(OptionMode.LIST);
						character.addChoice(value);
					}
				}
				 */
				objects[i++] = value;
			}
			calTable.addRow(new CalRow(objects));
		}
		return calTable;
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
	}
}
