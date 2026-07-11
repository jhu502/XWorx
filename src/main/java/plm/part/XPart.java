package plm.part;

import com.flame.annotations.XDefinition;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import plm.dynamic.ICharacted;
import xw.vc.VersionControlled;

@Entity
@Table(name = "XPart", uniqueConstraints = {})
@XDefinition(name = "XPart", config = XPartThing.class, icon = "images/part.png", description = "XPart", display = "Part", en_US = "Part", zh_CN = "部件")
public class XPart extends VersionControlled<XPartMaster> implements ICharacted {
	private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.EAGER) // EAGER：立即加载(也可设为LAZY懒加载)
    @JoinColumn(name = "view", referencedColumnName = "name", nullable = false)
	private ViewRB view;
	@Basic
	@Column(name = "endItem")
	private boolean endItem = false;
	@Basic
	@Column(name = "collapsible")
	private boolean collapsible = false;
	@ManyToOne(targetEntity = XPartMaster.class)
	@JoinColumn(name = "masterId", foreignKey = @ForeignKey(name = "MASTER_ID_FK"))
	protected XPartMaster master;

	public static XPart newInstance(String number, String name) {
		XPartMaster master = new XPartMaster();
		master.setGenericType(GenericType.Dynamic);
		XPart xpart = new XPart(master);
		xpart.setNumber(number);
		xpart.setName(name);

		return xpart;
	}

	public XPart() {
	}

	private XPart(XPartMaster master) {
		this.setMaster(master);
	}

	@Override
	public XPartMaster getMaster() {
		return this.master;
	}

	@Override
	public void setMaster(XPartMaster master) {
		this.master = master;
	}

	public ViewRB getView() {
		return view;
	}

	public void setView(ViewRB view) {
		this.view = view;
	}

	public boolean isEndItem() {
		return endItem;
	}

	public void setEndItem(boolean endItem) {
		this.endItem = endItem;
	}

	public boolean isCollapsible() {
		return this.collapsible;
	}

	public void setCollapsible(boolean bool) {
		this.collapsible = bool;
	}

	public String getDisplay() {
		return this.getNumber() + "," + this.getName();
	}
}
