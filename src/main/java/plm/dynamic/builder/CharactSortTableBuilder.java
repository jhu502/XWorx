package plm.dynamic.builder;

import java.util.ArrayList;
import java.util.List;

import plm.part.XPart;
import plm.dynamic.XCharacteristic;
import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.widget.IconBox;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.service.TableComponentRow;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.orm.PersistenceHelper;

@UIDataGrid(idField = "rowId", toolbar = "sortCharact-tb", rowNumber = false, singleSelect = true, fit = true, pagination = false, //
		columns = { //
				@UIColumn(field = "rowId", checkbox = true), //
				@UIColumn(field = "icon", width = "25", align = "left"), //
				@UIColumn(field = "number", width = "80", sortable = false), //
				@UIColumn(field = "name", width = "100", sortable = false), //
				@UIColumn(field = "description", width = "150", sortable = false), //
				@UIColumn(field = "baseType", align = "center", width = "80"), //
				@UIColumn(field = "inputType", align = "center", width = "80"), //
				@UIColumn(field = "multivalue", align = "center", width = "40"), //
				@UIColumn(field = "createdStamp", width = "130", align = "center", sortable = false), //
				@UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = false) //
		} //
)
public class CharactSortTableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XPart part = (XPart) commandBean.getPrimaryObj();
		if (part == null)
			return result;

		List<?> list = PersistenceHelper.service().query("select a from XCharacteristic a where a.characted.id = :id order by a.sortNo asc", new Object[][] { { "id", part.getXid() } });
		for (Object object : list) { //parameter.gif
			XCharacteristic charact = (XCharacteristic) object;
			TableComponentRow tableRow = TableComponentRow.newInstance(charact, charact.getOid());
			tableRow.addAttribute("icon", new IconBox("images/parameter.gif"));
			HyperLink linkComp = new HyperLink();
			linkComp.setInnerObject(new IconBox("images/details.gif"));
			linkComp.addEvent(HyperLink.ON_CLICK, "showCharactDetail('" + charact.getOid() + "')");
			tableRow.addAttribute("baseType", charact.getBaseType().getDisplay());
			tableRow.addAttribute("inputType", charact.getInputType().getDisplay());
			tableRow.addAttribute("details", linkComp);
			result.add(tableRow);
		}

		return result;
	}

}
