package com.flame.xui.widget;

import com.flame.common.serializer.XGridRowSerializer;
import com.flame.util.FlameUtils;
import com.flame.xui.XUIWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;

/**
 * 应用工具栏图标按钮，渲染为 {@code <button>} 标签。
 *
 * <p>自动添加 {@code xui-fillet} CSS 类实现圆角样式。
 * innerHTML 渲染为 {@code <img src="..."/>} 图标，
 * 内嵌在 Grid 行中时自动阻止点击冒泡。</p>
 */
public class AppButton extends XUIWidget {

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public AppButton(UIWidget widget) {
        super(widget);
        this.setEasyUI(false);
        this.addDomClass("xui-fillet");
    }

    /**
     * 通过名称构造，图标由 URL 属性指定。
     * @param name 按钮名称
     */
    public AppButton(String name) {
        super(WidgetType.AppButton);
        this.setName(name);
        this.setEasyUI(false);
        this.addDomClass("xui-fillet");
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "button";
    }

    /** 设置 {@code title}（显示 text）和 {@code style} DOM 属性。 */
    protected void appendDomAttributes() {
        this.appendEl("title", this.getText());
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

    /** 渲染为 {@code <img src="..."/>} 图标。 */
    @Override
    protected void genInnerHTML() {
        this.append("<img src=\"").append(this.getUrl()).append("\"/>");
    }
}
