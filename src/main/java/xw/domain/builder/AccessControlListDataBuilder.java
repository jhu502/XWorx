package xw.domain.builder;

import com.flame.xui.XCommandBean;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractComponentBuilder;

@UIDataGrid(idField = "oid", toolbar = "#context-tbar", rowNumber = false, fit = true,
		columns = {
				@UIColumn(field = "oid", checkbox = true),
				@UIColumn(field = "name", width = "100", align = "left"),
				@UIColumn(field = "description", width = "80", align = "center"),
				@UIColumn(field = "creator", width = "80", align = "center"),
				@UIColumn(field = "createdStamp", width = "90", align = "center"),
				@UIColumn(field = "modifiedStamp", width = "30%", align = "center")
		}
)
public class AccessControlListDataBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(XCommandBean commandBean) {
		return null;
	}

}
