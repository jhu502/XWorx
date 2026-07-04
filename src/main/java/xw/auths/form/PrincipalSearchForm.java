package xw.auths.form;

import com.flame.annotations.*;
import com.flame.xui.XCommandBean;
import com.flame.xui.WidgetType;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.widget.ComboBox;
import com.flame.xui.XUIMeshGrid;
import com.flame.localize.LocalizationHelper;
import com.flame.orm.XObject;
import xw.auths.entity.XGroup;
import xw.auths.entity.XUser;

@UIMeshGrid(grids = {
        @UIGrid(provider = XGroup.class, rows = { //
                @UIRow(cells = { //
                        @UICell(widget = { //
                                @UIWidget(type = WidgetType.Hidden, id = "principal", name = "oid"), //
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_type", required = true, widget = { //
                                @UIWidget(type = WidgetType.ComboBox, id = "principalType", name = "type", traits = "editable:true,required:true", style = "width:100px;height:24px"),//
                        }), //
                        @UICell(style = "text-align:right;", widget = {
                                @UIWidget(type = WidgetType.Button, id = "queryBtn", name = "query", text = "query", style = "width:50px;height:24px;", events = {
                                        @UIEvent(name = "onclick", value = "queryPrincipal();")
                                })
                        })
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_name", required = true, colspan = 2, widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "searchKey", name = "searchKey", traits = "editable:true,required:true", style = "width:250px;height:24px")//
                        }), //
                }), //
        })
})
public class PrincipalSearchForm extends AbstractMeshComponentBuilder {
    private static final String ALL = "all";

    public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
        XUIMeshGrid formConfig = super.buildComponentConfig(commandBean);
        XObject primary = commandBean.getPrimaryObj();

        ComboBox principalType = (ComboBox) formConfig.getXUIWidget("principalType");
        principalType.addOption(ALL, LocalizationHelper.get(ALL));
        principalType.addOption(XGroup.class.getName(), LocalizationHelper.get(XGroup.class.getName()));
        principalType.addOption(XUser.class.getName(), LocalizationHelper.get(XUser.class.getName()));
        principalType.setValue(ALL);

        return formConfig;
    }
}
