package com.flame.xui.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIComponent;
import com.flame.xui.service.TableComponentRow;

/**
 * 抽象表格组件构建器 —— 用于构建数据表格（DataGrid）的行数据。
 *
 * <h3>设计目的</h3>
 * <p>继承自 {@link AbstractComponentBuilder}，专门处理扁平表格数据的构建。
 * 与 {@link AbstractTreeComponentBuilder}（树形表格）和 {@link AbstractMeshComponentBuilder}（表单网格）
 * 并列，形成三种主要的组件数据构建模式。</p>
 *
 * <h3>数据构建流程 {@link #buildComponentData(XCommandBean)}</h3>
 * <ol>
 *   <li>先调用 {@code buildComponentConfig} 获取列定义（字段列表）</li>
 *   <li>调用子类实现的 {@link #getTableRows(XCommandBean)} 获取原始行数据</li>
 *   <li>将每行数据包装为 {@link TableComponentRow}（若非Map类型则通过 {@code newInstance} 转换）</li>
 *   <li>设置每行的字段列表（确保只序列化前端需要的列）</li>
 *   <li>返回标准格式：{@code {"total": N, "rows": [...]}}</li>
 * </ol>
 *
 * <h3>Primary 对象透传</h3>
 * <p>支持通过 {@link #setPrimaryObject(Object)} 设置一个主对象引用，该对象会在响应数据中
 * 以 {@code "primary"} 键返回给前端。前端可以使用此数据来渲染主从表或上下文相关的UI元素。</p>
 *
 * <h3>子类需实现</h3>
 * <p>{@link #getTableRows(XCommandBean)} —— 返回表格行数据列表，每行可以是实体对象、
 * Map 或 {@link TableComponentRow} 实例。</p>
 *
 * @author Flame
 * @see AbstractComponentBuilder
 * @see TableComponentRow
 */
public abstract class AbstractTableComponentBuilder extends AbstractComponentBuilder {
    /** 可选的附加主对象，会被透传到响应数据的 "primary" 字段中 */
    private Object primary;

    /**
     * 构建表格组件的数据内容。
     *
     * <p>将子类提供的业务数据行转换为前端可渲染的 {@link TableComponentRow} 列表，
     * 并为每行设置字段列表以确保只序列化需要的列数据。</p>
     *
     * @param commandBean 请求命令对象
     * @return 包含 "total"（行数）、"rows"（行数据列表）的Map；若设置了primary则额外包含"primary"键
     */
    public Object buildComponentData(XCommandBean commandBean) {
        XUIComponent elementConfig = this.buildComponentConfig(commandBean);
        List<String> columns = elementConfig.fields();

        Map<String, Object> result = new HashMap<>();
        List<Object> data = new ArrayList<>();
        for (Object object : this.getTableRows(commandBean)) {
            if (object instanceof TableComponentRow) {
                TableComponentRow tableRow = (TableComponentRow) object;
                tableRow.setFields(columns);
                data.add(tableRow);
            } else if (object instanceof Map) {
                TableComponentRow tableRow = TableComponentRow.newInstance(object);
                tableRow.setFields(columns);
                data.add(tableRow);
            } else {
                TableComponentRow tableRow = TableComponentRow.newInstance(object);
                tableRow.setFields(columns);
                data.add(tableRow);
            }
        }
        if (this.primary != null) {
            result.put("primary", this.primary);
        }
        result.put("total", data.size());
        result.put("rows", data);

        return result;
    }

    /**
     * 设置附加的主对象引用。
     *
     * <p>该对象会被直接放入响应数据的 {@code "primary"} 字段中，前端可以使用它来渲染
     * 主从表关联信息或页面上下文。通常在主从表场景下，primary 是主表实体，rows 是从表数据。</p>
     *
     * @param object 主对象
     */
    public void setPrimaryObject(Object object) {
        this.primary = object;
    }

    /**
     * 获取表格行数据列表（由子类实现）。
     *
     * <p>子类应根据 {@link XCommandBean} 中的请求参数（如分页、筛选、主对象OID等）
     * 返回对应的行数据。每行可以是：
     * <ul>
     *   <li>JPA实体对象 —— 其属性通过反射映射到表格列</li>
     *   <li>{@code Map<String, Object>} —— 手动构建的键值对</li>
     *   <li>{@link TableComponentRow} —— 已包装好的行对象</li>
     * </ul></p>
     *
     * @param commandBean 请求命令对象
     * @return 行数据列表
     */
    public abstract List<? extends Object> getTableRows(XCommandBean commandBean);
}
