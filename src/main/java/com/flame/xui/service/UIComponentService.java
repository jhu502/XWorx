package com.flame.xui.service;

import com.flame.orm.XObject;
import com.flame.util.XException;
import com.flame.xui.widget.*;
import org.springframework.stereotype.Component;

import com.flame.localize.LocalizationHelper;
import com.flame.thing.IPropertyDefinition;
import com.flame.type.XBaseType;
import com.flame.xui.IWidget;
import com.flame.xui.WidgetMode;

@Component
public class UIComponentService {

    /**
     * 根据 XBaseType 和 WidgetMode 生成对应的 IWidget。
     *
     * <p>集中管理所有 Primitive 类型的 Widget 创建逻辑，
     * Primitive 的 getIWidget 方法委托调用此静态方法。</p>
     *
     * @param model      组件模型（Create/Edit/Display/Primary）
     * @param definition 属性定义
     * @return 对应的 IWidget，NOTHING 类型返回 null
     */
    public static IWidget createWidget(IPropertyDefinition definition, WidgetMode model) {
        XBaseType baseType = definition.getBaseType();

        if (baseType == null) {
            return null;
        }

        switch (baseType) {
            case BOOLEAN:
                return createBooleanWidget(model, definition);
            case IMAGE:
                return createImageWidget(model, definition);
            case PASSWORD:
                return createPasswordWidget(model, definition);
            case BLOB:
                return createBlobWidget(model, definition);
            case TEXT:
                return createTextWidget(model, definition);
            case OBJECT:
            case QUERY:
            case SCHEDULE:
                return createObjectWidget(model, definition);
            case NOTHING:
                return null;
            default:
                return createStandardWidget(model, definition);
        }
    }

    // ==================== 标准文本模式 ====================

    /**
     * 标准文本模式：Display → TextDisplay，Edit/Create → TextBox（单行）。
     * 适用于 STRING, NUMBER, DATETIME, TIMESPAN, INFOTABLE, LOCATION, XML, JSON,
     * HTML, TAGS, VARIANT, GUID, INTEGER, LONG, HYPERLINK, IMAGELINK。
     */
    private static IWidget createStandardWidget(WidgetMode model, IPropertyDefinition definition) {
        String property = definition != null ? definition.getName() : "";
        String defaultValue = definition != null ? definition.getDefaultValue() : null;

        switch (model) {
            case Display:
                return createTextDisplay(property, defaultValue, model);
            case Edit:
                return createTextBox(property, definition, defaultValue, model, true, 1);
            case Create:
            default:
                return createTextBox(property, definition, null, model, false, 1);
        }
    }

    // ==================== 多行文本模式 ====================

    /**
     * 多行文本模式：Display → TextDisplay，Edit/Create → TextBox（多行 textarea）。
     * 适用于 TEXT。
     */
    private static IWidget createTextWidget(WidgetMode model, IPropertyDefinition definition) {
        String property = definition != null ? definition.getName() : "";
        String defaultValue = definition != null ? definition.getDefaultValue() : null;

        switch (model) {
            case Display:
                return createTextDisplay(property, defaultValue, model);
            case Edit:
                return createTextBox(property, definition, defaultValue, model, true, 5);
            case Create:
            default:
                return createTextBox(property, definition, null, model, false, 5);
        }
    }

    // ==================== 布尔模式 ====================

