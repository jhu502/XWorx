package plm.dynamic.engine.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.util.SetUtils;

import plm.dynamic.OptionMode;
import plm.dynamic.engine.cvm.AbstractRule;
import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalParam;
import com.flame.util.XException;

public class ExpressionRule extends AbstractRule {
	private static final long serialVersionUID = 1L;
	private String methodName = "";
	private Method method = null;
	private String description = "";

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExpressionRule(Emulator emulator, String name, String constraint) {
		this.name = name;
		this.detailName = emulator.getNumber() + ":" + name;
		this.methodName = "exec" + emulator.getNumber().replace("-", "") + "_" + this.name.replace("-", "") + "_" + this.getUUID().replace("-", "_");
		this.expression = constraint;
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

        boolean nonList = false;
        @SuppressWarnings("unchecked")
        Set<Object>[] inSetArray = new HashSet[this.getCharactMap().size()];
        CalParam[] paramArray = new CalParam[this.getCharactMap().size()];

        Map<String, Object> argsMap = new LinkedHashMap<>();
        Set<CalParam> noValueSet = new HashSet<>();

        int i = 0;
        for (CalCharacter option : this.getCharactMap().values()) {
            CalParam param = metabolic.get(option.getUUID());
            paramArray[i] = param;
            if (param.hasValue()) {
                argsMap.put(param.getVariableName(), param.getValue());
                inSetArray[i] = new HashSet<>(SetUtils.singletonSet(param.getAssign().value()));
            } else {
                if (OptionMode.LIST.equals(param.getOptionMode())) {
                    argsMap.put(param.getVariableName(), null);
                    noValueSet.add(param);

                    inSetArray[i] = new HashSet<>(param.getEnabledChoiceVals());
                } else {
                    nonList = true;
                }
                if (nonList) {
                    return recurseSet;
                }
            }

            i = i + 1;
        }

        if (noValueSet.isEmpty()) {
            try {
                boolean bool = (boolean) method.invoke(null, argsMap.values().toArray());

                if (!bool) {
                    if (inParam == null) {
                        throw new XException("M121", "加载初始化时，违反约束:“" + this.getDetailName() + "”\n" + this.getExpression());
                    } else if (OptionMode.DLIST.equals(inParam.getOptionMode())) {
                        throw new XException("M122", "当提交“" + inParam.getDetailName() + "”时，违反约束:“" + this.getDetailName() + "”\n" + this.getExpression());
                    } else {
                        throw new XException("M123", "当提交" + this.showParamInfo(inParam) + "时，违反约束:“" + this.getDetailName() + "”\n" + this.getExpression());
                    }

                }
            } catch (Exception e) {
                throw new XException(e.getMessage(), e);
            }
        } else {
            long calcAmount = this.estimateAmount(noValueSet);

            if (calcAmount < 10000) {
                @SuppressWarnings("unchecked")
                Set<Object>[] outSet = new HashSet[this.getCharactMap().size()];
                Object[] param = new Object[this.getCharactMap().size()];
                this._borderValueCaseCondition(inSetArray, method, 0, param, outSet);

                for (int j = 0; j < outSet.length; j++) {
                    Set<Object> enabled = outSet[j];
                    if (enabled == null || enabled.isEmpty()) {
                        if (inParam == null) {
                            throw new XException("E101~加载初始化时, 违反表达式约束:“" + this.getDetailName() + "”\n" + this.getExpression());
                        } else if (OptionMode.DLIST.equals(inParam.getOptionMode())) {
                            throw new XException("E102~设置“" + inParam.getDetailName() + "”时, 违反表达式约束:“" + this.getDetailName() + "”\n" + this.getExpression());
                        } else {
                            throw new XException("E103~设置" + this.showParamInfo(inParam) + "时, 违反表达式约束:“" + this.getDetailName() + "”\n" + this.getExpression());
                        }
                    } else {
                        for (Object value : paramArray[j].getEnabledChoiceVals().toArray()) {
                            if (OptionMode.LIST.equals(paramArray[j].getOptionMode()) && !enabled.contains(value)) {
                                String prompt = "E103~" + inParam == null ? "加载初始化" : "设置" + this.showParamInfo(inParam) + "时, 违反表达式约束:“" + this.getDetailName() + "”\n" + this.getExpression();
                                paramArray[j].disableChoice(value, prompt);
                                recurseSet.add(paramArray[j]);
                            }
                        }
                    }
                }
            }
        }

        return recurseSet;
    }

	private void _borderValueCaseCondition(Set<Object>[] inSet, Method method, int layer, Object[] param, Set<Object>[] out) {
		Set<Object> values = inSet[layer];

		int nlayer = layer + 1;
		for (Object value : values) {
			param[layer] = value;
			if (inSet.length > nlayer) {
				_borderValueCaseCondition(inSet, method, nlayer, param, out);
			} else {
				try {
					boolean bool = (boolean) method.invoke(null, param);
					if (bool) {
						for (int i = 0; i < param.length; i++) {
							Set<Object> set = out[i];
							if (set == null) {
								set = new HashSet<Object>();
								out[i] = set;
							}
							set.add(param[i]);
						}
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					XException.throwException(e);
				}
			}
		}
	}

	public long estimateAmount(Set<CalParam> nonValueSet) {
		long amount = 1;

		if (nonValueSet == null || nonValueSet.isEmpty()) {
			return amount;
		}

		for (CalParam param : nonValueSet) {
			amount = amount * param.getEnabledChoices().size();
		}

		return amount;
	}
}
