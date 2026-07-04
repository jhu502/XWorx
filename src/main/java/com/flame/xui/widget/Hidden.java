package com.flame.xui.widget;

import com.flame.xui.WidgetType;
import com.flame.xui.XUIWidget;
import com.flame.annotations.UIWidget;
import com.flame.util.FlameUtils;

/**
 * 隐藏域输入组件，渲染为 {@code <input type="hidden">} 标签。
 *
 * <p>用于在表单中嵌入不可见的参数域，设置 {@code setEasyGui(false)}
 * 防止 EasyUI 对其进行组件渲染。</p>
 */
public class Hidden extends XUIWidget {

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public Hidden(UIWidget widget) {
        super(widget);
        this.setEasyUI(false);
    }

    public Hidden(String id, String name, String value) {
        super(WidgetType.Hidden);
        this.setEasyUI(false);
        this.setId(id);
        this.setName(name);
        this.setValue(value);
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "input";
    }

    /** 设置 {@code type}（取自 widgetType）和 {@code value} DOM 属性。 */
    @Override
    protected void appendDomAttributes() {
        this.appendEl("type", this.getWidgetType());
        this.appendEl("value", this.getValue());
    }

    /** 若设置了 text，输出为 innerHTML。 */
    @Override
    protected void genInnerHTML() {
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        }
    }
}
