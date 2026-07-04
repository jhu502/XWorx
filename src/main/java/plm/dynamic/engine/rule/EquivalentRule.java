package plm.dynamic.engine.rule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import plm.dynamic.OptionMode;
import plm.dynamic.engine.cvm.AbstractRule;
import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalChoice;
import plm.dynamic.engine.mdb.CalParam;
import com.flame.util.XException;

public class EquivalentRule extends AbstractRule {
	private static final long serialVersionUID = 1L;

	public EquivalentRule(Emulator emulator, CalCharacter calOption) {
		this.name = calOption.getName();

		this.getCharactMap().put(calOption.getVariableName(), calOption);
		calOption.addRelatedRule(this);

		String referId = emulator.getReferenceId();
		if (referId == null || "".equals(referId)) {
			this.setDetailName(emulator.getNumber() + ":" + calOption.getName());
		} else {
			this.setDetailName(emulator.getNumber() + "(" + referId + "):" + calOption.getName());
		}
	}

	@Override
	public Set<CalParam> performRule(Map<String, CalParam> metabolic, CalParam inParam) {
		Set<CalParam> recurseSet = new HashSet<>();

		for (CalCharacter calOption : this.getCalCharact()) {
			String uuid = calOption.getUUID();
			CalParam calParam = metabolic.get(uuid);
			if (inParam.hasValue()) {
				if (calParam.hasValue()) {
					if (!calParam.getAssign().equals(inParam.getAssign())) {
						throw new XException("M202", "参数:“" + calParam.getDetailName() + "”和参数:“" + inParam.getDetailName() + "”违反对等约束:“" + this.getDetailName() + "”.");
					}
				} else {
					if (OptionMode.LIST.equals(calParam.getOptionMode())) {
						if (calParam.hasEnabledChoice(inParam.getAssign())) {
							calParam.setValue(inParam.getAssign()).setSource(CalParam.Source.DRIVEN);
							calParam.setPrompt("M504~当" + this.showParamInfo(inParam) + "时，对等约束:“" + this.getDetailName() + "”设置" + this.showParamInfo(calParam));
							recurseSet.add(calParam);
						} else {
							if (calParam.hasChoice(inParam.getAssign())) {
								String error = "当" + this.showParamInfo(inParam) + " 时，参数:“" + inParam.getDetailName() + "”违反对等约束;\n因" + calParam.getChoice(inParam.getAssign()).getPrompt();
								throw new XException(error);
							} else {
								String error = "当" + this.showParamInfo(inParam) + " 时，参数:“" + inParam.getDetailName() + "”违反对等约束;\n因无“" + calParam.getDetailName() + "=" + inParam.getAssign() + "”选项";
								throw new XException("M203", error);
							}
						}
					} else {
						calParam.setValue(inParam.getAssign()).setSource(CalParam.Source.DRIVEN);
						recurseSet.add(calParam);
					}
				}
			} else {
				boolean bool = false;
				if (calParam.hasValue()) {
					calParam.clearValue();
					bool = true;
				}
				for (CalChoice choice : calParam.getEnabledChoices().toArray(new CalChoice[0])) {
					if (!inParam.hasChoice(choice.value()) || !inParam.isEnabledOption(choice.value())) {
						calParam.disableChoice(choice.value(), "M505~对等约束:“" + this.getDetailName() + "”导致被禁用.");
						bool = true;
					}
				}
				if (bool) {
					recurseSet.add(calParam);
				}
			}
		}

		return recurseSet;
	}
}
