package xw.flow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.annotations.XDefinition;
import com.flame.auths.SessionHelper;
import com.flame.orm.JsonObjectConverter;
import com.flame.orm.ObjectReference;
import com.flame.orm.XConstant;
import com.flame.orm.XObject;
import com.flame.thing.IModelManaged;
import com.flame.thing.IThingModel;
import com.thing.common.DefaultThing;
import com.thing.entity.ModeledEntity;

import com.thing.entity.XThingModel;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;
import xw.auths.entity.XUser;
import xw.flow.constants.FlowContext;
import xw.flow.constants.FlowStatus;

@Entity
@Table(name = "XWorkInstance", uniqueConstraints = {})
@XDefinition(name = "XWorkInstance", config = DefaultThing.class, icon = "images/flow/flowinst.gif", description = "XWorkInstance", display = "Work Instance", en_US = "Work Instance", zh_CN = "流程实例")
public class XWorkInstance extends XExecutionObject implements IModelManaged {
	private static final long serialVersionUID = 1L;
	@Basic
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "pboId")), @AttributeOverride(name = "className", column = @Column(name = "pboClassname")) })
	private ObjectReference<XObject> businessRef;
	@Basic
	@Column(name = "name")
	private String name = "";
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "status", length = 100)
	private FlowStatus status = FlowStatus.OPEN_RUNNING;
	@Basic
	@Column(name = "processInstId", length = 150)
	private String processInstId = "";
	@Basic
	@Column(name = "processDefId", length = 150)
	private String processDefId = "";
	@ColumnTransformer(write = "?::jsonb")
	@Column(name = "flowContext", columnDefinition = XConstant.JSONB)
    @Convert(converter = JsonObjectConverter.class)
	private FlowContext flowContext = new FlowContext();
	@ManyToOne(targetEntity = XFlowDefinition.class)
	@JoinColumn(name = "definitionId", foreignKey = @ForeignKey(name = "FLOWDEFINITION_ID_FK"))
	private XFlowDefinition definition;
	@ManyToOne(targetEntity = XUser.class)
	@JoinColumn(name = "creatorId", foreignKey = @ForeignKey(name = "CREATOR_ID_FK"))
	private XUser creator;
	@ManyToOne(targetEntity = XThingModel.class)
	@JoinColumn(name = "modelId", nullable = false, foreignKey = @ForeignKey(name = "THINGMODEL_FK"))
	private IThingModel thingModel;

	public static XWorkInstance newInstance(XFlowDefinition definition, XObject pbo) {
		XWorkInstance instance = new XWorkInstance();
		instance.setDefinition(definition);
		instance.setBusinessRef((ObjectReference<XObject>) ObjectReference.newObjectReference(pbo));
		instance.setCreator((XUser) SessionHelper.getCurrentUser());
		return instance;
	}

	public ObjectReference<XObject> getBusinessRef() {
		return businessRef;
	}

	public void setBusinessRef(ObjectReference<XObject> businessRef) {
		this.businessRef = businessRef;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FlowStatus getStatus() {
		return status;
	}

	public void setStatus(FlowStatus status) {
		this.status = status;
	}

	public String getProcessInstId() {
		return processInstId;
	}

	public void setProcessInstId(String procInstId) {
		this.processInstId = procInstId;
	}

	public String getProcessDefId() {
		return processDefId;
	}

	public void setProcessDefId(String procDefId) {
		this.processDefId = procDefId;
	}

	public boolean hasVariable(String name) {
		return flowContext.getVariable(name) != null;
	}

	public FlowContext getFlowContext() {
		return flowContext;
	}

	public void setFlowContext(FlowContext flowContext) {
		this.flowContext = flowContext;
	}

	public XFlowDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(XFlowDefinition definition) {
		this.definition = definition;
	}

	public XUser getCreator() {
		return creator;
	}

	public String getCreatorName() {
		return this.getCreator().getName();
	}

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
	public String getIcon() {
		return this.thingModel.getIcon();
	}

	@Override
	public String getThingIdentity() {
		return this.getOid();
	}
}
