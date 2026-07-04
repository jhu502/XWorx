package xw.auths.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.type.ServiceType;
import com.flame.annotations.XConfig;
import com.flame.annotations.XDefinition;
import com.flame.annotations.XService;
import com.flame.xui.HREFactory;
import com.flame.orm.ItemEntity;
import com.flame.thing.IModelManaged;
import com.flame.thing.IThingModel;
import com.flame.type.XBaseType;
import com.thing.entity.XThingModel;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import xw.auths.XPrincipalThing;

@MappedSuperclass
@XDefinition(name = "XPrincipal", config = XPrincipalThing.class, icon = "images/principal.png", description = "XPrincipal", display = "Principal", en_US = "Principal", zh_CN = "承担者")
public class XPrincipal extends ItemEntity implements IModelManaged {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "fullName", length = 100)
	@XConfig(name = "fullName", friendlyName = "Full Name", baseType = XBaseType.STRING, description = "Full Name")
	protected String fullName = "";
	@Basic
	@Column(name = "englishName", length = 100)
	@XConfig(name = "englishName", friendlyName = "English Name", baseType = XBaseType.STRING, description = "English Name")
	protected String englishName = "";
	@Basic
	@Column(name = "appKey", length = 100)
	@XConfig(name = "appKey", friendlyName = "Application Key", baseType = XBaseType.STRING, description = "Application Key")
	private String appKey;
	@Basic
	@Column(name = "tel", length = 20)
	@XConfig(name = "tel", friendlyName = "Telephone", baseType = XBaseType.STRING, description = "Telephone")
	protected String tel = "";
	@Basic
	@Column(name = "fax", length = 20)
	@XConfig(name = "fax", friendlyName = "Fax", baseType = XBaseType.STRING, description = "Fax")
	protected String fax = "";
	@Basic
	@Column(name = "email", length = 40)
	@XConfig(name = "email", friendlyName = "EMail", baseType = XBaseType.STRING, description = "EMail")
	protected String email = "";
	@Basic
	@Column(name = "postalCode", length = 20)
	@XConfig(name = "postalCode", friendlyName = "Postal Code", baseType = XBaseType.STRING, description = "Postal Code")
	protected String postalCode = "";
	@Basic
	@Column(name = "address", length = 600)
	@XConfig(name = "address", friendlyName = "Address", baseType = XBaseType.STRING, description = "Address")
	private String address = "";
	@ManyToOne(targetEntity = XThingModel.class)
	@JoinColumn(name = "modelId", nullable = false, foreignKey = @ForeignKey(name = "THINGMODEL_FK"))
	private IThingModel thingModel;

	@XService(name = "getFullName", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@XService(name = "getEnglishName", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getEnglishName() {
		return this.englishName;
	}

	public void setEnglishName(String engName) {
		this.englishName = engName;
	}

	@XService(name = "getTel", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	@XService(name = "getFax", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@XService(name = "getEmail", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@XService(name = "getPostalCode", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@XService(name = "getAddress", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@XService(name = "getAppKey", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getAppKey() {
		return this.appKey;
	}

	public void setAppKey(String appkey) {
		this.appKey = appkey;
	}

	@XService(name = "getThingIdentity", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getThingIdentity() {
		return this.getClass().getSimpleName() + ":" + this.getName();
	}


	@XService(name = "getThingDisplay", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getThingDisplay() {
		return this.getName() + "(" + this.fullName + ")";
	}

	@JsonIgnore
	public IThingModel getThingModel() {
		return this.thingModel;
	}

	public void setThingModel(IThingModel thingModel) {
		this.thingModel = thingModel;
	}

	@XService(name = "getIcon", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getIcon() {
		return this.thingModel.getIcon();
	}

	public String getIconUI() {
		return "<img style='float:left' src='" + HREFactory.getHREF(this.thingModel.getIcon()) + "'/>";
	}

	public static String getIdentityField() {
		return "number";
	}
}
