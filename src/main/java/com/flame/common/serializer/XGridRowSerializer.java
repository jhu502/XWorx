package com.flame.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.flame.localize.AbstractEnumerated;
import com.flame.localize.LocalizationHelper;
import com.flame.orm.XPersistable;
import com.flame.util.XException;
import com.flame.xui.service.RowComponent;
import com.flame.xui.service.TreeComponentNode;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

/**
 * 网格行序列化器 —— 将 {@link RowComponent}（表格行、树节点）序列化为 JSON 对象。
 *
 * <h3>核心职责</h3>
 * <p>这是 Flame UI 框架中最关键的序列化器，负责将服务端的行数据按前端组件需要的格式输出 JSON。
 * 它连接了后端的列定义（{@code fields} 列表）与前端的数据消费。</p>
 *
 * <h3>两种序列化模式</h3>
 * <ol>
 *   <li><b>字段筛选模式</b>（{@code columns != null}） —— 推荐路径<br>
 *       只序列化列定义（{@code @UIColumn}）中声明的字段。通过 {@link RowComponent#getFieldValue(String)}
 *       获取每个字段的值，并按值类型分派序列化策略。不输出多余字段，减少网络传输量。</li>
 *   <li><b>全字段模式</b>（{@code columns == null}） —— 降级路径<br>
 *       遍历对象的所有 getter 方法，委托父类序列化所有属性。用于没有列定义的场景。</li>
 * </ol>
 *
 * <h3>字段值类型分派（字段筛选模式）</h3>
 * <ul>
 *   <li>{@link String} → 直接写字符串字段</li>
 *   <li>{@link XPersistable} → 只输出 OID（避免循环引用）</li>
 *   <li>{@link AbstractEnumerated} → 输出国际化显示名称</li>
 *   <li>枚举类型 → 输出 {@code name()} 值</li>
 *   <li>其他类型 → 调用 {@code writeObjectField} 正常输出</li>
 * </ul>
 *
 * <h3>树节点特殊处理</h3>
 * <p>当行对象是 {@link TreeComponentNode} 时，额外输出：
 * <ul>
 *   <li>{@code state$_} —— 树节点展开状态（"open"/"closed"/null）</li>
 *   <li>{@code children} —— 子节点列表（支持嵌套树结构）</li>
 * </ul></p>
 *
 * <h3>ROW_COMPONENT ThreadLocal</h3>
 * <p>通过 {@link #ROW_COMPONENT} 将当前正在序列化的行对象传递给 {@link com.flame.xui.IWidget}。
 * XUIWidgetSerializer 检测到此上下文后，会自动将 EasyUI 控件降级为普通 HTML，因为
 * EasyUI 控件无法在表格单元格内正常渲染。</p>
 *
 * @param <T> 行组件类型，必须继承 {@link RowComponent}
 * @author Hujin
 * @see RowComponent
 * @see TreeComponentNode
 * @see XUIWidgetSerializer
 */
@JsonComponent
public class XGridRowSerializer<T extends RowComponent> extends AbstractSerializer<T> {
    /**
     * 当前正在序列化的行组件上下文（ThreadLocal 保证线程安全）。
     *
     * <p>XUIWidgetSerializer 通过 {@link #getRowComponent()} 读取此值，用于判断 Widget 是否位于
     * 数据行内部。若位于行内，则自动关闭 EasyUI 模式，避免在表格单元格中渲染 EasyUI 组件。</p>
     */
    public static final ThreadLocal<RowComponent> ROW_COMPONENT = new ThreadLocal<>();

