package plm.dynamic.engine.cvm;

import com.flame.type.XBaseType;
import com.flame.util.XException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plm.dynamic.OptionMode;
import plm.dynamic.engine.exprs.Expression;
import plm.dynamic.engine.exprs.GenerateContext;
import plm.dynamic.engine.exprs.RuleExpressionGenerator;
import plm.dynamic.engine.load.Loader;
import plm.dynamic.engine.mdb.*;
import plm.dynamic.engine.mdb.CalParam.Source;
import plm.dynamic.engine.rule.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

public class DefaultEmulator extends AbstractEmulator {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(DefaultEmulator.class);
	private Map<String, CalCharacter> ALL_PV_MAPPING; // Parameter Number : CalOption
	private Map<String, CaseTableRule> ALL_CT_MAPPING; // CaseTable Number : CalTable
	private Map<String, EquivalentRule> ALL_EQ_MAPPING; // ExpressionRule Number : ExpressionRule
	private Map<String, ExpressionRule> ALL_ER_MAPPING; // ExpressionRule Number : ExpressionRule
	private Map<String, EvaluationRule> ALL_EV_MAPPING; // ExternalAppRule Number : EvaluationRule
	private Map<String, ExpressionRule> ALL_PC_MAPPING; // PreConditionRule Number : PreConditionRule
	private Map<String, DriverTableRule> ALL_DT_MAPPING; // DriverTableRule Number : DriverTableRule
	private Map<String, CalSelection> ALL_OD_MAPPING; // Expression Name : CalSelection
	private Map<String, Emulator> ALL_EMULATOR_MAPPING;

	public DefaultEmulator() {
		super();
	}

	public DefaultEmulator(EmulatorType eType) {
		super();
		this.emulatorType = eType;
	}

	public static Emulator loadData2Emulator(Loader loader, EmulatorType eType) {
		DefaultEmulator emulator = new DefaultEmulator(eType);
		loader.loadData2Emulator(emulator);
		return emulator;
	}

	public Map<String, CalCharacter> getAllOptionChoices() {
		if (this.ALL_PV_MAPPING == null) {
			this.ALL_PV_MAPPING = new LinkedHashMap<>();
			Set<Emulator> temp = new HashSet<>();
			temp.add(this);
			while (!temp.isEmpty()) {
				Emulator[] emulators = temp.toArray(new Emulator[] {});
				temp.clear();
				for (Emulator emulator : emulators) {
					for (CalCharacter calOption : emulator.getCharactChoices().values()) {
						this.ALL_PV_MAPPING.put(calOption.getUUID(), calOption);
					}
					temp.addAll(emulator.getSubLayerEmulators());
				}
			}
		}
		return this.ALL_PV_MAPPING;
	}

	public Map<String, CaseTableRule> getAllCaseTableRules() {
		if (this.ALL_CT_MAPPING == null) {
			this.ALL_CT_MAPPING = new LinkedHashMap<>();
			Set<Emulator> temp = new HashSet<>();
			temp.add(this);
			while (!temp.isEmpty()) {
				Emulator[] emulators = temp.toArray(new Emulator[] {});
				temp.clear();
				for (Emulator emulator : emulators) {
					for (CaseTableRule calTable : emulator.getCaseTableRules().values()) {
						this.ALL_CT_MAPPING.put(calTable.getUUID(), calTable);
					}
					temp.addAll(emulator.getSubLayerEmulators());
				}
			}
		}
		return this.ALL_CT_MAPPING;
	}

	public Map<String, EquivalentRule> getAllEquivalentRules() {
		if (this.ALL_EQ_MAPPING == null) {
			this.ALL_EQ_MAPPING = new LinkedHashMap<>();
			Set<Emulator> temp = new HashSet<>();
			temp.add(this);
			while (!temp.isEmpty()) {
				Emulator[] emulators = temp.toArray(new Emulator[] {});
				temp.clear();
				for (Emulator emulator : emulators) {
					for (EquivalentRule equivaRule : emulator.getEquivalentRules().values()) {
						this.ALL_EQ_MAPPING.put(equivaRule.getUUID(), equivaRule);
					}
					temp.addAll(emulator.getSubLayerEmulators());
				}
			}
		}
		return this.ALL_EQ_MAPPING;
	}

