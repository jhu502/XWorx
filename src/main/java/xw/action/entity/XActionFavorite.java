package xw.action.entity;

import com.flame.auths.SessionHelper;
import com.flame.orm.ObjectReference;
import com.flame.orm.XObject;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import xw.auths.entity.XUser;

@Entity
@Table(name = "XActionFavorite", uniqueConstraints = {})
public class XActionFavorite extends XFavoriteObject<XObject> {
	private static final long serialVersionUID = 1L;

	public static XActionFavorite newActionFavorite(XObject xObject) {
		XActionFavorite favoriteObject = new XActionFavorite();
		favoriteObject.setFavorite(ObjectReference.newObjectReference(xObject));
		favoriteObject.setCreator((XUser) SessionHelper.getCurrentUser());

		return favoriteObject;
	}
}
