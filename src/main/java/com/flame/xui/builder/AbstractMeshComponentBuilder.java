package com.flame.xui.builder;

import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.util.FlameUtils;
import com.flame.xui.IWidget;
import com.flame.xui.XUIComponent;
import com.flame.xui.service.FormComponentRow;
import com.flame.xui.XUIMeshGrid;

/**
 * 抽象网格表单构建器 —— 用于构建网格表单（MeshGrid）的布局配置和表单数据。
 *
 * <h3>核心概念</h3>
 * <p>网格表单（MeshGrid）是一种以网格布局编排的表单组件，由多个层级的结构组成：
 * <ul>
 *   <li>{@link XUIMeshGrid} —— 顶层容器，包含多个 {@code XGrid}（网格）</li>
 *   <li>{@link XUIMeshGrid.XUIGrid} —— 单个网格区域，包含多个 {@code XRow}（行）</li>
 *   <li>{@link XUIMeshGrid.XUIRow} —— 网格中的一行，包含多个 {@code XCell}（单元格）</li>
 *   <li>{@link XUIMeshGrid.XUICell} —— 单元格，包含多个 {@link IWidget}（UI控件，如输入框、下拉框等）</li>
 * </ul>
 * 这种层级结构允许开发者以声明式方式定义复杂表单布局。</p>
 *
 * <h3>类型注入 {@link #buildComponentConfig(XCommandBean)}</h3>
 * <p>在构建配置时，如果请求参数中包含 {@code class} 参数（目标实体类的全限定名），
 * 会对所有网格中的所有控件调用 {@link IWidget#inflateType(Class)} 进行类型注入。
 * 这使得控件可以根据目标实体的字段类型自动适配（例如：枚举字段自动渲染为下拉框、
 * 日期字段自动渲染为日期选择器等）。</p>
 *
 * <h3>表单数据构建 {@link #buildComponentData(XCommandBean)}</h3>
 * <p>数据构建相对简单：调用 {@link #getFormRow(XCommandBean)} 获取表单行数据，
 * 包装为 {@link FormComponentRow} 后返回。默认实现直接返回 {@code commandBean.getPrimaryObj()}
 * 作为表单数据源，子类可重写以提供自定义数据。</p>
 *
 * <h3>子类可重写</h3>
 * <p>{@link #getFormRow(XCommandBean)} —— 默认返回主对象；子类可重写以提供编辑/新建时的初始数据。</p>
 *
 * @author Flame
 * @see AbstractComponentBuilder
 * @see FormComponentRow
 * @see XUIMeshGrid
 */
public abstract class AbstractMeshComponentBuilder extends AbstractComponentBuilder {
    /** 请求参数key：目标实体类的全限定名，用于控件类型注入 */
    protected static final String CLASS = "class";
    /** 操作类型常量：创建 */
    protected static final String CREATE = "Create";
    /** 操作类型常量：编辑 */
    protected static final String EDIT = "Edit";
    /** 操作类型常量：查看/值 */
    protected static final String VALUE = "Value";
    /** 控件状态：可编辑 */
    protected static final String EDITABLE = "editable";
    /** 控件状态：禁用 */
    protected static final String DISABLED = "disabled";

    /**
     * 构建网格表单的组件配置，并对控件进行类型注入。
     *
     * <p>在父类 {@link AbstractComponentBuilder#buildComponentConfig} 返回的配置基础上，
     * 额外处理：
     * <ol>
     *   <li>从请求参数中提取目标实体类名（{@code class} 参数）</li>
     *   <li>遍历所有网格 → 行 → 单元格 → 控件的层级结构</li>
     *   <li>对每个控件调用 {@link IWidget#inflateType(Class)} 进行类型适配</li>
     * </ol>
     * 类型注入使得控件能根据实体字段的类型自动调整渲染方式和校验规则。</p>
     *
     * @param commandBean 请求命令对象，其 {@code class} 参数指定目标实体类型
     * @return 配置完成的 XMeshGrid 实例，所有控件已完成类型注入
     */
    @Override
    public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
        XUIMeshGrid meshGrid = (XUIMeshGrid) super.buildComponentConfig(commandBean);

        String primaryType = commandBean.getTextParameter(CLASS);
        if (FlameUtils.isNotBlank(primaryType)) {
            try {
                Class<?> primaryClass = Class.forName(primaryType);
                meshGrid.setType(primaryClass);
            } catch (ClassNotFoundException e) {
                logger.error("Form queryParams:'class' %s", e.getMessage());
            }
        }

        // 遍历所有网格 → 行 → 单元格 → 控件，进行类型注入
        for (XUIMeshGrid.XUIGrid xGrid : meshGrid.getGrids()) {
            if (xGrid == null)
                continue;
            
            for (XUIMeshGrid.XUIRow xRow : xGrid.getRows()) {
                if (xRow == null)
                    continue;
                for (XUIMeshGrid.XUICell xCell : xRow.getCells()) {
                    if (xCell == null)
                        continue;
                    for (IWidget widget : xCell.getWidgets()) {
                        if (widget == null)
                            continue;
                        if (xGrid.getProvider() != null) {
                            // 通知控件其绑定的目标类型，控件可据此调整渲染方式
                            widget.inflate(xGrid.getProvider());
                        }
                    }
                }
            }
        }

        return meshGrid;
    }

    /**
     * 构建网格表单的数据内容。
     *
     * <p>获取表单行数据并包装为 {@link FormComponentRow}。与表格构建器的差异在于：
     * 表单组件通常只返回单行数据（当前编辑的对象），而非多行列表。</p>
     *
     * @param commandBean 请求命令对象
     * @return 包装后的 FormComponentRow 实例
     */
    @Override
    public Object buildComponentData(XCommandBean commandBean) {
        XUIComponent elementConfig = this.buildComponentConfig(commandBean);
        List<String> fields = elementConfig.fields();

        Object object = this.getFormRow(commandBean);
        if (object instanceof FormComponentRow) {
            FormComponentRow formRow = (FormComponentRow) object;
            formRow.setFields(fields);
            return formRow;
        } else {
            FormComponentRow formRow = FormComponentRow.newFormComponentRow(object);
            formRow.setFields(fields);
            return formRow;
        }
    }

    /**
     * 获取表单行数据（默认返回主对象，子类可重写）。
     *
     * <p>默认实现返回 {@code commandBean.getPrimaryObj()} —— 即页面上下文中通过 primaryOid
     * 解析出的持久化对象。子类可重写以提供编辑/新建场景下的特殊数据：
     * <ul>
     *   <li>编辑场景：返回从数据库加载的已有实体</li>
     *   <li>新建场景：返回带有默认值的新实体实例</li>
     *   <li>查看场景：返回只读数据对象</li>
     * </ul></p>
     *
     * @param commandBean 请求命令对象
     * @return 表单行绑定的数据对象
     */
    public Object getFormRow(XCommandBean commandBean) {
        return commandBean.getPrimaryObj();
    }
}