	public Map<String, ExpressionRule> getAllExpressionRules() {
		if (this.ALL_ER_MAPPING == null) {
			this.ALL_ER_MAPPING = new LinkedHashMap<>();
			Set<Emulator> temp = new HashSet<>();
			temp.add(this);
			while (!temp.isEmpty()) {
				Emulator[] emulators = temp.toArray(new Emulator[] {});
				temp.clear();
				for (Emulator emulator : emulators) {
					for (ExpressionRule calCondition : emulator.getExpressionRules().values()) {
						this.ALL_ER_MAPPING.put(calCondition.getUUID(), calCondition);
					}
					temp.addAll(emulator.getSubLayerEmulators());
				}
			}
		}
		return this.ALL_ER_MAPPING;
	}

	public Map<String, EvaluationRule> getAllEvaluationRules() {
		if (this.ALL_EV_MAPPING == null) {
			this.ALL_EV_MAPPING = new LinkedHashMap<>();
			Set<Emulator> _temp = new HashSet<>();
			_temp.add(this);
			while (!_temp.isEmpty()) {
				Emulator[] emulators = _temp.toArray(new Emulator[] {});
				_temp.clear();
				for (Emulator emulator : emulators) {
					for (EvaluationRule evaluation : emulator.getEvaluationRules().values()) {
						this.ALL_EV_MAPPING.put(evaluation.getUUID(), evaluation);
					}
					_temp.addAll(emulator.getSubLayerEmulators());
				}
			}
		}
		return this.ALL_EV_MAPPING;
	}

	public Map<String, ExpressionRule> getAllPreConditionRules() {
		if (this.ALL_PC_MAPPING == null) {
			this.ALL_PC_MAPPING = new LinkedHashMap<>();
			Set<Emulator> temp = new HashSet<>();
			temp.add(this);
			while (!temp.isEmpty()) {
				Emulator[] emulators = temp.toArray(new Emulator[] {});
				temp.clear();
				for (Emulator emulator : emulators) {
					for (ExpressionRule calCondition : emulator.getPreConditionRules().values()) {
						this.ALL_PC_MAPPING.put(calCondition.getUUID(), calCondition);
					}
					temp.addAll(emulator.getSubLayerEmulators());
				}
			}
		}
		return this.ALL_PC_MAPPING;
	}

	public Map<String, DriverTableRule> getAllDriverTableRules() {
		if (this.ALL_DT_MAPPING == null) {
			this.ALL_DT_MAPPING = new LinkedHashMap<>();
			Set<Emulator> temp = new HashSet<>();
			temp.add(this);
			while (!temp.isEmpty()) {
				Emulator[] emulators = temp.toArray(new Emulator[] {});
				temp.clear();
				for (Emulator emulator : emulators) {
					for (DriverTableRule driverTable : emulator.getDriverTableRules().values()) {
						this.ALL_DT_MAPPING.put(driverTable.getUUID(), driverTable);
					}
					temp.addAll(emulator.getSubLayerEmulators());
				}
			}
		}
		return this.ALL_DT_MAPPING;
	}

	public Map<String, CalSelection> getAllOdExpressions() {
		if (this.ALL_OD_MAPPING == null) {
			this.ALL_OD_MAPPING = new LinkedHashMap<>();
			Set<Emulator> temp = new HashSet<>();
			temp.add(this);
			while (!temp.isEmpty()) {
				Emulator[] emulators = temp.toArray(new Emulator[] {});
				temp.clear();
				for (Emulator emulator : emulators) {
					for (CalSelection odExpression : emulator.getOdExpressions().values()) {
						this.ALL_OD_MAPPING.put(odExpression.getUUID(), odExpression);
					}
					temp.addAll(emulator.getSubLayerEmulators());
				}
			}
		}
		return this.ALL_OD_MAPPING;
	}

