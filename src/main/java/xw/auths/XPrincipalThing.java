package xw.auths;

import com.flame.type.ServiceType;
import com.flame.annotations.XParam;
import com.flame.annotations.XService;
import com.flame.type.XBaseType;
import com.thing.ThingEntityHelper;
import com.thing.common.AbstractThingModel;
import com.thing.common.IThingManaged;
import xw.auths.entity.XGroup;
import xw.auths.entity.XPrincipal;

import java.util.Set;

public abstract class XPrincipalThing extends AbstractThingModel<XPrincipal> {

	public XPrincipalThing(XPrincipal target) {
		super(target);
	}

	public Set<XGroup> getUseGroup() {
		return XGroupHelper.service().listParentGroup(this.getThingEntity().getOid());
	}

	@XService(name = "sendMessage", serviceType = ServiceType.Local, resultType = XBaseType.STRING, //
			params = {@XParam(name = "userName", type = XBaseType.STRING), @XParam(name = "message", type = XBaseType.STRING)} //
	)
	public String sendMessage(String userName, String message) {
		IThingManaged<?> ftuser = ThingEntityHelper.dispatch().getInflatedThingEntity("XUser:" + userName);
		return (String) ftuser.invokeMember("receiveMessage", message);
	}

	@XService(name = "receiveMessage", serviceType = ServiceType.Remote, resultType = XBaseType.STRING, params = {@XParam(name = "message", type = XBaseType.STRING)})
	public void receiveMessage(String message) {
	}
}
