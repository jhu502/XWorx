package com.flame.localize;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.orm.XPersistable;
import com.flame.util.FlameUtils;

import java.util.Map;

public class LocalizationProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        XObject primary = commandBean.getPrimaryObj();

        if (primary instanceof ILocalization) {
            boolean updateFlag = false;
            ILocalization localization = (ILocalization) primary;
            Map<String, Object> paramMap = commandBean.getParameterMap();
            if (paramMap.containsKey("display")) {
                String display = commandBean.getTextParameter("display");
                display = display == null ? "" : display;
                if (!FlameUtils.equals(display, localization.getDisplay())) {
                    updateFlag = true;
                    localization.setDisplay(display);
                }
            }
            if (paramMap.containsKey("en_US")) {
                String en_US = commandBean.getTextParameter("en_US");
                en_US = en_US == null ? "" : en_US;
                if (!FlameUtils.equals(en_US, localization.getEn_US())) {
                    updateFlag = true;
                    localization.setEn_US(en_US);
                }
            }
            if (paramMap.containsKey("zh_CN")) {
                String zh_CN = commandBean.getTextParameter("zh_CN");
                zh_CN = zh_CN == null ? "" : zh_CN;
                if (!FlameUtils.equals(zh_CN, localization.getZh_CN())) {
                    updateFlag = true;
                    localization.setZh_CN(zh_CN);
                }
            }
            if (updateFlag) {
                if (localization instanceof XPersistable) {
                    PersistenceHelper.service().save((XPersistable) localization);
                }
            }
        }

        return formResult;
    }
}
