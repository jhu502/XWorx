package xw.auths.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flame.orm.ObjectReference;
import com.flame.orm.ObjectToObjectLink;

@Entity
@Table(name = "XGroupGroupLink", uniqueConstraints = {})
public class XGroupGroupLink extends ObjectToObjectLink<XGroup, XGroup> {
	private static final long serialVersionUID = 1L;
	
	public static XGroupGroupLink newGroupGroupLink(XGroup left, XGroup right) {
		XGroupGroupLink gglink = new XGroupGroupLink();
		gglink.setLeft(new ObjectReference<XGroup>(left));
		gglink.setRight(new ObjectReference<XGroup>(right));
		return gglink;
	}

}
