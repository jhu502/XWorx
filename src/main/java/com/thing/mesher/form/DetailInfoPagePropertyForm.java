package com.thing.mesher.form;

import com.flame.annotations.UIMeshGrid;
import com.flame.orm.XObject;
import com.flame.thing.*;
import com.flame.xui.WidgetMode;
import com.flame.xui.XCommandBean;
import com.flame.xui.XUIMeshGrid;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.thing.entity.MeshLayout;
import com.thing.entity.XPropertyLayout;

import java.util.List;

@UIMeshGrid()
public class DetailInfoPagePropertyForm extends AbstractMeshComponentBuilder {
    public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
        XUIMeshGrid xuiMeshGrid = super.buildComponentConfig(commandBean);
        xuiMeshGrid.setWidgetMode(WidgetMode.Display);
        XObject primaryObj = commandBean.getPrimaryObj();
        if (primaryObj instanceof IModelManaged modelManaged) {
            IThingModel thingModel = modelManaged.getThingModel();
            if (thingModel == null) {
                return xuiMeshGrid;
            }

            List<IPropertyLayout> layoutList = ThingModelHelper.manager().getPropertyLayout(thingModel, LayoutType.InfoPage_Primary);
            if (layoutList.isEmpty()) {
                return xuiMeshGrid;
            }

            IPropertyLayout propertyLayout = layoutList.get(0);
            if (propertyLayout instanceof XPropertyLayout) {
                MeshLayout layout = ((XPropertyLayout) propertyLayout).getLayout();
                if (layout != null) {
                    boolean first = true;
                    for (MeshLayout.MeshGrid mGrid : layout.getGrids()) {
                        XUIMeshGrid.XUIGrid xuiGrid = new XUIMeshGrid.XUIGrid(mGrid, thingModel, xuiMeshGrid.getWidgetMode());
                        if (first) {
                            xuiGrid.addDomClass("xui-fieldset");
                            xuiGrid.setBeforeHTML("""
                                    <div id="Thumbnail3DDiv" class="thumbnail">
                                        <!-- img style="width: 100%; height: 100%;" src="images/product_288.png" -->
                                    </div>
                                    """);
                            first = false;
                        }
                        xuiMeshGrid.addGrid(xuiGrid);
                    }
                }
            }
        }

        xuiMeshGrid.inflateObject(primaryObj);

        return xuiMeshGrid;
    }
}
