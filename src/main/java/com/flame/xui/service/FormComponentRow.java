package com.flame.xui.service;

import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractMeshComponentBuilder;

/**
 * 表单组件行 —— 用于承载表单（Mesh）组件的单行数据。
 *
 * <p>继承自 {@link RowComponent}，本身不添加额外字段，仅提供静态工厂方法以统一创建方式。
 * 在 {@link AbstractMeshComponentBuilder} 的 {@code buildComponentData} 方法中，
 * 每行表单数据被包装为 {@code FormComponentRow} 后返回给前端。</p>
 *
 * <p>与 {@link TableComponentRow} 的区别在于：前者用于可编辑表单，不自动从持久化对象提取rowId；
 * 后者用于数据表格，自动将 {@code XPersistable} 的 OID 设为 rowId。</p>
 *
 * @author Flame
 * @see RowComponent
 * @see TableComponentRow
 * @see AbstractMeshComponentBuilder
 */
public class FormComponentRow extends RowComponent {
    /**
     * 创建一个空的表单行（包装一个空Object）。
     *
     * @return 空表单行实例
     */
    public static FormComponentRow newFormComponentRow() {
        return FormComponentRow.newFormComponentRow(new Object());
    }

    /**
     * 以指定对象为数据源创建表单行。
     *
     * <p>与 {@link TableComponentRow#newInstance(Object)} 不同，此方法不会自动从持久化对象提取rowId，
     * 因为表单组件通常通过 {@link XCommandBean#getPrimaryObj()} 来定位主对象，
     * 表单行本身不需要独立的rowId标识。</p>
     *
     * @param object 表单行绑定的数据对象，可以是实体、Map或任意POJO
     * @return 表单行实例，其属性通过Jackson序列化时会被展平到行数据中
     */
    public static FormComponentRow newFormComponentRow(Object object) {
        FormComponentRow row = new FormComponentRow();
        row.setObject(object);
        return row;
    }
}
