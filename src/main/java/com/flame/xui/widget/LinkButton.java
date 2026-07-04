package com.flame.xui.widget;

import com.flame.common.serializer.XGridRowSerializer;
import com.flame.localize.LocalizationHelper;
import com.flame.util.FlameUtils;
import com.flame.xui.XUIWidget;
import com.flame.xui.IWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;

/**
 * 链接按钮组件，渲染为 {@code <a href="javascript:void(0)">} 标签。
 *
 * <p>类似于 {@link HyperLink} 但追加了 Grid 行内的冒泡阻止逻辑。
 * 文本会自动通过 {@link LocalizationHelper#get} 进行国际化转换。
 * 支持内嵌子组件渲染。</p>
 */
public class LinkButton extends XUIWidget {

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public LinkButton(UIWidget widget) {
        super(widget);
    }

    /** 无参默认构造，WidgetType 为 {@link WidgetType#LinkButton}。 */
    public LinkButton() {
        super(WidgetType.LinkButton);
    }

    /**
     * 通过文本构造。
     * @param text 按钮文本（会经过国际化转换）
     */
    public LinkButton(String text) {
        super(WidgetType.LinkButton);
        this.setText(text);
    }

    /**
     * 通过文本和点击事件构造。
     * @param text    按钮文本
     * @param onclick 点击事件 JS 代码
     */
    public LinkButton(String text, String onclick) {
        super(WidgetType.LinkButton);
        this.setText(text);
        this.addEvent("onclick", onclick);
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "a";
    }

    /** 设置 {@code href="javascript:void(0)"} 和 {@code style} DOM 属性。 */
    @Override
    protected void appendDomAttributes() {
        this.appendEl("href", "javascript:void(0)");
        this.appendEl("style", this.getStyle());
    }

    /** Grid 行内使用时自动阻止点击冒泡。 */
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

    /** 渲染 innerHTML：国际化 text → 递归渲染内嵌子组件。 */
    @Override
    protected void genInnerHTML() {
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(LocalizationHelper.get(this.getText()));
        }
        if (this.getInnerObject() instanceof IWidget) {
            IWidget widget = (IWidget) this.getInnerObject();
            this.append(widget.renderHTML());
        } else {
            if (this.getInnerObject() != null) {
                this.append(this.getInnerObject().toString());
            }
        }
    }
}
