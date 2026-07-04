package com.flame.xui.widget;

import com.flame.xui.XUIWidget;

/**
 * Grid 列配置模型，对应 EasyUI DataGrid/TreeGrid 的单个列定义。
 *
 * <p>由 {@code @UIColumn} 注解解析而来，最终序列化为 JSON
 * 传给前端渲染 EasyUI Grid 组件。</p>
 *
 * <p>支持的列属性：field、title、width、align、order、sortable、
 * formatter、checkbox、hidden、frozen、expander、editor。</p>
 */
public class GridColumn {
    /** 字段名，对应行数据的 key */
    private String field = "";
    /** 列标题（支持国际化 key） */
    private String title = "";
    /** 列宽度，如 "220" 或 "30%" */
    private String width = "";
    /** 对齐方式，默认 "left" */
    private String align = "left";
    /** 排序方向：asc / desc */
    private String order;
    /** JavaScript formatter 函数名 */
    private String formatter;
    /** 是否可排序 */
    private boolean sortable = true;
    /** 是否为复选框列 */
    private boolean checkbox = false;
    /** 是否隐藏列 */
    private boolean hidden = false;
    /** 是否冻结列 */
    private boolean frozen = false;
    /** 是否为展开列（TreeGrid 专用） */
    private boolean expander = false;
    /** 单元格编辑器组件 */
    private XUIWidget editor;

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public String getTitle() { return title; }
    /** 设置标题，为 null 时回退为空字符串 */
    public void setTitle(String title) { this.title = title; if (this.title == null) this.title = ""; }

    public String getWidth() { return width; }
    /** 设置宽度，为 null 时回退为空字符串 */
    public void setWidth(String width) { this.width = width; if (this.width == null) this.width = ""; }

    public String getAlign() { return align; }
    public void setAlign(String align) { this.align = align; }

    public String getOrder() { return order; }
    /** 设置排序方向，空值回退为 null */
    public void setOrder(String order) {
        if (order == null || order.trim().isEmpty()) this.order = null;
        else this.order = order;
    }

    public String getFormatter() { return formatter; }
    /** 设置 formatter 函数名，空值回退为 null */
    public void setFormatter(String formatter) {
        if (formatter == null || formatter.trim().isEmpty()) this.formatter = null;
        else this.formatter = formatter;
    }

    public boolean isSortable() { return sortable; }
    public void setSortable(boolean sortable) { this.sortable = sortable; }

    public boolean isCheckbox() { return checkbox; }
    public void setCheckbox(boolean checkbox) { this.checkbox = checkbox; }

    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }

    public boolean isFrozen() { return frozen; }
    public void setFrozen(boolean frozen) { this.frozen = frozen; }

    public boolean isExpander() { return expander; }
    public void setExpander(boolean expander) { this.expander = expander; }

    public XUIWidget getEditor() { return editor; }
    /** 设置单元格编辑器（如 TextBox、ComboBox 等） */
    public void setEditor(XUIWidget editor) { this.editor = editor; }
}
