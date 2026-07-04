package com.flame.xui.widget;

import java.util.ArrayList;
import java.util.List;

import com.flame.common.serializer.XGridRowSerializer;
import com.flame.util.FlameUtils;
import com.flame.xui.WidgetType;
import com.flame.xui.XUIAction;
import com.flame.xui.XUIWidget;
import com.flame.annotations.UIWidget;

/**
 * Grid 行内操作图标组组件，在 TableBuilder / TreeBuilder 的行中显示多个操作图标按钮。
 *
 * <p>每个按钮的行为与 {@code @UIAction} 定义一致，通过
 * {@code flame.executeAction(actionConfig, oid)} 统一分发。
 * 只显示图标，名称以 {@code title} 属性方式显示为悬浮提示。</p>
 *
 * }</pre>
 *
 * @see XUIAction
 */
public class ActionBox extends XUIWidget {
    /** 当前行数据的 rowId */
    private String rowId;
    /** 所属 Grid 的 compId，格式为 {@code component$uid$builder}，用于前端定位 Grid 配置以收集选中行参数 */
    private String compId;
    /** 操作项列表 */
    private final List<XUIAction> actions = new ArrayList<>();

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public ActionBox(UIWidget widget) {
        super(widget);
        this.setEasyUI(false);
    }

    /**
     * 通过行 oid 构造。
     * @param rowId 当前行数据的 oid，传入 onclick 回调
     */
    public ActionBox(String rowId) {
        super(WidgetType.Element);
        this.rowId = rowId;
        this.setEasyUI(false);
    }

    /**
     * 添加一个操作项。
     * @param action 操作配置
     * @return this（链式调用）
     */
    public ActionBox add(XUIAction action) {
        this.actions.add(action);
        return this;
    }

    /** @return 操作项列表 */
    public List<XUIAction> getActions() {
        return actions;
    }

    /** @return 当前行 oid */
    public String getRowId() {
        return rowId;
    }

    /** @param rowId 当前行 rowId */
    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    /** @return 所属 Grid 的 compId */
    public String getCompId() {
        return compId;
    }

    /** @param compId 所属 Grid 的 compId */
    public void setCompId(String compId) {
        this.compId = compId;
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "span";
    }

    @Override
    protected void appendDomAttributes() {
    }

    /** 阻止 Grid 行内的点击冒泡。 */
    @Override
    protected void appendEventElement() {
        if (XGridRowSerializer.getRowComponent() != null && !XUIWidget.isWidgetEmbedded()) {
            String value = this.getEventMap().get(ON_CLICK);
            if (FlameUtils.isBlank(value)) {
                value = "event.stopPropagation();";
            } else {
                value = value + ";event.stopPropagation();";
            }
            this.addEvent(ON_CLICK, value);
        }
        super.appendEventElement();
    }

    /** 渲染所有操作图标为 HTML。 */
    @Override
    protected void genInnerHTML() {
        for (XUIAction action : this.actions) {
            this.append(renderActionLink(action));
        }
    }

    /** 渲染单个操作链接。 */
    private String renderActionLink(XUIAction action) {
        StringBuilder html = new StringBuilder();
        html.append("<a href=\"javascript:void(0)\"");
        if (FlameUtils.isNotBlank(action.getDisplay())) {
            html.append(" title=\"").append(action.getDisplay()).append("\"");
        }
        String actionJson = this.buildActionConfig(action);
        String onclick = "flame.handleRowAction(" + actionJson + ",'" + this.rowId + "');event.stopPropagation();";
        html.append(" onclick=\"").append(onclick).append("\"");
        html.append(" style=\"display:inline-block;margin:0 1px;\"");
        html.append(">");

        html.append("<img src=\"").append(action.getIcon()).append("\"");
        if (FlameUtils.isNotBlank(action.getDisplay())) {
            html.append(" title=\"").append(action.getDisplay()).append("\"");
        }
        html.append(" style=\"cursor:pointer;margin-right:2px;vertical-align:middle\"");
        html.append("/>");

        html.append("</a>");
        return html.toString();
    }

    /** 将 {@link XUIAction} 序列化为 JS 对象字面量。 */
    private String buildActionConfig(XUIAction action) {
        StringBuilder json = new StringBuilder("{");
        appendJsonProp(json, "name", action.getName());
        if (FlameUtils.isNotBlank(action.getProcessor())) {
            appendJsonProp(json, "processor", action.getProcessor());
        }
        String winType = action.getWinType() != null ? action.getWinType().name() : "invoke";
        appendJsonProp(json, "winType", winType);
        if (FlameUtils.isNotBlank(action.getUrl())) {
            appendJsonProp(json, "url", action.getUrl());
        }
        if (FlameUtils.isNotBlank(action.getStyle())) {
            appendJsonProp(json, "style", action.getStyle());
        }
        if (FlameUtils.isNotBlank(action.getBeforeJS())) {
            appendJsonProp(json, "beforeJS", action.getBeforeJS());
        }
        if (FlameUtils.isNotBlank(action.getAfterJS())) {
            appendJsonProp(json, "afterJS", action.getAfterJS());
        }
        if (FlameUtils.isNotBlank(this.compId)) {
            appendJsonProp(json, "compId", this.compId);
        }
        if (json.charAt(json.length() - 1) == ',') {
            json.setLength(json.length() - 1);
        }
        json.append("}");
        return json.toString();
    }

    private void appendJsonProp(StringBuilder json, String key, String value) {
        if (value != null) {
            json.append(key).append(":&quot;").append(value).append("&quot;,");
        }
    }
}
