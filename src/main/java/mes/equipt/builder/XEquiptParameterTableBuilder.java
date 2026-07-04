package mes.equipt.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.thing.IThingModel;
import com.flame.thing.ThingModelHelper;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.xui.widget.DialogIcon;
import com.flame.xui.widget.IconBox;

import mes.apl.XPLineServiceHelper;
import mes.equipt.XEquiptInstance;
import mes.equipt.XEquiptParameter;

@UIDataGrid(idField = "rowId", actionModel = "XEquiptInstance:EquiptParameter-Toolbar", rowNumber = false, singleSelect = true, selectOnCheck = true, checkOnSelect = true, fit = true, //
        columns = { //
                @UIColumn(field = "rowId", checkbox = true), //
                @UIColumn(field = "checkout", width = "25", align = "left"), //
                @UIColumn(field = "icon", width = "25", align = "left"), //
                @UIColumn(field = "number", width = "150", sortable = true), //
                @UIColumn(field = "name", width = "200", sortable = true), //
                @UIColumn(field = "details", width = "25", align = "center"), //
                @UIColumn(field = "value", width = "100"), //
                @UIColumn(field = "description", width = "200"), //
                @UIColumn(field = "creator", width = "100"), //
                @UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
                @UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
        } //
)
public class XEquiptParameterTableBuilder extends AbstractTableComponentBuilder {
    @Override
    public List<? extends Object> getTableRows(XCommandBean commandBean) {
        List<TableComponentRow> result = new ArrayList<>();
        XObject xobject = commandBean.getPrimaryObj();
        if (xobject instanceof XEquiptInstance) {
            XEquiptInstance instance = (XEquiptInstance) xobject;
            IThingModel thingModel = ThingModelHelper.manager().getThingModel(XEquiptParameter.class);
            List<?> list = XPLineServiceHelper.service().getEquiptParameter(instance);
            for (Object object : list) {
                XEquiptParameter parameter = (XEquiptParameter) object;
                TableComponentRow tableRow = TableComponentRow.newInstance(parameter);
                tableRow.addAttribute("icon", new IconBox(thingModel.getIcon()));
                tableRow.addAttribute("details", new DialogIcon(parameter));
                result.add(tableRow);
            }
        }

        return result;
    }
}
