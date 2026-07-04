package com.flame.xui.widget;

import com.flame.xui.XUIWidget;
import com.flame.xui.IWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;
import com.flame.util.FlameUtils;

/**
 * 文本显示组件，渲染为 {@code <span>} 标签。
 *
 * <p>默认设置 {@code vertical-align:middle} 使文本与同行的图标对齐。
 * 支持内嵌子组件递归渲染。</p>
 *
 * <p>常用作 {@link ArrayComponent} 的子元素，与 {@link IconBox} 组合
 * 渲染树节点或列表项。</p>
 */
public class TextDisplay extends XUIWidget {

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public TextDisplay(UIWidget widget) {
        super(widget);
    }

    /** 无参默认构造，WidgetType 为 {@link WidgetType#TextDisplay}。 */
    public TextDisplay() {
        super(WidgetType.TextDisplay);
    }

    /**
     * 通过文本内容构造。
     * @param text 显示的文本
     */
    public TextDisplay(String text) {
        super(WidgetType.TextDisplay);
        this.setText(text);
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "span";
    }

    /**
     * 设置 {@code style} DOM 属性。
     * 未指定时默认使用 {@code vertical-align:middle}。
     */
    @Override
    protected void appendDomAttributes() {
        if (FlameUtils.isNotBlank(this.getStyle())) {
            this.appendEl("style", this.getStyle());
        } else {
            this.appendEl("style", "vertical-align:middle;");
        }
    }

    /** 渲染 innerHTML：优先 text，其次 value（toString），最后递归渲染内嵌子组件。 */
    @Override
    protected void genInnerHTML() {
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        } else if (this.getValue() != null) {
            this.append(this.getValue().toString());
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
