package com.flame.xui.widget;

import com.flame.common.serializer.XGridRowSerializer;
import com.flame.util.FlameUtils;
import com.flame.xui.WidgetMode;
import com.flame.xui.XUIWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;
import org.apache.commons.text.StringEscapeUtils;

/**
 * 数字输入框组件，渲染为 {@code <input>} 标签（由 EasyUI 初始化为 NumberBox）。
 *
 * <p>通过 {@code data-options} 的 {@code precision} 属性控制小数精度。
 * 内嵌在 Grid 行中时自动阻止点击冒泡。</p>
 *
 * <p>Display 模式下渲染为 {@code <span>}，显示转义后的数字值。</p>
 */
public class NumberBox extends XUIWidget {

    /** 小数精度，-1 表示不限制 */
    private int precision = -1;

    /**
     * 通过 {@link UIWidget} 注解构造，自动读取 precision 属性。
     * @param widget UIWidget 注解实例
     */
    public NumberBox(UIWidget widget) {
        super(widget);
        if (FlameUtils.isBlank(widget.precision())) {
            this.setPrecision(Integer.parseInt(widget.precision()));
        }
    }

    /** 无参默认构造，WidgetType 为 {@link WidgetType#NumberBox}。 */
    public NumberBox() {
        super(WidgetType.NumberBox);
    }

    @Override
    public String getTag() {
        if (WidgetMode.Display.equals(this.getWidgetMode())) {
            return "span";
        }
        return "input";
    }

    /** @return 小数精度 */
    public int getPrecision() {
        return precision;
    }

    /** @param precision 小数精度，-1 表示不限制 */
    public void setPrecision(int precision) {
        this.precision = precision;
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
            if (this.getValue() != null) {
                this.append(StringEscapeUtils.escapeHtml4(this.getValue().toString()));
            }
            return;
        }
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        }
    }

    /**
     * 生成 EasyUI NumberBox 的 {@code data-options} 属性。
     * 当 precision &gt;= 0 时自动追加 {@code precision:} 配置。
     */
    @Override
    protected void genDataOptions() {
        String dataOptions = this.genTraitString();
        if (this.getPrecision() > -1) {
            if (dataOptions.isEmpty()) {
                dataOptions = "precision:" + this.getPrecision();
            } else {
                dataOptions = ",precision:" + this.getPrecision();
            }
        }
        if (FlameUtils.isNotBlank(dataOptions)) {
            this.appendEl("data-options", dataOptions);
        }
    }
}
