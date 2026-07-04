package plm.dynamic.builder;

import java.util.ArrayList;
import java.util.List;

import plm.dynamic.OptionMode;
import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.cvm.XWorxSimulator;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.service.FlameSession;
import plm.dynamic.service.FlameSessionFactory;
import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.widget.IconBox;
import com.flame.xui.service.TableComponentRow;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.type.XBaseType;

/**
 * 模拟选配界面中显示被选择的参数及其选中的值
 * 
 * @author hujin
 *
 */
@UIDataGrid(idField = "rowId", actionModel = "XSimulator:CharactToolbar", rowNumber = true, pagination = false, singleSelect = true, fit = true, //
		columns = { //
				@UIColumn(field = "rowId", hidden = true), //
				@UIColumn(field = "icon", width = "20", expander = true, frozen = true), //
				@UIColumn(field = "number", width = "200"), //
				@UIColumn(field = "name", formatter = "displayStyle", width = "300"), //align = "center",
				@UIColumn(field = "baseType", formatter = "displayStyle", width = "40"), //
				@UIColumn(field = "source", align = "center", width = "40"), //
				@UIColumn(field = "value", formatter = "showTooltip", width = "600"), //
				@UIColumn(field = "optionMode", hidden = true), //
				@UIColumn(field = "prompt", hidden = true), //
				@UIColumn(field = "required", hidden = true), //
				@UIColumn(field = "readonly", hidden = true), //
				@UIColumn(field = "inputuri", hidden = true) //
		} //
)
public class SimulateCharactTableBuilder extends AbstractTableComponentBuilder {
	private static final String INPUT_URI = "inputuri";

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		XUIRowId uiRowId = commandBean.getRowId();
		if (uiRowId == null)
			return result;

		FlameSession.XParamValBean valBean = FlameSession.convertRowId2XGUID(uiRowId.getValue());
		FlameSession xworxSession = FlameSessionFactory.getSimulatorSession(valBean.getSessionId());
		XWorxSimulator simulator = xworxSession.getSimulator().getSimulator(valBean.getSimulatorId());
		Emulator emulator = simulator.getEmulator(valBean.getEmulatorId());
		for (CalCharacter charact : emulator.getCalCharacts()) {
			CalParam calParam = simulator.getCalParameter(charact.getUUID());
			if (!calParam.isDisplay())
				continue;

			TableComponentRow tableRow = TableComponentRow.newInstance();
			if (calParam.isRequired()) {
				tableRow.addAttribute("icon", new IconBox("images/required_star.png"));
			}
			tableRow.setRowId(valBean.getEmulatorGUID() + "~" + calParam.getUUID());
			tableRow.addAttribute("number", calParam.getName());
			tableRow.addAttribute("name", calParam.getDisplayName());
			tableRow.addAttribute("baseType", calParam.getBaseType().getDisplay());
			tableRow.addAttribute("source", calParam.getSource().toDisplay());
			tableRow.addAttribute("value", calParam.hasValue() ? calParam.getValue() : "");
			tableRow.addAttribute("optionMode", calParam.getOptionMode().name());
			tableRow.addAttribute("required", calParam.isRequired());
			tableRow.addAttribute("readonly", calParam.isReadonly());
			tableRow.addAttribute("prompt", calParam.getPrompt());
			if (OptionMode.DLIST.equals(calParam.getOptionMode()) && XBaseType.INFOTABLE.equals(calParam.getBaseType())) {
				tableRow.addAttribute("expander", true);
			}
			this.handleInputURI(tableRow, calParam);
			result.add(tableRow);
		}

		return result;
	}

	private void handleInputURI(TableComponentRow tableRow, CalParam calParam) {

		if (OptionMode.LIST.equals(calParam.getOptionMode())) {
			tableRow.addAttribute(INPUT_URI, "thymeleaf/part/dynamic/simulate/singleSelectParam.html");
		} else if (OptionMode.DLIST.equals(calParam.getOptionMode())) {
			if (XBaseType.INFOTABLE.equals(calParam.getBaseType())) {
				tableRow.addAttribute(INPUT_URI, "thymeleaf/part/dynamic/simulate/infoTableDListParam.html");
			} else {
				tableRow.addAttribute(INPUT_URI, "thymeleaf/part/dynamic/simulate/singleSelectParam.html");
			}
		} else if (OptionMode.NONE.equals(calParam.getOptionMode())) {
			tableRow.addAttribute(INPUT_URI, "thymeleaf/part/dynamic/simulate/inputParamValue.html");
		} else {
			tableRow.addAttribute(INPUT_URI, "thymeleaf/part/dynamic/simulate/defaultDisplay.html");
		}
	}
}
