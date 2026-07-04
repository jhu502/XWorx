package xw.team.processor;

import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.util.XException;

import xw.auths.XGroupHelper;
import xw.auths.entity.XPrincipal;
import xw.team.entity.XContainerRoleMap;

public class RemoveTeamUserProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);

        // 从组合 rowId 中提取 roleMapOid 和叶子实体 OID，直接定位到具体的角色映射，
        // 避免同一用户被添加到多个角色时，通过 parentOid 间接查找可能定位到错误的映射。
        XUIRowId[] uiRowIds = commandBean.getRowIds();
        if (uiRowIds == null || uiRowIds.length == 0) {
            throw new XException("未选中节点");
        }

        for (XUIRowId uiRowId : uiRowIds) {

            // 通过 rowId 获取待移除的用户实体
            Object rowObj = uiRowId.getRowObject();
            if (rowObj instanceof XPrincipal principal) {
                XContainerRoleMap roleMap = (XContainerRoleMap) uiRowId.getRoot().getRowObject(0);

                XGroupHelper.service().removeMember(roleMap.getGroup(), List.of(principal));
            }
        }

        formResult.setMessage("用户移除成功");
        return formResult;
    }
}
