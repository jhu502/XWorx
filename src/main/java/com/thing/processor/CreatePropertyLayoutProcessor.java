package com.thing.processor;

import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.localize.LocalizationHelper;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.thing.LayoutType;
import com.flame.xui.XCommandBean;
import com.thing.entity.XPropertyLayout;
import com.thing.entity.XThingModel;

import java.util.List;

public class CreatePropertyLayoutProcessor extends DefaultFormProcessor {
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        XObject primary = commandBean.getPrimaryObj();
        if (primary instanceof XThingModel thingModel) {
            String layoutType = commandBean.getTextParameter("layoutType");
            LayoutType type = LayoutType.valueOf(layoutType);

            List<XPropertyLayout> existing = PersistenceHelper.service().query(XPropertyLayout.class, new Object[][]{{"propertyProvider", thingModel}, {"layoutType", type}});
            if (!existing.isEmpty()) {
                String layoutTypeName = LocalizationHelper.get(layoutType);
                formResult.setMessage(LocalizationHelper.get("promptLayoutExists", layoutTypeName));
                formResult.setStatus(FormStatus.FAILURE);
                return formResult;
            }

            XPropertyLayout propertyLayout = new XPropertyLayout();
            propertyLayout.setPropertyProvider(thingModel);
            String layoutName = commandBean.getTextParameter("name");
            propertyLayout.setName(layoutName);
            propertyLayout.setDisplay(layoutName);
            propertyLayout.setEn_US(layoutName);
            propertyLayout.setZh_CN(layoutName);
            propertyLayout.setLayoutType(type);
            propertyLayout = PersistenceHelper.service().save(propertyLayout);

            formResult.setData(propertyLayout);
            formResult.setStatus(FormStatus.SUCCESS);
        }

        return formResult;
    }
}