	public Map<String, Emulator> getAllSubEmulators() {
		if (this.ALL_EMULATOR_MAPPING == null) {
			this.ALL_EMULATOR_MAPPING = new LinkedHashMap<>();
			Set<Emulator> temp = new HashSet<>();
			temp.add(this);
			while (!temp.isEmpty()) {
				Emulator[] emulators = temp.toArray(new Emulator[] {});
				temp.clear();
				for (Emulator emulator : emulators) {
					for (Emulator _emulator : emulator.getSubLayerEmulators()) {
						this.ALL_EMULATOR_MAPPING.put(_emulator.getUUID(), _emulator);
					}
					temp.addAll(emulator.getSubLayerEmulators());
				}
			}
		}
		return this.ALL_EMULATOR_MAPPING;
	}

	public void simulateConfiguration(Simulator simulator, CalParam inParam) {
		StringBuilder errorMsg = new StringBuilder();
		Set<CalParam> recurseSet = new HashSet<>();
		recurseSet.add(inParam);

		Map<String, CalParam> metabolic = simulator.getCalParameters();

		while (!recurseSet.isEmpty()) {
			Collection<Object> relatedRuleSet = new HashSet<>();
			Map<EquivalentRule, CalParam> equivaRuleMap = new HashMap<>();

			/** Get all related CasTable by parameters that were contained in recurse_set */
			for (CalParam param : recurseSet) {
				for (AbstractRule calRule : param.getRelatedRule()) {
					if (calRule instanceof EquivalentRule) {
						equivaRuleMap.put((EquivalentRule) calRule, param);
					} else {
						relatedRuleSet.add(calRule);
					}

				}
			}
			recurseSet.clear();

			/**
			 * 对等规则(EquivalentRule)需要在其他规则(CaseTableRule、CaseConditionRule、PreConditionRule)之前运行
			 */
			for (Entry<EquivalentRule, CalParam> entry : equivaRuleMap.entrySet()) {
				EquivalentRule equivaRule = entry.getKey();
				CalParam param = entry.getValue();
				Set<CalParam> changeSet = equivaRule.performRule(metabolic, param);
				if (!changeSet.isEmpty()) {
					recurseSet.addAll(changeSet);
				}
			}

			for (Object rule : relatedRuleSet) {
				if (rule instanceof CaseTableRule) {
					CaseTableRule casetableRule = (CaseTableRule) rule;
					Set<CalParam> changeSet = casetableRule.performRule(metabolic, inParam);
					if (!changeSet.isEmpty()) {
						recurseSet.addAll(changeSet);
						changeSet = this.processDrivenOption(metabolic, inParam, "CaseTableRule:" + casetableRule.getDetailName());
						if (!changeSet.isEmpty()) {
							recurseSet.addAll(changeSet);
						}
					}
				} else if (rule instanceof ExpressionRule) {
					ExpressionRule expressionRule = (ExpressionRule) rule;
					Set<CalParam> changeSet = expressionRule.performRule(metabolic, inParam);
					if (!changeSet.isEmpty()) {
						recurseSet.addAll(changeSet);
						changeSet = this.processDrivenOption(metabolic, inParam, "ExpressionRule:" + expressionRule.getDetailName());
						if (!changeSet.isEmpty()) {
							recurseSet.addAll(changeSet);
						}
					}
				} else if (rule instanceof PreConditionRule) {
					PreConditionRule conditionRule = (PreConditionRule) rule;
					Set<CalParam> changeSet = conditionRule.performRule(metabolic, inParam);
					if (!changeSet.isEmpty()) {
						recurseSet.addAll(changeSet);
						changeSet = this.processDrivenOption(metabolic, inParam, "PreConditionRule:" + conditionRule.getDetailName());
						if (!changeSet.isEmpty()) {
							recurseSet.addAll(changeSet);
						}
					}
				} else if (rule instanceof EvaluationRule) {
					EvaluationRule evaluationRule = (EvaluationRule) rule;
					Set<CalParam> changeSet = evaluationRule.performRule(metabolic, inParam);
					if (!changeSet.isEmpty()) {
						recurseSet.addAll(changeSet);
						changeSet = this.processDrivenOption(metabolic, inParam, "EvaluationRule:" + evaluationRule.getDetailName());
						if (!changeSet.isEmpty()) {
							recurseSet.addAll(changeSet);
						}
					}
				} else if (rule instanceof DriverTableRule) {
					DriverTableRule drivertableRule = (DriverTableRule) rule;
					Set<CalParam> changeSet = drivertableRule.performRule(metabolic, inParam);
					if (!changeSet.isEmpty()) {
						recurseSet.addAll(changeSet);
						changeSet = this.processDrivenOption(metabolic, inParam, "DriverTableRule:" + drivertableRule.getDetailName());
						if (!changeSet.isEmpty()) {
							recurseSet.addAll(changeSet);
						}
					}
				} else if (rule instanceof UsageLinkRule) {
					UsageLinkRule usageTableRule = (UsageLinkRule) rule;
					Set<CalParam> changeSet = usageTableRule.performRule(metabolic, inParam);
					if (!changeSet.isEmpty()) {
						recurseSet.addAll(changeSet);
						changeSet = this.processDrivenOption(metabolic, inParam, "UsageTableRule:" + usageTableRule.getDetailName());
						if (!changeSet.isEmpty()) {
							recurseSet.addAll(changeSet);
						}
					}
				}
			}

			/**
			 * 对只有唯一的可选参数值的必选参数进行赋值处理, 并对其他参数进行计算
			 */
			if (!recurseSet.isEmpty()) {
				Set<CalParam> changeSet = processDrivenOption(metabolic);
				if (!changeSet.isEmpty()) {
					recurseSet.addAll(changeSet);
				}
			}
		}
		if (errorMsg.length() > 0) {
			throw new XException(errorMsg.toString());
		}

	}

