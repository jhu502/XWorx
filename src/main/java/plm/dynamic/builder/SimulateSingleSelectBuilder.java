package plm.dynamic.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import plm.dynamic.engine.cvm.XWorxSimulator;
import plm.dynamic.engine.mdb.CalChoice;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.service.FlameSession;
import plm.dynamic.service.FlameSessionFactory;
import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;

@UIDataGrid(idField = "rowId", rowNumber = true, singleSelect = true, fit = true, pagination = false, //
		columns = { //
				@UIColumn(field = "rowId", checkbox = true), //
				@UIColumn(field = "value", width = "150"), //
				@UIColumn(field = "description", formatter = "valueTooltip", width = "300"), //
		} //
)
public class SimulateSingleSelectBuilder extends AbstractTableComponentBuilder {
	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		XUIRowId uiRowId = commandBean.getRowId();
		if (uiRowId == null)
			return result;

		String[] keys = uiRowId.getObjectIds();
		String sessionId = keys[0];
		String simulatorId = keys[1];
		String parameterId = keys[3];
		FlameSession xworxSession = FlameSessionFactory.getSimulatorSession(sessionId);
		XWorxSimulator simulator = xworxSession.getSimulator().getSimulator(simulatorId);
		CalParam calParam = simulator.getCalParameter(parameterId);
		Set<CalChoice> disabledSet = new LinkedHashSet<>();
		for (CalChoice choice : calParam.getChoices()) {
			if (CalChoice.Status.ENABLED.equals(choice.getStatus())) {
				TableComponentRow tableRow = TableComponentRow.newInstance();
				tableRow.setRowId(uiRowId + "~" + choice.value());
				tableRow.addAttribute("value", choice.value());
				tableRow.addAttribute("description", choice.getDescription());
				tableRow.addAttribute("status", choice.getStatus().name());
				if (calParam.hasValue(choice.value())) {
					tableRow.addAttribute("checked", true);
				}
				result.add(tableRow);
			} else {
				disabledSet.add(choice);
			}
		}
		for (CalChoice choice : disabledSet) {
			TableComponentRow tableRow = TableComponentRow.newInstance();
			tableRow.setRowId(uiRowId + "~" + choice.value());
			tableRow.addAttribute("value", choice.value());
			tableRow.addAttribute("description", choice.getDescription());
			tableRow.addAttribute("status", choice.getStatus().name());
			result.add(tableRow);
		}
		Map<String, Object> primaryMap = new HashMap<>();
		primaryMap.put("source", calParam.getSource());
		this.setPrimaryObject(primaryMap);

		return result;
	}
}
