package com.flame.xui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.flame.action.IAction;
import com.flame.action.IActionItem;
import com.flame.action.IActionModel;
import com.flame.util.JsonUtils;
import com.flame.annotations.UIAction;

public class XUIAction {
	private String key;
	private String id;
	private String name;
	private String display;
	private String icon;
	private String style;
	private String type;
	private String url;
	private String iconCls;
	private String processor;
	private String beforeJS;
	private String afterJS;
	private String onclick;
	private boolean close;
	private boolean leaf = false;
	private String source;
	private WinType winType;
	private final List<XUIAction> children = new ArrayList<>();

	public static XUIAction newXAction(UIAction action) {
		XUIAction xaction = new XUIAction();
		xaction.setId(action.name());
		xaction.setName(action.name());
		xaction.setDisplay(action.display());
		xaction.setIcon(action.icon());
		xaction.setStyle(action.style());
		xaction.setUrl(action.url());
		xaction.setProcessor(action.processor());
		xaction.setWinType(action.winType());
		xaction.setBeforeJS(action.beforeJS());
		xaction.setAfterJS(action.afterJS());
		xaction.setLeaf(true);

		return xaction;
	}

    public static XUIAction toXUIAction(IAction action) {
        if (action instanceof IActionModel) {
            IActionModel model = (IActionModel) action;
            XUIAction result = new XUIAction();
            result.setId(model.getName());
            result.setName(model.getName());
            result.setDisplay(model.getLocalDisplay());
            result.setIcon(model.getIcon());
            result.setStyle(model.getStyle());
            result.setType(model.getType());
            result.setUrl(model.getUrl());
            result.setProcessor(model.getProcessor());
			result.setWinType(WinType.model);
            result.setKey(model.getActionKey());
            result.setLeaf(false);

            return result;
        } else if (action instanceof IActionItem) {
            IActionItem item = (IActionItem) action;
            XUIAction result = new XUIAction();
            result.setId(item.getName());
            result.setName(item.getName());
            result.setDisplay(item.getLocalDisplay());
            result.setIcon(item.getIcon());
            result.setStyle(item.getStyle());
            result.setType(item.getType());
            result.setUrl(item.getUrl());
            result.setProcessor(item.getProcessor());
            result.setWinType(item.getWinType());
            result.setBeforeJS(item.getBeforeJS());
            result.setAfterJS(item.getAfterJS());
            result.setKey(item.getActionKey());
            result.setLeaf(true);

            return result;
        }

        return null;
    }

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public String getBeforeJS() {
		return beforeJS;
	}

	public void setBeforeJS(String beforeJS) {
		this.beforeJS = beforeJS;
	}

	public String getAfterJS() {
		return afterJS;
	}

	public void setAfterJS(String afterJS) {
		this.afterJS = afterJS;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public WinType getWinType() {
		return winType;
	}

	public void setWinType(WinType winType) {
		this.winType = winType;
	}

	public List<XUIAction> getChildren() {
		return children;
	}

	public void addChildren(Collection<XUIAction> children) {
		if (children != null && !children.isEmpty()) {
			this.children.addAll(children);
		}
	}

	public void addChild(XUIAction bean) {
		this.children.add(bean);
	}

	/**
	 * 序列化为 JS 对象字面量，供 handleBarAction / handleRowAction 等前端 Action 入口使用。
	 * 通过 Jackson 注解驱动序列化，字符串属性使用单引号包裹，
	 * {@code @JsonRawValue} 标注的 JS 表达式属性（onclick / beforeJS / afterJS）保持原样输出。
	 *
	 * @return JS 对象字面量字符串，如 {@code {actionKey:'type:name',winType:'popup',onclick:function(p){...}}}
	 */
	public String toJSObject() {
		String json = JsonUtils.toJson(this);
		// HTML onclick 属性使用双引号，JSON 内部改用单引号
		return json.replace('"', '\'');
	}
}
