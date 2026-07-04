package xw.context.builder;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.xui.ArrayComponent;
import com.flame.xui.widget.IconBox;
import com.flame.xui.widget.TextDisplay;
import com.flame.xui.service.TreeComponentNode;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import xw.context.entity.Container;
import xw.context.ContextHelper;
import xw.context.entity.XFolder;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.orm.XPersistable;

import java.util.ArrayList;
import java.util.List;

@UITreeGrid(idField = "oid", treeField = "display", rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "rowId", hidden = true), //
				@UIColumn(field = "oid", hidden = true), //
				@UIColumn(field = "display", width = "300") //
		} //
)
public class FolderListTreeBuilder extends AbstractTreeComponentBuilder {

	@Override
	public List<?> getRootNode(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();

		XObject xobj = commandBean.getPrimaryObj();
		if (xobj instanceof Container) {
			Container container = (Container) xobj;
			TreeComponentNode rootNode = TreeComponentNode.newTreeComponentNode(container);
			rootNode.addAttribute("display", new ArrayComponent(new IconBox("images/email.png"), new TextDisplay(container.getName())));
			result.add(rootNode);

			List<XFolder> list = ContextHelper.repository().listFolder(container);
			for (XFolder folder : list) {
				TreeComponentNode childNode = TreeComponentNode.newTreeComponentNode(folder, folder.getOid());
				childNode.addAttribute("display", new ArrayComponent(new IconBox("images/folder.png"), new TextDisplay(folder.getName())));
				rootNode.addChildren(childNode);
			}
		}
		return result;
	}

	@Override
	public List<?> getNode(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		XUIRowId uiRowId = commandBean.getRowId();
		XPersistable persist = PersistenceHelper.service().refresh(new ObjectReference<XPersistable>(uiRowId.getValue()));
		if (persist instanceof Container) {
			Container container = (Container) persist;
			List<XFolder> list = ContextHelper.repository().listFolder(container);
			for (XFolder folder : list) {
				TreeComponentNode childNode = TreeComponentNode.newTreeComponentNode(folder, folder.getOid());
				childNode.addAttribute("display", new ArrayComponent(new IconBox("images/folder.png"), new TextDisplay(folder.getName())));
				result.add(childNode);
			}
		} else if (persist instanceof XFolder) {
			XFolder folder = (XFolder) persist;
			List<XFolder> list = ContextHelper.repository().listFolder(folder);
			for (XFolder subFolder : list) {
				TreeComponentNode childNode = TreeComponentNode.newTreeComponentNode(subFolder, folder.getOid());
				childNode.addAttribute("display", new ArrayComponent(new IconBox("images/folder.png"), new TextDisplay(subFolder.getName())));
				result.add(childNode);
			}
		}
		return result;
	}

}
