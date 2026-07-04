package com.flame.xui.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIComponent;
import com.flame.xui.service.PropertyComponent;
import com.flame.xui.service.TableComponentRow;

/**
 * 抽象属性表格构建器 —— 用于构建属性网格（PropertyGrid）的行数据。
 *
 * <h3>与表格构建器的关系</h3>
 * <p>继承自 {@link AbstractComponentBuilder}，与 {@link AbstractTableComponentBuilder} 非常相似——
 * 都是构建扁平的行数据列表，响应格式也相同（{@code {"total": N, "rows": [...]}}）。
 * 差异在于：
 * <ul>
 *   <li>属性网格通常展示单个对象的属性列表（名称—值对），而非多条记录</li>
 *   <li>子类实现的抽象方法是 {@link #getPropertyRows} 而非 {@code getTableRows}</li>
 *   <li>前端渲染为属性编辑表单，而非数据表格</li>
 * </ul></p>
 *
 * <h3>数据构建流程 {@link #buildComponentData(XCommandBean)}</h3>
 * <ol>
 *   <li>调用 {@code buildComponentConfig} 获取列定义</li>
 *   <li>调用子类实现的 {@link #getPropertyRows(XCommandBean)} 获取属性行数据</li>
 *   <li>将每行数据包装为 {@link TableComponentRow}</li>
 *   <li>返回标准格式：{@code {"total": N, "rows": [...]}}</li>
 * </ol>
 *
 * <h3>子类需实现</h3>
 * <p>{@link #getPropertyRows(XCommandBean)} —— 返回属性行列表，每行对应对象的一个属性。</p>
 *
 * @author Flame
 * @see AbstractComponentBuilder
 * @see AbstractTableComponentBuilder
 * @see PropertyComponent
 */
public abstract class AbstractPropertyComponentBuilder extends AbstractComponentBuilder {
    /**
     * 构建属性网格的数据内容。
     *
     * <p>将子类提供的属性行数据转换为 {@link TableComponentRow} 列表并返回标准分页格式。</p>
     *
     * @param commandBean 请求命令对象
     * @return 包含 "total" 和 "rows" 的Map
     */
    public Object buildComponentData(XCommandBean commandBean) {
        XUIComponent elementConfig = this.buildComponentConfig(commandBean);
        List<String> columns = elementConfig.fields();

        Map<String, Object> result = new HashMap<String, Object>();

        List<Object> data = new ArrayList<>();
        for (Object object : this.getPropertyRows(commandBean)) {
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
        result.put("total", data.size());
        result.put("rows", data);

        return result;
    }

    /**
     * 获取属性行数据列表（由子类实现）。
     *
     * <p>子类应根据主对象使用 {@link PropertyComponent#newPropertyComponent} 等方法
     * 创建属性行，每行代表对象的一个属性及其值。</p>
     *
     * @param commandBean 请求命令对象
     * @return 属性行列表，每行通常为 {@link PropertyComponent} 或 {@link TableComponentRow} 实例
     */
    public abstract List<?> getPropertyRows(XCommandBean commandBean);
}
