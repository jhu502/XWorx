package com.flame.localize;

import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.xui.WidgetType;
import com.flame.annotations.UICell;
import com.flame.annotations.UIGrid;
import com.flame.annotations.UIMeshGrid;
import com.flame.annotations.UIRow;
import com.flame.annotations.UIWidget;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.XUIMeshGrid;

@UIMeshGrid(grids = {
		@UIGrid(provider = ILocalization.class, rows = { //
				@UIRow(cells = { //
						@UICell(widget = { //
								@UIWidget(type = WidgetType.Hidden, id = "localeOid", name = "oid"), //
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_display", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "localeDisplay", name = "display", traits = "editable:true", style = "width:250px;height:24px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_en_US", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "localeEn_US", name = "en_US", traits = "editable:true", style = "width:250px;height:24px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_zh_CN", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "localeZh_CN", name = "zh_CN", traits = "editable:true", style = "width:250px;height:24px")//
						}), //
				}), //
		})
})
public class LocalizationSettingForm extends AbstractMeshComponentBuilder {
	@Override
	public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
		XUIMeshGrid formConfig = super.buildComponentConfig(commandBean);
		XObject primary = commandBean.getPrimaryObj();

		formConfig.inflateObject(primary);
		//formConfig.setWidgetMode(WidgetMode.Display);

		return formConfig;
	}
}