	public void calculateEmulatorStatus(Simulator simulator) {
		Map<String, CalParam> metabolic = simulator.getCalParameters();
		Set<String> enabled = simulator.getEnabledEmulator();
		enabled.clear();
		enabled.add(this.getUUID());

		Expression expression = simulator.getExpression();

		Map<String, Object> values = new HashMap<>();
		for (CalParam parameter : metabolic.values()) {
			if (parameter.hasValue()) {
				values.put(parameter.getVariableName(), parameter.getValue());
			}
		}

		Set<Emulator> emulatorSet = new HashSet<>();
		emulatorSet.add(this);
		expression.setCharValues(values);

		while (!emulatorSet.isEmpty()) {
			Emulator[] emulators = emulatorSet.toArray(new Emulator[0]);
			emulatorSet.clear();

			for (Emulator e : emulators) {
				for (Emulator _emulator : e.getSubLayerEmulators()) {
					CalSelection selection = _emulator.getCalSelection();
					if (selection == null) {
						enabled.add(_emulator.getUUID());
						emulatorSet.add(_emulator);
					} else {
						Set<String> varSet = selection.getVariables();
						if (values.keySet().containsAll(varSet)) {
							boolean bool = (boolean) expression.executeMethod(selection.getMethodName(), new Class<?>[0], new Object[0]);
							if (bool) {
								enabled.add(_emulator.getUUID());
								emulatorSet.add(_emulator);
							}
						} else {
							enabled.add(_emulator.getUUID());
							emulatorSet.add(_emulator);
						}
					}
				}
			}
		}

	}

