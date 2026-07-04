package xw.auths.builder;

import com.flame.xui.XCommandBean;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.localize.LocalizationHelper;
import xw.auths.XGroupHelper;
import xw.auths.entity.XGroup;
import xw.auths.entity.XPrincipal;
import com.flame.orm.XObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@UIDataGrid(idField = "oid", singleSelect = true, rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "oid", checkbox = true), //
				@UIColumn(field = "iconUI", width = "24", align = "left"), //
				@UIColumn(field = "name", width = "130", sortable = true), //
				@UIColumn(field = "fullName", width = "150", sortable = true), //
				@UIColumn(field = "groupType", width = "80", sortable = true), //
				@UIColumn(field = "tel", width = "80", sortable = true), //
				@UIColumn(field = "fax", width = "80", sortable = true), //
				@UIColumn(field = "description", width = "150"), //
				@UIColumn(field = "address", width = "150", sortable = true), //
				@UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
				@UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
		} //
)
public class GroupListTableBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(XCommandBean commandBean) {
		List<TableComponentRow> result = new ArrayList<>();
		XObject xObject = commandBean.getPrimaryObj();
		if (xObject instanceof XPrincipal) {
			XPrincipal xPrincipal = (XPrincipal) xObject;
			Set<XGroup> groupList = XGroupHelper.service().listParentGroup(xPrincipal.getOid());
			for (XGroup xGroup : groupList) {
				TableComponentRow tableRow = TableComponentRow.newInstance(xGroup);
				tableRow.addAttribute("groupType", xGroup.getGroupType().getDisplay(LocalizationHelper.getLocale()));
				result.add(tableRow);
			}
		}
		return result;
	}

}
