package plm.part.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.PropertyComponent;
import com.flame.xui.service.TableComponentRow;

import plm.part.XPart;

@UIDataGrid(groupField = "group", fit = true, //
		columns = { //
				@UIColumn(field = "property", hidden = true), //
				@UIColumn(field = "display", width = "150", sortable = true), //
				@UIColumn(field = "value", width = "300", sortable = true), //
				@UIColumn(field = "group", hidden = true) //
		} //
) //
public class XPartDetailPropertyBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XPart xpart = (XPart) commandBean.getPrimaryObj();
		if (xpart == null)
			return result;

		PropertyComponent numberProperty = PropertyComponent.newPropertyComponent(xpart, "number", "Basic");
		result.add(TableComponentRow.newInstance(numberProperty));
		PropertyComponent nameProperty = PropertyComponent.newPropertyComponent(xpart, "name", "Basic");
		result.add(TableComponentRow.newInstance(nameProperty));

		return result;
	}

}
