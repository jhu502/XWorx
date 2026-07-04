package com.flame.xui;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.localize.ILocalization;
import com.flame.localize.LocalizationHelper;
import com.flame.thing.IPropertyDefinition;
import com.flame.thing.IThingModel;
import com.flame.thing.ThingModelHelper;
import com.flame.type.IPrimitiveType;
import com.flame.xui.widget.Hidden;
import com.flame.xui.widget.TextBox;
import com.flame.util.FlameUtils;
import com.flame.util.XException;
import com.flame.annotations.UICell;
import com.flame.annotations.UIGrid;
import com.flame.annotations.UIMeshGrid;
import com.flame.annotations.UIRow;
import com.flame.annotations.UIWidget;
import com.flame.xui.widget.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EasyUI MeshGrid（表单网格）服务端配置模型。
 *
 * <p>由 {@code @UIMeshGrid} 注解通过 {@code AbstractMeshComponentBuilder.buildComponentConfig}
 * 自动转换而来，序列化为 JSON 返回客户端渲染为 HTML 表单。
 * 与 DataGrid/TreeGrid 不同，MeshGrid 用于渲染可编辑的表单布局，支持多 Grid 分组、
 * 标签对齐、字段集（fieldset）、单元格跨列等。</p>
 *
 * <h3>内部类结构</h3>
 * <pre>
 * XMeshGrid
 *   ├── XGrid      — 单个表单分组（对应 {@code @UIGrid}）
 *   │    └── XRow  — 表单行（对应 {@code @UIRow}）
 *   │         └── XCell — 表单单元格（对应 {@code @UICell}）
 *   └── widgetMap  — 所有组件 ID → IWidget 映射
 * </pre>
 *
 * @see XUIDataGrid
 * @see XUITreeGrid
 */
public class XUIMeshGrid extends XUIComponent {
	protected static final Logger logger = LoggerFactory.getLogger(XUIMeshGrid.class);
	private transient final Map<String, IWidget> widgetMap = new HashMap<>();
	private final List<Hidden> hiddens = new ArrayList<>();
	private final List<XUIGrid> grids = new ArrayList<>();

	public XUIMeshGrid() {
		this.setWidgetType(WidgetType.MeshGrid);
	}

	public XUIMeshGrid(UIMeshGrid uiMeshGrid) {
		this.setWidgetType(WidgetType.MeshGrid);

		if (uiMeshGrid == null || uiMeshGrid.grids() == null)
			return;

		for (UIGrid uiGrid : uiMeshGrid.grids()) {
			XUIGrid xGrid = new XUIGrid(this, uiGrid);
			this.grids.add(xGrid);
		}
	}

	public XUIMeshGrid(UIMeshGrid uiMeshGrid, String builder) {
		this(uiMeshGrid);
		this.setName(builder);
	}

	public List<Hidden> getHiddens() {
		return hiddens;
	}

	public void addHidden(Hidden hidden) {
		this.hiddens.add(hidden);
	}

	public List<XUIGrid> getGrids() {
		return this.grids;
	}

	public XUIGrid getGrids(int i) {
		return this.getGrids(i);
	}

	public void addGrid(XUIGrid xuiGrid) {
		this.grids.add(xuiGrid);
		for (XUIRow xuiRow : xuiGrid.getRows()) {
			for (XUICell xuiCell : xuiRow.getCells()) {
				for (IWidget widget : xuiCell.getWidgets()) {
					this.addXUIWidget(widget.getId(), widget);
				}
			}
		}
		xuiGrid.meshGrid = this;
	}

	@Override
	public List<String> fields() {
		List<String> fields = new ArrayList<>();
		for (XUIGrid xGrid : this.grids) {
			fields.addAll(xGrid.fields());
		}
		return fields;
	}

	public void setType(Object type) {
		for (XUIGrid xGrid : this.grids) {
			xGrid.setProvider(type);
		}
	}

