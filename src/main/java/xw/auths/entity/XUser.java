package xw.auths.entity;

import java.util.Collection;

import com.flame.auths.IAuthUser;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import com.flame.type.ServiceType;
import com.flame.annotations.XConfig;
import com.flame.annotations.XDefinition;
import com.flame.annotations.XService;
import com.flame.type.XBaseType;

import xw.auths.XPrincipalThing;
import xw.context.entity.XOrganization;

/**
 * UserDetails是SpringBoot进行登录认证的接口，Flamethrower需要使用XUser定义的信息进行
 * 登录认证，所以需要实现UserDetails接口
 * 
 * @author Hujin
 *
 */

@Entity
@Table(name = "XUser", uniqueConstraints = {})
@XDefinition(name = "XUser", config = XPrincipalThing.class, icon = "images/person.png", description = "XUser", display = "User", en_US = "User", zh_CN = "用户")
public class XUser extends XPrincipal implements IAuthUser {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "password", length = 100)
	@XConfig(name = "password", friendlyName = "Password", baseType = XBaseType.PASSWORD, description = "Password")
	protected String password = "";
	@Basic
	@Column(name = "expired")
	@XConfig(name = "expired", friendlyName = "Expired", baseType = XBaseType.BOOLEAN, description = "Expired")
	protected boolean expired = false;
	@Basic
	@Column(name = "locked")
	@XConfig(name = "locked", friendlyName = "Locked", baseType = XBaseType.BOOLEAN, description = "Locked")
	protected boolean locked = false;
	@Basic
	@Column(name = "enabled")
	@XConfig(name = "enabled", friendlyName = "Enabled", baseType = XBaseType.BOOLEAN, description = "Enabled")
	protected boolean enabled = true;
	@ManyToOne(targetEntity = XOrganization.class)
	@JoinColumn(name = "orgId", foreignKey = @ForeignKey(name = "ORGANIZATION_ID_FK"))
	private XOrganization organization;

	private transient Collection<GrantedAuthority> authorities;

	@XService(name = "getUsername", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getUsername() {
		return this.getName();
	}

	@XService(name = "getPassword", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void authorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return !this.expired;
	}
	
	@XService(name = "isExpired", serviceType = ServiceType.Local, resultType = XBaseType.BOOLEAN)
	public boolean isExpired() {
		return this.expired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !this.locked;
	}
	
	@XService(name = "isLocked", serviceType = ServiceType.Local, resultType = XBaseType.BOOLEAN)
	public boolean isLocked() {
		return this.locked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	@XService(name = "isEnabled", serviceType = ServiceType.Local, resultType = XBaseType.BOOLEAN)
	public boolean isEnabled() {
		return enabled;
	}

	public XOrganization getOrganization() {
		return organization;
	}

	public void setOrganization(XOrganization organization) {
		this.organization = organization;
	}

	public String toString() {
		return this.getUsername();
	}
}
