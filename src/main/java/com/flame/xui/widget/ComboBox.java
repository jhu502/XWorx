package com.flame.xui.widget;

import java.beans.PropertyDescriptor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.annotations.UIWidget;
import com.flame.common.serializer.XGridRowSerializer;
import com.flame.config.JPAConfiguration;
import com.flame.localize.AbstractEnumerated;
import com.flame.localize.IEnumeratedType;
import com.flame.localize.LocalizationHelper;
import com.flame.util.FlameUtils;
import com.flame.util.XException;
import com.flame.xui.WidgetMode;
import com.flame.xui.WidgetType;
import com.flame.xui.XUIWidget;

/**
 * 下拉选择框组件，渲染为 {@code <select>} 标签（由 EasyUI 初始化为 ComboBox）。
 *
 * <p>支持通过 {@link #addOption(String, String)} 逐项添加选项，
 * 或通过 {@link #inflateType(Object)} 自动从枚举/实体类型填充选项。</p>
 *
 * <p><b>inflateType 支持的自动填充类型：</b></p>
 * <ul>
 *   <li>{@code Enum} 类 — 从枚举常量自动填充</li>
 *   <li>{@link AbstractEnumerated} 子类 — 从数据库 RB 表自动填充</li>
 *   <li>{@code boolean/Boolean} 属性 — 填充 true/false</li>
 *   <li>实体类属性对应的枚举类型 — 自动检测并填充</li>
 * </ul>
 *
 * <p>Display 模式下渲染为 {@code <span>}，显示选中选项的 display 文本。</p>
 */
public class ComboBox extends XUIWidget {

    /** 选项集合：key = value, value = display */
    private Map<String, Object> optionMap = new LinkedHashMap<>();

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public ComboBox(UIWidget widget) {
        super(widget);
    }

    /**
     * 通过名称构造。
     * @param name 组件名称
     */
    public ComboBox(String name) {
        super(WidgetType.ComboBox);
        this.setName(name);
    }

    @Override
    public String getTag() {
        if (WidgetMode.Display.equals(this.getWidgetMode())) {
            return "span";
        }
        return "select";
    }

    /** @return 选项集合（Jackson 序列化时忽略） */
    @JsonIgnore
    public Map<String, Object> getOptionMap() {
        return optionMap;
    }

    /** @param optionMap 选项集合 */
    public void setOptionMap(Map<String, Object> optionMap) {
        this.optionMap = optionMap;
    }

    /**
     * 添加一个下拉选项。
     * @param value   选项值
     * @param display 显示文本
     * @return this（链式调用）
     */
    public ComboBox addOption(String value, String display) {
        this.optionMap.put(value, display);
        return this;
    }

    /**
     * 根据实体类型自动填充选项。
     *
     * <p>支持自动识别并填充：
     * Java 枚举、{@link AbstractEnumerated} 子类、
     * boolean 属性、实体类属性对应的枚举。</p>
     *
     * @param object 类型对象（Class 或 IThingModel）
     * @throws XException 当 name 属性为空时抛出
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void inflate(Object object) {
        if (object == null)
            return;

        if (object instanceof Class clazz) {
            if (FlameUtils.isBlank(this.getName()))
                throw new XException("Property:name is null.");

            if (clazz.isEnum()) {
                for (Object enumObj : clazz.getEnumConstants()) {
                    this.addOption(enumObj.toString(), LocalizationHelper.get(enumObj.toString()));
                }
            } else if (AbstractEnumerated.class.isAssignableFrom(clazz)) {
                Class<AbstractEnumerated> enumClass = (Class<AbstractEnumerated>) clazz;
                List<? extends AbstractEnumerated> list = JPAConfiguration.toRBTypeList(enumClass);
                for (IEnumeratedType<?> enumerated : list) {
                    this.addOption(enumerated.getName(), enumerated.getDisplay(LocalizationHelper.getLocale()));
                }
            } else {
                PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(clazz, this.getName());
                if (descriptor == null)
                    return;
                Class classType = descriptor.getPropertyType();
                this.addOption("", "");
                if (AbstractEnumerated.class.isAssignableFrom(classType)) {
                    List<? extends IEnumeratedType<?>> list = JPAConfiguration.toRBTypeList(classType);
                    for (IEnumeratedType<?> enumerated : list) {
                        this.addOption(enumerated.getName(), enumerated.getDisplay(LocalizationHelper.getLocale()));
                    }
                } else if (boolean.class.equals(classType) || Boolean.class.equals(classType)) {
                    this.addOption("false", LocalizationHelper.get("false"));
                    this.addOption("true", LocalizationHelper.get("true"));
                } else if (classType.isEnum()) {
                    for (Object enumObj : classType.getEnumConstants()) {
                        this.addOption(enumObj.toString(), LocalizationHelper.get(enumObj.toString()));
                    }
                }
            }
        } else {
            super.inflate(object);
        }
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
            if (this.getValue() != null && this.optionMap.containsKey(this.getValue().toString())) {
                this.append(StringEscapeUtils.escapeHtml4(this.optionMap.get(this.getValue().toString()).toString()));
            }
            return;
        }
        this.append("<option value=\"\"></option>");
        for (Entry<String, Object> entry : this.optionMap.entrySet()) {
            if (this.getValue().equals(entry.getKey())) {
                this.append("<option value=\"").append(entry.getKey()).append("\" selected=\"selected\">").append(entry.getValue()).append("</option>");
            } else {
                this.append("<option value=\"").append(entry.getKey()).append("\">").append(entry.getValue()).append("</option>");
            }
        }
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        }
    }

    @Override
    protected void genDataOptions() {
        if (WidgetMode.Display.equals(this.getWidgetMode()) && this.getValue() != null) {
            this.addTrait("value", this.getValue().toString());
        }
        super.genDataOptions();
    }
}
