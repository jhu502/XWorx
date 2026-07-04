package mes.equipt.form;

import com.flame.annotations.*;
import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.xui.WidgetType;
import com.flame.xui.builder.AbstractMeshComponentBuilder;

import mes.equipt.XEquipment;

import com.flame.xui.XUIMeshGrid;

@UIMeshGrid(grids = {
        @UIGrid(provider = XEquipment.class, rows = { //
                @UIRow(cells = { //
                        @UICell(widget = { //
                                @UIWidget(type = WidgetType.Hidden, id = "equiptOid", name = "oid"), //
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "*编号：", style = "width:80px", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "equiptNumber", name = "number", traits = "required:true", style = "width:250px;height:24px")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "*名称：", style = "width:80px", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "equiptName", name = "name", traits = "editable:true,required:true", style = "width:350px;height:24px")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "描述：", style = "width:80px", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "equiptDescription", name = "description", traits = "editable:true,multiline:true", style = "width:500px;height:60px")//
                        }), //
                })
        })
})
public class XEquiptDefinitionForm extends AbstractMeshComponentBuilder {
    public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
        XUIMeshGrid formConfig = (XUIMeshGrid) super.buildComponentConfig(commandBean);
        XObject primary = (XObject) commandBean.getPrimaryObj();

        return formConfig;
    }
}
