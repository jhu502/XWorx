package com.thing.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.localize.ILocalization;
import com.flame.localize.LocalizationHelper;
import com.flame.orm.XJsonType;
import com.flame.thing.IPropertyDefinition;
import com.flame.thing.IPropertyProvider;
import com.flame.util.FlameUtils;
import com.flame.util.PropertyUtil;
import com.flame.xui.ICell;
import com.flame.xui.IGrid;
import com.flame.xui.IRow;
import com.flame.xui.IWidget;

public class MeshLayout extends XJsonType<MeshLayout> {
    private static final long serialVersionUID = 1L;
    private transient IPropertyProvider provider;
    private List<MeshGrid> grids = new ArrayList<>();

    public MeshLayout() {
    }

    public void setProvider(IPropertyProvider provider) {
        this.provider = provider;
    }

    @JsonIgnore
    public IPropertyProvider getProvider() {
        return this.provider;
    }

    public List<MeshGrid> getGrids() {
        return this.grids;
    }

    public void addGrid(MeshGrid mGrid) {
        this.grids.add(mGrid);
    }

    public void postProcess() {
        for (MeshGrid mGrid : grids) {
            for (MeshRow xRow : mGrid.getRows()) {
                for (MeshCell mCell : xRow.getCells()) {
                    mCell.layout = this;
                }
            }
        }
    }

    public static class MeshGrid implements IGrid<MeshRow>, ILocalization, java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private transient Object type;
        private String name;
        private String display;
        private String en_US;
        private String zh_CN;
        private int rowCount = 0;
        private int cellCount = 0;
        private boolean fieldSet = true;
        private boolean alignLabel = false;
        private final List<MeshRow> rows = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getDisplay() {
            return this.display;
        }

        @Override
        public void setDisplay(String display) {
            this.display = display;
        }

        @JsonIgnore
        public Object getProvider() {
            return type;
        }

        public void setType(Object type) {
            this.type = type;
        }

        @Override
        public String getEn_US() {
            return this.en_US;
        }

        @Override
        public void setEn_US(String en_US) {
            this.en_US = en_US;
        }

        @Override
        public String getZh_CN() {
            return this.zh_CN;
        }

        @Override
        public void setZh_CN(String zh_CN) {
            this.zh_CN = zh_CN;
        }

        @Override
        public String getDisplay(Locale locale) {
            if (locale == null)
                return this.display;

            String property = locale.getLanguage() + "_" + locale.getCountry();
            String value = (String) PropertyUtil.getProperty(this, property);
            if (value == null || value.isBlank()) {
                return this.display;
            } else {
                return value;
            }
        }

        @JsonIgnore
        public int getRowCount() {
            return rowCount;
        }

        @JsonIgnore
        public int getCellCount() {
            return cellCount;
        }

        @JsonIgnore
        public boolean isFieldSet() {
            return fieldSet;
        }

        public void setFieldSet(boolean fieldSet) {
            this.fieldSet = fieldSet;
        }

        @JsonIgnore
        public boolean isAlignLabel() {
            return alignLabel;
        }

        public void setAlignLabel(boolean alignLabel) {
            this.alignLabel = alignLabel;
        }

        public List<MeshRow> getRows() {
            return rows;
        }

        public MeshRow addRow(MeshRow mRow) {
            this.rows.add(mRow);
            /** 计算rowCount / cellCount */
            this.rowCount = this.rows.size();
            int size = mRow.size();
            if (size > this.cellCount) {
                this.cellCount = size;
            }
            return mRow;
        }

        public MeshRow addRow(String property) {
            MeshRow mRow = new MeshRow();
            MeshCell mCell = new MeshCell();
            mCell.setValue(property);
            mRow.addCell(mCell);
            this.rows.add(mRow);
            /** 计算rowCount / cellCount */
            this.rowCount = this.rows.size();
            int size = mRow.size();
            if (size > this.cellCount) {
                this.cellCount = size;
            }

            return mRow;
        }
    }

    public static class MeshRow implements IRow<MeshCell>, java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final List<MeshCell> cells = new ArrayList<>();

        public List<MeshCell> getCells() {
            return cells;
        }

        public MeshCell getCell(int i) {
            return this.getCell(i);
        }

        public MeshCell addCell(MeshCell cell) {
            if (cell == null)
                return null;

            this.cells.add(cell);
            return cell;
        }

        public MeshCell setCell(int i, MeshCell cell) {
            int len = this.cells.size();
            if (i >= len) {
                for (int j = i; j >= len; j--) {
                    this.cells.add(new MeshCell());
                }
            }
            this.cells.set(i, cell);
            return cell;
        }

        public int size() {
            return cells.size();
        }
    }

    public static class MeshCell implements ICell<MeshRow>, java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private transient MeshLayout layout;
        private String uiType = ICell.WIDGET;
        private String value;
        private String style;
        private int colspan;
        private int rowCount = 1;

        public MeshCell() {
        }

        public String getUiType() {
            return uiType;
        }

        public void setUiType(String uiType) {
            this.uiType = uiType;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public String getDisplay() {
            if (FlameUtils.isBlank(this.getValue()))
                return "";

            if (this.layout == null)
                return this.getValue();
            
            IPropertyProvider provider = this.layout.getProvider();
            if (provider == null)
                return this.getValue();
            
            IPropertyDefinition definition = provider.getPropertyDefinition(this.getValue());
            if (definition == null) {
                return LocalizationHelper.get(this.getValue());
            } else {
                Locale locale = LocalizationHelper.getLocale();
                return definition.getDisplay(locale);
            }
        }

        public void addWidget(IWidget widget) {
        }

        public int getColspan() {
            return colspan;
        }

        public void setColspan(int colspan) {
            this.colspan = colspan;
        }

        public int getRowCount() {
            return rowCount;
        }

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
        }
    }
}
