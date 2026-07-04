package com.flame.xui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.localize.LocalizationHelper;
import com.flame.util.FlameUtils;
import com.flame.annotations.UIAction;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.annotations.UIWidget;
import com.flame.xui.widget.GridColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * EasyUI TreeGrid 服务端配置模型。
 *
 * <p>由 {@code @UITreeGrid} 注解通过 {@code AbstractTreeComponentBuilder.buildComponentConfig}
 * 自动转换而来，序列化为 JSON 返回客户端渲染为 EasyUI TreeGrid。</p>
 *
 * <p>与 {@link XUIDataGrid} 相比额外支持：{@code treeField}（树形字段）、
 * {@code animate}（展开动画）、{@code fitColumns}（自适应列宽）等树表专属配置。</p>
 *
 * @author hujin
 * @see XUIDataGrid
 * @see GridColumn
 */
public class XUITreeGrid extends GridComponent {
    private String treeField = "";
    private boolean animate = true;
    private boolean fitColumns = true;
    private boolean singleSelect = true;
    private boolean pagination = false;
    private boolean autoRowHeight = false;
    private int pageSize = 20;
    private int rownumberWidth = 20;
    private int scrollbarSize = 20;
    private List<List<GridColumn>> frozenColumns = new ArrayList<>();
    private List<List<GridColumn>> columns = new ArrayList<>();

    public XUITreeGrid(UITreeGrid uiGrid, String builder) {
        this(uiGrid);
        this.setName(builder);
        if (FlameUtils.isBlank(this.getTitle())) {
            String display = LocalizationHelper.get(this.getName());
            if (FlameUtils.equals(display, this.getName())) {
                this.setTitle("");
            } else {
                this.setTitle(display);
            }
        }
    }

    public XUITreeGrid(UITreeGrid uiGrid) {
        if (FlameUtils.isBlank(uiGrid.idField())) {
            this.setIdField(ROW_ID);
        } else {
            this.setIdField(uiGrid.idField());
        }
        this.setTreeField(uiGrid.treeField());
        if (FlameUtils.isNotBlank(uiGrid.title())) {
            this.setTitle(uiGrid.title());
        }
        if (!FlameUtils.isBlank(uiGrid.toolbar())) {
            if (uiGrid.toolbar().startsWith("#")) {
                this.setToolbar(uiGrid.toolbar());
            } else {
                this.setToolbar("#" + uiGrid.toolbar());
            }
        }
        this.setContextMenu(uiGrid.contextMenu());
        this.setActionModel(uiGrid.actionModel());
        this.setFit(uiGrid.fit());
        this.setAnimate(uiGrid.animate());
        this.setRownumbers(uiGrid.rowNumber());
        this.setFitColumns(uiGrid.fitColumns());
        this.setSingleSelect(uiGrid.singleSelect());
        this.setPagination(uiGrid.pagination());
        this.setAutoRowHeight(uiGrid.autoRowHeight());
        this.setPageSize(uiGrid.pageSize());
        this.setStriped(uiGrid.striped());
        this.setSortOrder(uiGrid.sortOrder());
        this.setSortName(uiGrid.sortName());
        this.setSelectOnCheck(uiGrid.selectOnCheck());
        this.setCheckOnSelect(uiGrid.checkOnSelect());

        List<GridColumn> columnList = new ArrayList<>();
        List<GridColumn> frozenList = new ArrayList<>();

        GridColumn rowIdColumn = null;
        for (UIColumn uicolumn : uiGrid.columns()) {
            GridColumn column = new GridColumn();
            column.setField(uicolumn.field());
            /**
             * 若当前非hidden/checkbox自动, 并且在title属性中未设置值, 则需要从资源文件中获取多语言信息;
             */
            if (!uicolumn.hidden() && !uicolumn.checkbox()) {
                if (FlameUtils.isBlank(uicolumn.title())) {
                    int width = -1;
                    if (FlameUtils.isInteger(uicolumn.width())) {
                        width = Integer.parseInt(uicolumn.width());
                    }
                    if (width < 0 || width > 25) {
                        column.setTitle(LocalizationHelper.get(uicolumn.field()));
                    }
                } else {
                    column.setTitle(uicolumn.title());
                }
            }
            column.setWidth(uicolumn.width());
            column.setAlign(uicolumn.align());
            column.setOrder(uicolumn.order());
            column.setFormatter(uicolumn.formatter());
            column.setSortable(uicolumn.sortable());
            column.setCheckbox(uicolumn.checkbox());
            column.setHidden(uicolumn.hidden());
            column.setFrozen(uicolumn.frozen());
            if (uicolumn.widget().length > 0) {
                UIWidget uiWidget = uicolumn.widget()[0];
                XUIWidget guiWidget = XUIWidget.getWidget(uiWidget);
                column.setEditor(guiWidget);
            }
            if (column.isFrozen()) {
                frozenList.add(column);
            } else {
                if (this.getIdField().equals(column.getField()) && !column.isHidden()) {
                    frozenList.add(column);
                } else {
                    columnList.add(column);
                }
            }

            if (FlameUtils.equals(ROW_ID, column.getField())) {
                rowIdColumn = column;
            }
        }
        if (rowIdColumn == null) {
            rowIdColumn = new GridColumn();
            rowIdColumn.setField(ROW_ID);
            rowIdColumn.setHidden(true);
            rowIdColumn.setCheckbox(true);
            rowIdColumn.setFrozen(false);
            rowIdColumn.setExpander(false);
            rowIdColumn.setSortable(false);
            columnList.add(0, rowIdColumn);
        }
        
        /**
         * 在最后增加一个空白列
         */
        GridColumn column = new GridColumn();
        column.setWidth("40");
        columnList.add(column);
        this.addColumn(columnList);
        this.addFrozenColumn(frozenList);

        for (UIAction action : uiGrid.actions()) {
            XUIAction xaction = XUIAction.newXAction(action);
            this.addXAction(xaction);
        }
    }

