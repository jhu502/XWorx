package xw.action.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import xw.action.entity.XActionFavorite;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;

public class RemoveFavoritesProcessor extends DefaultFormProcessor {
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        XObject xObject = commandBean.getPrimaryObj();

        if (xObject instanceof XActionFavorite) {
            XActionFavorite userFavorites = (XActionFavorite) xObject;
            PersistenceHelper.service().remove(userFavorites);
            formResult.setData("Remove from favorites successfully.");
        }
        formResult.setStatus(FormStatus.SUCCESS);

        return formResult;
    }
}
