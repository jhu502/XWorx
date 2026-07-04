package com.flame.xui.widget;

import com.flame.xui.XUIWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;
import com.flame.util.FlameUtils;

/**
 * 标签组件，渲染为 {@code <label>} 标签。
 *
 * <p>常用于表单字段前显示标签文本，支持通过 {@code addDomClass("xui-form-label")}
 * 配合 MeshGrid 的 {@code alignLabel} 功能实现标签宽度对齐。</p>
 */
public class Label extends XUIWidget {

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public Label(UIWidget widget) {
        super(widget);
        this.setEasyUI(false);
    }

    /**
     * 通过名称构造。
     * @param name 标签名称，也作为国际化 key
     */
    public Label(String name) {
        super(WidgetType.Label);
        this.setName(name);
        this.setEasyUI(false);
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "label";
    }

    /** 设置 {@code style} DOM 属性。 */
    @Override
    protected void appendDomAttributes() {
        this.appendEl("style", this.getStyle());
    }

    /** 若设置了 text，输出为 innerHTML。 */
    @Override
    protected void genInnerHTML() {
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        }
    }
}
