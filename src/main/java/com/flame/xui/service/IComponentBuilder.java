package com.flame.xui.service;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIComponent;

/**
 * UI组件构建器接口 —— 定义组件配置与数据构建的契约。
 *
 * <p>每个实现了此接口的构建器负责将后端的业务数据转换为前端可消费的组件配置（如列定义、操作按钮等）
 * 以及组件数据（如表行、树节点等）。构建器通过 {@link XCommandBean} 获取请求上下文，包括主对象、
 * 请求参数、行ID等信息。</p>
 *
 * <p>典型调用流程：
 * <ol>
 *   <li>前端请求组件配置 → 调用 {@link #buildComponentConfig(XCommandBean)} 获取字段定义、工具栏等元数据</li>
 *   <li>前端请求组件数据 → 调用 {@link #buildComponentData(XCommandBean)} 获取实际数据行/节点</li>
 * </ol>
 * 两个方法共享同一个 {@link XCommandBean} 上下文，确保配置与数据的参数一致性。</p>
 *
 * @author Flame
 */
public interface IComponentBuilder {
    /**
     * 构建UI组件的元数据配置（列定义、操作按钮、分页信息等）。
     *
     * <p>该方法根据构建器类上的注解（如 {@code @UIDataGrid}、{@code @UITreeGrid}）反射生成
     * 对应的 {@link XUIComponent} 配置对象。前端在首次渲染组件时调用此方法获取结构定义。</p>
     *
     * @param commandBean 请求命令对象，携带主对象OID、请求参数、compId等上下文信息
     * @return 组件配置对象，包含字段列表(columns)、工具栏(actions)、URL等元数据；若无法构建则返回null
     */
    XUIComponent buildComponentConfig(XCommandBean commandBean);

    /**
     * 构建UI组件的实际数据内容（表格行、树节点、表单数据等）。
     *
     * <p>该方法在组件配置的基础上，调用子类实现的具体数据获取逻辑（如 {@code getTableRows}、
     * {@code getRootNode} 等抽象方法），将业务实体转换为前端可渲染的 {@link RowComponent} 列表。</p>
     *
     * @param commandBean 请求命令对象，携带主对象、分页参数、展开请求等上下文
     * @return 组件数据，通常为包含 "total" 和 "rows" 键的 Map，或树节点的 List
     */
    Object buildComponentData(XCommandBean commandBean);
}
