package com.flame.xui.widget;

import com.flame.common.serializer.XGridRowSerializer;
import com.flame.util.FlameUtils;
import org.apache.commons.text.StringEscapeUtils;
import com.flame.xui.WidgetMode;
import com.flame.xui.XUIWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;

/**
 * 复选框组件，渲染为 {@code <button>} 标签（由 EasyUI 初始化为 CheckBox）。
 *
 * <p>支持通过 {@code onclick} 参数直接绑定点击事件。
 * 内嵌在 Grid 行中时自动阻止点击冒泡。</p>
 *
 * <p>Display 模式下渲染为 {@code <span>}，显示 ☑/☐ + 文本。</p>
 */
public class CheckBox extends XUIWidget {

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public CheckBox(UIWidget widget) {
        super(widget);
        this.setEasyUI(false);
    }

    /**
     * 通过文本构造，名称默认为文本值。
     * @param text 复选框标签文本
     */
    public CheckBox(String text) {
        super(WidgetType.CheckBox);
        this.setText(text);
        if (FlameUtils.isBlank(this.getName())) {
            this.setName(text);
        }
        this.setEasyUI(false);
    }

    /**
     * 通过文本和点击事件构造。
     * @param text    复选框标签文本
     * @param onclick 点击事件 JS 代码
     */
    public CheckBox(String text, String onclick) {
        super(WidgetType.CheckBox);
        this.setText(text);
        if (FlameUtils.isBlank(this.getName())) {
            this.setName(text);
        }
        this.setEasyUI(false);
        this.addEvent("onclick", onclick);
    }

    @Override
    public String getTag() {
        if (WidgetMode.Display.equals(this.getWidgetMode())) {
            return "span";
        }
        return "button";
    }

    @Override
    protected void appendDomAttributes() {
        this.appendEl("style", this.getStyle());
    }

    /** Grid 行内使用时自动阻止点击冒泡。Display 模式下跳过。 */
    @Override
    protected void appendEventElement() {
        if (WidgetMode.Display.equals(this.getWidgetMode())) {
            return;
        }
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

    @Override
    protected void genInnerHTML() {
        if (WidgetMode.Display.equals(this.getWidgetMode())) {
            String text = this.getText();
            String escaped = text != null ? StringEscapeUtils.escapeHtml4(text) : "";
            if (this.getValue() != null && "true".equals(this.getValue().toString())) {
                this.append("☑ ").append(escaped);
            } else {
                this.append("☐ ").append(escaped);
            }
            return;
        }
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        }
    }
}
