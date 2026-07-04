package com.thing.processor;

import java.util.List;

import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.orm.XObject;
import com.flame.thing.IPropertyLayout;
import com.flame.thing.ThingModelHelper;
import com.flame.xui.XCommandBean;
import com.thing.bean.MeshLayoutBean;
import com.thing.entity.XPropertyLayout;
import com.thing.entity.XThingModel;

public class GetPropertyLayoutProcessor extends DefaultFormProcessor {
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        XObject primary = commandBean.getPrimaryObj();
        if (primary instanceof XPropertyLayout propertyLayout) {
            formResult.setData(MeshLayoutBean.newInstance(propertyLayout));
            formResult.setStatus(FormStatus.SUCCESS);
        } else if (primary instanceof XThingModel thingModel) {
            List<IPropertyLayout> layoutList = ThingModelHelper.manager().getPropertyLayout(thingModel);
            if (!layoutList.isEmpty()) {
                MeshLayoutBean layoutBean = MeshLayoutBean.newInstance((XPropertyLayout) layoutList.get(0));
                formResult.setData(layoutBean);
                formResult.setStatus(FormStatus.SUCCESS);
            }
        }

        return formResult;
    }
}
