package xw.vc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.type.ServiceType;
import com.flame.annotations.XService;
import com.flame.auths.ICreatorInfo;
import com.flame.xui.HREFactory;
import com.flame.lifecycle.ILifeCycleManaged;
import com.flame.lifecycle.LifeCycleState;
import com.flame.orm.AbstractEntity;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.thing.IModelManaged;
import com.flame.thing.IThingModel;
import com.flame.type.XBaseType;
import com.flame.vc.CheckOutInfo;
import com.flame.vc.IAttributes;
import com.flame.vc.IVersioned;
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
import xw.content.IContentHolder;
import xw.context.entity.Container;
import xw.context.IContained;
import xw.context.IFolded;
import xw.context.entity.XFolder;
import xw.team.ITeamManaged;
import xw.team.entity.XFlowTeam;

@MappedSuperclass
public abstract class VersionControlled<T extends Master> extends AbstractEntity
		implements IContained, IContentHolder, IModelManaged, IFolded, ILifeCycleManaged, ITeamManaged<XFlowTeam>, IVersioned<T>, ICreatorInfo<XUser>, IAttributes {
	private static final long serialVersionUID = 1L;
	@Column(name = "state")
	@Enumerated(EnumType.STRING)
	private LifeCycleState state;
	@Basic
	@Column(name = "description", length = 1000)
	private String description = "";
	@Basic
	@Column(name = "version", length = 10)
	private String version;
	@Basic
	@Column(name = "latest")
	private boolean latest = false;
	@Basic
	@Column(name = "previous")
	private long previous = -1;
	@Column(name = "checkout_info")
	@Enumerated(EnumType.STRING)
	private CheckOutInfo checkOutInfo = CheckOutInfo.ci;
	@ManyToOne(targetEntity = XFlowTeam.class)
	@JoinColumn(name = "teamId", foreignKey = @ForeignKey(name = "TEAM_ID_FK"))
	private XFlowTeam team;
	@ManyToOne(targetEntity = XFolder.class)
	@JoinColumn(name = "folder", foreignKey = @ForeignKey(name = "FOLDERING_FK"))
	private XFolder folder;
	@ManyToOne(targetEntity = XUser.class)
	@JoinColumn(name = "creatorId", foreignKey = @ForeignKey(name = "CREATOR_ID_FK"))
	private XUser creator;
	@ManyToOne(targetEntity = XThingModel.class)
	@JoinColumn(name = "modelId", nullable = false, foreignKey = @ForeignKey(name = "THINGMODEL_FK"))
	private IThingModel thingModel;
	@Basic
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "containerId")), @AttributeOverride(name = "className", column = @Column(name = "containerClassname")) })
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
	public LifeCycleState getState() {
		return state;
	}

	@Override
	public void setState(LifeCycleState state) {
		this.state = state;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
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

	public XFolder getFolder() {
		return folder;
	}

	public void setFolder(XFolder folder) {
		this.folder = folder;
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
	public XFlowTeam getTeam() {
		return null;
	}

	@Override
	public void setTeam(XFlowTeam team) {
		this.team = team;
	}

	@Override
	public XUser getCreator() {
		return creator;
	}

	@Override
	public void setCreator(XUser creator) {
		this.creator = creator;
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
	@XService(name = "getIcon", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getIcon() {
		return this.thingModel.getIcon();
	}

	public String getIconUI() {
		return "<img style='float:left' src='" + HREFactory.getHREF(this.thingModel.getIcon()) + "'/>";
	}

	@XService(name = "getThingDisplay", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getThingDisplay() {
		return this.getNumber() + ", " + this.getName();
	}

	@Override
	@XService(name = "getThingIdentity", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getThingIdentity() {
		return this.getClass().getSimpleName() + ":" + this.getNumber();
	}

	public static String getIdentityField() {
		return "number";
	}
}
