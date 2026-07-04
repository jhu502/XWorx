package xw.team.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.localize.LocalizationHelper;

import xw.auths.entity.RoleRB;

public class QueryRoleProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);

        List<Map<String, String>> resultData = new ArrayList<>();
        RoleRB[] roles = RoleRB.getRoleRBSet(RoleRB.RoleType.ROLE);
        for (RoleRB role : roles) {
            Map<String, String> roleRow = new HashMap<>();
            roleRow.put("name", role.getName());
            roleRow.put("display", role.getDisplay(LocalizationHelper.getLocale()));
            resultData.add(roleRow);
        }

        formResult.setData(resultData);
        return formResult;
    }
}
