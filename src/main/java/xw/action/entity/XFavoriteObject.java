package xw.action.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

import com.flame.orm.ObjectReference;
import com.flame.orm.XObject;

import xw.auths.entity.XUser;

@MappedSuperclass
public abstract class XFavoriteObject<T extends XObject> extends XObject {
	private static final long serialVersionUID = 1L;
	@Basic
	@Embedded
	@AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "favorite_id")), @AttributeOverride(name = "className", column = @Column(name = "favorite_classname"))})
	private ObjectReference<T> favorite = null;
	@ManyToOne
	@JoinColumn(name = "creatorId", foreignKey = @ForeignKey(name = "CREATOR_ID_FK"))
	private XUser creator;

	public ObjectReference<T> getFavorite() {
		return favorite;
	}

	public T getFavoriteObject() {
		return this.favorite.getObject();
	}

	public void setFavorite(ObjectReference<T> favorite) {
		this.favorite = favorite;
	}

	public XUser getCreator() {
		return creator;
	}

	public void setCreator(XUser creator) {
		this.creator = creator;
	}
}
