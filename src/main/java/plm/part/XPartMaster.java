package plm.part;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import com.flame.vc.Master;

@Entity
@Table(name = "XPartMaster", uniqueConstraints = {})
public class XPartMaster extends Master {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "endItem", nullable = false)
	private boolean endItem = false;
	@Basic
	@Column(name = "collapsible", nullable = false)
	private boolean collapsible = false;
	@Basic
	@Column(name = "phantom", nullable = false)
	private boolean phantom = false;
	@Enumerated(EnumType.STRING)
	@Column(name = "generic_type", nullable = false)
	private GenericType genericType = GenericType.Standard;

	public boolean isPhantom() {
		return phantom;
	}

	public void setPhantom(boolean phantom) {
		this.phantom = phantom;
	}

	public boolean isEndItem() {
		return endItem;
	}

	public void setEndItem(boolean endItem) {
		this.endItem = endItem;
	}

	public boolean isCollapsible() {
		return collapsible;
	}

	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
	}

	public GenericType getGenericType() {
		return genericType;
	}

	public void setGenericType(GenericType genericType) {
		this.genericType = genericType;
	}

}
