package plm.dynamic.builder;

import java.util.ArrayList;
import java.util.List;

import plm.dynamic.InputType;
import plm.dynamic.XCharacteristic;
import plm.dynamic.service.DynamicServiceHelper;
import plm.part.XPart;
import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;
import com.flame.xui.service.TableComponentRow;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;

@UIDataGrid(idField = "rowId", actionModel = "XCharacter:CharactToolbar", rowNumber = false, singleSelect = false, fit = true, //
		columns = { //
				@UIColumn(field = "rowId", checkbox = true), //
				@UIColumn(field = "flag", width = "25", align = "left"), //
				@UIColumn(field = "icon", width = "25", align = "left"), //
				@UIColumn(field = "number", width = "100", sortable = false), //
				@UIColumn(field = "name", width = "150", sortable = false), //
				@UIColumn(field = "description", width = "250", sortable = false), //
				@UIColumn(field = "details", width = "25", align = "center"), //
				@UIColumn(field = "baseType", align = "center", width = "80"), //
				@UIColumn(field = "optionMode", align = "center", width = "80"), //
				@UIColumn(field = "inputType", align = "center", width = "80"), //
				@UIColumn(field = "multivalue", align = "center", width = "40"), //
				@UIColumn(field = "createdStamp", width = "130", align = "center", sortable = false), //
				@UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = false) //
		} //
)
public class CharacteristicsTableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XPart part = (XPart) commandBean.getPrimaryObj();
		if (part == null)
			return result;

		List<XCharacteristic> list = DynamicServiceHelper.repository().getSortedXCharacteristic(part);
		for (XCharacteristic charact : list) {
			TableComponentRow tableRow = TableComponentRow.newInstance(charact, charact.getOid());
			if (InputType.REQUIRED.equals(charact.getInputType())) {
				tableRow.addAttribute("flag", new IconBox("images/required_star.png"));
			}
			tableRow.addAttribute("icon", new IconBox("images/characteristic.png"));
			HyperLink linkComp = new HyperLink();
			linkComp.setInnerObject(new IconBox("images/details.gif"));
			linkComp.addEvent(HyperLink.ON_CLICK, "javascript:showCharactDetail('" + charact.getOid() + "')");
			tableRow.addAttribute("baseType", charact.getBaseType().getDisplay());
			tableRow.addAttribute("optionMode", charact.getOptionMode().getDisplay());
			tableRow.addAttribute("inputType", charact.getInputType().getDisplay());
			tableRow.addAttribute("details", linkComp);
			result.add(tableRow);
		}

		return result;
	}

}