	/**
	 * 根据参数上的选择条件来计算参数的状态:必选、非必选
	 */
	public void calculateCharactStatus(Simulator simulator) {
		Map<String, CalParam> metabolic = simulator.getCalParameters();
		Set<String> enabled = simulator.getEnabledEmulator();

		Map<String, Object> values = new HashMap<>();

		for (CalParam parameter : metabolic.values()) {
			if (parameter.hasValue()) {
				values.put(parameter.getVariableName(), parameter.getValue());
			}
		}

		Expression expression = getExpressionObject();
		expression.setCharValues(values);

		for (CalParam parameter : metabolic.values()) {
			String conditionNo = parameter.getSelectConditionNo();
			boolean bool = false;
			if (!isBlank(conditionNo)) {
				bool = (boolean) expression.executeMethod("exec" + conditionNo, new Class<?>[0], new Object[0]);
				parameter.setRequired(bool);
				logger.trace("Charact Status SelCondition:" + conditionNo + "     Required:" + bool);
			}
		}

		for (CalParam parameter : metabolic.values()) {
			String conditionNo = parameter.getPreConditionNo();
			boolean bool = false;
			if (!isBlank(conditionNo)) {
				bool = (boolean) expression.executeMethod("exec" + conditionNo, new Class<?>[0], new Object[0]);
				if (!bool && Source.INPUT.equals(parameter.getSource())) {
					throw new XException("M002", "参数：" + parameter.getName() + "违反其前提条件！");
				}
				parameter.setDisplay(bool);
				logger.trace("Charact Status PreCondition:" + conditionNo + "     Display:" + bool);
			}
		}

		for (CalParam parameter : metabolic.values()) {
			if (!enabled.contains(parameter.getEmulatorUUID())) {
				parameter.setDisplay(false);
			}
		}
	}

	protected Set<CalParam> processDrivenOption(Map<String, CalParam> metabolic) {
		Set<CalParam> changeSet = new HashSet<>();

		for (CalParam param : metabolic.values()) {
			if ((param.isReadonly() || param.isRequired()) && !Source.INPUT.equals(param.getSource()) && !Source.DRIVEN.equals(param.getSource())) {
				Collection<CalChoice> colls = param.getEnabledChoices();
				if (OptionMode.LIST.equals(param.getOptionMode()) && colls.size() == 1) {
					CalChoice choice = colls.iterator().next();
					if (!param.hasValue()) {
						param.setValue(choice.value());
						param.setSource(Source.DRIVEN);
						changeSet.add(param);
					} else {
						if (!choice.value().equals(param.getAssign())) {
							if (Source.DEFAULT.equals(param.getSource())) {
								param.setValue(choice.value());
								param.setSource(Source.DRIVEN);
								changeSet.add(param);
							}
						}
					}
				}
			}
		}

		return changeSet;
	}

	protected Set<CalParam> processDrivenOption(Map<String, CalParam> metabolic, CalParam inParam, String info) {
		Set<CalParam> recurseSet = new HashSet<>();

		//处理参数被驱动出来的情况
		for (CalParam param : metabolic.values()) {
			/**
			 * 每次运算后，如果已经有值的参数的可选值被清空，如果该参数已经选择了值，也不需要清除该参数的选择；
			 */
			if (OptionMode.LIST.equals(param.getOptionMode())) {
				if (param.hasValue()) {
					if (!param.hasEnabledChoice(param.getAssign())) {
						String error = info + "禁用" + param.getDetailName() + "=" + param.getAssign() + ", 当选择" + inParam.getDetailName() + "=" + inParam.getAssign() + "时;";
						throw new XException("M204", error);
					}
					
				} else if (param.isRequired()) {
					if (param.getEnabledChoices().isEmpty()) {
						throw new XException("必选参数：" + param.getDetailName() + "的值无值可选，当设置" + inParam.getName() + "='" + inParam.getAssign() + "'时.");
					}
				}
			}
			/**
			 * 每次运算后如果有参数只有唯一的一个选项，将该选项设置为“驱出”的值
			 */
			if (!Source.INPUT.equals(param.getSource()) && !Source.DRIVEN.equals(param.getSource())) {
				Collection<CalChoice> colls = param.getEnabledChoices();
				if (OptionMode.LIST.equals(param.getOptionMode()) && colls.size() == 1) {
					CalChoice value = colls.iterator().next();
					boolean bool = false;
					if (!param.hasValue()) {
						param.setValue(value);
						param.setPrompt("M503~" + info + "约束导致" + param.getDetailName() + "=" + value.value() + "被选中, 当选择" + inParam.getDetailName() + "=" + inParam.getAssign() + "时;");
						param.setSource(Source.DRIVEN);
						bool = true;
					} else {
						if (!value.value().equals(param.getAssign())) {
							if (Source.DEFAULT.equals(param.getSource())) {
								param.setValue(CalAssign.toCalAssign(param, value.value()));
								param.setSource(Source.DRIVEN);
								bool = true;
							}
						}
					}
					logger.trace(param.getName() + ":" + value.value() + "    Driven Calculate    " + param.getCalCharact().getRelatedRule());
					/** 
					 * 当前驱动出来的值可能会影响其他的参数的值的有效性，因此首先判断该参数是否有相关联的约束
					 * 若有，添加进recurse_param_set变量，且将循环标识recurse_flag=true
					 */
					if (bool && !param.getRelatedRule().isEmpty()) {
						recurseSet.add(param);
					}
				}
			}
		}

		return recurseSet;
	}

