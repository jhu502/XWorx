package xw.context.form;

import com.flame.annotations.*;
import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.xui.WidgetType;
import com.flame.xui.builder.AbstractMeshComponentBuilder;

import xw.context.entity.XLibrary;

import com.flame.xui.XUIMeshGrid;

@UIMeshGrid(grids = {
        @UIGrid(provider = XLibrary.class, rows = { //
                @UIRow(cells = { //
                        @UICell(widget = { //
                                @UIWidget(type = WidgetType.Hidden, id = "libraryOid", name = "oid"), //
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_number", required = true, widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "libNumber", name = "number", traits = "required:true", style = "width:250px;height:24px")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_name", required = true, widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "libName", name = "name", traits = "editable:true,required:true", style = "width:350px;height:24px")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_description", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "libDescription", name = "description", traits = "editable:true,multiline:true", style = "width:500px;height:60px")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_libraryType", required = true, widget = { //
                                @UIWidget(type = WidgetType.ComboBox, id = "libraryType", name = "libraryType", traits = "panelHeight:'auto',editable:false,required:true", style = "height:25px; width:150px;")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_effectiveDate", required = true, widget = { //
                                @UIWidget(type = WidgetType.DateBox, id = "effectiveDate", name = "effectiveDate", traits = "editable:false,required:true", style = "height:25px; width:150px;")//
                        }), //
                }) //
        })
})
public class CreateLibraryPropertyForm extends AbstractMeshComponentBuilder {
    public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
        XUIMeshGrid formConfig = (XUIMeshGrid) super.buildComponentConfig(commandBean);
        XObject primary = (XObject) commandBean.getPrimaryObj();

        return formConfig;
    }
}