    /**
     * 布尔模式：Display → TextDisplay（国际化），Edit/Create → ComboBox（true/false）。
     */
    private static IWidget createBooleanWidget(WidgetMode model, IPropertyDefinition definition) {
        String property = definition != null ? definition.getName() : "";
        String defaultValue = definition != null ? definition.getDefaultValue() : null;

        switch (model) {
            case Display: {
                TextDisplay textDisplay = new TextDisplay();
                textDisplay.setId(property);
                textDisplay.setName(property);
                if (defaultValue != null) {
                    textDisplay.setText(LocalizationHelper.get(defaultValue));
                }
                textDisplay.setWidgetMode(model);
                return textDisplay;
            }
            case Edit: {
                ComboBox comboBox = new ComboBox(property);
                comboBox.setId(property);
                comboBox.setName(property);
                comboBox.setRequired(!definition.isNullable());
                comboBox.setStyle("width:80px;height:25px;");
                comboBox.addOption("false", LocalizationHelper.get("false"));
                comboBox.addOption("true", LocalizationHelper.get("true"));
                if (defaultValue != null) {
                    comboBox.setValue(defaultValue);
                }
                comboBox.setWidgetMode(model);
                return comboBox;
            }
            case Create:
            default: {
                ComboBox comboBox = new ComboBox(property);
                comboBox.setId(property);
                comboBox.setName(property);
                comboBox.setRequired(!definition.isNullable());
                comboBox.setStyle("width:80px;height:25px;");
                comboBox.addOption("false", LocalizationHelper.get("false"));
                comboBox.addOption("true", LocalizationHelper.get("true"));
                comboBox.setWidgetMode(model);
                return comboBox;
            }
        }
    }

    // ==================== 枚举感知模式 ====================

    /**
     * 枚举感知模式：根据 propertyClass 是否枚举类型，选择 ComboBox 或标准文本。
     * 适用于 OBJECT, QUERY, SCHEDULE。
     */
    private static IWidget createObjectWidget(WidgetMode model, IPropertyDefinition definition) {
        String property = definition != null ? definition.getName() : "";
        String defaultValue = definition != null ? definition.getDefaultValue() : null;
        Class<?> propertyClass = definition != null ? definition.getPropertyClass() : null;
        boolean isEnum = propertyClass != null && propertyClass.isEnum();
        boolean isXObject = propertyClass != null && XObject.class.isAssignableFrom(propertyClass);

        try {
            switch (model) {
                case Display:
                    if (isXObject) {
                        XObject xObject = (XObject) propertyClass.newInstance();
                        return xObject.getXUIWidget(model);
                    }
                    return createTextDisplay(property, defaultValue, model);
                case Edit: {
                    if (isEnum) {
                        return createEnumComboBox(property, definition, defaultValue, model, true);
                    } else if (isXObject) {
                        XObject xObject = (XObject) propertyClass.newInstance();
                        return xObject.getXUIWidget(model);
                    }
                    return createTextBox(property, definition, defaultValue, model, true, 1);
                }
                case Create:
                default: {
                    if (isEnum) {
                        return createEnumComboBox(property, definition, null, model, false);
                    } else if (isXObject) {
                        XObject xObject = (XObject) propertyClass.newInstance();
                        return xObject.getXUIWidget(model);
                    }
                    return createTextBox(property, definition, null, model, false, 1);
                }
            }
        } catch (Exception e) {
            throw new XException(e);
        }
    }

    // ==================== 图片模式 ====================

    /**
     * 图片模式：Display → HTMLElement(img)，Edit/Create → TextBox。
     */
    private static IWidget createImageWidget(WidgetMode model, IPropertyDefinition definition) {
        String property = definition != null ? definition.getName() : "";
        String defaultValue = definition != null ? definition.getDefaultValue() : null;

        switch (model) {
            case Display: {
                IconBox iconBox = new IconBox(property);
                iconBox.setId(property);
                iconBox.setStyle("max-width:200px;max-height:200px;");
                if (defaultValue != null) {
                    iconBox.setValue(defaultValue);
                }
                iconBox.setWidgetMode(model);
                return iconBox;
            }
            case Edit:
                return createTextBox(property, definition, defaultValue, model, true, 1);
            case Create:
            default:
                return createTextBox(property, definition, null, model, false, 1);
        }
    }

    // ==================== 密码模式 ====================

