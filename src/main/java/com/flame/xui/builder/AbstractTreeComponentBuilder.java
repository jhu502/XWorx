package com.flame.xui.builder;

import com.flame.xui.XCommandBean;
import com.flame.util.FlameUtils;
import com.flame.xui.XUIRowId;
import com.flame.xui.XUIComponent;
import com.flame.xui.service.TreeComponentNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象树形组件构建器 —— 用于构建树形网格（TreeGrid）的层级节点数据。
 *
 * <h3>核心概念：懒加载与展开请求</h3>
 * <p>树形组件通常数据量较大，采用懒加载策略：首次加载只获取根节点，用户点击展开时再加载子节点。
 * 通过请求参数 {@code xx_flame_expand_yy} 来区分这两种请求类型：
 * <ul>
 *   <li><b>非展开请求</b>（首次加载）：调用 {@link #getRootNode(XCommandBean)} 获取根节点列表</li>
 *   <li><b>展开请求</b>（用户点击展开）：调用 {@link #getNode(XCommandBean)} 获取指定节点的子节点列表</li>
 * </ul>
 * 展开请求的判断逻辑：请求参数中包含 {@code xx_flame_expand_yy=true} 时视为展开请求。</p>
 *
 * <h3>数据构建流程 {@link #buildComponentData(XCommandBean)}</h3>
 * <ol>
 *   <li>调用 {@code buildComponentConfig} 获取列定义</li>
 *   <li>根据 {@code xx_flame_expand_yy} 参数判断请求类型</li>
 *   <li>展开请求：调用 {@link #getNode(XCommandBean)} → 返回子节点列表（数组格式）</li>
 *   <li>非展开请求：调用 {@link #getRootNode(XCommandBean)} → 返回根节点及包络格式 {@code {"total": N, "rows": [...]}}</li>
 *   <li>每个节点被包装为 {@link TreeComponentNode}（若已不是则通过 {@code newTreeComponentNode} 转换）</li>
 * </ol>
 *
 * <h3>响应格式差异</h3>
 * <p>展开请求和非展开请求的响应格式不同：
 * <ul>
 *   <li>展开请求（getNode）：直接返回节点数组 {@code [{...}, {...}]}<br>
 *       —— 前端EasyUI TreeGrid的懒加载期望此格式</li>
 *   <li>非展开请求（getRootNode）：返回 {@code {"total": N, "rows": [{...}, {...}]}}<br>
 *       —— 前端EasyUI TreeGrid的首次加载期望此格式</li>
 * </ul></p>
 *
 * <h3>子类需实现</h3>
 * <ul>
 *   <li>{@link #getRootNode(XCommandBean)} —— 返回根节点列表（首次加载时调用）</li>
 *   <li>{@link #getNode(XCommandBean)} —— 返回指定节点的子节点列表（展开时调用）</li>
 * </ul>
 *
 * @author Flame
 * @see AbstractComponentBuilder
 * @see TreeComponentNode
 */
public abstract class AbstractTreeComponentBuilder extends AbstractComponentBuilder {
    /** 展开请求标识参数名 —— 前端在用户点击树节点展开时自动发送此参数 */
    private static final String FLAME_EXPAND_REQUEST = "xx_flame_expand_yy";

    /**
     * 构建树组件的数据内容。
     *
     * <p>根据是否为展开请求，分别调用 {@link #getRootNode} 或 {@link #getNode}。
     * 所有节点（无论是根节点还是子节点）都被统一包装为 {@link TreeComponentNode} 格式。</p>
     *
     * @param commandBean 请求命令对象，包含 {@code xx_flame_expand_yy} 等参数
     * @return 展开请求时返回节点List，否则返回 {"total": N, "rows": [...]} 的Map
     */
    @Override
    public Object buildComponentData(XCommandBean commandBean) {
        XUIComponent elementConfig = this.buildComponentConfig(commandBean);
        List<String> columns = elementConfig.fields();

        Object expandReq = commandBean.getParameter(FLAME_EXPAND_REQUEST);
        /**
         * 根据请求参数是否包含xx_flame_expand_yy，来识别当前请求是否是Expand（展开）请求；
         * 展开请求时前端需要的是子节点数组，非展开请求时需要的是标准分页格式。
         */
        if (Boolean.TRUE.equals(expandReq) || Boolean.TRUE.toString().equals(expandReq)) {
            List<Object> nodes = new ArrayList<>();
            for (Object object : getNode(commandBean)) {
                if (object instanceof TreeComponentNode) {
                    TreeComponentNode node = (TreeComponentNode) object;
                    node.setFields(columns);
                    nodes.add(node);
                } else if (object instanceof Map) {
                    TreeComponentNode node = TreeComponentNode.newTreeComponentNode(object);
                    node.setFields(columns);
                    nodes.add(node);
                } else {
                    TreeComponentNode node = TreeComponentNode.newTreeComponentNode(object);
                    node.setFields(columns);
                    nodes.add(node);
                }
            }
            return nodes;
        } else {
            Map<String, Object> result = new HashMap<>();
            List<Object> nodes = new ArrayList<>();
            for (Object object : getRootNode(commandBean)) {
                if (object instanceof TreeComponentNode) {
                    TreeComponentNode node = (TreeComponentNode) object;
                    node.setFields(columns);
                    nodes.add(node);
                } else if (object instanceof Map) {
                    TreeComponentNode node = TreeComponentNode.newTreeComponentNode(object);
                    node.setFields(columns);
                    nodes.add(node);
                } else {
                    TreeComponentNode node = TreeComponentNode.newTreeComponentNode(object);
                    node.setFields(columns);
                    nodes.add(node);
                }
            }
            result.put("total", nodes.size());
            result.put("rows", nodes);
            return result;
        }
    }
    
    public String assemRowId(String... objectIds) {
        return assemRowId(null, objectIds);
    }
    
    /**
     * 组装树节点的行ID（rowId）。
     * <p>
     * 规则：若 commandBean 的 rowId 不为空，则以 {@code ^} 作为层级分隔符将其作为前缀；
     * 多个 objectId 之间以 {@code _} 作为对象分隔符拼接。
     * 例如：父节点 rowId 为 "A"，objectIds 为 ["B", "C"]，则结果为 "A^B_C"。
     *
     * @param parentRowId 命令上下文（可为 null，若为 null 或其 rowId 为空则不添加前缀）
     * @param objectIds   树节点对应的对象ID序列（可为 null，为空则不拼接对象ID部分）
     * @return 组装后的行ID字符串
     */
    public String assemRowId(String parentRowId, String[] objectIds) {
        StringBuilder builder = new StringBuilder();
        if (FlameUtils.isNotBlank(parentRowId)) {
            builder.append(parentRowId).append(XUIRowId.LEVEL_SEP);
        }

        if (objectIds != null) {
            boolean bool = true;
            for (String objectId : objectIds) {
                if (bool) {
                    builder.append(objectId);
                    bool = false;
                } else {
                    builder.append(XUIRowId.OBJECT_SEP).append(objectId);
                }
            }
        }

        return builder.toString();
    }

    /**
     * 获取树的根节点列表（由子类实现）。
     *
     * <p>在树组件首次加载时调用。子类应根据 {@link XCommandBean#getPrimaryObj()} 获取主对象上下文，
     * 返回该上下文下的顶层节点。每个节点可以是实体对象、Map 或 {@link TreeComponentNode}。</p>
     *
     * @param commandBean 请求命令对象，可通过 {@code getPrimaryObj()} 获取当前页面容器/主对象
     * @return 根节点列表
     */
    public abstract List<? extends Object> getRootNode(XCommandBean commandBean);

    /**
     * 获取指定节点的子节点列表（由子类实现）。
     *
     * <p>在用户点击展开树节点时调用。子类应根据 {@link XCommandBean#getRowId()} 获取被展开节点的OID，
     * 返回该节点的直接子节点列表。</p>
     *
     * @param commandBean 请求命令对象，可通过 {@code getRowId()} 获取被展开节点的OID
     * @return 子节点列表
     */
    public abstract List<? extends Object> getNode(XCommandBean commandBean);
}
