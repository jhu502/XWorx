package xw.context.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.flame.annotations.XConfig;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.type.XBaseType;

import xw.auths.entity.XUser;
import xw.context.IContained;
import xw.context.IFolded;
import xw.domain.IAdminDomain;
import xw.domain.entity.XAdminDomain;

@Entity
@Table(name = "XFolder", uniqueConstraints = {})
public class XFolder extends XObject implements IFolded, IContained<Container>, IAdminDomain {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "name", length = 100)
	@XConfig(name = "name", friendlyName = "Name", baseType = XBaseType.STRING, description = "Name")
	private String name = "";
	@Basic
	@Column(name = "description", length = 1000)
	@XConfig(name = "description", friendlyName = "Description", baseType = XBaseType.STRING, description = "Description")
	private String description = "";
	@Basic
	@Embedded
	@AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "container_id")), @AttributeOverride(name = "className", column = @Column(name = "container_classname"))})
	private ObjectReference<Container> container;
	@ManyToOne(targetEntity = XAdminDomain.class)
	@JoinColumn(name = "adminDomain", foreignKey = @ForeignKey(name = "ADMIN_DOMAIN_FK"))
	private XAdminDomain adminDomain;
	@ManyToOne(targetEntity = XFolder.class)
	@JoinColumn(name = "folder", foreignKey = @ForeignKey(name = "FOLDERING_FK"))
	private XFolder folder;
	@ManyToOne(targetEntity = XUser.class)
	@JoinColumn(name = "creator_id", foreignKey = @ForeignKey(name = "CREATOR_ID_FK"))
	private XUser creator;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Container getContainer() {
		return PersistenceHelper.service().refresh(this.container);
	}

	public void setContainer(Container container) {
		this.container = new ObjectReference<Container>(container);
	}

	public XFolder getFolder() {
		return folder;
	}

	public void setFolder(XFolder folder) {
		this.folder = folder;
	}

	public XAdminDomain getAdminDomain() {
		return adminDomain;
	}

	public void setAdminDomain(XAdminDomain domain) {
		this.adminDomain = domain;
	}

	public XUser getCreator() {
		return creator;
	}

	public void setCreator(XUser creator) {
		this.creator = creator;
	}
}