	public void buildExpressionRuntime() {
		RuleExpressionGenerator generator = new RuleExpressionGenerator(this);

		try {
			Thread thread = GenerateContext.execGenContext(generator);
			thread.join();
			Exception ex = generator.getException();
			if (ex != null) {
			    throw ex;
			}
		} catch (Exception e) {
			XException.throwException(e);
		}

		Map<String, ExpressionRule> expressRules = this.getAllExpressionRules();
		for (ExpressionRule expressRule : expressRules.values()) {
			try {
				Method method = this.getRuleExpressionClass().getMethod(expressRule.getMethodName(), XBaseType.toPrototypes(expressRule.getCharactType()));
				expressRule.setMethod(method);
			} catch (NoSuchMethodException | SecurityException e) {
				XException.throwException(e);
			}
		}

		Map<String, EvaluationRule> evaluationRules = this.getAllEvaluationRules();
		for (EvaluationRule evaluationRule : evaluationRules.values()) {
			try {
				Method method = this.getRuleExpressionClass().getMethod(evaluationRule.getMethodName(), XBaseType.toPrototypes(evaluationRule.getCharactType()));
				evaluationRule.setMethod(method);
			} catch (NoSuchMethodException | SecurityException e) {
				XException.throwException(e);
			}
		}

		Map<String, DriverTableRule> driverTableRules = this.getAllDriverTableRules();
		for (DriverTableRule driverTableRule : driverTableRules.values()) {
			try {
				Method method = this.getRuleExpressionClass().getMethod(driverTableRule.getMethodName(), XBaseType.toPrototypes(driverTableRule.getCharactType()));
				driverTableRule.setMethod(method);
			} catch (NoSuchMethodException | SecurityException e) {
				XException.throwException(e);
			}
		}

		Map<String, CalSelection> odependencies = this.getAllOdExpressions();
		for (CalSelection dependency : odependencies.values()) {
			try {
				Method method = this.getRuleExpressionClass().getMethod(dependency.getMethodName(), new Class[0]);
				dependency.setMethod(method);
			} catch (NoSuchMethodException | SecurityException e) {
				XException.throwException(e);
			}
		}
	}

	/**
	 * 在Emulator环境初始化时进行调用，用了分析初始参数/参数值的可用性，分析的约束条件如下：
	 * a. CaseTable
	 * b. PreCondition
	 * c. CaseCondition
	 */
	public void preliminaryAnalysis() {
		Set<CalCharacter> recurseSet = new HashSet<>(); //存放有变动的CalOption，例如：参数状态变化、参数值状态变化

		logger.trace("Clean CV:******************************************************************************************************************");
		boolean isFirst = true; //标识是否是初次分析
		while (isFirst || !recurseSet.isEmpty()) {
			isFirst = false;
			Collection<Object> relatedRuleSet = new HashSet<Object>(); //CalTable or CalCondition

			if (recurseSet.isEmpty()) {
				relatedRuleSet.addAll(this.getAllCaseTableRules().values());
				relatedRuleSet.addAll(this.getAllExpressionRules().values());
				logger.debug("Initial clean: " + this.getAllCaseTableRules().size() + " CalTables are used to match initial C/V tables.");
			} else {
				for (CalCharacter option : recurseSet) {
					for (AbstractRule calRule : option.getRelatedRule()) {
						if (calRule != null) {
							relatedRuleSet.add(calRule);
						}
					}
				}
				recurseSet.clear();
				logger.debug("Repeated clean: " + relatedRuleSet.size() + " CalTables are used to match initial C/V tables.");
			}

			for (Object rule : relatedRuleSet) {
				if (rule instanceof CaseTableRule) {
					CaseTableRule tableRule = (CaseTableRule) rule;
					Set<CalCharacter> changeSet = tableRule.performRule();
					if (!changeSet.isEmpty()) {
						recurseSet.addAll(changeSet);
					}
				} else if (rule instanceof ExpressionRule) {
				} else if (rule instanceof PreConditionRule) {
				} else if (rule instanceof EquivalentRule) {
				}
			}
		}

	}