	public void inflateObject(Object object) {
		if (object == null)
			return;

		for (XUIGrid xGrid : this.grids) {
			for (XUIRow row : xGrid.getRows()) {
				if (row == null)
					continue;

				for (XUICell cell : row.getCells()) {
					if (cell == null)
						continue;

					for (IWidget widget : cell.getWidgets()) {
						if (widget == null)
							continue;

						if (object != null)
							widget.inflate(object);
					}
				}
			}
		}
	}

	public IWidget getXUIWidget(String id) {
		return this.widgetMap.get(id);
	}

	@JsonIgnore
	public Map<String, IWidget> getWidgetMap() {
		return this.widgetMap;
	}

	public void addXUIWidget(String id, IWidget widget) {
		this.widgetMap.put(id, widget);
	}

	public void setWidgetMode(WidgetMode widgetMode) {
		super.setWidgetMode(widgetMode);
		for (IWidget guiWidget : widgetMap.values()) {
			guiWidget.setWidgetMode(widgetMode);
		}
	}
	
	public static class XUIGrid extends XUIComponent implements IGrid<XUIRow> {
		private transient XUIMeshGrid meshGrid = null;
		private transient Object provider;
		private String display = "";
		private int rowCount = 0;
		private int cellCount = 0;
		private boolean fieldSet = false;
		private boolean alignLabel = false;
		private String beforeHTML = "";
		private String afterHTML = "";
		private final List<XUIRow> rows = new ArrayList<>();

		public XUIGrid(XUIMeshGrid meshGrid, UIGrid uiGrid) {
			if (uiGrid == null || uiGrid.rows() == null || uiGrid.rows().length == 0)
				return;

			this.meshGrid = meshGrid;

			this.setId("UIGrid_" + FlameUtils.getRandomConst());
			this.setName(uiGrid.title());
			this.setFieldSet(uiGrid.fieldSet());
			this.setAlignLabel(uiGrid.alignLabel());
            if (!Class.class.equals(uiGrid.provider())) {
                IThingModel thingModel = ThingModelHelper.manager().getThingModel(uiGrid.provider());
                if (thingModel == null) {
                    this.setProvider(uiGrid.provider());
                } else {
                    this.setProvider(thingModel);
                }
            }
			if (FlameUtils.isBlank(uiGrid.title())) {
				this.setDisplay(LocalizationHelper.get(this.getName()));
			} else {
				this.setDisplay(LocalizationHelper.get(uiGrid.title()));
			}

			for (UIRow uirow : uiGrid.rows()) {
				if (uirow == null || uirow.cells() == null || uirow.cells().length == 0)
					continue;
				XUIRow row = new XUIRow(uirow);
				for (UICell uicell : uirow.cells()) {
					XUICell cell = new XUICell(uicell);
					cell.setRowCount(uicell.rowCount());

					for (UIWidget uiWidget : uicell.widget()) {
						XUIWidget xuiWidget = XUIWidget.getWidget(uiWidget);
						WidgetMode componentModel = this.getWidgetMode();
						if (FlameUtils.isNotBlank(uiWidget.style())) {
							xuiWidget.setStyle(uiWidget.style());
						}
						if (xuiWidget instanceof TextBox textBox && uicell.rowCount() > 0) {
							textBox.setRowCount(uicell.rowCount());
						}
						if (componentModel != null) {
							xuiWidget.setWidgetMode(componentModel);
						}
						if (FlameUtils.isBlank(uiWidget.id())) {
							if (FlameUtils.isBlank(xuiWidget.getName())) {
								String genId = FlameUtils.getRandomConst() + '-' + this.meshGrid.getRandomId();
								xuiWidget.setId(genId);
							} else {
								String genId = xuiWidget.getName() + '-' + this.meshGrid.getRandomId();
								xuiWidget.setId(genId);
							}
						} else {
							xuiWidget.setId(uiWidget.id());
						}

						IWidget widget = this.meshGrid.getXUIWidget(xuiWidget.getId());
						if (widget != null) {
							throw new XException("Widget Id:%s is duplicated.", widget.getId());
						}
						this.meshGrid.addXUIWidget(xuiWidget.getId(), xuiWidget);
						cell.addWidget(xuiWidget);
					}
					row.addCell(cell);
				}

				this.rows.add(row);
				int size = row.size();
				if (size > this.cellCount) {
					this.cellCount = size;
				}
			}
			this.rowCount = this.rows.size();
		}

