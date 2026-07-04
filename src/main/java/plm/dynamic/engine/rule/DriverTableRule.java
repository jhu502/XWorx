package plm.dynamic.engine.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plm.dynamic.OptionMode;
import plm.dynamic.engine.cvm.AbstractRule;
import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.exprs.RuleExpressionUtility;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.engine.type.DriverTable;
import com.flame.type.XBaseType;
import com.flame.util.XException;

public class DriverTableRule extends AbstractRule {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(DriverTableRule.class);
	private String methodName = "";
	private Method method = null;
	protected DriverTable driverTable;
	private Map<String, CalCharacter> outOptions = new HashMap<String, CalCharacter>();

	public DriverTableRule(Emulator emulator, DriverTable driverTable) {
		this.resultType = Map.class;
		this.name = driverTable.getNumber();
		this.detailName = emulator.getNumber() + ":" + this.name;
		this.methodName = "exec" + emulator.getNumber().replaceAll("-", "") + "_" + this.name.replaceAll("-", "") + "_" + this.getUUID().replaceAll("-", "_");

		for (String name : driverTable.getInput()) {
			CalCharacter calOption = emulator.getCalCharact(name);
			if (calOption != null) {
				this.addCalCharact(calOption);
			}
		}
		for (String name : driverTable.getOutput()) {
			CalCharacter calOption = emulator.getCalCharact(name);
			if (calOption != null) {
				this.addOutCalOption(calOption);
			}
		}

		StringBuilder methodBody = new StringBuilder("java.util.Map result = new java.util.HashMap();");

		boolean isFirstRow = true;
		for (DriverTable.Rule rule : driverTable.getRows()) {
			if (isFirstRow) {
				isFirstRow = false;
				RuleExpressionUtility utility = RuleExpressionUtility.parserExpressionRule(emulator, rule.getCondition());
				methodBody.append("if (").append(utility.getExpression()).append(") {");
			} else {
				RuleExpressionUtility utility = RuleExpressionUtility.parserExpressionRule(emulator, rule.getCondition());
				methodBody.append(" else if (").append(utility.getExpression()).append(") {");
			}
			for (DriverTable.Driver driver : rule.getOutput()) {
				CalCharacter caloption = emulator.getCalCharact(driver.getParameter());
				String vname = caloption.getVariableName();
				if ("value".equalsIgnoreCase(driver.getType())) {
					if (XBaseType.STRING.equals(caloption.getBaseType())) {
						methodBody.append("result.put(\"").append(caloption.getUUID()).append("\", \"").append(driver.getExpression()).append("\");");
					} else if (XBaseType.BOOLEAN.equals(caloption.getBaseType())) {
						methodBody.append("result.put(\"").append(caloption.getUUID()).append("\", toBoolean(" + driver.getExpression() + "));");
					} else if (XBaseType.NUMBER.equals(caloption.getBaseType())) {
						methodBody.append("result.put(\"").append(caloption.getUUID()).append("\", toDouble(" + driver.getExpression() + "));");
					} else if (XBaseType.LONG.equals(caloption.getBaseType())) {
						methodBody.append("result.put(\"").append(caloption.getUUID()).append("\", toLong(" + driver.getExpression() + "));");
					} else {
						methodBody.append("result.put(\"").append(caloption.getUUID()).append("\", ").append(driver.getExpression()).append(");");
					}
				} else {
					RuleExpressionUtility _utility = RuleExpressionUtility.parserExpressionRule(emulator, driver.getExpression());
					if (XBaseType.BOOLEAN.equals(caloption.getBaseType())) {
						methodBody.append("boolean " + vname + "_bool = ").append(_utility.getExpression()).append(";");
						methodBody.append("result.put(\"").append(caloption.getUUID()).append("\", toBoolean(" + _utility.getExpression() + "));");
					} else if (XBaseType.NUMBER.equals(caloption.getBaseType())) {
						methodBody.append("result.put(\"").append(caloption.getUUID()).append("\", toDouble(" + _utility.getExpression() + "));");
					} else if (XBaseType.LONG.equals(caloption.getBaseType())) {
						methodBody.append("result.put(\"").append(caloption.getUUID()).append("\", toLong(" + _utility.getExpression() + "));");
					} else {
						methodBody.append("result.put(\"").append(caloption.getUUID()).append("\", ").append(_utility.getExpression()).append(");");
					}
				}

			}
			methodBody.append("}");
		}

		methodBody.append("return result;");
		logger.debug(this.detailName + ": " + methodBody.toString());
		this.sentence = methodBody.toString();
	}

	public String getMethodName() {
		return this.methodName;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public void setDriverTable(DriverTable driverTable) {
		this.driverTable = driverTable;
	}

	public void addOutCalOption(CalCharacter calOption) {
		this.outOptions.put(calOption.getVariableName(), calOption);
	}

	public Collection<CalCharacter> getOutCalOption() {
		return this.outOptions.values();
	}

	@Override
	public Set<CalParam> performRule(Map<String, CalParam> metabolic, CalParam inParam) {
		Set<CalParam> recurseSet = new HashSet<>();

		Map<String, Object> argsMap = new LinkedHashMap<>();
		for (CalCharacter option : this.getCharactMap().values()) {
			CalParam param = metabolic.get(option.getUUID());
			if (!param.hasValue()) {
				return recurseSet;
			}

			argsMap.put(param.getVariableName(), param.getValue());
		}

		try {
			Map<?, ?> mresult = (Map<?, ?>) method.invoke(null, argsMap.values().toArray());
			for (Entry<?, ?> entry : mresult.entrySet()) {
				String uuid = entry.getKey().toString();
				Object value = entry.getValue();
				if (value != null && !"".equals(value)) {
					CalParam calParam = metabolic.get(uuid);
					if (calParam == null) {
						throw new XException("UUID: " + uuid + " 无对应的参数!");
					} else {
						if (calParam.hasValue()) {
							if (!value.equals(calParam.getValue())) {
								throw new XException("D101~驱动表:“" + this.getName() + "”驱出参数:“" + calParam.getDisplayName() + "=" + value + "”, 但是" + this.showParamInfo(calParam));
							}
						} else {
							if (OptionMode.LIST.equals(calParam.getOptionMode())) {
								if (calParam.hasEnabledChoice(value)) {
									calParam.setValue(value).setSource(CalParam.Source.DRIVEN);
									recurseSet.add(calParam);
								} else {
									if (calParam.hasChoice(value)) {
										throw new XException("D102~参数:“" + calParam.getDetailName() + "”中被禁用驱动表:" + this.getName() + "驱出的值:“" + value + "”在.");
									} else {
										throw new XException("D103~驱动表:“" + this.getName() + "”驱出的值:“" + value + "”在参数:“" + calParam.getDetailName() + "”中不存在.");
									}
								}
							} else {
								calParam.setValue(value).setSource(CalParam.Source.DRIVEN);
								recurseSet.add(calParam);
							}
						}
					}
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			XException.throwException(e);
		}

		return recurseSet;
	}
}
