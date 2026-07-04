package com.thing.processor;

import java.util.List;
import java.util.Map;

import com.flame.util.JsonUtils;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.thing.IPropertyLayout;
import com.flame.thing.ThingModelHelper;
import com.thing.bean.MeshLayoutBean;
import com.thing.entity.MeshLayout;
import com.thing.entity.XPropertyLayout;
import com.thing.entity.XThingModel;

public class SavePropertyLayoutProcessor extends DefaultFormProcessor {
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        Map<String, Object> jsonMap = (Map<String, Object>) commandBean.getParameter("json");
        XObject primary = commandBean.getPrimaryObj();

        MeshLayoutBean layoutBean = JsonUtils.toObject(JsonUtils.toJson(jsonMap), MeshLayoutBean.class);

        if (primary instanceof XThingModel thingModel) {
            List<IPropertyLayout> layoutList = ThingModelHelper.manager().getPropertyLayout(thingModel);
            XPropertyLayout propertyLayout;
            if (layoutList.isEmpty()) {
                propertyLayout = new XPropertyLayout();
                propertyLayout.setPropertyProvider(thingModel);
            } else {
                propertyLayout = (XPropertyLayout) layoutList.get(0);
            }
            // 将 layoutBean 中的值写入 propertyLayout
            layoutBean.convert2XPropertyLayout(propertyLayout);
            propertyLayout = PersistenceHelper.service().save(propertyLayout);
            formResult.setStatus(FormStatus.SUCCESS);
            formResult.setData(propertyLayout);
        } else if (primary instanceof XPropertyLayout propertyLayout) {
            layoutBean.convert2XPropertyLayout(propertyLayout);
            propertyLayout = PersistenceHelper.service().save(propertyLayout);
            formResult.setStatus(FormStatus.SUCCESS);
            formResult.setData(propertyLayout);
        }

        return formResult;
    }
}
