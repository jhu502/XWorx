package xw.domain.builder;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import com.flame.xui.service.TreeComponentNode;
import com.flame.orm.ObjectReference;
import xw.domain.DomainHelper;
import xw.domain.entity.XAdminDomain;

import java.util.ArrayList;
import java.util.List;

@UITreeGrid(idField = "oid", treeField = "name", toolbar = "#hierarchy-tbar", rowNumber = false, fit = true, //
		columns = { //
				@UIColumn(field = "oid", checkbox = true), //
				@UIColumn(field = "name", width = "200px", align = "left"), //
				@UIColumn(field = "description", width = "200px", align = "left"), //
				@UIColumn(field = "modifiedStamp", width = "120px", align = "center") //
		} //
)
public class DomainHierarchyTreeBuilder extends AbstractTreeComponentBuilder {

	@Override
	public List<?> getRootNode(XCommandBean commandBean) {
		List<Object> result = new ArrayList<>();
		XAdminDomain rootDomain = DomainHelper.repository().getRootDomain();
		TreeComponentNode rootNode = TreeComponentNode.newTreeComponentNode(rootDomain);
		result.add(rootNode);
		List<XAdminDomain> list = DomainHelper.repository().queryChildDomain(rootDomain);
		for (Object object : list) {
			XAdminDomain domain = (XAdminDomain) object;
			TreeComponentNode node = TreeComponentNode.newTreeComponentNode(domain);
			rootNode.addChildren(node);
			List<XAdminDomain> children = DomainHelper.repository().queryChildDomain(domain);
			for (Object obj : children) {
				node.addChildren(TreeComponentNode.newTreeComponentNode(obj));
			}
		}
		return result;
	}

	@Override
	public List<?> getNode(XCommandBean commandBean) {
	    XUIRowId uiRowId = commandBean.getRowId();
		ObjectReference<XAdminDomain> domainRef = new ObjectReference<>(uiRowId.getValue());
		return DomainHelper.repository().queryChildDomain(domainRef);
	}
}
