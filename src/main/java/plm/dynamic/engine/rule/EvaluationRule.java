package plm.dynamic.engine.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import plm.dynamic.OptionMode;
import plm.dynamic.engine.cvm.AbstractRule;
import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalParam;
import com.flame.util.XException;

public class EvaluationRule extends AbstractRule {
	private static final long serialVersionUID = 1L;
	private String methodName = "";
	private Method method = null;
	private CalCharacter calOption = null;

	public EvaluationRule(Emulator emulator, String name, String expression, CalCharacter calOption) {
		this.name = name;
		this.detailName = emulator.getNumber() + ":" + name;
		this.methodName = "exec" + emulator.getNumber().replace("-", "") + "_" + name.replace("-", "") + "_" + this.getUUID().replace("-", "_");
		this.expression = expression;
		this.calOption = calOption;
		this.resultType = this.calOption.getBaseType().getPrototype();
	}

	public String getMethodName() {
		return this.methodName;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	@Override
	public Set<CalParam> performRule(Map<String, CalParam> metabolic, CalParam inParam) {
		Set<CalParam> recurseSet = new HashSet<>();

		int i = 0;
		CalParam[] paramArray = new CalParam[this.getCharactMap().size()];
		Map<String, Object> argsMap = new LinkedHashMap<>();
		for (CalCharacter option : this.getCharactMap().values()) {
			CalParam param = metabolic.get(option.getUUID());
			paramArray[i] = param;
			if (param.hasValue()) {
				argsMap.put(param.getVariableName(), param.getValue());
			} else {
				return recurseSet;
			}

			i = i + 1;
		}

		try {
			Object value = method.invoke(null, argsMap.values().toArray());
			if (value != null && !"".equals(value)) {
				CalParam calParam = metabolic.get(this.calOption.getUUID());
				if (calParam.hasValue()) {
					if (!value.equals(calParam.getValue())) {
						throw new XException("EvaluationRule:" + this.getDetailName() + "驱出" + calParam.getDetailName() + "=" + value + ", 但是" + this.showParamInfo(calParam));
					}
				} else {
					if (OptionMode.LIST.equals(calParam.getOptionMode())) {
						if (calParam.hasEnabledChoice(value)) {
							calParam.setValue(value).setSource(CalParam.Source.DRIVEN);
							recurseSet.add(calParam);
						} else {
							throw new XException("EvaluationRule:" + this.getDetailName() + "驱出" + calParam.getDetailName() + "=" + value + ", 但是该值已被禁用!");
						}
					} else {
						calParam.setValue(value).setSource(CalParam.Source.DRIVEN);
						recurseSet.add(calParam);
					}
				}
			}

		} catch (InvocationTargetException e) {
			Throwable throwable = e.getTargetException();
			StringBuilder messager = new StringBuilder("EvaluationRule:" + this.getDetailName() + "执行错误:" + throwable.getClass().getName() + ":" + throwable.getMessage());
			messager.append("\n").append("Expression: ").append(this.getExpression());
			throw new XException(messager.toString());
		} catch (Exception e) {
			StringBuilder messager = new StringBuilder("EvaluationRule:" + this.getDetailName() + "执行错误:" + e.getClass().getName() + ":" + e.getMessage());
			messager.append("\n").append("Expression: ").append(this.getExpression());
			throw new XException(messager.toString());
		}

		return recurseSet;
	}
}
