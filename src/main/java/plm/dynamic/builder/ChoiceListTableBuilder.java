package plm.dynamic.builder;

import java.util.ArrayList;
import java.util.List;

import plm.dynamic.XCharacteristic;
import plm.dynamic.bean.XChoice;
import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;
import com.flame.xui.service.TableComponentRow;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.annotations.UIWidget;
import com.flame.xui.WidgetType;
import com.flame.orm.XObject;

@UIDataGrid(toolbar = "choiceList-tb", rowNumber = true, singleSelect = true, fit = true, pagination = false, //
		columns = { //
				@UIColumn(field = "value", width = "200", widget = { @UIWidget(type = WidgetType.TextBox) }, sortable = false), //
				@UIColumn(field = "description", width = "350", widget = { @UIWidget(type = WidgetType.TextBox) }, sortable = false), //
				@UIColumn(field = "button", width = "30", sortable = false), //
				@UIColumn(field = "statement", hidden = true) //
		} //
)
public class ChoiceListTableBuilder extends AbstractTableComponentBuilder {
	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		XObject primary = commandBean.getPrimaryObj();
		if (primary == null)
			return result;
		if (primary instanceof XCharacteristic) {
			XCharacteristic charact = (XCharacteristic) primary;
			List<XChoice> choiceList = charact.getChoices();
			for (XChoice choice : choiceList) {
				TableComponentRow tableRow = TableComponentRow.newInstance(choice);
				HyperLink linkComp = new HyperLink();
				linkComp.setInnerObject(new IconBox("images/statement.png"));
				linkComp.addEvent(HyperLink.ON_CLICK, "alert('')");
				tableRow.addAttribute("button", linkComp);
				result.add(tableRow);
			}
		}

		return result;
	}
}
