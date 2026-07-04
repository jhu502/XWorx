package com.flame.xui.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flame.xui.service.IComponentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flame.action.XActionHelper;
import com.flame.action.IAction;
import com.flame.action.IActionItem;
import com.flame.xui.HREFactory;
import com.flame.xui.XCommandBean;
import com.flame.localize.LocalizationHelper;
import com.flame.orm.XObject;
import com.flame.orm.XPersistable;
import com.flame.util.FlameUtils;
import com.flame.util.XException;
import com.flame.xui.GridComponent;
import com.flame.xui.IComponent;
import com.flame.xui.WidgetType;
import com.flame.xui.XUIAction;
import com.flame.xui.XUIComponent;
import com.flame.annotations.UIDataGrid;
import com.flame.annotations.UIDefinition;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.XUIDataGrid;
import com.flame.xui.XUIMeshGrid;
import com.flame.xui.XUITreeGrid;

/**
 * 抽象组件构建器 —— 所有UI组件构建器的基类。
 *
 * <h3>职责</h3>
 * <p>本类是 Flame UI 框架的核心，负责将注解驱动的组件定义转换为前端可消费的配置和数据。
 * 子类只需声明对应的UI注解（如 {@code @UIDataGrid}、{@code @UITreeGrid}）并实现数据获取的抽象方法，
 * 即可完成从后端实体到前端组件的映射。</p>
 *
 * <h3>组件配置构建流程 {@link #buildComponentConfig(XCommandBean)}</h3>
 * <ol>
 *   <li>通过反射获取子类上的UI注解（如 {@code @UIDataGrid}、{@code @UITreeGrid}）</li>
 *   <li>从注解的元注解 {@link UIDefinition} 中提取 {@link WidgetType}（组件类型）</li>
 *   <li>根据组件类型实例化对应的配置对象：
 *     <ul>
 *       <li>{@code DataGrid} → {@link XUIDataGrid} —— 数据表格，支持分组字段、工具栏</li>
 *       <li>{@code TreeGrid} → {@link XUITreeGrid} —— 树形表格，支持懒加载展开</li>
 *       <li>{@code MeshGrid} → {@link XUIMeshGrid} —— 网格表单，支持多网格布局</li>
 *     </ul>
 *   </li>
 *   <li>为 DataGrid/TreeGrid 调用 {@link #generateToolBar(GridComponent, XPersistable)} 生成工具栏按钮</li>
 *   <li>设置组件的URL地址（前端通过此URL请求 {@code buildComponentData}）和查询参数</li>
 * </ol>
 *
 * <h3>工具栏生成 {@link #generateToolBar(GridComponent, XPersistable)}</h3>
 * <p>工具栏按钮有两个来源：
 * <ol>
 *   <li>从 {@code XActionModel} 菜单配置库中获取（通过 {@code actionModel} 属性指定菜单标识）</li>
 *   <li>从构建器注解的 {@code actions} 属性中获取（通过 {@code @UIAction} 声明）</li>
 * </ol>
 * 两个来源的按钮最终合并为一个工具栏列表。actionKey 的格式区分了两种来源：
 * <ul>
 *   <li>菜单配置来源：{@code "{type}:{name}"}</li>
 *   <li>注解来源：{@code "builder${fullClassName}:{name}"}</li>
 * </ul></p>
 *
 * <h3>子类扩展点</h3>
 * <p>子类通常不重写 {@code buildComponentConfig}，而是通过以下方式扩展：
 * <ul>
 *   <li>声明对应的UI注解（{@code @UIDataGrid}、{@code @UITreeGrid}）指定列定义和操作按钮</li>
 *   <li>实现具体的数据获取方法（如 {@code getTableRows}、{@code getRootNode}）</li>
 * </ul></p>
 *
 * @author Flame
 * @see AbstractTableComponentBuilder
 * @see AbstractTreeComponentBuilder
 * @see AbstractMeshComponentBuilder
 * @see AbstractPropertyComponentBuilder
 */
