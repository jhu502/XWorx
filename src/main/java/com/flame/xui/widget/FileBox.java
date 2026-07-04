package com.flame.xui.widget;

import com.flame.common.serializer.XGridRowSerializer;
import com.flame.util.FlameUtils;
import com.flame.xui.XUIWidget;
import com.flame.annotations.UIWidget;

/**
 * 文件上传输入框组件，渲染为 {@code <input>} 标签。
 *
 * <p>内嵌在 Grid 行中时自动阻止点击事件冒泡（{@code event.stopPropagation()}），
 * 防止触发父级行选择。</p>
 */
public class FileBox extends XUIWidget {

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public FileBox(UIWidget widget) {
        super(widget);
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "input";
    }

    /** 设置 {@code style} 和 {@code value} DOM 属性。 */
    @Override
    protected void appendDomAttributes() {
        this.appendEl("style", this.getStyle());
        this.appendEl("value", this.getValue());
    }

    /**
     * 当组件位于 Grid 行内且未被父级 GuiWidget 包裹时，
     * 自动追加 {@code event.stopPropagation()} 阻止点击冒泡到行选择事件。
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

    /** 若设置了 text，输出为 innerHTML。 */
    @Override
    protected void genInnerHTML() {
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        }
    }
}
