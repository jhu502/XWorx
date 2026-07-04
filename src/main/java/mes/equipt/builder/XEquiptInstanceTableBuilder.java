package mes.equipt.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.HREFactory;
import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.thing.ThingModelHelper;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;
import com.thing.entity.XThingModel;

import mes.apl.XPLineServiceHelper;
import mes.equipt.XEquipment;
import mes.equipt.XEquiptInstance;
import mes.equipt.XEquiptInstanceLink;

@UIDataGrid(idField = "oid", actionModel = "XEquiptInstance:EquiptInstance-Toolbar", rowNumber = false, singleSelect = true, selectOnCheck = true, checkOnSelect = true, fit = true, //
        columns = { //
                @UIColumn(field = "oid", checkbox = true), //
                @UIColumn(field = "checkout", width = "25", align = "left"), //
                @UIColumn(field = "icon", width = "25", align = "left"), //
                @UIColumn(field = "number", width = "200", sortable = true), //
                @UIColumn(field = "name", width = "200", sortable = true), //
                @UIColumn(field = "details", width = "25", align = "center"), //
                @UIColumn(field = "description", width = "400"), //
                @UIColumn(field = "creatorName", width = "100"), //
                @UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
                @UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
        } //
)
public class XEquiptInstanceTableBuilder extends AbstractTableComponentBuilder {
    @Override
    public List<? extends Object> getTableRows(XCommandBean commandBean) {
        List<TableComponentRow> result = new ArrayList<>();
        XObject xobject = commandBean.getPrimaryObj();
        if (xobject instanceof XEquipment) {
            XEquipment equipment = (XEquipment) xobject;
            XThingModel thingModel = (XThingModel) ThingModelHelper.manager().getThingModel(XEquiptInstance.class);
            List<?> list = XPLineServiceHelper.service().getEquiptInstance(equipment);
            for (Object object : list) {
                Object[] objs = (Object[]) object;
                XEquiptInstanceLink instanceLink = (XEquiptInstanceLink) objs[0];
                XEquiptInstance equiptInstance = (XEquiptInstance) objs[1];
                TableComponentRow tableRow = TableComponentRow.newInstance(equiptInstance);
                tableRow.addAttribute("icon", new IconBox(thingModel.getIcon()));
                HyperLink linkComp = new HyperLink();
                linkComp.setInnerObject(new IconBox("images/details.gif"));
                linkComp.setUrl(HREFactory.hashInfoPage(equiptInstance));
                tableRow.addAttribute("details", linkComp);
                result.add(tableRow);
            }
        }
        return result;
    }
}
