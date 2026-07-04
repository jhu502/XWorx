package plm.dynamic.builder;

import java.util.ArrayList;
import java.util.List;

import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.cvm.XWorxSimulator;
import plm.dynamic.engine.mdb.CalCell;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.engine.mdb.CalRow;
import plm.dynamic.engine.rule.UsageLinkRule;
import plm.dynamic.service.FlameSession;
import plm.dynamic.service.FlameSessionFactory;
import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.XUIComponent;
import com.flame.xui.XUIRowId;
import com.flame.xui.XUIDataGrid;
import com.flame.xui.widget.GridColumn;

/**
 * 模拟选配界面InfoTable DList参数值的表格显示方式，用来显示动态合成的参数值
 * 
 * @author hujin
 *
 */
@UIDataGrid(idField = "rowId", rowNumber = false, singleSelect = false, fit = true, fitColumns = true, //
		columns = { //
				@UIColumn(field = "rowId", checkbox = true), //
		} //
)
public class SimulateDListTableBuilder extends AbstractTableComponentBuilder {
	@Override
	public XUIComponent buildComponentConfig(XCommandBean commandBean) {
		XUIComponent methodConfig = super.buildComponentConfig(commandBean);
		if (methodConfig instanceof XUIDataGrid) {
			XUIDataGrid datagrid = (XUIDataGrid) methodConfig;
			XUIRowId uiRowId = commandBean.getRowId();
			if (uiRowId == null)
				return methodConfig;

			FlameSession.XParamValBean valBean = FlameSession.convertRowId2XGUID(uiRowId.getValue());
			FlameSession xworxSession = FlameSessionFactory.getSimulatorSession(valBean.getSessionId());
			XWorxSimulator simulator = xworxSession.getSimulator().getSimulator(valBean.getSimulatorId());
			Emulator emulator = simulator.getEmulator(valBean.getEmulatorId());
			UsageLinkRule usageRule = emulator.getUsageRule();
			for (CalCharacter charact : usageRule.getColumns()) {
				CalParam calParam = simulator.getCalParameter(charact.getUUID());
				GridColumn column = new GridColumn();
				column.setField(calParam.getName());
				column.setTitle(calParam.getDisplayName());
				datagrid.addColumn(column);
			}
		}
		return methodConfig;
	}

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		XUIRowId uiRowId = commandBean.getRowId();
		if (uiRowId == null)
			return result;

		FlameSession.XParamValBean valBean = FlameSession.convertRowId2XGUID(uiRowId.getValue());
		FlameSession xworxSession = FlameSessionFactory.getSimulatorSession(valBean.getSessionId());
		Emulator emulator = xworxSession.getSimulator().getEmulator(valBean.getEmulatorId());
		XWorxSimulator simulator = xworxSession.getSimulator().getSimulator(valBean.getSimulatorId());
		CalParam parameter = simulator.getCalParameter(valBean.getParameterId());
		if (parameter == null)
			return result;

		UsageLinkRule usageRule = emulator.getUsageRule();
		int colNum = usageRule.getColumns().length;
		for (CalRow row : usageRule.getRows()) {
			TableComponentRow tableRow = TableComponentRow.newInstance();
			boolean bool = true;
			for (int i = 0; i < colNum; i++) {
				CalCell cell = row.getCell(i);
				CalCharacter column = usageRule.getColumns()[i];
				CalParam calparam = simulator.getCalParameter(column.getUUID());
				if (!calparam.hasEnabledChoice(cell.getValue())) {
					bool = false;
					break;
				}
				tableRow.addAttribute(column.getName(), cell.getValue());
			}
			if (bool)
				result.add(tableRow);
		}

		return result;
	}

}
