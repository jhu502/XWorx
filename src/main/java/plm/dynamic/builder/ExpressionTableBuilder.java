package plm.dynamic.builder;

import java.util.ArrayList;
import java.util.List;

import plm.dynamic.XExpression;
import plm.dynamic.service.DynamicServiceHelper;
import plm.part.XPart;
import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;
import com.flame.xui.service.TableComponentRow;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;

@UIDataGrid(idField = "rowId", actionModel = "XExpression:ExpressToolbar", rowNumber = false, singleSelect = false, fit = true, //
		columns = { //
				@UIColumn(field = "rowId", checkbox = true), //
				@UIColumn(field = "checkout", width = "25", align = "left"), //
				@UIColumn(field = "icon", width = "25", align = "left"), //
				@UIColumn(field = "number", width = "100", sortable = false), //
				@UIColumn(field = "name", width = "150", sortable = false), //
				@UIColumn(field = "description", width = "250", sortable = false), //
				@UIColumn(field = "details", width = "25", align = "center"), //
				@UIColumn(field = "expression", align = "left", width = "400"), //
				@UIColumn(field = "createdStamp", width = "130", align = "center", sortable = false), //
				@UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = false) //
		} //
)
public class ExpressionTableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XPart part = (XPart) commandBean.getPrimaryObj();
		if (part == null)
			return result;

		List<XExpression> list = DynamicServiceHelper.repository().getRelatedXExpression(part);
		for (XExpression express : list) {
			TableComponentRow tableRow = TableComponentRow.newInstance(express, express.getOid());
			tableRow.addAttribute("icon", new IconBox("images/parameter.gif"));
			HyperLink linkComp = new HyperLink();
			linkComp.setInnerObject(new IconBox("images/details.gif"));
			linkComp.addEvent(HyperLink.ON_CLICK, "showCharactDetail('" + express.getOid() + "')");
			tableRow.addAttribute("expression", express.getExpression());
			tableRow.addAttribute("details", linkComp);
			result.add(tableRow);
		}

		return result;
	}
}
