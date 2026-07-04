package xw.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import xw.context.entity.Container;
import xw.domain.IAdminDomain;
import com.flame.orm.ObjectReference;
import com.flame.orm.XObject;

import jakarta.persistence.*;

@Entity
@Table(name = "XAdminDomain", uniqueConstraints = {})
public class XAdminDomain extends XObject implements IAdminDomain {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "number")
	private String number = "";
	@Basic
	@Column(name = "name")
	private String name = "";
	@Basic
	@Column(name = "description", length = 500)
	private String description = "";
	@ManyToOne
	@JoinColumn(name = "adminDomain", foreignKey = @ForeignKey(name = "ADMIN_DOMAIN_FK"))
	private XAdminDomain adminDomain;
	@Basic
	@Embedded
	@AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "context_id")), @AttributeOverride(name = "className", column = @Column(name = "context_classname"))})
	private ObjectReference<Container> contextRef;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

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

	@Override
	@JsonIgnore
	public XAdminDomain getAdminDomain() {
		return this.adminDomain;
	}

	@Override
	public void setAdminDomain(XAdminDomain domain) {
		this.adminDomain = domain;
	}

	public ObjectReference<Container> getContextRef() {
		return contextRef;
	}

	@JsonIgnore
	public Container getContext() {
		if (this.getContextRef() == null)
			return null;
		return this.getContextRef().getObject();
	}

	public void setContextRef(ObjectReference<Container> contextRef) {
		this.contextRef = contextRef;
	}
}
