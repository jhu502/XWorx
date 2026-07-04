package xw.auths.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.flame.orm.ObjectReference;
import com.flame.orm.ObjectToObjectLink;

@Entity
@Table(name = "XGroupUserLink", uniqueConstraints = {})
public class XGroupUserLink extends ObjectToObjectLink<XGroup, XUser> {
	private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.EAGER) // EAGER：立即加载(也可设为LAZY懒加载)
    @JoinColumn(name = "role", referencedColumnName = "name", nullable = false)
	private RoleRB role;

	public static XGroupUserLink newGroupUserLink(XGroup left, XUser right) {
		XGroupUserLink gulink = new XGroupUserLink();
		gulink.setLeft(new ObjectReference<XGroup>(left));
		gulink.setRight(new ObjectReference<XUser>(right));
		return gulink;
	}

	public RoleRB getRole() {
		return this.role;
	}

	public void setRole(RoleRB role) {
		this.role = role;
	}
}
