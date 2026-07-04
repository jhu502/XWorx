package xw.context.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.HREFactory;
import com.flame.xui.XCommandBean;
import com.flame.auths.SessionHelper;
import com.flame.xui.WinType;
import com.flame.annotations.UIAction;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;

import xw.auths.entity.XUser;
import xw.context.ContextHelper;
import xw.context.entity.XLibrary;

@UIDataGrid(idField = "rowId", rowNumber = false, singleSelect = false, fit = true, //
        actions = { //
                @UIAction(name = "newLibrary", processor = "xw.context.processor.CreateLibraryContextProcessor", url = "thymeleaf/context/library/newLibrary.html", icon = "images/body/newlibrary.png", winType = WinType.popup, style = "width:680px;height:380px;padding:5px;"), //
                @UIAction(name = "delete", processor = "xw.object.processor.DeleteObjectProcessor", icon = "images/delete.png", winType = WinType.invoke) //
        }, //
        columns = { //
                @UIColumn(field = "rowId", checkbox = true), //
                @UIColumn(field = "icon", width = "25"), //
                @UIColumn(field = "number", width = "150", sortable = true), //
                @UIColumn(field = "name", width = "180", sortable = true), //
                @UIColumn(field = "details", width = "25", align = "center"), //
                @UIColumn(field = "description", width = "200"), //
                @UIColumn(field = "libraryType", width = "50"), //
                @UIColumn(field = "creatorName", width = "100"), //
                @UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
                @UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
        } //
)
public class LibraryListViewTableBuilder extends AbstractComponentBuilder {
    @Override
    public Object buildComponentData(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();
        XUser xuser = (XUser) SessionHelper.getCurrentUser();
        List<XLibrary> list = ContextHelper.repository().listLibrary(xuser);
        for (XLibrary library : list) {
            TableComponentRow tableRow = TableComponentRow.newInstance(library);
            tableRow.addAttribute("icon", new IconBox(library.getIcon()));
            HyperLink detailLink = new HyperLink();
            detailLink.setInnerObject(new IconBox("images/details.gif"));
            detailLink.setUrl(HREFactory.hashInfoPage(library));
            tableRow.addAttribute("details", detailLink);
            tableRow.addAttribute("libraryType", library.getLibraryType().getDisplay());
            result.add(tableRow);
        }
        return result;
    }
}
