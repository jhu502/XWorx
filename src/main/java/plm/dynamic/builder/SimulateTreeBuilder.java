package plm.dynamic.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.thing.IThingModel;
import com.flame.thing.ThingModelHelper;
import com.flame.xui.ArrayComponent;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import com.flame.xui.service.TreeComponentNode;
import com.flame.xui.widget.IconBox;
import com.flame.xui.widget.TextDisplay;

import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.cvm.XWorxSimulator;
import plm.dynamic.service.FlameSession;
import plm.dynamic.service.FlameSessionFactory;
import plm.part.XPart;

@UITreeGrid(idField = "rowId", treeField = "identity", sortName = "identity", sortOrder = "asc", rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "rowId", hidden = true), //
				@UIColumn(field = "identity", width = "240", align = "left", order = "asc", sortable = true), //
				@UIColumn(field = "quantity", width = "40", align = "center") //
		} //
)
public class SimulateTreeBuilder extends AbstractTreeComponentBuilder {
	private static final String IDENTITY = "identity";
	private static final String QUANTITY = "quantity";
	private FlameSession xworxSession;

	@Override
	public Object buildComponentData(XCommandBean commandBean) {
		XPart primary = (XPart) commandBean.getPrimaryObj();
		if (primary != null) {
			xworxSession = FlameSessionFactory.generateSimulatorSession(primary, null);
		}
		return super.buildComponentData(commandBean);
	}

	@Override
	public List<?> getRootNode(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		if (this.xworxSession == null)
			return result;

		IThingModel thingModel = ThingModelHelper.manager().getThingModel(XPart.class);

		XWorxSimulator simulator = xworxSession.getSimulator();
		Emulator emulator = simulator.getEmulator();
		TreeComponentNode rowNode = TreeComponentNode.newTreeComponentNode();
		rowNode.setRowId(this.assemRowId(xworxSession.getSessionID(), simulator.getUUID(), emulator.getUUID()));
		rowNode.addAttribute(IDENTITY, new ArrayComponent(new IconBox(thingModel.getIcon()), new TextDisplay(emulator.getDetailName())));
		rowNode.addAttribute(QUANTITY, emulator.getQuantity());
		result.add(rowNode);

		for (Emulator _emulator : emulator.getSubLayerEmulators()) {
			TreeComponentNode childNode = TreeComponentNode.newTreeComponentNode();
			childNode.setRowId(this.assemRowId(xworxSession.getSessionID(), simulator.getUUID(), _emulator.getUUID()));
			childNode.addAttribute(IDENTITY, new ArrayComponent(new IconBox(thingModel.getIcon()), new TextDisplay(_emulator.getDetailName())));
			childNode.addAttribute(QUANTITY, _emulator.getQuantity());
			rowNode.addChildren(childNode);
		}
		return result;
	}

	@Override
	public List<?> getNode(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		XUIRowId uiRowId = commandBean.getRowId();
		if (uiRowId == null)
			return result;

		String[] keys = uiRowId.getObjectIds();
		String sessionId = keys[0];
		String simulatorId = keys[1];
		String emulatorId = keys[2];

		this.xworxSession = FlameSessionFactory.getSimulatorSession(sessionId);
		XWorxSimulator simulator = xworxSession.getSimulator().getSimulator(simulatorId);
		Emulator emulator = simulator.getEmulator(emulatorId);

		IThingModel thingModel = ThingModelHelper.manager().getThingModel(XPart.class);
		for (Emulator _emulator : emulator.getSubLayerEmulators()) {
			TreeComponentNode childNode = TreeComponentNode.newTreeComponentNode();
			childNode.setRowId(this.assemRowId(xworxSession.getSessionID(), simulator.getUUID(), _emulator.getUUID()));
			childNode.addAttribute(IDENTITY, new ArrayComponent(new IconBox(thingModel.getIcon()), new TextDisplay(_emulator.getDetailName())));
			childNode.addAttribute(QUANTITY, _emulator.getQuantity());
			result.add(childNode);
		}

		return result;
	}

}