	public void initializeSimulator(Simulator simulator) {
		logger.debug("AbstractSimulator initialization.");
		/**
		 * Simulator初始化时，从全局的CalOption的Mapping中复制一份CalParam为当前Simulator所用， 避免当前Simulator的
		 * 模拟选配影响到全局的CalOption，这样一个Emulator就可以为多个Simulator使用
		 */
		Map<String, CalParam> validParams = simulator.getCalParameters();
		for (Entry<String, CalCharacter> entry : this.getAllOptionChoices().entrySet()) {
			CalCharacter calOption = entry.getValue();
			CalParam calParam = validParams.get(calOption.getUUID());
			if (calParam == null) {
				calParam = new CalParam(entry.getValue());
				calParam.setSimulator(simulator);
				validParams.put(entry.getKey(), calParam);
			}
			calParam.setRequired(calParam.getCalCharact().isRequired());
			calParam.setDisplay(calParam.getCalCharact().isDisplay());
			calParam.setReadonly(calParam.getCalCharact().isReadonly());
			calParam.enableAllChoice();
			calParam.clearValue();
		}

		/**
		 * 处理驱动表：条件是true，为参数赋固定的值的驱动表
		 */
		for (DriverTableRule driverRule : this.getAllDriverTableRules().values()) {
			Set<CalParam> changeSet = driverRule.performRule(validParams, null);
			for (CalParam chgParam : changeSet) {
				if (chgParam.hasValue()) {
					this.simulateConfiguration(simulator, chgParam);
				}
			}
		}
		
		for (ExpressionRule expressRule : this.getAllExpressionRules().values()) {
			Set<CalParam> changeSet = expressRule.performRule(validParams, null);
			for (CalParam chgParam : changeSet) {
				if (chgParam.hasValue()) {
					this.simulateConfiguration(simulator, chgParam);
				}
			}
		}

		/**
		 * 处理已经设置了常量值的参数
		 */
		for (CalParam param : validParams.values()) {
			Object setting = param.getConstValue();
			if (setting != null) {
				simulator.setInputOption(param.getUUID(), CalAssign.toCalAssign(param, setting), Source.CONFIG);
			}
		}

		/**
		 * 处理参数的默认值
		 */
		for (CalParam param : validParams.values()) {
			String defaultValue = param.getDefaultValue();
			if (!isBlank(defaultValue) && param.isEnabledOption(defaultValue)) {
				simulator.setInputOption(param.getUUID(), CalAssign.toCalAssign(param, defaultValue), Source.DEFAULT);
			}
		}

		/**
		 * 处理对等关系：条件是true，为参数赋固定的值的对等关系
		 */
		for (EquivalentRule equivRule : this.getAllEquivalentRules().values()) {
			for (CalParam param : validParams.values()) {
				if (!param.hasValue())
					continue;

				if (!equivRule.getCalCharact().contains(param.getCalCharact()))
					continue;

				Set<CalParam> changeSet = equivRule.performRule(validParams, param);
				for (CalParam chgParam : changeSet) {
					if (chgParam.hasValue()) {
						this.simulateConfiguration(simulator, chgParam);
					}
				}
			}
		}

		this.calculateEmulatorStatus(simulator);
		this.calculateCharactStatus(simulator);
	}
}
