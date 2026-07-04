package xw.context.entity;

import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;

import com.thing.entity.ModeledEntity;

import xw.auths.entity.XUser;
import xw.context.IContainer;
import xw.domain.entity.XAdminDomain;
import xw.team.entity.XContainerTeam;

@MappedSuperclass
public abstract class Container extends ModeledEntity implements IContainer<XContainerTeam> {
	private static final long serialVersionUID = 1L;
	@OneToOne
	@JoinColumn(name = "team", foreignKey = @ForeignKey(name = "TEAM_FK"))
	private XContainerTeam team;
	@ManyToOne
	@JoinColumn(name = "adminDomain", foreignKey = @ForeignKey(name = "ADMIN_DOMAIN_FK"))
	private XAdminDomain adminDomain;
	@ManyToOne
	@JoinColumn(name = "creatorId", foreignKey = @ForeignKey(name = "CREATOR_ID_FK"))
	private XUser creator;

	public XContainerTeam getTeam() {
		return this.team;
	}

	public void setTeam(XContainerTeam team) {
		this.team = team;
	}

	public XAdminDomain getAdminDomain() {
		return adminDomain;
	}

	public void setAdminDomain(XAdminDomain domain) {
		this.adminDomain = domain;
	}

	public XUser getCreator() {
		return creator;
	}

	public void setCreator(XUser creator) {
		this.creator = creator;
	}

	public String getCreatorName() {
		return this.getCreator().getName();
	}
}
