package com.flame.xui.service;

import com.flame.xui.builder.AbstractTreeComponentBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 树组件节点 —— 用于承载树形网格（TreeGrid）组件的单个节点数据。
 *
 * <h3>核心设计</h3>
 * <p>继承自 {@link RowComponent}，额外维护：
 * <ul>
 *   <li>{@code state} —— 树节点展开/关闭状态（"open"/"closed"），用于前端EasyUI TreeGrid的懒加载</li>
 *   <li>{@code children} —— 子节点列表，支持嵌套树结构</li>
 * </ul></p>
 *
 * <h3>State 命名约定</h3>
 * <p>使用特殊方法名 {@code getState$_()} 而非 {@code getState()} 来避免与业务对象的 {@code state} 属性冲突。
 * 对应地，前端 easyui 的 jquery.easyui.min.js 中做了以下替换以适配此命名：
 * <ul>
 *   <li>{@code .state=="closed"} → {@code .state$_=="closed"}</li>
 *   <li>{@code .state=="open"} → {@code .state$_=="open"}</li>
 *   <li>{@code .state!="closed"} → {@code .state$_!="closed"}</li>
 *   <li>{@code .state!="open"} → {@code .state$_!="open"}</li>
 * </ul>
 * 这样既保留了EasyUI对树状态的内置处理，又避免了与业务模型中名为"state"的字段冲突。</p>
 *
 * <h3>叶子节点</h3>
 * <p>调用 {@link #setLeaf(boolean)} 传入 {@code true} 会将 {@code state} 设为 {@code null}，
 * 前端识别到 state 为 null 时不显示展开/折叠图标，将该节点渲染为叶子节点。</p>
 *
 * <h3>字段级联</h3>
 * <p>重写了 {@link #setFields(List)}，在设置当前节点字段的同时递归设置所有子节点的字段，
 * 确保整个子树的序列化字段一致。</p>
 *
 * @author Flame
 * @see RowComponent
 * @see AbstractTreeComponentBuilder
 */
public class TreeComponentNode extends RowComponent {
    /** 前端EasyUI TreeGrid用于识别树状态的特殊字段名（带$_后缀以避免与业务字段冲突） */
    public static final String STATE = "state$_";
    /** 前端EasyUI TreeGrid用于识别子节点列表的字段名 */
    public static final String CHILDREN = "children";
    /** 树节点展开状态："closed"（折叠）、"open"（展开）、null（叶子节点） */
    private String state = "closed";
    /** 行唯一标识符，默认为空字符串，通常对应持久化对象的OID */
    /** 子节点列表 */
    private List<Object> children = new ArrayList<>();

    /**
     * 创建一个空的树节点（包装一个空Object）。
     *
     * @return 空树节点实例，state默认为"closed"
     */
    public static TreeComponentNode newTreeComponentNode() {
        return TreeComponentNode.newTreeComponentNode(new Object());
    }

    /**
     * 以指定对象为数据源创建树节点。
     *
     * <p>节点初始state为"closed"（折叠状态），当通过 {@link #addChildren} 添加子节点时
     * state自动切换为"open"。</p>
     *
     * @param object 树节点绑定的业务数据对象，可以是实体、Map或任意POJO
     * @return 树节点实例
     */
    public static TreeComponentNode newTreeComponentNode(Object object) {
        TreeComponentNode node = new TreeComponentNode();
        node.setObject(object);
        return node;
    }

    public static TreeComponentNode newTreeComponentNode(Object object, String rowId) {
        TreeComponentNode node = new TreeComponentNode();
        node.setObject(object);
        node.setRowId(rowId);
        return node;
    }

    /**
     * 获取树节点状态（用于前端EasyUI TreeGrid的懒加载判断）。
     *
     * <p>方法名使用 {@code state$_} 而非 {@code state}，目的是避免与业务对象的 state 属性冲突。
     * Jackson序列化时会输出为 {@code "state$_": "closed"} 格式的JSON字段。
     * 前端 easyui 的 jquery.easyui.min.js 中已做对应替换以识别此字段名。</p>
     *
     * @return 树节点状态："closed"（折叠，有子节点未加载）、"open"（已展开）、null（叶子节点）
     */
    public String getState() {
        return state;
    }

    /**
     * 设置树节点展开状态。
     *
     * @param state "closed"（折叠）、"open"（展开）
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 设置节点是否为叶子节点。
     *
     * <p>当设为 {@code true} 时，state 被置为 {@code null}，前端不显示展开/折叠图标，
     * 且不再触发懒加载请求。</p>
     *
     * @param bool true 表示叶子节点，false 表示非叶子节点（需手动设回state）
     */
    public void setLeaf(boolean bool) {
        if (bool) {
            this.setState(null);
        }
    }

    /**
     * 获取子节点列表。
     *
     * @return 子节点列表（可能为空列表）
     */
    public List<Object> getChildren() {
        return children;
    }

    /**
     * 添加一个子节点并自动将当前节点状态设为"open"。
     *
     * @param children 要添加的子节点
     */
    public void addChildren(TreeComponentNode children) {
        this.children.add(children);
        this.setState("open");
    }

    /**
     * 从Map创建一个子节点并添加，同时将当前节点状态自动设为"open"。
     *
     * @param children 包含子节点属性的Map
     */
    public void addChildren(Map<String, Object> children) {
        this.children.add(children);
        this.setState("open");
    }

    /**
     * 设置需要序列化的字段列表，并递归设置所有子节点。
     *
     * <p>重写父类方法以支持树结构的字段级联：当父节点设置字段列表时，
     * 所有子节点也会同步设置相同的字段列表，确保整个子树序列化输出一致。</p>
     *
     * @param fields 字段名列表；若为null则跳过
     */
    @Override
    public void setFields(List<String> fields) {
        if (fields == null)
            return;

        super.setFields(fields);
        for (Object object : this.getChildren()) {
            if (object instanceof TreeComponentNode) {
                TreeComponentNode node = (TreeComponentNode) object;
                node.setFields(fields);
            }
        }
    }
}
