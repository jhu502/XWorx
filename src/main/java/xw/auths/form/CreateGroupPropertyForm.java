package xw.auths.form;

import com.flame.annotations.*;
import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.xui.WidgetType;
import com.flame.xui.builder.AbstractMeshComponentBuilder;

import xw.auths.entity.XGroup;

import com.flame.xui.XUIMeshGrid;

@UIMeshGrid(grids = {
        @UIGrid(provider = XGroup.class, title = "property", rows = { //
                @UIRow(cells = { //
                        @UICell(widget = { //
                                @UIWidget(type = WidgetType.Hidden, id = "groupOid", name = "oid"), //
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_groupName", required = true, widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "groupName", name = "name", traits = "editable:true,required:true", style = "width:250px;height:24px")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_englishName", required = true, widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "englishName", name = "englishName", traits = "editable:true,required:true", style = "width:350px;height:24px")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_groupFullName", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "fullName", name = "fullName", traits = "editable:true", style = "width:300px;height:25px")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_groupType", required = true, widget = { //
                                @UIWidget(type = WidgetType.ComboBox, id = "groupType", name = "groupType", traits = "panelHeight:'auto',editable:false,required:true", style = "width:150px;height:25px")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_description", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "description", name = "description", traits = "editable:true,multiline:true", style = "width:500px;height:60px")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_tel", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "groupTel", name = "tel", traits = "editable:true", style = "width:200px;height:25px")//
                        }), //
                        @UICell(label = "label_fax", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "groupFax", name = "fax", traits = "editable:true", style = "width:200px;height:25px")//
                        }), //
                }), //
                @UIRow(cells = { //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_address", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "groupAddress", name = "address", traits = "editable:true,multiline:true", style = "width:500px;height:25px")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_email", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "groupEmail", name = "email", traits = "editable:true,validType:'email'", style = "width:200px;height:25px")//
                        }), //
                        @UICell(label = "label_postalCode", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "postalCode", name = "postalCode", traits = "editable:true", style = "width:200px;height:25px")//
                        }), //
                }), //
        })
})
public class CreateGroupPropertyForm extends AbstractMeshComponentBuilder {
    public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
        XUIMeshGrid formConfig = (XUIMeshGrid) super.buildComponentConfig(commandBean);
        XObject primary = (XObject) commandBean.getPrimaryObj();

        return formConfig;
    }

}
