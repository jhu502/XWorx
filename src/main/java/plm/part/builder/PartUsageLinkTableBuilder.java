package plm.part.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.thing.IThingModel;
import com.flame.thing.ThingModelHelper;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;

import plm.part.XPart;
import plm.part.XPartUsageLink;
import plm.part.service.XPartServiceHelper;

@UIDataGrid(idField = "rowId", actionModel = "XPart:PSB-UsesTablebar", rowNumber = false, singleSelect = false, fit = true, //
		columns = { //
				@UIColumn(field = "rowId", checkbox = true), //
				@UIColumn(field = "checkout", width = "25", align = "left"), //
				@UIColumn(field = "thumbnail", width = "25"), //
				@UIColumn(field = "icon", width = "25", align = "left"), //
				@UIColumn(field = "number", width = "100", sortable = true), //
				@UIColumn(field = "name", width = "150", sortable = true), //
				@UIColumn(field = "version", align = "center", width = "80"), //
				@UIColumn(field = "state", align = "center", width = "80"), //
				@UIColumn(field = "quantity", align = "center", width = "40"), //
				@UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
				@UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
		} //
)
public class PartUsageLinkTableBuilder extends AbstractTableComponentBuilder {
	private static final String THUMBNAIL = "thumbnail";
	private static final String THUMBNAIL_PNG = "images/thumbnailnav.png";

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XPart context = (XPart) commandBean.getPrimaryObj();
		if (context == null)
			return result;
		
		IThingModel thingModel = ThingModelHelper.manager().getThingModel(XPart.class);
		List<?> list = XPartServiceHelper.repository().getUsedbyXPart(context);
		for (Object x : list) {
			Object[] objs = (Object[]) x;
			XPartUsageLink link = (XPartUsageLink) objs[0];
			XPart part = (XPart) objs[1];
			TableComponentRow tableRow = TableComponentRow.newInstance(part, link.getOid() + XUIRowId.OBJECT_SEP + part.getOid());
			tableRow.addAttribute("icon", new IconBox(thingModel.getIcon()));
			tableRow.addAttribute("quantity", link.getQuantity());
			HyperLink linkComp = new HyperLink();
			linkComp.setInnerObject(new IconBox(THUMBNAIL_PNG));
			linkComp.addEvent(HyperLink.ON_CLICK, "showThumbnail('thymeleaf/part/3d/showThumb3d.html?oid=" + part.getOid() + "')");
			tableRow.addAttribute(THUMBNAIL, linkComp);
			result.add(tableRow);
		}
		return result;
	}

}
