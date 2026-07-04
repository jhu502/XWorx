package com.thing.processor;

import com.thing.entity.XServiceDefinition;
import com.thing.ThingPerformHelper;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;

import java.util.Base64;

public class SaveServiceDefProcessor extends DefaultFormProcessor {
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        XObject xobj = commandBean.getPrimaryObj();
        String code = commandBean.getTextParameter("code");
        if (code != null && xobj instanceof XServiceDefinition) {
            XServiceDefinition serviceDef = (XServiceDefinition) xobj;
            String _code = new String(Base64.getDecoder().decode(code));
            serviceDef.setCode(_code);
            serviceDef = PersistenceHelper.service().save(serviceDef);

            /**
             * Service Definition保存后，需要刷新生成的ThingEntity
             */
            ThingPerformHelper.service().loadServiceDefinition(serviceDef);
            serviceDef = PersistenceHelper.service().refresh(serviceDef);

            formResult.setData(serviceDef);
        }
        return formResult;
    }
}
