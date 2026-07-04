package com.thing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.type.ServiceType;
import com.flame.annotations.XService;
import com.flame.auths.ICreatorInfo;
import com.flame.xui.HREFactory;
import com.flame.orm.ItemEntity;
import com.flame.thing.IThingModel;
import com.flame.type.XBaseType;

import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import xw.auths.entity.XUser;

@MappedSuperclass
public abstract class ModeledEntity extends ItemEntity implements IModeledEntity, ICreatorInfo<XUser> {
	private static final long serialVersionUID = 1L;
	@ManyToOne(targetEntity = XThingModel.class)
	@JoinColumn(name = "modelId", nullable = false, foreignKey = @ForeignKey(name = "THINGMODEL_FK"))
	private IThingModel thingModel;
	@ManyToOne(targetEntity = XUser.class)
	@JoinColumn(name = "creatorId", foreignKey = @ForeignKey(name = "CREATOR_ID_FK"))
	private XUser creator;

	public String getPageUri() {
		return this.getThingModel().getPageUri();
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
