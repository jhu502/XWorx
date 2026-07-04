package xw.team.entity;

import java.util.List;
import java.util.Map;

import jakarta.persistence.MappedSuperclass;

import com.flame.orm.XObject;

import xw.auths.entity.RoleRB;
import xw.auths.entity.XUser;

@MappedSuperclass
public abstract class AbstractTeam extends XObject implements IRoleHolder {
	private static final long serialVersionUID = 1L;

    public abstract List<XUser> members();

    public abstract Map<RoleRB, List<XUser>> roleMembers();
}
