package com.thing.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.thing.IPropertyDefinition;
import com.flame.thing.IPropertyProvider;
import com.flame.util.FlameUtils;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.thing.entity.XPropertyLayout;
import com.thing.entity.XThingModel;

@UIDataGrid(idField = "oid", fit = true, pagination = false, //
        columns = { //
                @UIColumn(field = "oid", hidden = true), //
                @UIColumn(field = "display", width = "130", sortable = true), //
                @UIColumn(field = "name", width = "130", sortable = true), //
        } //
)
public class PropertyLayoutTableBuilder extends AbstractTableComponentBuilder {
    @Override
    public List<? extends Object> getTableRows(XCommandBean commandBean) {
        List<TableComponentRow> result = new ArrayList<>();
        XObject xObject = commandBean.getPrimaryObj();
        if (xObject instanceof XPropertyLayout) {
            XPropertyLayout propertyLayout = (XPropertyLayout) xObject;
            IPropertyProvider propertyProvider = propertyLayout.getPropertyProvider();
            if (propertyProvider instanceof XThingModel) {
                XThingModel thingModel = (XThingModel) propertyProvider;
                List<IPropertyDefinition> propertyDefinitions = thingModel.getPropertyDefinitions();
                for (IPropertyDefinition definition : propertyDefinitions) {
                    TableComponentRow tableRow = TableComponentRow.newInstance(definition);
                    String display = definition.getLocalDisplay();
                    if (FlameUtils.isBlank(display)) {
                        display = definition.getName();
                    }
                    tableRow.addAttribute("display", display);
                    tableRow.addAttribute("name", definition.getName());
                    result.add(tableRow);
                }
            }
        }

        return result;
    }
}
