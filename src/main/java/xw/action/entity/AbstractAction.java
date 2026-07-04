package xw.action.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import com.flame.action.IAction;
import com.flame.localize.AbstractLocalization;

@MappedSuperclass
public abstract class AbstractAction extends AbstractLocalization implements IAction {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "name")
	private String name = "";
	@Basic
	@Column(name = "type")
	private String type = "Object";
	@Basic
	@Column(name = "style")
	private String style = "";
	@Basic
	@Column(name = "icon")
	private String icon = "";
	@Basic
	@Column(name = "supported_type")
	private String supportedType = "";

	public String getKey() {
		return this.getType() + ":" + this.getName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getActionKey() {
		return this.type + ":" + this.name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUrl() {
		return "#";
	}

	public String getStyle() {
		if (this.style == null)
			return "";
		else
			return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getSupportedType() {
		return supportedType;
	}

	public void setSupportedType(String supportedType) {
		this.supportedType = supportedType;
	}
}