    public String getTreeField() {
        return treeField;
    }

    public void setTreeField(String treeField) {
        this.treeField = treeField;
    }

    public boolean isAnimate() {
        return animate;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public boolean isFitColumns() {
        return fitColumns;
    }

    public void setFitColumns(boolean fitColumns) {
        this.fitColumns = fitColumns;
    }

    public boolean isSingleSelect() {
        return singleSelect;
    }

    public void setSingleSelect(boolean singleSelect) {
        this.singleSelect = singleSelect;
    }

    public boolean isPagination() {
        return pagination;
    }

    public void setPagination(boolean pagination) {
        this.pagination = pagination;
    }

    public boolean isAutoRowHeight() {
        return autoRowHeight;
    }

    public void setAutoRowHeight(boolean autoRowHeight) {
        this.autoRowHeight = autoRowHeight;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getRownumberWidth() {
        return rownumberWidth;
    }

    public void setRownumberWidth(int rownumberWidth) {
        this.rownumberWidth = rownumberWidth;
    }

    public int getScrollbarSize() {
        return scrollbarSize;
    }

    public void setScrollbarSize(int scrollbarSize) {
        this.scrollbarSize = scrollbarSize;
    }

    public int[] getPageList() {
        return new int[] { pageSize / 2, pageSize, pageSize * 2 };
    }

    public List<List<GridColumn>> getFrozenColumns() {
        return frozenColumns;
    }

    public void addFrozenColumn(List<GridColumn> list) {
        this.frozenColumns.add(list);
    }

    public List<List<GridColumn>> getColumns() {
        return columns;
    }

    public void addColumn(List<GridColumn> list) {
        this.columns.add(list);
    }

    @JsonIgnore
    public List<String> fields() {
        List<String> fields = new ArrayList<>();
        for (List<GridColumn> list : this.getColumns()) {
            for (GridColumn column : list) {
                fields.add(column.getField());
            }
        }
        for (List<GridColumn> list : this.getFrozenColumns()) {
            for (GridColumn column : list) {
                fields.add(column.getField());
            }
        }
        if (!fields.contains("oid")) {
            fields.add("oid");
        }
        return fields;
    }
}
