package xw.team.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;

import xw.auths.entity.GroupTypeRB;
import xw.auths.entity.RoleRB;
import xw.auths.entity.XGroup;
import xw.context.entity.Container;
import xw.team.entity.XContainerRoleMap;
import xw.team.entity.XContainerTeam;

public class AddTeamRoleProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);

        Container container = (Container) commandBean.getPrimaryObj();
        XContainerTeam team = container.getTeam();
        if (team == null) {
            team = XContainerTeam.newInstance();
            team = PersistenceHelper.service().save(team);
            container.setTeam(team);
            PersistenceHelper.service().save(container);
        }

        Object roleNameObj = commandBean.getParameter("roleName");
        if (roleNameObj == null) {
            throw new XException("角色名称不能为空");
        }

        String[] roleNames;
        if (roleNameObj instanceof String[]) {
            roleNames = (String[]) roleNameObj;
        } else {
            roleNames = new String[] { (String) roleNameObj };
        }

        for (String roleName : roleNames) {
            if (roleName == null || roleName.trim().isEmpty()) {
                continue;
            }
            RoleRB role = RoleRB.toRoleRB(roleName.trim());
            if (role == null) {
                throw new XException("角色不存在:" + roleName);
            }

            XGroup group = new XGroup();
            group.setNumber(container.getNumber() + "-" + role.getName());
            group.setName(role.getName());
            group.setGroupType(GroupTypeRB.toGroupTypeRB("SYSTEM"));
            group.setAdminDomain(container.getAdminDomain());
            group = PersistenceHelper.service().save(group);

            XContainerRoleMap roleMap = XContainerRoleMap.newInstance(team, role, group);
            PersistenceHelper.service().save(roleMap);
        }

        formResult.setMessage("角色添加成功");
        return formResult;
    }
}
