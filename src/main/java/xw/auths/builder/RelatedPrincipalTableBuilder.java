package xw.auths.builder;

import com.flame.common.TwoEntry;
import com.flame.xui.XCommandBean;
import com.flame.xui.WinType;
import com.flame.annotations.UIAction;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.localize.LocalizationHelper;
import xw.auths.XGroupHelper;
import xw.auths.entity.XGroup;
import xw.auths.entity.XGroupUserLink;
import xw.auths.entity.XUser;

import java.util.ArrayList;
import java.util.List;

@UIDataGrid(idField = "oid", singleSelect = true, rowNumber = false, fit = true, //
		actions = {
			@UIAction(name = "new", processor = "xw.auths.processor.CreateGroupProcessor", url = "thymeleaf/auths/newGroup.html", icon = "images/add16x16.gif", winType = WinType.popup, style = "width:680px;height:500px;padding:5px;"), //
			@UIAction(name = "delete", processor = "xw.object.processor.DeleteObjectProcessor", icon = "images/delete.png", beforeJS = "return flame.validateSelect(p, true);", winType = WinType.invoke), //
		},
		columns = { //
			@UIColumn(field = "oid", checkbox = true), //
			@UIColumn(field = "iconUI", width = "25", align = "left"), //
			@UIColumn(field = "name", width = "130", sortable = true), //
			@UIColumn(field = "fullName", width = "150", sortable = true), //
			@UIColumn(field = "role", width = "80", sortable = true), //
			@UIColumn(field = "tel", width = "80", sortable = true), //
			@UIColumn(field = "fax", width = "80", sortable = true), //
			@UIColumn(field = "description", width = "150"), //
			@UIColumn(field = "address", width = "150", sortable = true), //
			@UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
			@UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
		} //
)
public class RelatedPrincipalTableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<TableComponentRow> result = new ArrayList<>();

		XGroup parent = (XGroup) commandBean.getPrimaryObj();
		List<XGroup> groupList = XGroupHelper.repository().getGroupMember(parent);
		for (XGroup group : groupList) {
			TableComponentRow tableRow = TableComponentRow.newInstance(group);
			result.add(tableRow);
		}

		List<TwoEntry<XUser, XGroupUserLink>> userLinkList = XGroupHelper.repository().getUserMembers(parent);
		for (TwoEntry<XUser, XGroupUserLink> twoEntry : userLinkList) {
			XUser xuser = twoEntry.getOneValue();
			XGroupUserLink link = twoEntry.getTwoValue();
			TableComponentRow tableRow = TableComponentRow.newInstance(xuser);
			tableRow.addAttribute("role", link.getRole() == null ? "" : link.getRole().getDisplay(LocalizationHelper.getLocale()));
			result.add(tableRow);
		}
		return result;
	}

}
