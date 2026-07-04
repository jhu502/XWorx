package com.flame.xui.widget;

import com.flame.util.FlameUtils;
import com.flame.xui.WidgetType;
import com.flame.xui.XUIWidget;
import com.flame.annotations.UIWidget;

/**
 * 可拖拽容器组件，渲染为 {@code <div draggable="true">} 标签。
 *
 * <p>用于在表单中创建可拖拽的区域容器，设置 {@code setEasyGui(false)}
 * 防止 EasyUI 对其进行额外的组件初始化。</p>
 */
public class Draggable extends XUIWidget {

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public Draggable(UIWidget widget) {
        super(widget);
        this.setEasyUI(false);
    }

    /**
     * 通过名称和文本构造。
     * @param name 组件名称
     * @param text 显示文本
     */
    public Draggable(String name, String text) {
        super(WidgetType.Draggable);
        this.setName(name);
        this.setText(text);
        this.setEasyUI(false);
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "div";
    }

    /** 设置 {@code draggable="true"} DOM 属性。 */
    @Override
    protected void appendDomAttributes() {
        this.appendEl("draggable", true);
    }

    /** 若设置了 text，将其作为标签内部 HTML 文本输出。 */
    @Override
    protected void genInnerHTML() {
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        }
    }
}
