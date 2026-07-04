package plm.part.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.HREFactory;
import com.flame.xui.XCommandBean;
import com.flame.orm.PersistenceHelper;
import com.flame.thing.IThingModel;
import com.flame.thing.ThingModelHelper;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;

import plm.part.XPart;

@UIDataGrid(idField = "oid", rowNumber = false, singleSelect = false, fit = true, //
		columns = { //
				@UIColumn(field = "oid", checkbox = true), //
				@UIColumn(field = "checkout", width = "25", align = "left"), //
				@UIColumn(field = "icon", width = "25", align = "left"), //
				@UIColumn(field = "number", title = "Number", width = "120", sortable = true), //
				@UIColumn(field = "name", title = "Name", width = "150", sortable = true), //
				@UIColumn(field = "version", title = "Version", align = "center", width = "80"), //
				@UIColumn(field = "status", title = "Status", align = "center", width = "80"), //
				@UIColumn(field = "modifiedStamp", title = "Last Modified", width = "130", align = "center", sortable = true) //
		} //
)
public class PartSearchTableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		IThingModel thingModel = ThingModelHelper.manager().getThingModel(XPart.class);

		List<XPart> list = PersistenceHelper.service().query(XPart.class, new Object[0][0]);
		for (XPart xpart : list) {
			TableComponentRow tableRow = TableComponentRow.newInstance(xpart);
			tableRow.addAttribute("icon", new IconBox(thingModel.getIcon()));
			HyperLink linkComp = new HyperLink();
			linkComp.setInnerObject(new IconBox("images/details.gif"));
			linkComp.setUrl(HREFactory.hashInfoPage(xpart));
			tableRow.addAttribute("details", linkComp);
			result.add(tableRow);
		}
		return result;
	}

}
