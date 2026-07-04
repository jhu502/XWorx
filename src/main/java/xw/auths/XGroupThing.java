package xw.auths;

import com.flame.annotations.XDefinition;
import xw.auths.entity.XGroup;

import java.util.List;

@XDefinition(name = "XGroup", config = XGroup.class, icon = "images/group.png", description = "XGroup", display = "Group", en_US = "Group", zh_CN = "组")
public class XGroupThing extends XPrincipalThing {

	public XGroupThing(XGroup target) {
		super(target);
	}

	public List<Object> getMember() {
		return XGroupHelper.service().getMember(this.getThingEntity().getOid());
	}
}
