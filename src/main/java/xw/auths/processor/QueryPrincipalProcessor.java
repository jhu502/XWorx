package xw.auths.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;

import xw.auths.XGroupHelper;
import xw.auths.entity.XGroup;
import xw.auths.entity.XUser;

public class QueryPrincipalProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        String searchKey = commandBean.getTextParameter("searchKey");
        String principalType = commandBean.getTextParameter("principalType");

        List<Map<String, String>> resultData = new ArrayList<>();
        if ("all".equals(principalType)) {
            this.queryGroup(searchKey, resultData);
            this.queryUser(searchKey, resultData);
        } else if (XUser.class.getName().equals(principalType)) {
            this.queryUser(searchKey, resultData);
        } else if (XGroup.class.getName().equals(principalType)) {
            this.queryGroup(searchKey, resultData);
        }
        formResult.setData(resultData);

        return formResult;
    }

    private void queryUser(String searchKey, List<Map<String, String>> resultData) {
        List<XUser> userList = XGroupHelper.repository().findUserFuzzy(searchKey);
        for (XUser user : userList) {
            Map<String, String> userRow = new HashMap<>();
            userRow.put("oid", user.getOid());
            userRow.put("name", user.getUsername());
            userRow.put("display", user.getFullName() + " (" + user.getUsername() + ")");
            userRow.put("icon", user.getIcon());

            resultData.add(userRow);
        }
    }

    private void queryGroup(String searchKey, List<Map<String, String>> resultData) {
        List<XGroup> groupList = XGroupHelper.repository().findGroupFuzzy(searchKey);
        for (XGroup group : groupList) {
            Map<String, String> groupRow = new HashMap<>();
            groupRow.put("oid", group.getOid());
            groupRow.put("name", group.getName());
            groupRow.put("display", group.getFullName() + " (" + group.getName() + ")");
            groupRow.put("icon", group.getIcon());

            resultData.add(groupRow);
        }
    }
}