		public XUIGrid(IGrid<? extends IRow<? extends ICell<?>>> grid, IThingModel thingModel, WidgetMode widgetMode) {
			if (grid == null)
				return;

			this.setWidgetMode(widgetMode);
			this.setId("IGrid_" + FlameUtils.getRandomConst());
			this.setName(grid.getName());
			this.setProvider(grid.getProvider());
			this.setFieldSet(grid.isFieldSet());
			this.setAlignLabel(grid.isAlignLabel());
			if (grid instanceof ILocalization) {
				this.setDisplay(((ILocalization) grid).getDisplay(LocalizationHelper.getLocale()));
			} else if (FlameUtils.isBlank(grid.getDisplay())) {
				this.setDisplay(LocalizationHelper.get(grid.getName()));
			} else {
				this.setDisplay(LocalizationHelper.get(grid.getDisplay()));
			}

			for (IRow<? extends ICell<?>> iRow : grid.getRows()) {
				if (iRow == null || iRow.getCells() == null || iRow.getCells().isEmpty())
					continue;

				XUIRow xuiRow = new XUIRow(iRow, thingModel, widgetMode);
				this.rows.add(xuiRow);
				int size = xuiRow.size();
				if (size > this.cellCount) {
					this.cellCount = size;
				}
			}

			this.rowCount = this.getRows().size();
		}

		@JsonIgnore
		public Object getProvider() {
			return provider;
		}

		public void setProvider(Object provider) {
			this.provider = provider;
		}

		public String getDisplay() {
			return display;
		}

		public void setDisplay(String display) {
			this.display = display;
			if (FlameUtils.isNotBlank(display)) {
				this.setFieldSet(true);
			}
		}

		public boolean isFieldSet() {
			return fieldSet;
		}

		public void setFieldSet(boolean fieldSet) {
			this.fieldSet = fieldSet;
		}

		public boolean isAlignLabel() {
			return alignLabel;
		}

		public void setAlignLabel(boolean alignLabel) {
			this.alignLabel = alignLabel;
		}

		public List<XUIRow> getRows() {
			return rows;
		}

		public XUIRow addRow(XUIRow row) {
			this.rows.add(row);
			this.rowCount = this.rows.size();
			int size = row.size();
			if (size > this.cellCount) {
				this.cellCount = size;
			}

			return row;
		}

		public XUIRow addRow(int i, XUIRow row) {
			this.rows.add(i, row);
			this.rowCount = this.rows.size();
			int size = row.size();
			if (size > this.cellCount) {
				this.cellCount = size;
			}

			return row;
		}

		public int getRowCount() {
			return rowCount;
		}

		public int getCellCount() {
			return cellCount;
		}

		public String getBeforeHTML() {
			return beforeHTML;
		}

		public void setBeforeHTML(String beforeHTML) {
			this.beforeHTML = beforeHTML;
		}

		public String getAfterHTML() {
			return afterHTML;
		}

		public void setAfterHTML(String afterHTML) {
			this.afterHTML = afterHTML;
		}

		@JsonIgnore
		public List<String> fields() {
			Map<String, IWidget> widgetMap = this.meshGrid.getWidgetMap();
			List<String> fields = new ArrayList<>(widgetMap.keySet());
			if (!fields.contains("oid")) {
				fields.add("oid");
			}
			return fields;
		}
	}

	public static class XUIRow implements IRow<XUICell> {
		private final List<XUICell> cells = new ArrayList<>();

		public XUIRow() {
		}

		public XUIRow(IRow<? extends ICell<?>> iRow, IThingModel thingModel, WidgetMode widgetMode) {
			for (ICell<?> icell : iRow.getCells()) {
				XUICell xuiCell = new XUICell(icell, thingModel, widgetMode);
				this.addCell(xuiCell);
			}
		}

		public XUIRow(UIRow uiRow) {
		}

