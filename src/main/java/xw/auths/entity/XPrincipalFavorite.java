package xw.auths.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flame.auths.SessionHelper;
import com.flame.orm.ObjectReference;

import xw.action.entity.XFavoriteObject;

@Entity
@Table(name = "XPrincipalFavorite", uniqueConstraints = {})
public class XPrincipalFavorite extends XFavoriteObject<XPrincipal> {
	private static final long serialVersionUID = 1L;

	public static XPrincipalFavorite newPrincipalFavorite(XPrincipal principal) {
		XPrincipalFavorite favoriteObject = new XPrincipalFavorite();
		favoriteObject.setFavorite(ObjectReference.newObjectReference(principal));
		favoriteObject.setCreator((XUser) SessionHelper.getCurrentUser());

		return favoriteObject;
	}
}
