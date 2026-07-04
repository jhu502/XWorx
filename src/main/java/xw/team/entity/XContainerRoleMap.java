package xw.team.entity;

import com.flame.orm.ObjectToObjectLink;

import jakarta.persistence.*;
import xw.auths.entity.RoleRB;
import xw.auths.entity.XGroup;

@Entity
@Table(name = "XContainerRoleMap", uniqueConstraints = {})
public class XContainerRoleMap extends ObjectToObjectLink<XContainerTeam, RoleRB> {
	private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.EAGER) // EAGER：立即加载(也可设为LAZY懒加载)
    @JoinColumn(name = "groupId", foreignKey = @ForeignKey(name = "GROUP_ID_FK"))
	private XGroup group;

	public static XContainerRoleMap newInstance(XContainerTeam team, RoleRB role, XGroup group) {
		XContainerRoleMap teamRoleMap = new XContainerRoleMap();
		teamRoleMap.setLeftObject(team);
		teamRoleMap.setRightObject(role);
		teamRoleMap.setGroup(group);

		return teamRoleMap;
	}

	public XContainerTeam getTeam() {
		return this.getLeftObject();
	}

	public void setTeam(XContainerTeam team) {
		this.setLeftObject(team);
	}

	public RoleRB getRole() {
		return this.getRightObject();
	}

	public void setRole(RoleRB role) {
		this.setRightObject(role);
	}

	public XGroup getGroup() {
		return group;
	}

	public void setGroup(XGroup group) {
		this.group = group;
	}
}
