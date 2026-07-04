package xw.team.entity;

import xw.auths.entity.RoleRB;
import xw.auths.entity.XUser;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IRoleHolder extends Serializable {
    List<XUser> members();

    Map<RoleRB, List<XUser>> roleMembers();
}
