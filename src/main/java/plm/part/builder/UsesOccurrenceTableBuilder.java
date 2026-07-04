package plm.part.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.thing.ThingModelHelper;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.annotations.UIWidget;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.xui.widget.IconBox;
import com.thing.entity.XThingModel;

import plm.part.XPart;
import plm.part.XPartUsageLink;
import plm.part.service.XPartServiceHelper;

@UIDataGrid(idField = "rowId", actionModel = "XPart:PSB-OccurrenceToolbar", rowNumber = false, singleSelect = false, fit = true, //
		columns = { //
				@UIColumn(field = "rowId", checkbox = true), //
				@UIColumn(field = "icon", width = "25", align = "left"), //
				@UIColumn(field = "number", width = "100", sortable = true), //
				@UIColumn(field = "name", width = "150", sortable = true), //
				@UIColumn(field = "pitch", align = "center", width = "150", widget = @UIWidget(type = WidgetType.TextBox)), //
				@UIColumn(field = "yaw", align = "center", width = "150", widget = @UIWidget(type = WidgetType.TextBox)), //
				@UIColumn(field = "roll", align = "center", width = "150", widget = @UIWidget(type = WidgetType.TextBox)), //
				@UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
				@UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
		} //
)
public class UsesOccurrenceTableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		XThingModel thingModel = (XThingModel) ThingModelHelper.manager().getThingModel(XPart.class);

		List<XPartUsageLink> links = new ArrayList<>();
		XPart context = (XPart) commandBean.getPrimaryObj();
		if (context == null)
			return result;

		List<?> list = XPartServiceHelper.repository().getUsedbyXPart(context);
		for (Object x : list) {
			Object[] objs = (Object[]) x;
			XPartUsageLink link = (XPartUsageLink) objs[0];
			XPart part = (XPart) objs[1];
			links.add(link);
			TableComponentRow tableRow = TableComponentRow.newInstance(part, link.getOid() + XUIRowId.OBJECT_SEP + part.getOid());
			tableRow.addAttribute("icon", new IconBox(thingModel.getIcon()));
			result.add(tableRow);
		}

		return result;
	}

}