		public List<XUICell> getCells() {
			return cells;
		}

		public XUICell getCell(int i) {
			return this.cells.get(i);
		}

		public XUICell addCell(XUICell cell) {
			if (cell == null)
				return null;

			this.cells.add(cell);
			cell.setRow(this);
			return cell;
		}

		public XUICell setCell(int i, XUICell cell) {
			int len = this.cells.size();
			if (i >= len) {
				for (int j = i; j >= len; j--) {
					this.cells.add(new XUICell());
				}
			}

			this.cells.set(i, cell);
			return cell;
		}

		public int size() {
			return cells.size();
		}
	}

	public static class XUICell implements ICell<XUIRow> {
		private transient XUIRow row;
		private String uiType = ICell.WIDGET;
		private String style = "";
		private String value;
        private int colspan = 1;
        private int rowCount = 1;
        private IWidget label;
        private List<IWidget> widgets = new ArrayList<>();

		public XUICell() {
		}

		public XUICell(ICell<? extends IRow<?>> icell, IThingModel thingModel, WidgetMode widgetMode) {
			this.setUiType(icell.getUiType());
			this.setValue(icell.getValue());
			this.setRowCount(icell.getRowCount());
			this.setColspan(icell.getColspan());
			this.setStyle(icell.getStyle());

			IPropertyDefinition definition = thingModel.getPropertyDefinition(this.getValue());
			if (definition != null) {
				Label label = new Label("label_" + definition.getName());
				label.addDomClass("xui-form-label");
				if (!definition.isNullable() && !WidgetMode.Display.equals(widgetMode)) {
					label.addDomClass("required");
				}
				String display = definition.getLocalDisplay();
				label.setText(display + ": ");
				this.setLabel(label);
				try {
					Constructor<? extends IPrimitiveType<?>> constructor = definition.getBaseType().getPrimitive().getConstructor();
					IPrimitiveType<?> primitiveType = constructor.newInstance();
					IWidget xuiWidget = primitiveType.getIWidget(widgetMode, definition);

					if (xuiWidget != null) {
						if (FlameUtils.isNotBlank(this.getStyle())) {
							xuiWidget.setStyle(this.getStyle());
						}
						if (xuiWidget instanceof TextBox textBox) {
							textBox.setRowCount(this.getRowCount());
						}
						this.addWidget(xuiWidget);
					}
				} catch (Exception e) {
					throw new XException(e);
				}
			}
		}

		public XUICell(UICell uicell) {
			if (FlameUtils.isNotBlank(uicell.style())) {
				this.setStyle(uicell.style());
			}
			if (uicell.colspan() > 0) {
				this.setColspan(uicell.colspan());
			}

			if (!FlameUtils.isBlank(uicell.label())) {
				/**
				 * 在页面渲染时, jquery基于这个class=xui-form-label来获取UICell中定义的label, 然后计算这些label的最大maxWidth, 如
				 * 果UIXMeshGrid的alignLabel=true, 页面自动基于maxWidth进行label对齐;
				 */
				String labelText = LocalizationHelper.getLabel(uicell.label());
				Label label = new Label(uicell.label());
				label.addDomClass("xui-form-label");
				label.setStyle(uicell.style());
				if (FlameUtils.isBlank(labelText)) {
					if (uicell.required()) {
						label.addDomClass("required");
						label.setText(uicell.label());
					} else {
						label.setText(uicell.label());
					}
				} else {
					if (uicell.required()) {
						label.addDomClass("required");
						label.setText(labelText);
					} else {
						label.setText(labelText);
					}
				}
				this.setLabel(label);
			}
		}

		@JsonIgnore
		public XUIRow getRow() {
			return this.row;
		}

		public void setRow(XUIRow row) {
			this.row = row;
		}

		public String getUiType() {
		    return this.uiType;
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

		public IWidget getLabel() {
			return label;
		}

		public void setLabel(IWidget label) {
			this.label = label;
		}

		public List<IWidget> getWidgets() {
			return widgets;
		}

		public void addWidget(IWidget widget) {
			this.widgets.add(widget);
		}
	}
}
