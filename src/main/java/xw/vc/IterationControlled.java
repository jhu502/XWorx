package xw.vc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.type.ServiceType;
import com.flame.annotations.XDefinition;
import com.flame.annotations.XService;
import com.flame.auths.ICreatorInfo;
import com.flame.orm.AbstractEntity;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.thing.IModelManaged;
import com.flame.thing.IThingModel;
import com.flame.type.XBaseType;
import com.flame.vc.CheckOutInfo;
import com.flame.vc.IAttributes;
import com.flame.vc.Iterated;
import com.flame.vc.Master;
import com.thing.entity.XThingModel;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import xw.auths.entity.XUser;
import xw.context.entity.Container;
import xw.context.IContained;

@MappedSuperclass
public abstract class IterationControlled<T extends Master> extends AbstractEntity implements IContained, Iterated<T>, IModelManaged, ICreatorInfo<XUser>, IAttributes {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "description", length = 1000)
	private String description = "";
	@Basic
	@Column(name = "latest")
	private boolean latest = false;
	@Basic
	@Column(name = "previous")
	private long previous = -1;
	@Column(name = "checkout_info")
	@Enumerated(EnumType.STRING)
	private CheckOutInfo checkOutInfo = CheckOutInfo.ci;
	@ManyToOne
	@JoinColumn(name = "creator_id", foreignKey = @ForeignKey(name = "CREATOR_ID_FK"))
	private XUser creator;
	@ManyToOne(targetEntity = XThingModel.class)
	@JoinColumn(name = "modelId", nullable = false, foreignKey = @ForeignKey(name = "THINGMODEL_FK"))
	private IThingModel thingModel;
	@Basic
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "container_id")), @AttributeOverride(name = "className", column = @Column(name = "container_classname")) })
	private ObjectReference<Container> containerRef;

	@Override
	public String getNumber() {
		return this.getMaster().getNumber();
	}

	@Override
	public void setNumber(String number) {
		this.getMaster().setNumber(number);
	}

	@Override
	public String getName() {
		return this.getMaster().getName();
	}

	@Override
	public void setName(String name) {
		this.getMaster().setName(name);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean isLatest() {
		return this.latest;
	}

	@Override
	public void setLatest(boolean bool) {
		this.latest = bool;
	}

	public long getPrevious() {
		return previous;
	}

	public XObject getPreviousObj() {
		return PersistenceHelper.service().refresh(new ObjectReference<XObject>(this.getClass(), this.getPrevious()));
	}

	public void setPrevious(long previous) {
		this.previous = previous;
	}

	public CheckOutInfo getCheckOutInfo() {
		return checkOutInfo;
	}

	public void setCheckOutInfo(CheckOutInfo checkOutInfo) {
		this.checkOutInfo = checkOutInfo;
	}

	@Override
	public Container getContainer() {
		return PersistenceHelper.service().refresh(this.containerRef);
	}

	public ObjectReference<Container> getContainerRef() {
		return containerRef;
	}

	public void setContainerRef(ObjectReference<Container> containerRef) {
		this.containerRef = containerRef;
	}

	public void setContainer(ObjectReference<Container> container) {
		this.containerRef = container;
	}

	@Override
	public void setContainer(Container container) {
		this.containerRef = new ObjectReference<>(container);
	}

	@Override
	public XUser getCreator() {
		return creator;
	}

	public String getIcon() {
		if (this instanceof IModelManaged) {
			return this.thingModel.getIcon();
		} else {
			XDefinition xdefinition = this.getClass().getAnnotation(XDefinition.class);
			if (xdefinition == null) {
				return "";
			} else {
				return xdefinition.icon();
			}
		}
	}

	@Override
	public void setCreator(XUser creator) {
		this.creator = creator;
	}

	public static String getIdentityField() {
		return "number";
	}

	@Override
	@JsonIgnore
	public IThingModel getThingModel() {
		return this.thingModel;
	}

	@Override
	public void setThingModel(IThingModel thingModel) {
		this.thingModel = thingModel;
	}

	@Override
	@XService(name = "getThingIdentity", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getThingIdentity() {
		return this.getClass().getSimpleName() + ":" + this.getNumber();
	}
}
