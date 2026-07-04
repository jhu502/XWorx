package xw.context.builder;

import com.flame.xui.XCommandBean;
import com.flame.xui.WinType;
import com.flame.annotations.UIAction;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractComponentBuilder;

import java.util.ArrayList;
import java.util.List;

@UIDataGrid(idField = "rowId", rowNumber = false, singleSelect = false, fit = true, //
        actions = { //
                @UIAction(name = "newOrganize", display = "New", processor = "xw.context.processor.CreateOrganizationProcessor", url = "thymeleaf/domain/newOrganization.html", icon = "images/body/newlibrary.png", winType = WinType.popup, style = "width:680px;height:380px;padding:5px;"), //
                @UIAction(name = "delete", display = "Delete", processor = "xw.object.processor.DeleteObjectProcessor", icon = "images/delete.png", winType = WinType.invoke) //
        },
        columns = { //
                @UIColumn(field = "rowId", checkbox = true), //
                @UIColumn(field = "icon", width = "25", align = "left"), //
                @UIColumn(field = "number", title = "Number", width = "200", sortable = true), //
                @UIColumn(field = "name", title = "Name", width = "200", sortable = true), //
                @UIColumn(field = "description", title = "Description", width = "400"), //
                @UIColumn(field = "creator", title = "Creator", width = "100"), //
                @UIColumn(field = "createdStamp", title = "Created On", width = "130", align = "center", sortable = true), //
                @UIColumn(field = "modifiedStamp", title = "Last Modified", width = "130", align = "center", sortable = true) //
        }
)
public class OrganizationListTableBuilder extends AbstractComponentBuilder {
    @Override
    public Object buildComponentData(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();

        return result;
    }
}
