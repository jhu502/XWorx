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
                @UIColumn(field = "assignee", width = "100", align = "center"), //
                @UIColumn(field = "completedBy", width = "100", align = "center"), //
                @UIColumn(field = "completedOn", width = "130", align = "center"), //
                @UIColumn(field = "routes", width = "100"), //
                @UIColumn(field = "remarks", width = "300"), //
                @UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
                @UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
        } //
)
public class XProcessWorkTaskTableBuilder extends AbstractTableComponentBuilder {
    @Override
    public List<?> getTableRows(XCommandBean commandBean) {
        List<TableComponentRow> result = new ArrayList<>();
        XObject xobject = commandBean.getPrimaryObj();
        if (!(xobject instanceof XWorkInstance)) {
            return result;
        }
        XWorkInstance instance = (XWorkInstance) xobject;
        List<XWorkTask> list = XFlowRepositoryHelper.repository().findXWorkTask(instance);
        for (XWorkTask workTask : list) {
            TableComponentRow tableRow = TableComponentRow.newInstance(workTask, workTask.getOid());
            if (FlowStatus.OPEN.equals(workTask.getStatus())) {
                tableRow.addAttribute("icon", new IconBox("images/flow/usertask.png"));
                HyperLink nameLink = new HyperLink(true);
                nameLink.setInnerObject(workTask.getName());
                nameLink.setUrl(HREFactory.hashInfoPage(workTask));
                tableRow.addAttribute("name", nameLink);
                tableRow.addAttribute("assignee", workTask.getAssignee().getUsername());
            } else {
                tableRow.addAttribute("icon", new IconBox("images/flow/usertask-e.png"));
                HyperLink nameLink = new HyperLink(true);
                nameLink.setInnerObject(workTask.getName());
                nameLink.setUrl(HREFactory.hashInfoPage(workTask));
                tableRow.addAttribute("name", nameLink);
                tableRow.addAttribute("assignee", workTask.getAssignee().getUsername());
                tableRow.addAttribute("completedBy", workTask.getCompletedBy());
                tableRow.addAttribute("completedOn", workTask.getCompletedOn());
                tableRow.addAttribute("routes", workTask.getRoutes());
                tableRow.addAttribute("remarks", workTask.getRemarks());
            }

            result.add(tableRow);
        }

        return result;
    }
}
