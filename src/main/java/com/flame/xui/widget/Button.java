package com.flame.xui.widget;

import com.flame.common.serializer.XGridRowSerializer;
import com.flame.localize.LocalizationHelper;
import com.flame.util.FlameUtils;
import com.flame.xui.IWidget;
import com.flame.xui.WidgetType;
import com.flame.xui.XUIWidget;
import com.flame.annotations.UIWidget;

/**
 * 通用按钮组件，渲染为 {@code <button>} 标签。
 *
 * <p>支持文本按钮和图标+文本按钮（通过内嵌子组件）。
 * 文本会自动通过 {@link LocalizationHelper#get} 进行国际化转换。
 * 内嵌在 Grid 行中时自动阻止点击冒泡。</p>
 */
public class Button extends XUIWidget {

    /** 按钮类型，默认 {@code "button"} */
    private String type = "button";

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public Button(UIWidget widget) {
        super(widget);
        this.setEasyUI(false);
    }

    /**
     * 通过文本构造，名称默认为文本值。
     * @param text 按钮文本（会经过国际化转换）
     */
    public Button(String text) {
        super(WidgetType.Button);
        this.setText(text);
        if (FlameUtils.isBlank(this.getName())) {
            this.setName(text);
        }
        this.setEasyUI(false);
    }

    /**
     * 通过文本和点击事件构造。
     * @param text    按钮文本
     * @param onclick 点击事件 JS 代码
     */
    public Button(String text, String onclick) {
        super(WidgetType.Button);
        this.setText(text);
        if (FlameUtils.isBlank(this.getName())) {
            this.setName(text);
        }
        this.setEasyUI(false);
        this.addEvent("onclick", onclick);
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "button";
    }

    /** @return 按钮 type 属性值 */
    public String getType() {
        return this.type;
    }

    /** @param type 按钮 type 属性值 */
    public void setType(String type) {
        this.type = type;
    }

    /** 设置 {@code type} 和 {@code style} DOM 属性。 */
    @Override
    protected void appendDomAttributes() {
        if (FlameUtils.isNotBlank(this.getType())) {
            this.appendEl("type", this.getType());
        }
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
