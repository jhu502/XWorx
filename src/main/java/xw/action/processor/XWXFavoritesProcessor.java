package xw.action.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.flame.action.ActionKey;
import com.flame.annotations.XDefinition;
import com.flame.auths.IUser;
import com.flame.auths.SessionHelper;
import com.flame.xui.XCommandBean;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.orm.XObject;
import com.flame.thing.IModelManaged;
import com.flame.util.FlameUtils;
import com.flame.xui.WinType;
import com.flame.xui.XUIAction;
import com.thing.entity.XThingModel;

import xw.action.entity.XActionFavorite;
import xw.action.service.XActionServiceHelper;
import xw.auths.entity.XUser;

public class XWXFavoritesProcessor extends XWXComposerProcessor {
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        ActionKey actionKey = commandBean.getActionKey();

        Collection<XUIAction> result = (Collection<XUIAction>) formResult.getData();
        Collection<XUIAction> favorites = new ArrayList<>(result);
        IUser user = SessionHelper.getCurrentUser();
        if (user instanceof XUser) {
            List<XActionFavorite> favoriteList = XActionServiceHelper.repository().listXFavoriteObjects((XUser) user);
            for (XActionFavorite favorite : favoriteList) {
                XObject favoriteObj = favorite.getFavoriteObject();
                XUIAction actionBean = new XUIAction();
                if (favoriteObj instanceof XThingModel) {
                    XThingModel thingModel = (XThingModel) favoriteObj;
                    String pageUri = thingModel.getPageUri();
                    if (FlameUtils.isBlank(pageUri)) {
                        pageUri = "#XUI$/info/InfoPage";
                    }
                    actionBean.setKey(favorite.getOid());
                    actionBean.setIcon(thingModel.getIcon());
                    actionBean.setUrl(pageUri + "?oid=" + favoriteObj.getOid());
                    actionBean.setDisplay("XThingModel-" + favoriteObj.getDisplay());
                    actionBean.setWinType(WinType.page);
                    actionBean.setClose(true);
                    actionBean.setSource(actionKey.toString());
                    favorites.add(actionBean);
                } else if (favoriteObj instanceof IModelManaged) {
                    IModelManaged typeManaged = (IModelManaged) favoriteObj;
                    XThingModel thingModel = (XThingModel) typeManaged.getThingModel();
                    String pageUri = thingModel.getPageUri();
                    if (FlameUtils.isBlank(pageUri)) {
                        pageUri = "#XUI$/info/InfoPage";
                    }
                    actionBean.setKey(favorite.getOid());
                    actionBean.setIcon(thingModel.getIcon());
                    actionBean.setUrl(pageUri + "?oid=" + favoriteObj.getOid());
                    actionBean.setDisplay(favoriteObj.getDisplay());
                    actionBean.setWinType(WinType.page);
                    actionBean.setClose(true);
                    actionBean.setSource(actionKey.toString());
                    favorites.add(actionBean);
                } else {
                    XDefinition xDefinition = XActionServiceHelper.getXDefinition(favoriteObj);
                    if (xDefinition != null) {
                        String pageUri = xDefinition.pageUri();
                        if (FlameUtils.isBlank(pageUri)) {
                            pageUri = "#XUI$/info/InfoPage";
                        }
                        actionBean.setKey(favorite.getOid());
                        actionBean.setIcon(xDefinition.icon());
                        actionBean.setUrl(pageUri + "?oid=" + favoriteObj.getOid());
                        actionBean.setDisplay(xDefinition.en_US() + ":" + favoriteObj.getDisplay());
                        actionBean.setWinType(WinType.page);
                        actionBean.setClose(true);
                        actionBean.setSource(actionKey.toString());
                        favorites.add(actionBean);
                    }
                }
            }
        }

        formResult.setData(favorites);
        formResult.setStatus(FormStatus.SUCCESS);
        return formResult;
    }
}
