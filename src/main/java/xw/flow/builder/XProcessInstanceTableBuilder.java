package xw.flow.builder;

import com.flame.xui.HREFactory;
import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;
import com.flame.xui.service.TableComponentRow;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.orm.XObject;
import xw.flow.XFlowRepositoryHelper;
import xw.flow.entity.XWorkInstance;

import java.util.ArrayList;
import java.util.List;

@UIDataGrid(idField = "rowId", rowNumber = false, singleSelect = true, fit = true, //
        actions = {
                //@UIAction(name = "delete", display = "Delete", processor = "xw.flow.processor.DeleteXWorkInstanceProcessor", icon = "images/delete.png", beforeJS = "return flame.validateSelect(p, true);", winType = "invoke"), //
        },
        columns = { //
                @UIColumn(field = "rowId", checkbox = true), //
                @UIColumn(field = "icon", width = "25", align = "left"), //
                @UIColumn(field = "name", width = "200", sortable = true), //
                @UIColumn(field = "details", width = "25", align = "center"), //
                @UIColumn(field = "description", width = "400"), //
                @UIColumn(field = "status", width = "100", align = "center"), //
                @UIColumn(field = "creatorName", width = "100"), //
                @UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
                @UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
        } //
)
public class XProcessInstanceTableBuilder extends AbstractTableComponentBuilder {
    @Override
    public List<?> getTableRows(XCommandBean commandBean) {
        List<TableComponentRow> result = new ArrayList<>();
        XObject xobject = commandBean.getPrimaryObj();
        if (xobject == null)
            return result;
        List<XWorkInstance> list = XFlowRepositoryHelper.repository().findXWorkInstance(xobject);
        for (XWorkInstance instance : list) {
            TableComponentRow tableRow = TableComponentRow.newInstance(instance);
            tableRow.addAttribute("icon", new IconBox(instance.getIcon()));
            HyperLink linkComp = new HyperLink();
            linkComp.setInnerObject(new IconBox("images/details.gif"));
            linkComp.setUrl(HREFactory.hashInfoPage(instance));
            tableRow.addAttribute("details", linkComp);
            result.add(tableRow);
        }

        return result;
    }
}
