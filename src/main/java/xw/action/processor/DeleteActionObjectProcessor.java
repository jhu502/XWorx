package xw.action.processor;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XPersistable;

import xw.action.entity.XActionItem;
import xw.action.entity.XActionItemLink;
import xw.action.entity.XActionModel;
import xw.action.service.XActionServiceHelper;

public class DeleteActionObjectProcessor extends DefaultFormProcessor {
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        List<Object> rowObjs = commandBean.getRowObjects();

        List<XPersistable> persists = new ArrayList<>();
        for (Object rowObject : rowObjs) {
            if (rowObject instanceof XActionModel) {
                XActionModel actionModel = (XActionModel) rowObject;
                List<Object[]> upperList = XActionServiceHelper.repository().findUsedByModelAndLink(actionModel);
                for (Object[] objects : upperList) {
                    XActionItemLink link = (XActionItemLink) objects[1];
                    persists.add(link);
                }
                List<Object[]> belowListA = XActionServiceHelper.repository().getXActionModelAndLink(actionModel);
                for (Object[] objects : belowListA) {
                    XActionItemLink link = (XActionItemLink) objects[1];
                    persists.add(link);
                }
                List<Object[]> belowListB = XActionServiceHelper.repository().getXActionItemAndLink(actionModel);
                for (Object[] objects : belowListB) {
                    XActionItemLink link = (XActionItemLink) objects[1];
                    persists.add(link);
                }
                persists.add(actionModel);
            } else if (rowObject instanceof XActionItem) {
                XActionItem actionItem = (XActionItem) rowObject;
                List<Object[]> upperList = XActionServiceHelper.repository().findUsedByModelAndLink(actionItem);
                for (Object[] objects : upperList) {
                    XActionModel model = (XActionModel) objects[0];
                    XActionItemLink link = (XActionItemLink) objects[1];
                    System.out.println(model.getActionKey());
                }
                persists.add(actionItem);
            }
        }
        if (!persists.isEmpty()) {
            PersistenceHelper.service().remove(persists);
        }

        return formResult;
    }

}
