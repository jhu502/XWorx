package xw.team.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;

import com.flame.xui.XUIRowId;
import xw.auths.XGroupHelper;
import xw.auths.entity.RoleRB;
import xw.auths.entity.XGroup;
import xw.context.entity.Container;
import xw.team.entity.XContainerRoleMap;
import xw.team.entity.XContainerTeam;

public class RemoveTeamRoleProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);

        Container container = (Container) commandBean.getPrimaryObj();
        XContainerTeam team = container.getTeam();
        if (team == null) {
            throw new XException("容器尚未初始化团队");
        }

        XUIRowId[] xuiRowIds = commandBean.getRowIds();
        if (xuiRowIds == null || xuiRowIds.length == 0) {
            throw new XException("未选中节点");
        }

        for (XUIRowId xuiRowId : xuiRowIds) {
            Object rowObj = xuiRowId.getRowObject();

            if (rowObj instanceof RoleRB role) {
                XContainerRoleMap containerRoleMap = (XContainerRoleMap) xuiRowId.getRowObject(0);
                XGroup group = containerRoleMap.getGroup();
                PersistenceHelper.service().remove(containerRoleMap);

                if (group != null) {
                    XGroupHelper.service().removeGroup(group);
                }
            }
        }

        formResult.setMessage("角色移除成功");
        return formResult;
    }
}
