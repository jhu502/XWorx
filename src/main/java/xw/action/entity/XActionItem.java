package xw.action.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import com.flame.action.IActionItem;
import com.flame.xui.WinType;

@Entity
@Table(name = "XActionItem", uniqueConstraints = {})
public class XActionItem extends AbstractAction implements IActionItem {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "iconCls")
	private String iconCls = "";
	@Basic
	@Column(name = "url")
	private String url = "";
	@Basic
	@Column(name = "processor")
	private String processor = "";
	@Basic
	@Column(name = "onclick")
	private String onclick = "";
	@Basic
	@Column(name = "beforejs")
	private String beforejs = "";
	@Basic
	@Column(name = "afterjs")
	private String afterjs = "";
	@Column(name = "winType", nullable = false)
	@Enumerated(EnumType.STRING)
	private WinType winType;

	public XActionItem() {
	}

	public XActionItem(String name, String type, String display, String icon, String iconCls, String style, String url, WinType winType) {
		this.setName(name);
		this.setType(type);
		this.setDisplay(display);
		this.setIcon(icon);
		this.setIconCls(iconCls);
		this.setStyle(style);
		this.setUrl(url);
		this.setWinType(winType);
	}

	public XActionItem(String name, String type, String display, String icon, String iconCls, String style, String url, WinType winType, String supportedType) {
		this.setName(name);
		this.setType(type);
		this.setDisplay(display);
		this.setIcon(icon);
		this.setIconCls(iconCls);
		this.setStyle(style);
		this.setUrl(url);
		this.setWinType(winType);
		this.setSupportedType(supportedType);
	}

	public String getIconCls() {
		return this.iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProcessor() {
		return this.processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getBeforeJS() {
		return beforejs;
	}

	public void setBeforeJS(String beforejs) {
		this.beforejs = beforejs;
	}

	public String getAfterJS() {
		return afterjs;
	}

	public void setAfterJS(String afterjs) {
		this.afterjs = afterjs;
	}

	public WinType getWinType() {
		return this.winType;
	}

	public void setWinType(WinType winType) {
		this.winType = winType;
	}
}
