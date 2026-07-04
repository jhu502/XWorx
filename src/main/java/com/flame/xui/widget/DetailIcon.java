package com.flame.xui.widget;

import com.flame.xui.HREFactory;
import com.flame.common.serializer.XGridRowSerializer;
import com.flame.orm.XPersistable;
import com.flame.util.FlameUtils;
import com.flame.xui.XUIWidget;
import com.flame.xui.IWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;

import java.util.HashMap;
import java.util.Map;

/**
 * 详情图标链接组件，渲染为 {@code <a href="javascript:void(0)"><img src="images/details.gif"/></a>}。
 *
 * <p>点击默认触发 {@code flame.openDetailsPage(event, infoPageUrl)} 打开实体详情页。
 * 支持通过 {@link #addEvent addEvent(ON_CLICK, ...)} 覆盖默认点击行为，
 * 通过 {@link #setInnerObject} 替换默认图标。</p>
 *
 * <p><b>自定义图标和事件的用法示例：</b></p>
 * <pre>{@code
 * DetailIcon icon = new DetailIcon((Object) null);
 * icon.setInnerObject(new IconBox("images/custom.gif"));
 * icon.addEvent(ON_CLICK, "myFunction('param')");
 * }</pre>
 *
 * @see DialogIcon
 */
public class DetailIcon extends XUIWidget {
    private static final String DETAILS_GIF = "images/details.gif";

    /** URL 查询参数 */
    private Map<String, Object> params = new HashMap<>();
    /** 是否为锚点链接 */
    private boolean anchor = false;

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public DetailIcon(UIWidget widget) {
        super(widget);
        this.setEasyUI(false);
        this.setInnerObject(new IconBox(DETAILS_GIF));
    }

    /**
     * 通过实体对象构造。
     * 若未手动设置 onclick，则默认渲染为 {@code flame.openDetailsPage(event, infoPageUrl)}。
     *
     * @param value 实体对象（{@link XPersistable}）或 OID 字符串
     */
    public DetailIcon(Object value) {
        super(WidgetType.HyperLink);
        this.setValue(value);
        this.setEasyUI(false);
        this.setInnerObject(new IconBox(DETAILS_GIF));
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "a";
    }

    /**
     * Grid 行内使用时自动追加 {@code event.stopPropagation()} 阻止点击冒泡到行选择。
     * 若已有自定义 onclick，则在其后追加。
     */
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

    /**
     * 设置 {@code href} 和 {@code onclick} DOM 属性。
     * 若未手动设置 onclick 且 value 不为空，则自动生成
     * {@code flame.openDetailsPage(event, infoPageUrl)} 调用。
     */
    @Override
    protected void appendDomAttributes() {
        if (FlameUtils.isBlank(this.getUrl())) {
            this.appendEl("href", "javascript:void(0)");
        } else {
            this.appendEl("href", HREFactory.getHREF(this.getUrl(), this.params, this.anchor));
        }
        if (!this.getEventMap().containsKey(ON_CLICK)) {
            if (this.getValue() instanceof XPersistable) {
                this.addEvent(ON_CLICK, "flame.openDetailsPage(event, '" + HREFactory.hashInfoPage((XPersistable) this.getValue()) + "')");
            } else {
                this.addEvent(ON_CLICK, "flame.openDetailsPage(event, '" + HREFactory.hashInfoPage(String.valueOf(this.getValue())) + "')");
            }
        }
        this.appendEl("style", this.getStyle());
    }

    /** 渲染 innerHTML：text → 图标组件。 */
    @Override
    protected void genInnerHTML() {
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        }
        if (this.getInnerObject() instanceof IWidget) {
            IWidget iWidget = (IWidget) this.getInnerObject();
            this.append(iWidget.renderHTML());
        } else {
            if (this.getInnerObject() != null) {
                this.append(this.getInnerObject().toString());
            }
        }
    }
}
