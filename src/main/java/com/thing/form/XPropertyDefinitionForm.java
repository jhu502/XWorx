package com.thing.form;

import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;

import com.flame.xui.XCommandBean;
import com.flame.localize.LocalizationHelper;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.thing.PropertyType;
import com.flame.xui.WidgetMode;
import com.flame.xui.WidgetType;
import com.flame.annotations.UICell;
import com.flame.annotations.UIEvent;
import com.flame.annotations.UIGrid;
import com.flame.annotations.UIMeshGrid;
import com.flame.annotations.UIRow;
import com.flame.annotations.UIWidget;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.widget.Label;
import com.flame.xui.widget.TextDisplay;
import com.flame.xui.XUIMeshGrid;
import com.thing.entity.XPropertyDefinition;
import com.thing.entity.XThingModel;

@UIMeshGrid(grids = {
        @UIGrid(provider = XPropertyDefinition.class, rows = { //
                @UIRow(cells = { //
                        @UICell(widget = { //
                                @UIWidget(type = WidgetType.Hidden, id = "propertyOid", name = "oid"), //
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_internalName", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "propertyName", name = "name", traits = "disabled:true", style = "width:250px;height:25px;"),//
                                @UIWidget(type = WidgetType.IconBox, id = "propertyLocale", name = "locale", url = "images/values.png", style = "margin-left:5px;", events = {
                                        @UIEvent(name = "onclick", value = "flame.setLocalization('propertyOid')")
                                })//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_displayName", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "propertyDisplayName", name = "displayName", traits = "disabled:true", style = "width:300px;height:25px;")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_description", widget = { //
                                @UIWidget(type = WidgetType.TextBox, id = "propertyDescription", name = "description", traits = "multiline:true", style = "width:300px;height:50px")//
                        }), //
                }), //
                @UIRow(cells = { //
                        @UICell(label = "label_type", widget = { //
                                @UIWidget(type = WidgetType.ComboBox, id = "propertyType", name = "baseType", traits = "panelHeight:'auto',required:true", style = "width:200px;height:25px;")//
                        }), //
                }), //
        })
})
public class XPropertyDefinitionForm extends AbstractMeshComponentBuilder {
    @Override
    public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
        XUIMeshGrid xuiMeshGrid = super.buildComponentConfig(commandBean);
        XObject primary = commandBean.getPrimaryObj();

        xuiMeshGrid.setWidgetMode(WidgetMode.Display);
        if (primary instanceof XPropertyDefinition) {
            XPropertyDefinition definition = (XPropertyDefinition) primary;
            if (PropertyType.MBA.equals(definition.getPropertyType())) {
                this.addColumnNameRow(xuiMeshGrid, definition);
            }
        }
        xuiMeshGrid.inflateObject(primary);

        return xuiMeshGrid;
    }

    /**
     * @param meshGrid
     * @param definition
     */
    private void addColumnNameRow(XUIMeshGrid meshGrid, XPropertyDefinition definition) {
        XThingModel thingModel = (XThingModel) definition.getPropertyProvider();
        Class<?> entityClass = thingModel.getEntityClass();
        EntityPersister entityPersister = PersistenceHelper.service().getEntityPersister(entityClass.getSimpleName());
        if (entityPersister instanceof AbstractEntityPersister) {
            AbstractEntityPersister abstractPersist = (AbstractEntityPersister) entityPersister;
            if (definition.getName().equals(definition.getSource())) {
                String[] names = abstractPersist.getPropertyColumnNames(definition.getSource());
                if (names != null && names.length > 0) {
                    String columnName = names[0];
                    for (XUIMeshGrid.XUIGrid xGrid : meshGrid.getGrids()) {
                        XUIMeshGrid.XUIRow xuiRow = xGrid.addRow(5, new XUIMeshGrid.XUIRow());
                        XUIMeshGrid.XUICell xuiCell = xuiRow.addCell(new XUIMeshGrid.XUICell());
                        xuiCell.setStyle("padding-left:5px;height:29px");

                        Label colLabel = new Label("label_columnName");
                        colLabel.addDomClass("xui-form-label");
                        colLabel.setText(LocalizationHelper.get("label_columnName"));
                        xuiCell.setLabel(colLabel);

                        TextDisplay colDisplay = new TextDisplay();
                        colDisplay.setText(columnName);
                        colDisplay.setValue(columnName);
                        xuiCell.addWidget(colDisplay);
                    }
                }
            } else {

            }
        }
    }
}
