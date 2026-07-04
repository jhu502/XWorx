package com.thing.mesher;

import com.flame.util.XMLInfo;
import com.flame.xui.*;
import com.thing.entity.XThingModel;

import jakarta.xml.bind.annotation.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@XmlType(name = "XMLayout", propOrder = {"MGrids"})
@XmlRootElement(name = "XMLayout")
public class XMLayout extends XMLInfo {
	private static final long serialVersionUID = 1L;
	private final List<MGrid> grids = new ArrayList<>();
	private XThingModel thingModel;
	private GridComponent gridComponent;

	@XmlTransient
	public XThingModel getModelType() {
		return this.thingModel;
	}

	public void setModelType(XThingModel thingModel) {
		this.thingModel = thingModel;
	}

	@XmlAttribute(name = "componentModel")
	public GridComponent getGridComponent() {
		return gridComponent;
	}

	public void setGridComponent(GridComponent gridComponent) {
		this.gridComponent = gridComponent;
	}

	@XmlElement(name = "MGrid")
	public List<MGrid> getMGrids() {
		return this.grids;
	}

	public MGrid addMGrid(MGrid mesh) {
		this.grids.add(mesh);
		return mesh;
	}

	@XmlType(name = "MGrid", propOrder = {"rows"})
	public static class MGrid implements IGrid<MRow> {
		private Class<?> type;
		private String name;
		private String display;
		private boolean fieldSet;
		private final List<MRow> rows = new ArrayList<>();

		public MGrid() {
		}

		@XmlAttribute(name = "name")
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@XmlTransient
		public Class<?> getProvider() {
			return type;
		}

		@XmlAttribute(name = "display")
		public String getDisplay() {
			return display;
		}

		public void setDisplay(String display) {
			this.display = display;
		}

		@XmlAttribute(name = "fieldSet")
		public boolean isFieldSet() {
			return fieldSet;
		}

		public void setFieldSet(boolean fieldSet) {
			this.fieldSet = fieldSet;
		}

		public void setType(Class<?> type) {
			this.type = type;
		}

		@XmlElement(name = "MRow")
		public List<MRow> getRows() {
			return rows;
		}

		public MRow addRow(MRow row) {
			this.rows.add(row);
			return row;
		}
	}

	@XmlType(name = "MRow", propOrder = {"cells"})
	public static class MRow implements IRow<MCell> {
		private final List<MCell> cells = new ArrayList<>();

		@XmlElement(name = "MCell")
		public List<MCell> getCells() {
			return cells;
		}

		public MCell getCell(int i) {
			return this.getCell(i);
		}

		public MCell addCell(MCell cell) {
			if (cell == null)
				return null;

			this.cells.add(cell);
			cell.setRow(this);
			return cell;
		}

		public MCell setCell(int i, MCell cell) {
			this.cells.add(i, cell);
			return cell;
		}

		public int size() {
			return cells.size();
		}
	}

	@XmlType(name = "MCell", propOrder = {"colspan", "style"})
	public static class MCell implements ICell<MRow> {
		private transient MRow row;
		private String uiType;
		private String style;
		private String value;
		private String display;
        private int colspan;
        private int rowCount;
		private final List<String> attributes = new ArrayList<>();
        private final List<IWidget> widgets = new ArrayList<>();

		public MCell() {
		}

		@XmlTransient
		public MRow getRow() {
			return this.row;
		}

		public void setRow(MRow row) {
			this.row = row;
		}

		public String getUiType() {
            return uiType;
        }

        public void setUiType(String uiType) {
            this.uiType = uiType;
        }

        @XmlAttribute(name = "style")
		public String getStyle() {
			return style;
		}

		public void setStyle(String style) {
			this.style = style;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getDisplay() {
			return display;
		}

		public void setDisplay(String display) {
			this.display = display;
		}

        @XmlAttribute(name = "colspan")
        public int getColspan() {
            return colspan;
        }

        public void setColspan(int colspan) {
            this.colspan = colspan;
        }

        @Override
        public int getRowCount() {
            return this.rowCount;
        }

        @Override
        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
        }
        
        public List<String> getAttributes() {
            return attributes;
        }

        public List<IWidget> getWidgets(WidgetMode model) {
            return this.widgets;
        }

        public void addWidget(IWidget widget) {
            this.widgets.add(widget);
        }
	}

	public static void main(String[] args) throws Exception {
		XMLayout configSpec = XMLInfo.xmlToBean(new File("D:/MZB4212CNX.xml"), XMLayout.class);
		System.out.println(configSpec.toXML());
	}
}
