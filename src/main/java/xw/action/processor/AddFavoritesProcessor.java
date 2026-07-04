package xw.action.processor;

import com.flame.auths.SessionHelper;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;

import xw.action.entity.XActionFavorite;
import xw.action.service.XActionServiceHelper;
import xw.auths.entity.XUser;

public class AddFavoritesProcessor extends DefaultFormProcessor {
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        XObject xObject = commandBean.getPrimaryObj();

        if (xObject instanceof XActionFavorite) {
        } else if (xObject instanceof XObject) {
            XUser xUser = (XUser) SessionHelper.getCurrentUser();
            XActionFavorite userFavorites = XActionServiceHelper.repository().getXFavoriteObject(xObject, xUser);
            if (userFavorites == null) {
                userFavorites = XActionFavorite.newActionFavorite(xObject);
                PersistenceHelper.service().save(userFavorites);
                formResult.setData("Add into favorites successfully.");
            }
        }
        formResult.setStatus(FormStatus.SUCCESS);

        return formResult;
    }
}
