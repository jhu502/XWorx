package plm.dynamic.builder;

import java.util.ArrayList;
import java.util.List;

import plm.dynamic.XCaseTable;
import plm.part.XPart;
import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;
import com.flame.xui.service.TableComponentRow;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.orm.PersistenceHelper;

@UIDataGrid(idField = "rowId", actionModel = "XCaseTable:CaseTableToolbar", rowNumber = false, singleSelect = false, fit = true, //
		columns = { //
				@UIColumn(field = "rowId", checkbox = true), //
				@UIColumn(field = "checkout", width = "25", align = "left"), //
				@UIColumn(field = "icon", width = "25", align = "left"), //
				@UIColumn(field = "number", width = "100", sortable = false), //
				@UIColumn(field = "name", width = "150", sortable = false), //
				@UIColumn(field = "description", width = "250", sortable = false), //
				@UIColumn(field = "details", width = "25", align = "center"), //
				@UIColumn(field = "columns", align = "left", width = "400"), //
				@UIColumn(field = "createdStamp", width = "130", align = "center", sortable = false), //
				@UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = false) //
		} //
)
public class CaseTableListTableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XPart part = (XPart) commandBean.getPrimaryObj();
		if (part == null)
			return result;

		List<?> list = PersistenceHelper.service().query("select a from XCaseTable a where a.part.id = :id", new Object[][] { { "id", part.getXid() } });
		for (Object object : list) { //parameter.gif
			XCaseTable caseTable = (XCaseTable) object;
			TableComponentRow tableRow = TableComponentRow.newInstance(caseTable, caseTable.getOid());
			tableRow.addAttribute("icon", new IconBox("images/casetable.png"));
			HyperLink linkComp = new HyperLink();
			linkComp.setInnerObject(new IconBox("images/details.gif"));
			linkComp.addEvent(HyperLink.ON_CLICK, "showCaseTableDetail('" + caseTable.getOid() + "')");
			tableRow.addAttribute("details", linkComp);
			tableRow.addAttribute("columns", caseTable.getHead().toString());
			result.add(tableRow);
		}
		return result;
	}

}
