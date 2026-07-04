package com.thing.processor;

import java.util.ArrayList;
import java.util.List;

import com.flame.auths.SessionHelper;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.orm.XObject;
import com.flame.thing.IPropertyLayout;
import com.flame.thing.IThingModel;
import com.flame.thing.ThingModelHelper;
import com.flame.xui.WinType;
import com.flame.xui.XUIAction;
import com.thing.entity.XThingModel;

public class GetLayoutMenuProcessor extends DefaultFormProcessor {
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        XObject primary = commandBean.getPrimaryObj();

        List<XUIAction> result = new ArrayList<>();
        if (primary instanceof XThingModel) {
            IThingModel thingModel = (XThingModel) primary;
            List<IPropertyLayout> layouts = ThingModelHelper.manager().getPropertyLayout(thingModel);
            for (IPropertyLayout layout : layouts) {
                XUIAction xAction = new XUIAction();
                xAction.setId(layout.getOid());
                xAction.setName(layout.getName());
                SessionHelper.getLocale();
                xAction.setDisplay(layout.getDisplay(SessionHelper.getLocale()));
                xAction.setIcon("images/layout.png");
                xAction.setOnclick("openModelLayout('" + layout.getOid() + "', '" + layout.getName() + "')");
                xAction.setWinType(WinType.script);

                result.add(xAction);
            }
        }
        formResult.setData(result);
        formResult.setStatus(FormStatus.SUCCESS);

        return formResult;
    }
}
