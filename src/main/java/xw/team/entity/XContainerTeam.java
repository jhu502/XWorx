package xw.team.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import xw.auths.entity.RoleRB;
import xw.auths.entity.XUser;

@Entity
@Table(name = "XContainerTeam", uniqueConstraints = {})
public class XContainerTeam extends AbstractTeam {
	private static final long serialVersionUID = 1L;
	
	public static XContainerTeam newInstance() {
	    XContainerTeam team = new XContainerTeam();
	    
	    return team;
	}

	public XContainerTeam() {
	}

	@Override
	public List<XUser> members() {
		return new ArrayList<>();
	}

	@Override
	public Map<RoleRB, List<XUser>> roleMembers() {
		return new HashMap<>();
	}
}
