package plm.dynamic.form;

import com.flame.xui.XCommandBean;
import com.flame.xui.IWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UICell;
import com.flame.annotations.UIEvent;
import com.flame.annotations.UIGrid;
import com.flame.annotations.UIMeshGrid;
import com.flame.annotations.UIRow;
import com.flame.annotations.UIWidget;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.widget.Hidden;
import com.flame.xui.widget.Label;
import com.flame.xui.widget.TextBox;
import com.flame.xui.XUIMeshGrid;

import plm.dynamic.engine.cvm.XWorxSimulator;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.service.FlameSession;
import plm.dynamic.service.FlameSession.XParamValBean;
import plm.dynamic.service.FlameSessionFactory;

@UIMeshGrid(grids = {
		@UIGrid(rows = { //
				@UIRow(cells = { //
						@UICell(widget = { //
								@UIWidget(type = WidgetType.Hidden, id = "xworxParamGuid", name = "xworxParamGuid"), //
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(widget = { //
								@UIWidget(type = WidgetType.LinkButton, traits = "iconCls:'icon-reload'", text = "Submit", style = "margin-left:10px;width:80px", events = {@UIEvent(name = "onclick", value = "flame.submitForm(this, refreshSimulateBuilder);")})//
						}), //
				})//
		})
})
public class InputInfoTableForm extends AbstractMeshComponentBuilder {
	@Override
	public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
		XUIMeshGrid xMeshGrid = super.buildComponentConfig(commandBean);
		String rowId = commandBean.getTextParameter("rowId"); //SessionId ~ SimulatorId ~ EmulatorId ~ ParameterId
		if (rowId != null) {
			IWidget guiWidget = xMeshGrid.getXUIWidget("xworxParamGuid");
			if (guiWidget instanceof Hidden) {
				Hidden hidden = (Hidden) guiWidget;
				hidden.setValue(commandBean.getTextParameter("rowId"));
			}

			XParamValBean valBean = FlameSession.convertRowId2XGUID(rowId);
			FlameSession xworxSession = FlameSessionFactory.getSimulatorSession(valBean.getSessionId());
			XWorxSimulator simulator = xworxSession.getSimulator().getSimulator(valBean.getSimulatorId());
			CalParam calparam = simulator.getCalParameter(valBean.getParameterId());

			int index = 1;
			CalCharacter calCharact = calparam.getCalCharact();
			for (CalCharacter charact : calCharact.getChildCharacts()) {
				XUIMeshGrid.XUIRow xRow = new XUIMeshGrid.XUIRow();
				XUIMeshGrid.XUICell uiCell = new XUIMeshGrid.XUICell();

				Label label = new Label("label_" + charact.getName());
				label.setText(charact.getDisplayName() + ":");
				label.setStyle("width:70px;height:25px");
				TextBox textBox = new TextBox(charact.getName());
				textBox.setId("charact" + charact.getName());
				textBox.setTraits("editable:true,multiline:true");
				textBox.setStyle("width:300px;height:40px");
				CalParam childParam = simulator.getCalParameter(charact.getUUID());
				if (childParam != null) {
				}
				xMeshGrid.getWidgetMap().put(textBox.getId(), textBox);
				uiCell.addWidget(label);
				uiCell.addWidget(textBox);
				xRow.addCell(uiCell);
				XUIMeshGrid.XUIGrid xGrid = xMeshGrid.getGrids(0);
				xGrid.addRow(index++, xRow);
			}
		}
		return xMeshGrid;
	}
}
