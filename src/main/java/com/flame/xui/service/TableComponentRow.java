package com.flame.xui.service;

import com.flame.orm.XPersistable;
import com.flame.xui.builder.AbstractPropertyComponentBuilder;
import com.flame.xui.builder.AbstractTableComponentBuilder;

/**
 * 表格组件行 —— 用于承载表格（Table/DataGrid/PropertyGrid）组件的单行数据。
 *
 * <p>继承自 {@link RowComponent}，额外维护一个 {@code rowId} 字段，用于标识行的唯一键。
 * 前端表格组件使用 rowId 来完成行选中、行编辑、行删除等操作。</p>
 *
 * <p>创建行时会自动检测数据对象类型：
 * <ul>
 *   <li>若对象实现了 {@link XPersistable}，则自动提取其 {@code oid} 作为 rowId</li>
 *   <li>否则 rowId 为空字符串，调用方需手动设置</li>
 * </ul></p>
 *
 * @author Flame
 * @see RowComponent
 * @see AbstractTableComponentBuilder
 * @see AbstractPropertyComponentBuilder
 */
public class TableComponentRow extends RowComponent {
    /**
     * 创建一个空的表格行（包装一个空Object，rowId为空字符串）。
     *
     * @return 空表格行实例
     */
    public static TableComponentRow newInstance() {
        return TableComponentRow.newInstance(new Object());
    }

    /**
     * 以指定对象为数据源创建表格行。
     *
     * <p>若对象为 {@link XPersistable} 实例，会自动将其 {@code oid} 设置为 rowId，
     * 确保前端可以通过 rowId 精确定位到对应的数据库记录。</p>
     *
     * @param object 表格行绑定的数据对象，可以是实体、Map或任意POJO
     * @return 表格行实例，rowId已根据对象类型自动设置
     */
    public static TableComponentRow newInstance(Object object) {
        TableComponentRow row = new TableComponentRow();
        row.setObject(object);
        if (object instanceof XPersistable) {
            XPersistable persist = (XPersistable) object;
            row.setRowId(persist.getOid());
        }
        return row;
    }
    
    public static TableComponentRow newInstance(Object object, String rowId) {
        TableComponentRow resultRow = newInstance(object);
        resultRow.setRowId(rowId);
        
        return resultRow;
    }
}
