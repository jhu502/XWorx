package xw.action.form;

import com.flame.annotations.*;
import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.xui.WidgetType;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.XUIMeshGrid;

@UIMeshGrid(grids = {
        @UIGrid(title = "query", rows = { //
                @UIRow(cells = { //
                        @UICell(widget = { //
                                @UIWidget(type = WidgetType.Hidden, id = "principal", name = "oid"), //
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_name", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "actionName", name = "name", traits = "editable:true", style = "width:150px;height:24px"),//
                        }), //
                        @UICell(label = "label_display", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "actionDisplay", name = "display", traits = "editable:true", style = "width:150px;height:24px")//
                        }), //
                        @UICell(style = "padding:0px 10px;", widget = {
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
public class ActionSearchForm extends AbstractMeshComponentBuilder {
    private static final String ALL = "all";

    public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
        XUIMeshGrid formConfig = (XUIMeshGrid) super.buildComponentConfig(commandBean);
        XObject primary = (XObject) commandBean.getPrimaryObj();

        return formConfig;
    }
}
