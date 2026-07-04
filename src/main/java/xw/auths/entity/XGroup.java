package xw.auths.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.annotations.XConfig;
import com.flame.annotations.XDefinition;
import com.flame.auths.IGroup;
import com.flame.type.XBaseType;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import xw.auths.XGroupHelper;
import xw.auths.XGroupThing;
import xw.domain.IAdminDomain;
import xw.domain.entity.XAdminDomain;
import xw.team.entity.IRoleHolder;

@Entity
@Table(name = "XGroup", uniqueConstraints = {})
@XDefinition(name = "XGroup", config = XGroupThing.class, icon = "images/group.png", description = "XGroup", display = "Group", en_US = "Group", zh_CN = "组")
public class XGroup extends XPrincipal implements IAdminDomain, IRoleHolder, IGroup {
	private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.EAGER) // EAGER：立即加载(也可设为LAZY懒加载)
	@JoinColumn(name = "groupType", referencedColumnName = "name", nullable = false)
	private GroupTypeRB groupType;
	@ManyToOne
	@JoinColumn(name = "adminDomain", foreignKey = @ForeignKey(name = "ADMIN_DOMAIN_FK"))
	private XAdminDomain adminDomain;
	@Basic
	@Column(name = "scope", length = 100)
	@XConfig(name = "scope", friendlyName = "OAuth2 Scope", baseType = XBaseType.STRING, description = "OAuth2 Scope")
	protected String scope = "";
	@Basic
	@Column(name = "resourceIds", length = 200)
	@XConfig(name = "resourceIds", friendlyName = "Resource Ids", baseType = XBaseType.STRING, description = "Resource Ids")
	protected String resourceIds = "";
	@Basic
	@Column(name = "grantTypes", length = 200)
	@XConfig(name = "grantTypes", friendlyName = "GrantTypes", baseType = XBaseType.STRING, description = "GrantTypes")
	protected String grantTypes = "";
	@Basic
	@Column(name = "redirectUri", length = 300)
	@XConfig(name = "redirectUri", friendlyName = "Redirect URI", baseType = XBaseType.STRING, description = "Redirect URI")
	protected String redirectUri = "";
	@Basic
	@Column(name = "secretRequired")
	@XConfig(name = "secretRequired", friendlyName = "Secret Required", baseType = XBaseType.BOOLEAN, description = "Secret Required")
	protected boolean secretRequired = false;

	public GroupTypeRB getGroupType() {
		return this.groupType;
	}

	public void setGroupType(GroupTypeRB groupType) {
		this.groupType = groupType;
	}

	@JsonIgnore
	public XAdminDomain getAdminDomain() {
		return adminDomain;
	}

	public void setAdminDomain(XAdminDomain domain) {
		this.adminDomain = domain;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public void setScope(String scope) {
		this.scope = scope == null ? "" : scope;
	}

	public void setSecretRequired(boolean secretRequired) {
		this.secretRequired = secretRequired;
	}

	public void setResourceIds(String resourceIds) {
		this.resourceIds = resourceIds == null ? "" : resourceIds;
	}

	public void setGrantTypes(String grantTypes) {
		this.grantTypes = grantTypes == null ? "" : grantTypes;
	}

	@Override
	public List<XUser> members() {
		return XGroupHelper.repository().getUserMember(this);
	}

	@Override
	public Map<RoleRB, List<XUser>> roleMembers() {
		return new HashMap<>();
	}
}
