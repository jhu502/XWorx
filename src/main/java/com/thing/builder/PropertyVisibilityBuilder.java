package com.thing.builder;

import com.flame.xui.XCommandBean;
import com.flame.localize.LocalizationHelper;
import com.flame.orm.XObject;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.xui.widget.ComboBox;
import com.thing.entity.XPropertyDefinition;

import java.util.ArrayList;
import java.util.List;

@UIDataGrid(fit = true, //groupField = "group",
        columns = { //
                @UIColumn(field = "name", hidden = true), //
                @UIColumn(field = "display", width = "150", sortable = true), //
                @UIColumn(field = "value", width = "300", sortable = true), //
                @UIColumn(field = "group", hidden = true), //
        } //
) //
public class PropertyVisibilityBuilder extends AbstractTableComponentBuilder {
    @Override
    public List<?> getTableRows(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();
        XObject primary = commandBean.getPrimaryObj();
        if (!(primary instanceof XPropertyDefinition))
            return result;

        XPropertyDefinition definition = (XPropertyDefinition) primary;

        String basic = LocalizationHelper.get("Basic");
        String create = LocalizationHelper.get("create");
        String edit = LocalizationHelper.get("edit");
        String display = LocalizationHelper.get("localDisplay");

        TableComponentRow createRow = TableComponentRow.newInstance(definition);
        createRow.addAttribute("name", "create");
        createRow.addAttribute("display", create);
        createRow.addAttribute("group", basic);
        ComboBox createBox = createRow.addAttribute("value", new ComboBox("createVisibility"));
        createBox.addOption("read/write", LocalizationHelper.get("read/write")).addOption("readOnly", LocalizationHelper.get("readOnly")).addOption("hidden", LocalizationHelper.get("hidden"));
        result.add(createRow);

        TableComponentRow editRow = TableComponentRow.newInstance(definition);
        editRow.addAttribute("name", "edit");
        editRow.addAttribute("display", edit);
        editRow.addAttribute("group", basic);
        ComboBox editBox = editRow.addAttribute("value", new ComboBox("createVisibility"));
        editBox.addOption("read/write", LocalizationHelper.get("read/write")).addOption("readOnly", LocalizationHelper.get("readOnly")).addOption("hidden", LocalizationHelper.get("hidden"));
        result.add(editRow);

        TableComponentRow displayRow = TableComponentRow.newInstance(definition);
        displayRow.addAttribute("name", "display");
        displayRow.addAttribute("display", display);
        displayRow.addAttribute("group", basic);
        ComboBox displayBox = displayRow.addAttribute("value", new ComboBox("createVisibility"));
        displayBox.addOption("readOnly", LocalizationHelper.get("readOnly")).addOption("hidden", LocalizationHelper.get("hidden"));
        result.add(displayRow);

        return result;
    }
}