    /**
     * 序列化行组件为 JSON 对象。
     *
     * <h4>执行流程</h4>
     * <ol>
     *   <li>将当前行对象设置到 {@link #ROW_COMPONENT} ThreadLocal 中（供 XUIWidgetSerializer 读取）</li>
     *   <li>获取行的字段列表（{@code columns}）：
     *     <ul>
     *       <li>若为 null → 全字段模式：遍历所有 getter 方法序列化</li>
     *       <li>若不为 null → 字段筛选模式：按列定义逐个获取字段值并分派序列化</li>
     *     </ul>
     *   </li>
     *   <li>若行对象为 {@link TreeComponentNode} → 额外输出 {@code state$_} 和 {@code children}</li>
     *   <li>finally 块中清除 ThreadLocal，防止内存泄漏</li>
     * </ol>
     *
     * @param object     行组件实例（可为null）
     * @param generator  JSON 生成器
     * @param provider   序列化器提供者（未使用）
     */
    @Override
    public void serialize(T object, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (object == null) {
            generator.writeObject(null);
            return;
        }

        try {
            ROW_COMPONENT.set(object);
            generator.writeStartObject();
            List<String> columns = object.getFields();
            if (columns == null) {
                // 全字段模式：没有列定义时，序列化所有getter方法
                for (Method method : object.getClass().getMethods()) {
                    this.serialize(object, method, generator);
                }
            } else {
                /**
                 * 字段筛选模式：根据@UIDefinition[UITreeGrid/UIDataGrid/UIPropertyGrid/UIMeshGrid]
                 * 中定义的@UIColumn的field来生成json，只输出前端需要的列。
                 */
                for (String field : columns) {
                    Object fieldValue = object.getFieldValue(field);
                    if (fieldValue == null)
                        continue;

                    if (fieldValue instanceof String) {
                        generator.writeStringField(field, (String) fieldValue);
                    } else if (fieldValue instanceof XPersistable) {
                        // 持久化对象只输出OID，防止循环引用
                        XPersistable persist = (XPersistable) fieldValue;
                        generator.writeStringField(field, persist.getOid());
                    } else if (fieldValue instanceof AbstractEnumerated) {
                        // 枚举值输出国际化显示名称
                        Locale locale = LocalizationHelper.getLocale();
                        generator.writeStringField(field, ((AbstractEnumerated<?>) fieldValue).getDisplay(locale));
                    } else {
                        Class<?> fieldType = fieldValue.getClass();
                        if (fieldType.isEnum()) {
                            // Java原生枚举输出name()
                            try {
                                Method method = fieldType.getMethod("name", new Class[0]);
                                Object _value = method.invoke(fieldValue, new Object[0]);
                                generator.writeObjectField(field, _value);
                            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                throw new XException(e);
                            }
                        } else {
                            generator.writeObjectField(field, fieldValue);
                        }
                    }
                }
                /**
                 * TreeComponentNode需要额外的生成state$_（树节点展开状态）、children（子节点列表）数据。
                 * 这些字段不在列定义中，但对前端EasyUI TreeGrid是必需的。
                 */
                if (object instanceof TreeComponentNode) {
                    TreeComponentNode treeNode = (TreeComponentNode) object;
                    generator.writeStringField(TreeComponentNode.STATE, treeNode.getState());
                    List<Object> children = treeNode.getChildren();
                    generator.writeObjectField(TreeComponentNode.CHILDREN, children);
                }
            }
            generator.writeEndObject();
        } finally {
            ROW_COMPONENT.remove();
        }
    }

    /**
     * 获取当前线程正在序列化的行组件（供 XUIWidgetSerializer 等外部组件查询上下文）。
     *
     * <h4>用途</h4>
     * <p>XUIWidgetSerializer 通过此方法判断 Widget 是否嵌套在数据行中。
     * 若返回非 null，说明 Widget 位于 DataGrid/TreeGrid/PropertyGrid 的数据行内部，
     * 此时 EasyUI 控件无法正常渲染，需关闭 EasyUI 模式降级为普通 HTML。</p>
     *
     * @return 当前线程的行组件；未在行序列化上下文中时返回 null
     */
    public static RowComponent getRowComponent() {
        return XGridRowSerializer.ROW_COMPONENT.get();
    }
}
