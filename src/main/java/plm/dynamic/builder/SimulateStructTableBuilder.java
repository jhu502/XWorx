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

@UIDataGrid(idField = "rowId", actionModel = "XSimulator:CharactToolbar", rowNumber = true, pagination = false, singleSelect = true, fit = true, //
		columns = { //
				@UIColumn(field = "rowId", hidden = true), //
				@UIColumn(field = "icon", width = "20"), //
				@UIColumn(field = "number", formatter = "displayStyle", width = "100"), //
				@UIColumn(field = "name", formatter = "displayStyle", width = "200"), //align = "center",
				@UIColumn(field = "baseType", formatter = "displayStyle", width = "40"), //
				@UIColumn(field = "source", align = "center", width = "40"), //
				@UIColumn(field = "value", formatter = "showTooltip", width = "230"), //
				@UIColumn(field = "prompt", hidden = true), //
				@UIColumn(field = "required", hidden = true), //
				@UIColumn(field = "readonly", hidden = true), //
				@UIColumn(field = "inputuri", hidden = true) //
		} //
)
public class SimulateStructTableBuilder extends AbstractTableComponentBuilder {
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
			TableComponentRow tableRow = TableComponentRow.newInstance();
			if (calParam.isRequired()) {
				tableRow.addAttribute("icon", new IconBox("images/required_star.png"));
			}
			/**
			 * SessionId ~ SimulatorId ~ EmulatorId ~ ParameterId
			 */
			tableRow.setRowId(uiRowId + "~" + calParam.getUUID());
			tableRow.addAttribute("number", calParam.getName());
			tableRow.addAttribute("name", calParam.getDisplayName());
			tableRow.addAttribute("baseType", calParam.getBaseType().getDisplay());
			tableRow.addAttribute("source", calParam.getSource().toDisplay());
			tableRow.addAttribute("value", calParam.hasValue() ? calParam.getValue() : "");

			if (OptionMode.LIST.equals(calParam.getOptionMode())) {
				tableRow.addAttribute(INPUT_URI, "thymeleaf/part/dynamic/simulate/singleSelectParam.html");
			} else if (OptionMode.NONE.equals(calParam.getOptionMode())) {
				tableRow.addAttribute(INPUT_URI, "thymeleaf/part/dynamic/simulate/inputParamValue.html");
			} else {
				tableRow.addAttribute(INPUT_URI, "thymeleaf/part/dynamic/simulate/defaultDisplay.html");
			}
			result.add(tableRow);
		}

		return result;
	}

}
