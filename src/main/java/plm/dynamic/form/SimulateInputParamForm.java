package plm.dynamic.form;

import java.util.HashMap;
import java.util.Map;

import com.flame.xui.XCommandBean;
import com.flame.util.XException;
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
import com.flame.xui.widget.TextBox;
import com.flame.xui.XUIMeshGrid;

import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.cvm.XWorxModel;
import plm.dynamic.engine.cvm.XWorxSimulator;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.engine.mdb.CalParam.Source;
import plm.dynamic.service.FlameSession;
import plm.dynamic.service.FlameSessionFactory;

@UIMeshGrid(grids = {
		@UIGrid(rows = { //
				@UIRow(cells = { //
						@UICell(widget = { //
								@UIWidget(type = WidgetType.Hidden, id = "xworx_param_uuid", name = "xworx_param_uuid"), //
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "输入参数值：", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "xworx_param_value", name = "xworx_param_value", traits = "width:250,editable:true,required:true"), //
								@UIWidget(type = WidgetType.LinkButton, traits = "iconCls:'icon-reload'", text = "Submit", style = "margin-left:10px;width:80px", events = {@UIEvent(name = "onclick", value = "submitInputParamValue(this)")})//
						}), //
				})//
		})
})
public class SimulateInputParamForm extends AbstractMeshComponentBuilder {
	@Override
	public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
		XUIMeshGrid formConfig = super.buildComponentConfig(commandBean);
		String rowId = commandBean.getTextParameter("rowId"); //SessionId ~ SimulatorId ~ EmulatorId ~ ParameterId
		if (rowId != null) {
			IWidget guiWidget = formConfig.getXUIWidget("xworx_param_uuid");
			if (guiWidget instanceof Hidden) {
				Hidden hidden = (Hidden) guiWidget;
				hidden.setValue(commandBean.getTextParameter("rowId"));
			}

			FlameSession.XParamValBean valBean = FlameSession.convertRowId2XGUID(rowId);
			FlameSession xworxSession = FlameSessionFactory.getSimulatorSession(valBean.getSessionId());
			XWorxSimulator simulator = xworxSession.getSimulator().getSimulator(valBean.getSimulatorId());
			CalParam calparam = simulator.getCalParameter(valBean.getParameterId());

			IWidget widget = formConfig.getXUIWidget("xworx_param_value");
			if (widget instanceof TextBox) {
				TextBox textBox = (TextBox) widget;
				if (calparam.hasValue())
					textBox.setValue(calparam.getValue().toString());
			}
		}
		return formConfig;
	}

	@Override
	public Object buildComponentData(XCommandBean commandBean) {
		String parameterUuid = commandBean.getTextParameter("xworx_param_uuid");
		String parameterValue = commandBean.getTextParameter("xworx_param_value");
		if (isBlank(parameterUuid))
			throw new XException("参数请求异常.");

		FlameSession.XParamValBean valBean = FlameSession.convertRowId2XGUID(parameterUuid);
		FlameSession xworxSession = FlameSessionFactory.getSimulatorSession(valBean.getSessionId());
		XWorxSimulator simulator = xworxSession.getSimulator().getSimulator(valBean.getSimulatorId());
		simulator.pushInputOption(valBean.getParameterId(), parameterValue, Source.INPUT);

		Map<String, XWorxModel.XParam> resultList = new HashMap<>();
		Emulator emulator = simulator.getEmulator(valBean.getEmulatorId());
		for (CalCharacter option : emulator.getCalCharacts()) {
			CalParam calParam = simulator.getCalParameter(option.getUUID());
			if (calParam.getRedraw()) {
				String key = valBean.getSessionId() + "~" + valBean.getSimulatorId() + "~" + valBean.getEmulatorId() + "~" + calParam.getUUID();
				resultList.put(key, XWorxModel.XParam.newXParam(calParam));
			}
		}

		return resultList;
	}
}
