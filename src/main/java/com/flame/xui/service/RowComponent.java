package com.flame.xui.service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.flame.xui.GridComponent;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.flame.localize.AbstractEnumerated;
import com.flame.util.XException;
import com.flame.xui.IComponent;

/**
 * 行组件基类 —— 所有组件行（表格行、树节点、表单行、属性行）的公共父类。
 *
 * <h3>核心设计</h3>
 * <p>行组件封装一个业务数据对象（{@code object}），并支持通过 {@code fields} 列表控制序列化输出的字段范围。
 * Jackson序列化时：
 * <ul>
 *   <li>{@link JsonUnwrapped @JsonUnwrapped} 注解的 {@code object} 属性会被展平——对象的属性直接出现在行JSON中，而非嵌套在"object"键下</li>
 *   <li>{@link JsonAnyGetter @JsonAnyGetter} 注解的 {@code attributes} Map 的键值对也会被展平到行JSON中</li>
 *   <li>{@link JsonIgnore @JsonIgnore} 注解的 {@code fields} 列表仅用于服务端字段筛选，不输出到JSON</li>
 * </ul></p>
 *
 * <h3>字段值解析</h3>
 * <p>{@link #getFieldValue(String)} 方法按以下优先级获取字段值：
 * <ol>
 *   <li>从 {@code unwrapMap}（通过 {@code addAttribute} 添加的显式属性）中查找</li>
 *   <li>从 {@code object} 的JavaBean属性中通过反射读取</li>
 * </ol>
 * 对于枚举类型，自动提取其 {@code name} 值；对于 {@link AbstractEnumerated} 子类，提取其业务名称。</p>
 *
 * @author Flame
 * @see TableComponentRow
 * @see TreeComponentNode
 * @see FormComponentRow
 */
public class RowComponent implements IComponent {
    /** 行绑定的业务数据对象，序列化时其属性会被展平到行JSON中 */
    private Object object;
    /** 通过 addAttribute 显式添加的附加属性，序列化时同样被展平 */
    private Map<String, Object> unwrapMap = new LinkedHashMap<>();
    /**
     * Jackson根据fields包含的字段列表，只序列化object中匹配的属性。
     * 设为transient避免此字段本身被序列化到JSON输出中。
     */
    private transient List<String> fields;

    /**
     * 获取行绑定的业务数据对象。
     *
     * <p>通过 {@link JsonUnwrapped @JsonUnwrapped} 注解，Jackson序列化时会将此对象的属性
     * "展平"到父级JSON中。例如对象有 name、age 属性，则序列化为 {"name":"...", "age":...}
     * 而非 {"object":{"name":"...", "age":...}}。</p>
     *
     * @return 业务数据对象
     */
    @JsonUnwrapped
    public Object getObject() {
        return object;
    }

    /**
     * 设置行绑定的业务数据对象（将对象的属性序列化到引用对象中，而不是嵌套对象）。
     *
     * @param object 业务数据对象，其公开属性将在序列化时展平
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * 获取通过 {@code addAttribute} 显式添加的附加属性Map。
     *
     * <p>通过 {@link JsonAnyGetter @JsonAnyGetter} 注解，Jackson会将此Map中的键值对
     * 展平序列化到父级JSON中，与 {@code object} 的属性处于同一层级。</p>
     *
     * @return 附加属性Map，键为属性名，值为属性值
     */
    @JsonAnyGetter //将Map对象的Key/Value序列化到引用对象中，而不是嵌套对象
    public Map<String, Object> getAttributes() {
        return this.unwrapMap;
    }

    /**
     * 向行中添加一个附加属性。
     *
     * <p>附加属性优先级高于 object 的同名属性。当调用 {@link #getFieldValue(String)} 时，
     * 会优先返回通过此方法添加的值。常用于添加前端需要的虚拟列（如操作按钮列、图标列等）。</p>
     *
     * @param <T>   属性值类型
     * @param field 属性名（对应前端列定义的field）
     * @param value 属性值
     * @return 传入的value，便于链式调用
     */
    public <T extends Object> T addAttribute(String field, T value) {
        this.unwrapMap.put(field, value);
        return value;
    }

    /**
     * 获取需要序列化的字段列表（用于Jackson的PropertyFilter进行字段筛选）。
     *
     * @return 字段名列表；null表示输出所有字段
     */
    @JsonIgnore
    public List<String> getFields() {
        return fields;
    }

    /**
     * 设置需要序列化的字段列表。
     *
     * <p>由子类构建器在 {@code buildComponentData} 中调用，将组件配置中的列定义传递给行数据，
     * 确保只序列化前端需要的字段，减少网络传输量。</p>
     *
     * @param fields 字段名列表，来源于 {@link com.flame.xui.XUIComponent#fields()}
     */
    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    /**
     * 根据字段名获取对应的值。
     *
     * <h4>查找优先级</h4>
     * <ol>
     *   <li>从 {@code unwrapMap} 中直接查找（通过 {@code addAttribute} 设置的显式属性）</li>
     *   <li>若 object 不为null，通过JavaBean反射查找对应的getter方法返回值</li>
     *   <li>若字段类型为 {@code boolean}/{@code Boolean}，原样返回</li>
     *   <li>若字段类型为 {@link AbstractEnumerated} 子类，调用其 {@code getName()} 获取业务名称</li>
     *   <li>若字段类型为普通枚举，调用其 {@code name()} 获取枚举常量名</li>
     *   <li>其他类型原样返回</li>
     * </ol>
     *
     * <p>此方法在Jackson序列化的PropertyFilter中被回调，用于判断某个字段是否应该包含在输出中，
     * 以及获取该字段的实际值。</p>
     *
     * @param field 字段名
     * @return 字段值，若不存在则返回null
     * @throws XException 反射调用异常时抛出
     */
    public Object getFieldValue(String field) {
        if (this.unwrapMap.containsKey(field)) {
            return this.unwrapMap.get(field);
        } else {
            if (object == null)
                return null;

            PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(object.getClass(), field);
            if (descriptor == null)
                return null;

            Class<?> fieldType = descriptor.getPropertyType();
            Method readMethod = descriptor.getReadMethod();
            if (readMethod == null)
                return null;

            try {
                Object fieldValue = readMethod.invoke(object, new Object[0]);
                if (fieldValue == null)
                    return null;

                if (boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) {
                    return fieldValue;
                } else if (AbstractEnumerated.class.isAssignableFrom(fieldType)) {
                    return ((AbstractEnumerated<?>) fieldValue).getName();
                } else if (fieldType.isEnum()) {
                    try {
                        Method method = fieldType.getMethod("name", new Class[0]);
                        return method.invoke(fieldValue, new Object[0]);
                    } catch (NoSuchMethodException e) {
                        throw new XException(e);
                    }
                } else {
                    return fieldValue;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new XException(e);
            }
        }
    }

    /**
     * 获取行唯一标识符。
     *
     * @return 行ID，通常为持久化对象的OID；未设置时为空字符串
     */
    public Object getRowId() {
        return this.unwrapMap.get(GridComponent.ROW_ID);
    }

    /**
     * 设置行唯一标识符。
     *
     * @param rowId 行ID，用于前端表格的行定位和操作
     */
    public void setRowId(Object rowId) {
        this.unwrapMap.put(GridComponent.ROW_ID, rowId);
    }
}
