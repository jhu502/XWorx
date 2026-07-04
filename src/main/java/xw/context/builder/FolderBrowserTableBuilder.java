package xw.context.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.flame.xui.HREFactory;
import com.flame.xui.XCommandBean;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.orm.XPersistable;
import com.flame.thing.IThingModel;
import com.flame.thing.ThingModelHelper;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractTableComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import com.flame.xui.widget.HyperLink;
import com.flame.xui.widget.IconBox;

import xw.context.entity.Container;
import xw.context.ContextHelper;
import xw.context.entity.XFolder;
import xw.vc.VersionControlled;

@UIDataGrid(idField = "rowId", actionModel = "XFolder:folderToolbar", rowNumber = false, singleSelect = false, fit = true, //
		columns = { //
				@UIColumn(field = "rowId", checkbox = true), //
				@UIColumn(field = "checkout", title = "", width = "25", align = "left"), //
				@UIColumn(field = "icon", title = "", width = "25", align = "left"), //
				@UIColumn(field = "number", width = "150", sortable = true), //
				@UIColumn(field = "name", width = "150", sortable = true), //
				@UIColumn(field = "details", title = "", width = "25", align = "center"), //
				@UIColumn(field = "version", align = "center", width = "80"), //
				@UIColumn(field = "status", align = "center", width = "80"), //
				@UIColumn(field = "creatorName", align = "center", width = "80"), //
				@UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
				@UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
		} //
)
public class FolderBrowserTableBuilder extends AbstractTableComponentBuilder {

	@Override
	public List<?> getTableRows(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XObject primary = commandBean.getPrimaryObj();
		if (primary instanceof Container) {
			Container container = (Container) primary;
			List<XFolder> list = ContextHelper.repository().listFolder(container);
			for (XFolder subFolder : list) {
				TableComponentRow tableRow = TableComponentRow.newInstance(subFolder);
				tableRow.addAttribute("icon", new IconBox("images/folder.png"));
				HyperLink linkComp = new HyperLink();
				linkComp.setInnerObject(new IconBox("images/details.gif"));
				linkComp.addEvent(HyperLink.ON_CLICK, "enterFolder('" + subFolder.getOid() + "')");
				tableRow.addAttribute("details", linkComp);
				result.add(tableRow);
			}

			Map<Class<?>, IThingModel> modelCache = new HashMap<>();

			Set<Class<? extends XPersistable>> classSet = PersistenceHelper.service().getEntityClass(VersionControlled.class);
			for (Class<?> clazz : classSet) {
				List<?> objList = PersistenceHelper.service().query(clazz, new Object[][] { { "containerRef.id", container.getXid() } });

				for (Object object : objList) {
					if (!(object instanceof VersionControlled)) {
						continue;
					}

					VersionControlled<?> version = (VersionControlled<?>) object;
					IThingModel thingModel = modelCache.get(version.getClass());
					if (thingModel == null) {
						thingModel = ThingModelHelper.manager().getThingModel(version.getClass());
						modelCache.put(version.getClass(), thingModel);
					}
					TableComponentRow tableRow = TableComponentRow.newInstance(version);
					tableRow.addAttribute("icon", new IconBox(thingModel.getIcon()));
					HyperLink linkComp = new HyperLink();
					linkComp.setInnerObject(new IconBox("images/details.gif"));
					linkComp.setUrl(HREFactory.hashInfoPage(version));
					//linkComp.setOnClick("alert()");
					tableRow.addAttribute("details", linkComp);
					result.add(tableRow);
				}
			}

		} else if (primary instanceof XFolder) {
			XFolder folder = (XFolder) primary;
			List<XFolder> list = ContextHelper.repository().listFolder(folder);
			for (XFolder subFolder : list) {
				TableComponentRow tableRow = TableComponentRow.newInstance(subFolder);
				tableRow.addAttribute("icon", new IconBox("images/folder.png"));
				HyperLink linkComp = new HyperLink();
				linkComp.setInnerObject(new IconBox("images/details.gif"));
				linkComp.addEvent(HyperLink.ON_CLICK, "enterFolder('" + subFolder.getOid() + "')");
				tableRow.addAttribute("details", linkComp);
				result.add(tableRow);
			}
		}
		return result;
	}

}
