package com.thing.form;

import java.util.List;

import com.flame.annotations.UICell;
import com.flame.annotations.UIEvent;
import com.flame.annotations.UIGrid;
import com.flame.annotations.UIMeshGrid;
import com.flame.annotations.UIRow;
import com.flame.annotations.UIWidget;
import com.flame.orm.PersistenceHelper;
import com.flame.thing.IThingModel;
import com.flame.xui.WidgetType;
import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.widget.ComboBox;
import com.flame.xui.XUIMeshGrid;

/**
 * 通用对象搜索表单 —— 选择对象类型并按关键字进行模糊搜索。
 *
 * <p>用于启动流程实例时选择关联的业务对象。对象类型下拉框从 ThingModel 中动态加载所有模型类型。</p>
 */
@UIMeshGrid(grids = {
        @UIGrid(title = "query", rows = {
                @UIRow(cells = {
                        @UICell(label = "对象类型", required = true, widget = {
                                @UIWidget(type = WidgetType.ComboBox, id = "objectType", name = "objectType", traits = "editable:true,required:true", style = "width:150px;height:24px")
                        }),
                }),
                @UIRow(cells = {
                        @UICell(label = "编号/名称", required = true, colspan = 2, widget = {
                                @UIWidget(type = WidgetType.TextBox, id = "searchKey", name = "searchKey", traits = "editable:true,required:true", style = "width:200px;height:24px")
                        }),
                        @UICell(style = "padding-left:8px;", widget = {
                                @UIWidget(type = WidgetType.Button, id = "queryBtn", name = "query", text = "查询", style = "width:50px;height:24px;", events = {
                                        @UIEvent(name = "onclick", value = "queryObject();")
                                })
                        })
                })
        })
})
public class ObjectTypeSearchForm extends AbstractMeshComponentBuilder {

    @Override
    public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
        XUIMeshGrid formConfig = (XUIMeshGrid) super.buildComponentConfig(commandBean);

        ComboBox objectTypeBox = (ComboBox) formConfig.getXUIWidget("objectType");
        List<?> models = PersistenceHelper.service().query("select m from XThingModel m", null);
        String firstValue = null;
        for (Object obj : models) {
            if (obj instanceof IThingModel model) {
                Class<?> entityClass = model.getEntityClass();
                if (entityClass != null) {
                    String value = entityClass.getName();
                    String display = model.getName();
                    objectTypeBox.addOption(value, display);
                    if (firstValue == null) {
                        firstValue = value;
                    }
                }
            }
        }
        if (firstValue != null) {
            objectTypeBox.setValue(firstValue);
        }

        return formConfig;
    }
}
