package mes.apl.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.HREFactory;
import com.flame.xui.XCommandBean;
import com.flame.thing.ThingModelHelper;
import com.flame.xui.WinType;
import com.flame.annotations.UIAction;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;
import com.thing.entity.XThingModel;

import mes.apl.XAplRepositoryHelper;
import mes.apl.XAssemblyLine;

@UIDataGrid(idField = "rowId", rowNumber = false, singleSelect = false, fit = true, //
        actions = {
                @UIAction(name = "newProductLine", processor = "mes.apl.processor.CreateProductionLineProcessor", url = "thymeleaf/mes/apl/newProductionLine.html", icon = "images/newdoc.gif", winType = WinType.popup, style = "width:680px;height:380px;padding:5px;"), //
                @UIAction(name = "delete", processor = "xw.object.processor.DeleteObjectProcessor", icon = "images/delete.png", winType = WinType.invoke) //
        },
        columns = { //
                @UIColumn(field = "rowId", checkbox = true), //
                @UIColumn(field = "checkout", width = "25", align = "left"), //
                @UIColumn(field = "icon", width = "25", align = "left"), //
                @UIColumn(field = "number", width = "200", sortable = true), //
                @UIColumn(field = "name", width = "200", sortable = true), //
                @UIColumn(field = "details", width = "25", align = "center"), //
                @UIColumn(field = "description", width = "400"), //
                @UIColumn(field = "creator", width = "100"), //
                @UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
                @UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
        } //
)
public class XAssemblyLineTableBuilder extends AbstractTableComponentBuilder {

    @Override
    public List<?> getTableRows(XCommandBean commandBean) {
        List<TableComponentRow> result = new ArrayList<>();

        XThingModel thingModel = (XThingModel) ThingModelHelper.manager().getThingModel(XAssemblyLine.class);
        XAplRepositoryHelper.repository().findAll();
        List<XAssemblyLine> list = XAplRepositoryHelper.repository().findAll();
        for (XAssemblyLine xprodLine : list) {
            TableComponentRow tableRow = TableComponentRow.newInstance(xprodLine);
            tableRow.addAttribute("icon", new IconBox(thingModel.getIcon()));
            HyperLink linkComp = new HyperLink();
            linkComp.setInnerObject(new IconBox("images/details.gif"));
            linkComp.setUrl(HREFactory.hashInfoPage(xprodLine));
            tableRow.addAttribute("details", linkComp);
            result.add(tableRow);
        }
        return result;
    }
}
