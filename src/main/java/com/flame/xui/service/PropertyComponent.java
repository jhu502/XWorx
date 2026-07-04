package com.flame.xui.service;

import com.flame.localize.LocalizationHelper;
import com.flame.orm.XObject;
import com.flame.util.PropertyUtil;
import com.flame.xui.XUIWidget;
import com.flame.xui.builder.AbstractPropertyComponentBuilder;

/**
 * 属性组件 —— 表示属性网格（PropertyGrid）中的单个属性项。
 *
 * <h3>用途</h3>
 * <p>在属性网格中，每行展示一个对象属性的"名称—值"对。本组件持有：
 * <ul>
 *   <li>{@code rowId} —— 行的唯一标识，格式为 {@code "OID|对象OID~MBA|属性名"}</li>
 *   <li>{@code property} —— 属性名（对应JavaBean属性）</li>
 *   <li>{@code display} —— 属性的显示名称（经过国际化）</li>
 *   <li>{@code value} —— 属性的当前值（通过反射从对象中读取）</li>
 *   <li>{@code group} —— 属性分组名称（用于属性网格的分组折叠）</li>
 * </ul></p>
 *
 * <h3>创建方式</h3>
 * <p>提供两个静态工厂方法：
 * <ul>
 *   <li>{@link #newPropertyComponent(String, String, Object, String)} —— 手动指定各字段值</li>
 *   <li>{@link #newPropertyComponent(XObject, String, String)} —— 通过反射自动从持久化对象读取属性值</li>
 * </ul></p>
 *
 * @author Flame
 * @see AbstractPropertyComponentBuilder
 */
public class PropertyComponent {
    /** 关联的主对象引用（属性的来源对象） */
    private XObject primary;
    /** JavaBean属性名 */
    private String property;
    /**
     * 行唯一标识，格式为 "OID|{对象OID}~MBA|{属性名}"。
     * "OID"前缀和"MBA"前缀用于前端区分不同类型的行标识。
     */
    private String rowId;
    /** 属性的显示名称（国际化后的文本） */
    private Object display;
    /** 属性的当前值 */
    private Object value;
    /** 属性分组名称，用于属性网格的分组折叠展示 */
    private String group;

    /**
     * 手动创建属性组件（指定所有字段值）。
     *
     * <p>若 value 为 {@link XUIWidget} 实例，会自动将其设为非简易模式（{@code setEasyGui(false)}），
     * 确保UI组件在属性网格中以完整形态渲染。</p>
     *
     * @param property 属性名
     * @param display  显示名称
     * @param value    属性值
     * @param group    分组名称
     * @return 属性组件实例
     */
    public static PropertyComponent newPropertyComponent(String property, String display, Object value, String group) {
        PropertyComponent component = new PropertyComponent();
        component.rowId = property;
        component.property = property;
        component.display = display;
        component.value = value;
        if (component.value instanceof XUIWidget) {
            XUIWidget guiWidget = (XUIWidget) component.value;
            guiWidget.setEasyUI(false);
        }
        component.group = group;

        return component;
    }

    /**
     * 从持久化对象自动创建属性组件（通过反射读取属性值）。
     *
     * <p>rowId 自动生成为 {@code "OID|" + rowObject.getOid() + "~MBA|" + property} 格式，
     * display 和 group 会经过 {@link LocalizationHelper#get(String)} 国际化处理。</p>
     *
     * @param rowObject 持久化对象，属性值通过 {@link PropertyUtil#getProperty} 反射读取
     * @param property  JavaBean属性名
     * @param group     分组名称（原始key，会自动国际化）
     * @return 属性组件实例
     */
    public static PropertyComponent newPropertyComponent(XObject rowObject, String property, String group) {
        PropertyComponent component = new PropertyComponent();
        component.primary = rowObject;
        component.rowId = "OID|" + rowObject.getOid() + "~MBA|" + property;
        component.property = property;
        component.display = LocalizationHelper.get(property);
        component.value = PropertyUtil.getProperty(component.primary, component.property);
        component.group = LocalizationHelper.get(group);

        return component;
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getDisplay() {
        return display;
    }

    public void setDisplay(Object display) {
        this.display = display;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