    /**
     * 密码模式：Display → TextDisplay("******")，Edit/Create → TextBox。
     */
    private static IWidget createPasswordWidget(WidgetMode model, IPropertyDefinition definition) {
        String property = definition != null ? definition.getName() : "";
        String defaultValue = definition != null ? definition.getDefaultValue() : null;

        switch (model) {
            case Display: {
                TextDisplay textDisplay = new TextDisplay();
                textDisplay.setId(property);
                textDisplay.setName(property);
                if (defaultValue != null) {
                    textDisplay.setText("******");
                }
                textDisplay.setWidgetMode(model);
                return textDisplay;
            }
            case Edit:
                return createTextBox(property, definition, defaultValue, model, true, 1);
            case Create:
            default:
                return createTextBox(property, definition, null, model, false, 1);
        }
    }

    // ==================== BLOB 模式 ====================

    /**
     * BLOB 模式：Display → TextDisplay（超链接），Edit/Create → TextBox。
     */
    private static IWidget createBlobWidget(WidgetMode model, IPropertyDefinition definition) {
        String property = definition != null ? definition.getName() : "";
        String defaultValue = definition != null ? definition.getDefaultValue() : null;

        switch (model) {
            case Display: {
                TextDisplay textDisplay = new TextDisplay();
                textDisplay.setId(property);
                textDisplay.setName(property);
                if (defaultValue != null) {
                    textDisplay.setText("<a href='" + defaultValue + "'>" + defaultValue + "</a>");
                }
                textDisplay.setWidgetMode(model);
                return textDisplay;
            }
            case Edit:
                return createTextBox(property, definition, defaultValue, model, true, 1);
            case Create:
            default:
                return createTextBox(property, definition, null, model, false, 1);
        }
    }

    // ==================== 公共辅助方法 ====================

    /**
     * 创建 Display 模式的 TextDisplay。
     */
    private static TextDisplay createTextDisplay(String property, String defaultValue, WidgetMode model) {
        TextDisplay textDisplay = new TextDisplay();
        textDisplay.setId(property);
        textDisplay.setName(property);
        if (defaultValue != null) {
            textDisplay.setText(defaultValue);
        }
        textDisplay.setWidgetMode(model);
        return textDisplay;
    }

    /**
     * 创建 Edit/Create 模式的 TextBox。
     *
     * @param includeDefaultValue 是否设置默认值（Edit 为 true，Create 为 false）
     * @param rows                文本行数，1 为单行（{@code <input>}），大于 1 为多行（{@code <textarea>}）
     */
    private static TextBox createTextBox(String property, IPropertyDefinition definition, String defaultValue, WidgetMode model, boolean includeDefaultValue, int rows) {
        TextBox textBox = new TextBox(property);
        textBox.setId(property);
        textBox.setName(property);
        textBox.setRequired(!definition.isNullable());
        textBox.setRowCount(rows);
        textBox.setStyle(rows > 1 ? "width:200px;height:100px;" : "width:200px;height:25px;");
        if (includeDefaultValue && defaultValue != null) {
            textBox.setValue(defaultValue);
        }
        textBox.setWidgetMode(model);
        return textBox;
    }

    /**
     * 创建枚举类型的 ComboBox（Edit/Create 模式）。
     *
     * @param includeDefaultValue 是否设置默认值
     */
    private static ComboBox createEnumComboBox(String property, IPropertyDefinition definition, String defaultValue, WidgetMode model, boolean includeDefaultValue) {
        Class<?> propertyClass = definition != null ? definition.getPropertyClass() : null;
        ComboBox comboBox = new ComboBox(property);
        comboBox.setId(property);
        comboBox.setName(property);
        comboBox.setRequired(!definition.isNullable());
        comboBox.setStyle("height:25px;min-width:80px;");
        comboBox.inflate(propertyClass);
        if (includeDefaultValue && defaultValue != null) {
            comboBox.setValue(defaultValue);
        }
        comboBox.setWidgetMode(model);
        return comboBox;
    }
}
