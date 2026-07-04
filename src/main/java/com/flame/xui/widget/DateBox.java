package com.flame.xui.widget;

import com.flame.common.serializer.XGridRowSerializer;
import com.flame.localize.LocalizationHelper;
import com.flame.util.FlameUtils;
import com.flame.xui.WidgetMode;
import com.flame.xui.XUIWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;
import org.apache.commons.text.StringEscapeUtils;

/**
 * 日期选择框组件，渲染为 {@code <input>} 标签（由 EasyUI 初始化为 DateBox）。
 *
 * <p>通过 {@code data-options} 属性注入 EasyUI 配置：
 * 自动设置面板宽高（170px × 190px）和本地化日期格式化函数
 * {@code datebox$local(date, locale)}。</p>
 *
 * <p>内嵌在 Grid 行中时自动阻止点击冒泡。</p>
 *
 * <p>Display 模式下渲染为 {@code <span>}，显示转义后的日期文本。</p>
 */
public class DateBox extends XUIWidget {

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public DateBox(UIWidget widget) {
        super(widget);
    }

    /**
     * 通过文本构造（作为默认值）。
     * @param text 默认日期文本
     */
    public DateBox(String text) {
        super(WidgetType.DateBox);
        this.setText(text);
    }

    /**
     * 通过文本和点击事件构造。
     * @param text    默认日期文本
     * @param onclick 点击事件 JS 代码
     */
    public DateBox(String text, String onclick) {
        super(WidgetType.DateBox);
        this.setText(text);
        this.addEvent("onclick", onclick);
    }

    @Override
    public String getTag() {
        if (WidgetMode.Display.equals(this.getWidgetMode())) {
            return "span";
        }
        return "input";
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
     * 生成 EasyUI DateBox 的 {@code data-options} 属性。
     * 自动追加面板宽高（170px×190px）和当前 locale 的日期格式化函数。
     */
    @Override
    protected void genDataOptions() {
        String dataOptions = this.genTraitString();
        if (FlameUtils.isBlank(dataOptions)) {
            dataOptions = "panelWidth:'170px',panelHeight:'190px'";
        } else {
            if (dataOptions.indexOf("panelWidth") < 0 && dataOptions.indexOf("panelHeight") < 0) {
                if (dataOptions.endsWith(",")) {
                    dataOptions = dataOptions + "panelWidth:'170px',panelHeight:'190px'";
                } else {
                    dataOptions = dataOptions + ",panelWidth:'170px',panelHeight:'190px'";
                }
            }
            if (dataOptions.indexOf("formatter:") < 0) {
                String local = LocalizationHelper.getLocale().toString();
                if (dataOptions.endsWith(",")) {
                    dataOptions = dataOptions + "formatter:function(date){return datebox$local(date,'" + local + "');}";
                } else {
                    dataOptions = dataOptions + ",formatter:function(date){return datebox$local(date,'" + local + "');}";
                }
            }
        }
        if (FlameUtils.isNotBlank(dataOptions)) {
            this.appendEl("data-options", dataOptions);
        }
    }
}
