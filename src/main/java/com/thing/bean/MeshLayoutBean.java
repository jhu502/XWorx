package com.thing.bean;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.flame.localize.LocalizationHelper;
import com.flame.util.PropertyUtil;
import com.flame.xui.ICell;
import com.thing.entity.MeshLayout;
import com.thing.entity.XPropertyLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class MeshLayoutBean {
    private static final String TYPE = "type";
    private Map<String, Object> fields = new LinkedHashMap<>();
    private List<Grid> grids = new ArrayList<>();

    /**
     * 将 XPropertyLayout 中的 MeshLayout 转换为前端可用的 MeshLayoutBean。
     *
     * @param propertyLayout 属性布局实体，其 getLayout() 已通过 setType 注入了 IPropertyProvider
     */
    public static MeshLayoutBean newInstance(XPropertyLayout propertyLayout) {
        MeshLayoutBean layoutBean = new MeshLayoutBean();

        MeshLayout meshLayout = propertyLayout.getLayout();
        if (meshLayout == null) {
            meshLayout = new MeshLayout();
        }

        for (String fieldName : getStringFields(XPropertyLayout.class)) {
            Object value = PropertyUtil.getProperty(propertyLayout, fieldName);
            layoutBean.fields.put(fieldName, value);
        }
        layoutBean.fields.put(TYPE, propertyLayout.getLayoutType().name());

        List<MeshLayout.MeshGrid> meshGrids = meshLayout.getGrids();
        if (meshGrids.isEmpty()) {
            MeshLayout.MeshGrid defaultGrid = new MeshLayout.MeshGrid();
            defaultGrid.setName("Primary");
            defaultGrid.setDisplay("Primary");
            defaultGrid.setEn_US("Primary");
            defaultGrid.setZh_CN("主属性");
            meshGrids = Collections.singletonList(defaultGrid);
        }

        for (MeshLayout.MeshGrid mGrid : meshGrids) {
            Grid grid = new Grid();

            // 反射获取 MGrid 的 String 类型成员变量，label 从多语言获取，回退为属性名
            for (String fieldName : getStringFields(MeshLayout.MeshGrid.class)) {
                String label = LocalizationHelper.get(fieldName) + ": ";
                if (label.isBlank()) {
                    label = fieldName + ": ";
                }
                Object value = PropertyUtil.getProperty(mGrid, fieldName);
                grid.getHead().add(Item.newItem(fieldName, label, value));
            }

            // 网格行数据
            for (MeshLayout.MeshRow mRow : mGrid.getRows()) {
                List<Cell> row = new ArrayList<>();
                for (MeshLayout.MeshCell mCell : mRow.getCells()) {
                    Cell cell = new Cell();
                    cell.setUiType(mCell.getUiType());
                    cell.setValue(mCell.getValue());
                    cell.setDisplay(mCell.getDisplay());
                    cell.setStyle(mCell.getStyle());
                    cell.setColspan(mCell.getColspan());
                    cell.setRowCount(mCell.getRowCount());
                    row.add(cell);
                }
                // 补齐到 4 列
                while (row.size() < 4) {
                    row.add(new Cell());
                }
                grid.addRows(row);
            }
            // 补齐到 10 行
            while (grid.getRows().size() < 15) {
                List<Cell> emptyRow = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    emptyRow.add(new Cell());
                }
                grid.addRows(emptyRow);
            }

            layoutBean.addGrids(grid);
        }

        return layoutBean;
    }

    /**
     * 将 MeshLayoutBean 的值写回 XPropertyLayout。
     *
     * @param propertyLayout 待写入的属性布局实体
     */
    public void convert2XPropertyLayout(XPropertyLayout propertyLayout) {
        // 将 fields 中的属性写回 XPropertyLayout
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            setProperty(propertyLayout, entry.getKey(), entry.getValue());
        }

        // 将 grids 转换为 MeshLayout
        MeshLayout meshLayout = new MeshLayout();
        for (Grid beanGrid : grids) {
            MeshLayout.MeshGrid mGrid = new MeshLayout.MeshGrid();

            // 将 head 中的值写回 MGrid 的 String 属性
            for (Item item : beanGrid.getHead()) {
                setProperty(mGrid, item.getName(), item.getValue());
            }

            // 将 rows 转换为 MRow
            for (List<Cell> beanRow : beanGrid.getRows()) {
                MeshLayout.MeshRow mRow = new MeshLayout.MeshRow();
                boolean hasValue = false;
                for (int i = 0; i < beanRow.size(); i++) {
                    Cell beanCell = beanRow.get(i);
                    if (beanCell.getValue() != null && !beanCell.getValue().toString().isBlank()) {
                        MeshLayout.MeshCell mCell = new MeshLayout.MeshCell();
                        mCell.setUiType(beanCell.getUiType());
                        mCell.setColspan(beanCell.getColspan());
                        mCell.setValue(beanCell.getValue().toString());
                        mCell.setStyle(beanCell.getStyle());
                        mCell.setRowCount(beanCell.getRowCount());
                        mRow.setCell(i, mCell);
                        hasValue = true;
                    }
                }
                if (hasValue) {
                    mGrid.addRow(mRow);
                }
            }

            meshLayout.addGrid(mGrid);
        }

        propertyLayout.setLayout(meshLayout);
    }

    /** 通过反射调用 setter 设置属性值 */
    private static void setProperty(Object target, String fieldName, Object value) {
        try {
            String setter = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            for (Method m : target.getClass().getMethods()) {
                if (m.getName().equals(setter) && m.getParameterCount() == 1) {
                    m.invoke(target, value);
                    return;
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }

    /** 反射获取指定类中 String 类型的实例成员变量名列表 */
    private static List<String> getStringFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
            .filter(f -> f.getType() == String.class)
            .filter(f -> !Modifier.isStatic(f.getModifiers()))
            .map(Field::getName)
            .toList();
    }

    @JsonAnyGetter
    public Map<String, Object> getFields() {
        return fields;
    }

    @JsonAnySetter
    public void setField(String key, Object value) {
        this.fields.put(key, value);
    }

    public List<Grid> getGrids() {
        return grids;
    }

    public void addGrids(Grid grid) {
        this.grids.add(grid);
    }

    public static class Grid {
        private List<Item> head = new ArrayList<>();
        private List<List<Cell>> rows = new ArrayList<>();

        public List<Item> getHead() {
            return head;
        }

        public void setHead(List<Item> head) {
            this.head = head;
        }

        public List<List<Cell>> getRows() {
            return rows;
        }

        public void addRows(List<Cell> row) {
            this.rows.add(row);
        }
    }

    public static class Item {
        private String name;
        private String label;
        private Object value;

        public static Item newItem(String name, String label, Object value) {
            Item item = new Item();
            item.setName(name);
            item.setLabel(label);
            item.setValue(value != null ? value : "");
            return item;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    public static class Cell {
        private int colspan = 1;
        private String uiType = ICell.WIDGET;
        private String style = "";
        private Integer rowCount = 1;
        private String display = "";
        private Object value = "";

        public int getColspan() {
            return colspan;
        }

        public void setColspan(int colspan) {
            this.colspan = colspan;
        }

        public String getUiType() {
            return uiType;
        }

        public void setUiType(String uiType) {
            this.uiType = uiType;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public Integer getRowCount() {
            return rowCount;
        }

        public void setRowCount(Integer rowCount) {
            this.rowCount = rowCount;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
