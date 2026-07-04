package xw.auths.builder;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.xui.ArrayComponent;
import com.flame.xui.WinType;
import com.flame.annotations.UIAction;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import com.flame.xui.service.TreeComponentNode;
import com.flame.xui.widget.IconBox;
import com.flame.xui.widget.TextDisplay;
import com.flame.localize.LocalizationHelper;
import xw.auths.XGroupHelper;
import xw.auths.entity.XGroup;
import xw.auths.entity.XUser;

import java.util.ArrayList;
import java.util.List;

@UITreeGrid(idField = "oid", treeField = "identity", contextMenu = "GroupHierarchy_menu", rowNumber = false, fit = true, //
		actions = {
				@UIAction(name = "add", processor = "xw.auths.processor.AddPrincipalProcessor", url = "thymeleaf/auths/addPrincipal.html", icon = "images/add16x16.gif", winType = WinType.popup, style = "width:680px;height:500px;padding:5px;"), //
				@UIAction(name = "newGroup", processor = "xw.auths.processor.CreateGroupProcessor", url = "thymeleaf/auths/newGroup.html", icon = "images/new_group.gif", winType = WinType.popup, style = "width:680px;height:500px;padding:5px;"), //
				@UIAction(name = "newUser", processor = "xw.auths.processor.CreateGroupProcessor", url = "thymeleaf/auths/newUser.html", icon = "images/new_user.gif", winType = WinType.popup, style = "width:680px;height:500px;padding:5px;"), //
				@UIAction(name = "delete", processor = "xw.object.processor.DeleteObjectProcessor", icon = "images/delete.png", beforeJS = "return flame.validateSelect(p, true);", winType = WinType.invoke), //
				@UIAction(name = "remove", processor = "xw.auths.processor.RemoveFavoriteProcessor", icon = "images/remove16x16.gif", beforeJS = "return flame.validateSelect(p, true);", winType = WinType.invoke), //
		},
		columns = { //
				@UIColumn(field = "oid", checkbox = true), //
				@UIColumn(field = "identity", width = "200"), //
				@UIColumn(field = "fullName", width = "130"), //
				@UIColumn(field = "groupType", width = "50", align = "center"), //
				@UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
		} //
)
public class PrincipalHierarchyTreeBuilder extends AbstractTreeComponentBuilder { //

	@Override
	public List<?> getRootNode(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		List<XGroup> list = XGroupHelper.repository().getRootGroup();
		if (list != null && !list.isEmpty()) {
			XGroup rootGroup = list.get(0);
			TreeComponentNode rootNode = TreeComponentNode.newTreeComponentNode(rootGroup);
			rootNode.addAttribute("identity", new ArrayComponent(new IconBox(rootGroup.getIcon()), new TextDisplay(rootGroup.getName())));
			rootNode.addAttribute("groupType", rootGroup.getGroupType().getDisplay(LocalizationHelper.getLocale()));
			result.add(rootNode);

			List<Object> members = XGroupHelper.service().getMember(rootGroup.getOid());
			for (Object obj : members) {
				if (obj instanceof XGroup) {
					XGroup group = (XGroup) obj;
					TreeComponentNode node = TreeComponentNode.newTreeComponentNode(group);
					node.addAttribute("identity", new ArrayComponent(new IconBox(group.getIcon()), new TextDisplay(group.getName())));
					node.addAttribute("groupType", group.getGroupType().getDisplay(LocalizationHelper.getLocale()));
					rootNode.addChildren(node);
				} else if (obj instanceof XUser) {
					XUser user = (XUser) obj;
					TreeComponentNode node = TreeComponentNode.newTreeComponentNode(user);
					node.addAttribute("identity", new ArrayComponent(new IconBox(user.getIcon()), new TextDisplay(user.getName())));
					node.setLeaf(true);
					rootNode.addChildren(node);
				}
			}
		}

		return result;
	}

	@Override
	public List<?> getNode(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		XUIRowId uiRowId = commandBean.getRowId();
		List<Object> members = XGroupHelper.service().getMember(uiRowId.getValue());
		for (Object obj : members) {
			if (obj instanceof XGroup) {
				XGroup group = (XGroup) obj;
				TreeComponentNode node = TreeComponentNode.newTreeComponentNode(group);
				node.addAttribute("identity", new ArrayComponent(new IconBox(group.getIcon()), new TextDisplay(group.getName())));
				node.addAttribute("groupType", group.getGroupType().getDisplay(LocalizationHelper.getLocale()));
				result.add(node);
			} else if (obj instanceof XUser) {
				XUser user = (XUser) obj;
				TreeComponentNode node = TreeComponentNode.newTreeComponentNode(user);
				node.addAttribute("identity", new ArrayComponent(new IconBox(user.getIcon()), new TextDisplay(user.getName())));
				node.setLeaf(true);
				result.add(node);
			}
		}
		return result;
	}

}
