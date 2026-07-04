package plm.part.builder;

import java.util.List;

import com.flame.action.XActionHelper;
import com.flame.action.ActionKey;
import com.flame.action.IAction;
import com.flame.action.IActionItem;
import com.flame.xui.*;
import com.flame.annotations.UICell;
import com.flame.annotations.UIGrid;
import com.flame.annotations.UIMeshGrid;
import com.flame.annotations.UIRow;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.widget.AppButton;

import plm.part.XPart;

import com.flame.xui.XUIMeshGrid;

@UIMeshGrid(grids = { //
		@UIGrid(provider = XPart.class, rows = { //
				@UIRow(cells = { @UICell(widget = {}) }) //
		})//
})
public class PartToolBarBoxBuilder extends AbstractMeshComponentBuilder {
	private static final String TOOLBAR_KEY = "toolbar";

	public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
		XUIMeshGrid xMeshGrid = super.buildComponentConfig(commandBean);
		String toolbarKey = commandBean.getTextParameter(TOOLBAR_KEY);
		ActionKey actionKey = ActionKey.newActionKey(toolbarKey);

		for (XUIMeshGrid.XUIGrid xGrid : xMeshGrid.getGrids()) {
			/** AppBoxToolBar不显示FieldSet */
			xGrid.setFieldSet(false);
			List<XUIMeshGrid.XUIRow> widgets = xGrid.getRows();
			IRow<?> firstRow = widgets.get(0);
			ICell<?> firstCell = firstRow.getCell(0);
			List<IAction> actionList = XActionHelper.manager().getSubActions(actionKey.getName(), actionKey.getType());
			for (IAction action : actionList) {
				AppButton appButton = new AppButton(action.getName());
				appButton.setText(action.getDisplay());
				appButton.setUrl(action.getIcon());
				firstCell.addWidget(appButton);
				if (action instanceof IActionItem) {
					IActionItem item = (IActionItem) action;
					XUIAction xAction = XUIAction.toXUIAction(item);
					String onclick = "flame.handleBarAction(" + xAction.toJSObject() + ");";
					appButton.addEvent(XUIWidget.ON_CLICK, onclick);
				}
			}
		}

		return xMeshGrid;
	}

}
