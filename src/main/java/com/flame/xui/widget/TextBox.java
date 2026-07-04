package com.flame.xui.widget;

import org.apache.commons.text.StringEscapeUtils;

import com.flame.common.serializer.XGridRowSerializer;
import com.flame.xui.WidgetMode;
import com.flame.xui.XUIWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;
import com.flame.util.FlameUtils;

/**
 * 文本输入框组件，支持单行{@code <input>}和多行{@code <textarea>}两种模式。
 *
 * <p>value 会通过 {@link StringEscapeUtils#escapeHtml4} 进行 HTML 转义，
 * 防止 XSS 注入。内嵌在 Grid 行中时自动阻止点击冒泡。</p>
 *
 * <p>当行数大于 1（{@code setRowCount(n)}）时渲染为 {@code <textarea>}，
 * 内容作为标签体输出（而非 value 属性），同时输出 {@code rows} 属性。</p>
 *
 * <p>Display 模式下渲染为 {@code <span>}，显示转义后的文本值。</p>
 */
public class TextBox extends XUIWidget {

    private int rowCount = 1;

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public TextBox(UIWidget widget) {
        super(widget);
    }

    /**
     * 通过名称构造。
     * @param name 输入框名称
     */
    public TextBox(String name) {
        super(WidgetType.TextBox);
        this.setName(name);
    }

    /** 获取文本行数。1 表示单行（{@code <input>}），大于 1 表示多行（{@code <textarea>}）。 */
    public int getRowCount() {
        return rowCount;
    }

    /** 设置文本行数。设为 1 渲染为单行输入框，大于 1 渲染为多行文本域。 */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public String getTag() {
        if (WidgetMode.Display.equals(this.getWidgetMode())) {
            return "span";
        }
        return this.rowCount > 1 ? "textarea" : "input";
    }

    /** Grid 行内使用时自动阻止点击冒泡。 */
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
    protected void appendDomAttributes() {
        this.appendEl("style", this.getStyle());
        if (WidgetMode.Display.equals(this.getWidgetMode())) {
            return;
        }
        if (this.rowCount > 1) {
            this.appendEl("rows", this.rowCount);
        } else if (this.getValue() != null) {
            this.appendEl("value", StringEscapeUtils.escapeHtml4(this.getValue().toString()));
        }
    }

    @Override
    protected void genDataOptions() {
        if (this.rowCount > 1) {
            this.addTrait("multiline", true);
            this.addTrait("rows", this.rowCount);
        }
        if (WidgetMode.Display.equals(this.getWidgetMode()) && this.getValue() != null) {
            this.addTrait("value", this.getValue().toString());
        }
        super.genDataOptions();
    }

    @Override
    protected void genInnerHTML() {
        if (WidgetMode.Display.equals(this.getWidgetMode())) {
            if (this.getValue() != null) {
                this.append(StringEscapeUtils.escapeHtml4(this.getValue().toString()));
            }
            return;
        }
        if (this.rowCount > 1 && this.getValue() != null) {
            this.append(StringEscapeUtils.escapeHtml4(this.getValue().toString()));
        } else if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        }
    }
}
