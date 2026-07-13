package com.flame.xui.widget;

import com.flame.util.FlameUtils;
import com.flame.xui.XUIWidget;
import com.flame.xui.IWidget;
import com.flame.xui.WidgetType;

/**
 * 通用 HTML 元素组件，渲染为 {@code <img>} 标签。
 *
 * <p>支持内嵌子组件（通过 {@code setInnerObject}），
 * 子组件会被递归渲染为 HTML。</p>
 */
public class HTMLElement extends XUIWidget {
    private String tag = "span";

    /**
     * 通过名称构造。
     * @param name 组件名称，用作 DOM 属性
     */
    public HTMLElement(String name, String tag) {
        super(WidgetType.Element);
        this.setEasyUI(false);
        this.setName(name);
        this.tag = tag;
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return this.tag;
    }

    /** 设置 {@code type}、{@code style}、{@code value} DOM 属性。 */
    @Override
    protected void appendDomAttributes() {
        this.appendEl("type", this.getWidgetType());
        this.appendEl("style", this.getStyle());
        this.appendEl("value", this.getValue());
    }

    /**
     * 渲染 innerHTML：先输出 text（如有），再递归渲染内嵌的 {@link IWidget}，
     * 或输出 innerObject 的字符串表示。
     */
    @Override
    protected void genInnerHTML() {
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
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
