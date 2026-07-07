package com.flame.xui.widget;

import com.flame.common.serializer.XGridRowSerializer;
import com.flame.util.FlameUtils;
import org.apache.commons.text.StringEscapeUtils;
import com.flame.xui.WidgetMode;
import com.flame.xui.XUIWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 单选按钮组组件，渲染为一组 {@code <span><input type="radio"/></span>} 标签。
 *
 * <p>通过 {@link #addRadio(String, String)} 逐项添加选项，
 * 默认选中与 {@code value} 匹配的项。
 * 内嵌在 Grid 行中时自动阻止点击冒泡到行选择事件。</p>
 *
 * <p>Display 模式下渲染为 {@code <span>}，显示选中选项的 display 文本。</p>
 */
public class RadioBox extends XUIWidget {
    private boolean isHorizontal = true;
    /** 选项集合：key = value, value = display */
    private Map<String, Object> radioSet = new LinkedHashMap<>();

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public RadioBox(UIWidget widget) {
        super(widget);
        this.setEasyUI(false);
    }

    /** 无参默认构造，WidgetType 为 {@link WidgetType#RadioBox}。 */
    public RadioBox() {
        super(WidgetType.RadioBox);
        this.setEasyUI(false);
    }

    @Override
    public String getTag() {
        return "span";
    }

    public void setHorizontal(boolean bool) {
        this.isHorizontal = bool;
    }

    /** @return 选项集合 */
    public Map<String, Object> getRadioSet() {
        return radioSet;
    }

    /** @param radioSet 选项集合 */
    public void setRadioSet(Map<String, Object> radioSet) {
        this.radioSet = radioSet;
    }

    /**
     * 添加一个单选选项。
     * @param value   选项值
     * @param display 显示文本
     */
    public void addRadio(String value, String display) {
        this.radioSet.put(value, display);
    }

    @Override
    protected void appendDomAttributes() {
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
            if (this.getValue() != null && this.radioSet.containsKey(this.getValue().toString())) {
                this.append(StringEscapeUtils.escapeHtml4(this.radioSet.get(this.getValue().toString()).toString()));
            }
            return;
        }
        for (Entry<String, Object> entry : this.radioSet.entrySet()) {
            StringBuilder el_radio = new StringBuilder();
            el_radio.append("<input type=\"radio\" ");
            if (FlameUtils.isNotBlank(this.getName())) {
                el_radio.append("name=\"").append(this.getName()).append("\" ");
            }
            el_radio.append("value=\"").append(entry.getKey()).append("\" ");
            if (this.getValue().equals(entry.getKey())) {
                el_radio.append("checked=true ");
            }
            if (FlameUtils.isNotBlank(this.getStyle())) {
                el_radio.append("style=\"").append(this.getStyle()).append("\" ");
            }
            for (Entry<String, String> event : this.getEventMap().entrySet()) {
                el_radio.append(event.getKey()).append("=\"").append(event.getValue().replaceAll("\"", "'")).append("\" ");
            }
            el_radio.append(">");
            el_radio.append(entry.getValue());
            el_radio.append("</input>");
            if (!this.isHorizontal) {
                el_radio.append("<br/>");
            }
            this.append(el_radio.toString());
        }
    }
}
