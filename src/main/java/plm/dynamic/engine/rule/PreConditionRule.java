package plm.dynamic.engine.rule;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.exprs.Expression;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalParam;
import com.flame.type.XBaseType;
import com.flame.util.XException;

public class PreConditionRule extends ExpressionRule {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(PreConditionRule.class);
	private String character = "";
	private String value = "";
	private Class<Expression> conditionClass;

	public PreConditionRule(Emulator emulator, String name, String constraint) {
		super(emulator, name, constraint);
	}

	public String getCharacter() {
		return this.character;
	}

	public String getValue() {
		return this.value;
	}

	public Set<CalParam> performRule(Map<String, CalParam> metabolic, CalParam parameter) {
		Set<CalParam> recurse_set = new HashSet<CalParam>();

		Class<?>[] clss = XBaseType.toPrototypes(this.getCharactType());
		String charact = this.getCharacter();
		String option = this.getValue();
		CalParam calCharact = metabolic.get(charact);

		if (calCharact == null) {
			XException.throwException("被PreCondition使用的参数:" + charact + "未被产品所引用.");
		}
		/**
		 * 对有前提条件的参数值进行状态计算
		 */
		if (calCharact != null && calCharact.isEnabledOption(option)) {
			boolean flag = true; //用了识别前提条件中使用的参数是否都选择了值
			CalCharacter[] calOptions = this.calCharacts();
			Object[] param = new Object[calOptions.length];
			for (int i = 0; i < calOptions.length; i++) {
				CalParam calParam = metabolic.get(calOptions[i].getUUID());
				if (calParam.hasValue()) {
					if (Integer.class.equals(calParam.getBaseType())) {
						param[i] = Long.parseLong(calParam.getValue().toString());
					} else {
						param[i] = calParam.getAssign();
					}
				} else {
					flag = false;
					break;
				}
			}

			if (flag) {
				try {
					Method method = this.conditionClass.getMethod("exec" + this.getName(), clss);
					boolean bool = (boolean) method.invoke(null, param);
					if (logger.isTraceEnabled()) {
						String showParam = "";
						for (Object p : param) {
							if (showParam.isEmpty())
								showParam = p.toString();
							else
								showParam = showParam + "," + p;
						}
						logger.trace("Method Name:exec" + this.getName() + "(" + showParam + ") " + charact + ":" + option + "  " + bool + "  " + calOptions.length);
					}
					if (!bool) {
						String prompt = "P101~设置“" + parameter.getDetailName() + "=" + parameter.getAssign() + "”时，参数值:" + option + " 被前提条件:“" + this.getDetailName() + "”禁用.";
						calCharact.disableChoice(option, prompt);
						recurse_set.add(calCharact);
					}
				} catch (Exception e) {
					XException.throwException(e);
				}
			}
		}

		return recurse_set;
	}
}
