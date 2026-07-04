package xw.flow.builder;

import com.flame.xui.HREFactory;
import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;
import com.flame.xui.service.TableComponentRow;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.orm.ObjectReference;
import com.flame.orm.XObject;
import com.flame.orm.XPersistable;
import xw.flow.XFlowExecutionHelper;
import xw.flow.constants.FlowStatus;
import xw.flow.entity.XWorkInstance;
import xw.flow.entity.XWorkTask;

import java.util.ArrayList;
import java.util.List;

@UIDataGrid(idField = "rowId", rowNumber = false, singleSelect = true, fit = true, //
        columns = { //
                @UIColumn(field = "rowId", hidden = true), //
                @UIColumn(field = "blank", width = "25", align = "left"), //
                @UIColumn(field = "icon", width = "25", align = "left"), //
                @UIColumn(field = "name", width = "200", sortable = true), //
                @UIColumn(field = "status", width = "80", sortable = true), //
                @UIColumn(field = "details", width = "25", align = "center"), //
                @UIColumn(field = "primaryObject", width = "200", sortable = true), //
                @UIColumn(field = "description", width = "400"), //
                @UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true) //
        } //
)
public class XFlowTaskEntityTableBuilder extends AbstractTableComponentBuilder {
    @Override
    public List<?> getTableRows(XCommandBean commandBean) {
        List<TableComponentRow> result = new ArrayList<>();
        List<XWorkTask> workItems = XFlowExecutionHelper.execution().listOwnedWorkItem(FlowStatus.OPEN);
        for (XWorkTask workItem : workItems) {
            TableComponentRow tableRow = TableComponentRow.newInstance();
            tableRow.setRowId(workItem.getOid());
            tableRow.addAttribute("icon", new IconBox(""));
            HyperLink nameLink = new HyperLink(true);
            nameLink.setInnerObject(workItem.getName());
            nameLink.setUrl(HREFactory.hashInfoPage(workItem));
            tableRow.addAttribute("name", nameLink);
            tableRow.addAttribute("status", workItem.getStatus().name());
            XWorkInstance instance = workItem.getInstance();
            ObjectReference<XObject> businessRef = instance.getBusinessRef();
            if (businessRef != null) {
                XPersistable businessObject = businessRef.getObject();
                tableRow.addAttribute("primaryObject", businessObject.getDisplay());
                HyperLink detailLink = new HyperLink();
                detailLink.setInnerObject(new IconBox("images/details.gif"));
                detailLink.setUrl(HREFactory.hashInfoPage(businessObject));
                tableRow.addAttribute("details", detailLink);
            }
            tableRow.addAttribute("createdStamp", workItem.getCreatedStamp());
            result.add(tableRow);
        }

        return result;
    }
}
