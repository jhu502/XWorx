package xw.auths.form;

import com.flame.annotations.*;
import com.flame.xui.XCommandBean;
import com.flame.xui.WidgetType;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.XUIMeshGrid;

/**
 * 角色搜索表单。
 * 
 * <p>提供角色名称和显示名称的搜索功能。</p>
 * 
 * @author XClaw Team
 */
@UIMeshGrid(grids = {
		@UIGrid(title = "query", rows = { //
				@UIRow(cells = { //
						@UICell(label = "label_name", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "roleName", name = "name", traits = "editable:true", style = "width:150px;height:24px"),//
						}), //
						@UICell(label = "label_display", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "roleDisplay", name = "display", traits = "editable:true", style = "width:150px;height:24px")//
						}), //
						@UICell(style = "padding-left:20px;", widget = {
								@UIWidget(type = WidgetType.Button, id = "submitForm", name = "query", text = "query", style = "width:50px;height:24px;", events = {
										@UIEvent(name = "onclick", value = "submitForm();")
								}),
								@UIWidget(type = WidgetType.Button, id = "clearForm", name = "clear", text = "clear", style = "margin-left:2px;width:50px;height:24px;", events = {
										@UIEvent(name = "onclick", value = "clearForm();")
								})
						}),
				})
		})
})
public class RoleSearchForm extends AbstractMeshComponentBuilder {

	public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
		XUIMeshGrid formConfig = (XUIMeshGrid) super.buildComponentConfig(commandBean);
		return formConfig;
	}
}