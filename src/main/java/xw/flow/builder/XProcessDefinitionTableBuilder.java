package xw.flow.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.HREFactory;
import com.flame.xui.XCommandBean;
import com.flame.vc.CheckOutInfo;
import com.flame.xui.WinType;
import com.flame.annotations.UIAction;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;

import xw.flow.XFlowRepositoryHelper;
import xw.flow.entity.XFlowDefinition;

@UIDataGrid(idField = "rowId", rowNumber = false, singleSelect = true, fit = true, //
        actions = {
                @UIAction(name = "createDefinition", processor = "xw.flow.processor.SaveProcessDefinitionProcessor", url = "thymeleaf/xflow/definition/newProcessDefinition.html", icon = "images/newtemplate.gif", winType = WinType.popup, style = "width:92%;height:95%;padding:5px;"), //
                @UIAction(name = "delete", processor = "xw.object.processor.DeleteObjectProcessor", icon = "images/delete.png", beforeJS = "return flame.validateSelect(p, true);", winType = WinType.invoke), //
                @UIAction(name = "deployment", processor = "xw.flow.processor.DeployFlowDefinitionProcessor", icon = "images/flow/deployment.png", beforeJS = "return xworx.validateSelect(p, true);", winType = WinType.invoke) //
        },
        columns = { //
                @UIColumn(field = "rowId", checkbox = true), //
                @UIColumn(field = "checkout", width = "25", align = "left"), //
                @UIColumn(field = "icon", width = "25", align = "left"), //
                @UIColumn(field = "number", width = "200", sortable = true), //
                @UIColumn(field = "name", width = "200", sortable = true), //
                @UIColumn(field = "details", width = "25", align = "center"), //
                @UIColumn(field = "deployed", width = "60", align = "center"), //
                @UIColumn(field = "description", width = "400"), //
                @UIColumn(field = "creatorName", width = "100"), //
                @UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
                @UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
        } //
)
public class XProcessDefinitionTableBuilder extends AbstractTableComponentBuilder {

    @Override
    public List<?> getTableRows(XCommandBean commandBean) {
        List<TableComponentRow> result = new ArrayList<>();

        List<XFlowDefinition> list = XFlowRepositoryHelper.repository().findLatestDefinitions();
        for (XFlowDefinition definition : list) {
            TableComponentRow tableRow = TableComponentRow.newInstance(definition);
            CheckOutInfo checkInfo = definition.getCheckOutInfo();
            if (CheckOutInfo.co.equals(checkInfo) || CheckOutInfo.wrk.equals(checkInfo)) {
                tableRow.addAttribute("checkout", new IconBox("images/checkedout.png"));
            }
            tableRow.addAttribute("icon", new IconBox(definition.getIcon()));
            HyperLink linkComp = new HyperLink();
            linkComp.setInnerObject(new IconBox("images/details.gif"));
            linkComp.setUrl(HREFactory.hashInfoPage(definition));
            tableRow.addAttribute("details", linkComp);
            result.add(tableRow);
        }
        return result;
    }
}