public abstract class AbstractComponentBuilder implements IComponentBuilder {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractComponentBuilder.class);
    public static final String UI_DATA = "XUI$/data/";

    /**
     * 构建UI组件的元数据配置。
     *
     * <p>通过反射读取子类上的UI注解，根据注解中声明的组件类型实例化对应的配置对象。
     * 配置对象包含：列定义、操作按钮、URL、查询参数等，前端在首次渲染时使用这些信息初始化组件。</p>
     *
     * <h4>DataGrid 特殊处理</h4>
     * <p>如果配置中设置了 {@code groupField}（分组字段），组件类型会被自动切换为 {@code PropertyGrid}，
     * 从而在前端以分组属性表的形式展示数据。</p>
     *
     * @param commandBean 请求命令对象，携带主对象OID、请求参数等信息
     * @return 组件配置对象（XDataGrid/XTreeGrid/XMeshGrid）；若无法识别注解则返回null
     * @throws XException 反射实例化失败时抛出
     */
    public XUIComponent buildComponentConfig(XCommandBean commandBean) {
        Annotation annotation = getUIAnnotation(this.getClass());
        if (annotation == null)
            return null;

        UIDefinition uiDefinition = annotation.annotationType().getAnnotation(UIDefinition.class);
        WidgetType compType = uiDefinition.component();
        Class<? extends IComponent> configClass = compType.getWidget();
        if (configClass == null)
            return null;

        String gridId = commandBean.getGridId();
        if (XUIDataGrid.class.equals(configClass)) {
            try {
                XUIDataGrid dataGrid = new XUIDataGrid((UIDataGrid) annotation, this.getClass().getName());
                dataGrid.setId(commandBean.getGridId());
                XObject primary = commandBean.getPrimaryObj();
                this.generateToolBar(dataGrid, primary);
                if (FlameUtils.isBlank(dataGrid.getGroupField())) {
                    dataGrid.setComponent(compType.getName());
                } else {
                    // 设置了groupField时，自动切换为PropertyGrid（属性分组表格）
                    dataGrid.setComponent(WidgetType.PropertyGrid.getName());
                }
                dataGrid.setUrl(HREFactory.getHREF(UI_DATA + this.getClass().getName()));
                dataGrid.setQueryParams(commandBean.getParameterMap());
                dataGrid.setCompId(this.generateCompId(gridId));
                return dataGrid;
            } catch (Exception e) {
                throw new XException(e);
            }
        } else if (XUITreeGrid.class.equals(configClass)) {
            try {
                XUITreeGrid treeGrid = new XUITreeGrid((UITreeGrid) annotation, this.getClass().getName());
                treeGrid.setId(commandBean.getGridId());
                XObject primary = commandBean.getPrimaryObj();
                this.generateToolBar(treeGrid, primary);
                treeGrid.setComponent(compType.getName());
                treeGrid.setUrl(HREFactory.getHREF(UI_DATA + this.getClass().getName()));
                treeGrid.setQueryParams(commandBean.getParameterMap());
                treeGrid.setCompId(this.generateCompId(gridId));
                return treeGrid;
            } catch (Exception e) {
                throw new XException(e);
            }
        } else if (XUIMeshGrid.class.isAssignableFrom(configClass)) {
            try {
                Constructor<? extends IComponent> constructor = configClass.getConstructor(annotation.annotationType(), String.class);
                XUIMeshGrid compConfig = (XUIMeshGrid) constructor.newInstance(annotation, this.getClass().getName());
                compConfig.setId(commandBean.getGridId());
                compConfig.setComponent(compType.getName());
                compConfig.setUrl(HREFactory.getHREF(UI_DATA + this.getClass().getName()));
                compConfig.setQueryParams(commandBean.getParameterMap());
                compConfig.setCompId(this.generateCompId(gridId));
                return compConfig;
            } catch (Exception e) {
                throw new XException(e);
            }
        } else {
            return null;
        }
    }

    /**
     * 为TreeGrid、DataGrid、PropertyGrid的工具栏生成按钮。
     *
     * <h4>按钮来源（两个来源合并）</h4>
     * <ol>
     *   <li><b>菜单配置库（XActionModel）</b> —— 通过 {@code actionModel} 属性指定菜单标识，
     *       从数据库中的菜单配置表动态获取操作按钮。actionKey格式：{@code "{type}:{name}"}</li>
     *   <li><b>注解声明（@UIAction）</b> —— 通过构建器注解的 {@code actions} 属性声明，
     *       编译期确定、无需数据库配置。actionKey格式：{@code "builder${fullClassName}:{name}"}</li>
     * </ol>
     * 两种来源的按钮会合并到同一个工具栏列表中。</p>
     *
     * <p>每个按钮包含以下属性：id(操作标识)、text(显示文本)、icon(图标URL)、iconCls(CSS图标类)、
     * style(样式)、actionKey(操作键，用于路由到具体处理器)、url(弹窗页面地址)、
     * processor(处理器类名)、beforeJS(前置JS校验)、afterJS(后置JS回调)、winType(窗口类型)。</p>
     *
     * @param gridConfig 网格组件配置对象，工具栏按钮将设置到其上
     * @param persist    持久化主对象，用于菜单配置中基于对象类型的权限过滤
     */
    private void generateToolBar(GridComponent gridConfig, XPersistable persist) {
        List<Map<String, Object>> toolbar = new ArrayList<>();
        /**
         * 从XActionModel菜单配置库中获取工具栏项。
         * 菜单配置支持基于 persist 对象的类型进行动态权限过滤。
         */
        if (!isBlank(gridConfig.getActionModel())) {
            List<IAction> itemList = XActionHelper.manager().getActions(gridConfig.getActionModel(), persist);
            for (Object object : itemList) {
                if (object instanceof IActionItem) {
                    IActionItem action = (IActionItem) object;
                    Map<String, Object> button = new HashMap<>();
                    button.put("id", action.getName());
                    button.put("text", action.getLocalDisplay());
                    button.put("icon", HREFactory.getHREF(action.getIcon()));
                    button.put("iconCls", action.getIconCls());
                    button.put("style", action.getStyle());
                    button.put("actionKey", action.getType() + ":" + action.getName());
                    button.put("url", action.getUrl());
                    button.put("processor", action.getProcessor());
                    button.put("beforeJS", action.getBeforeJS());
                    button.put("afterJS", action.getAfterJS());
                    button.put("winType", action.getWinType());
                    button.put("iconAlign", "top");
                    button.put("size", "larger");
                    toolbar.add(button);
                }
            }
        }

        /**
         * 从Builder的@UIAction注解中获取工具栏项。
         * 这些操作直接声明在构建器的UI注解中，无需数据库配置。
         */
        List<XUIAction> actionList = gridConfig.getActions();
        for (XUIAction action : actionList) {
            Map<String, Object> button = new HashMap<>();
            button.put("id", action.getName());
            if (FlameUtils.isBlank(action.getDisplay())) {
                // 未指定显示文本时，通过国际化获取
                button.put("text", LocalizationHelper.get(action.getName()));
            } else {
                button.put("text", action.getDisplay());
            }
            button.put("icon", HREFactory.getHREF(action.getIcon()));
            button.put("iconCls", action.getIconCls());
            button.put("style", action.getStyle());
            // actionKey以"builder$"前缀标识来源为注解声明
            button.put("actionKey", "builder$" + this.getClass().getName() + ":" + action.getName());
            button.put("url", action.getUrl());
            button.put("processor", action.getProcessor());
            button.put("beforeJS", action.getBeforeJS());
            button.put("afterJS", action.getAfterJS());
            button.put("winType", action.getWinType());
            button.put("iconAlign", "top");
            button.put("size", "larger");
            toolbar.add(button);
        }

        if (!toolbar.isEmpty()) {
            gridConfig.setToolbar(toolbar);
        }
    }

    /**
     * 通过操作名称获取对应的 {@link XUIAction} 对象。
     *
     * <p>从构建器注解的 {@code actions} 列表中查找指定名称的操作。
     * 注意：此方法仅查找注解声明的操作，不包括菜单配置库（XActionModel）中的操作。</p>
     *
     * @param name 操作名称（对应 {@code @UIAction} 的 name 属性）
     * @return 匹配的 XUIAction 对象；未找到或组件类型不匹配则返回null
     */
    public XUIAction getXAction(String name) {
        Annotation annotation = getUIAnnotation(this.getClass());
        if (annotation == null)
            return null;

        UIDefinition uiDefinition = annotation.annotationType().getAnnotation(UIDefinition.class);
        WidgetType compType = uiDefinition.component();
        Class<? extends IComponent> configClazz = compType.getWidget();
        if (configClazz == null)
            return null;

        if (GridComponent.class.isAssignableFrom(configClazz)) {
            try {
                Constructor<? extends IComponent> constructor = configClazz.getConstructor(annotation.annotationType());
                GridComponent compConfig = (GridComponent) constructor.newInstance(annotation);
                for (XUIAction action : compConfig.getActions()) {
                    if (action.getName().equals(name)) {
                        return action;
                    }
                }
            } catch (Exception e) {
                throw new XException(e);
            }
        }

        return null;
    }

    /**
     * 从指定类及其父类上获取 Flame UI 注解。
     *
     * <p>遍历类的所有注解，找到带有 {@link UIDefinition @UIDefinition} 元注解的那个注解。
     * 通常子类上标注了 {@code @UIDataGrid}、{@code @UITreeGrid} 或 {@code @UIMeshGrid} 等注解，
     * 这些注解本身又被 {@code @UIDefinition} 标注。</p>
     *
     * @param clazz 要检查的类
     * @return 带有 @UIDefinition 元注解的注解实例；未找到则返回null
     */
    public Annotation getUIAnnotation(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            UIDefinition uiDef = annotation.annotationType().getAnnotation(UIDefinition.class);
            if (uiDef != null) {
                return annotation;
            }
        }

        return null;
    }
    
    public String generateCompId(String gridId) {
        String resultCompId = "unknown$" + gridId + "$" + this.getClass().getName();
        
        Annotation annotation = getUIAnnotation(this.getClass());
        if (annotation == null) {
            return resultCompId;
        }

        String annotationName = annotation.annotationType().getSimpleName();
        switch (annotationName) {
            case "UIDataGrid":
                XUIDataGrid dataGrid = new XUIDataGrid((UIDataGrid) annotation, this.getClass().getName());
                if (FlameUtils.isBlank(dataGrid.getGroupField())) {
                    resultCompId = "datagrid$" + gridId + "$" + this.getClass().getName();
                } else {
                    resultCompId = "propertygrid$" + gridId + "$" + this.getClass().getName();
                }
                break;
            case "UITreeGrid":
                resultCompId = "treegrid$" + gridId + "$" + this.getClass().getName();
                break;
            case "UIMeshGrid":
                resultCompId = "meshgrid$" + gridId + "$" + this.getClass().getName();
                break;
            default:
                resultCompId = "unknown$" + gridId + "$" + this.getClass().getName();
                break;
        }

        return resultCompId;
    }

    /**
     * 判断字符串是否为空白（null、空串或纯空白字符）。
     *
     * @param value 要检查的字符串
     * @return true 如果字符串为空白
     */
    public boolean isBlank(String value) {
        if (value == null)
            return true;

        return (value.isEmpty() || value.trim().isEmpty());
    }
}
