package xw.team.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.*;
import com.flame.localize.LocalizationHelper;
import com.flame.annotations.UIAction;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import com.flame.xui.service.TreeComponentNode;
import com.flame.xui.widget.ActionBox;
import com.flame.xui.widget.IconBox;
import com.flame.xui.widget.TextDisplay;

import xw.auths.XGroupHelper;
import xw.auths.entity.RoleRB;
import xw.auths.entity.XGroup;
import xw.auths.entity.XUser;
import xw.context.entity.Container;
import xw.team.TeamHelper;
import xw.team.entity.XContainerRoleMap;
import xw.team.entity.XContainerTeam;

@UITreeGrid(idField = "rowId", treeField = "identity", rowNumber = false, fit = true, //
        actions = { //
                @UIAction(name = "addRole", processor = "xw.team.processor.AddTeamRoleProcessor", url = "thymeleaf/team/addTeamRole.html", icon = "images/add16x16.gif", winType = WinType.popup, style = "width:450px;height:360px;padding:5px;"), //
                @UIAction(name = "removeRole", processor = "xw.team.processor.RemoveTeamRoleProcessor", icon = "images/remove16x16.gif", beforeJS = "return flame.validateSelect(p, false, '请选择角色节点');", winType = WinType.invoke), //
                @UIAction(name = "addUser", processor = "xw.team.processor.AddTeamUserProcessor", url = "thymeleaf/team/addTeamUser.html", icon = "images/add16x16.gif", beforeJS = "return flame.validateSelect(p, false, '请选择角色节点');", winType = WinType.popup, style = "width:680px;height:500px;padding:5px;"), //
                @UIAction(name = "removeUser", processor = "xw.team.processor.RemoveTeamUserProcessor", icon = "images/remove16x16.gif", beforeJS = "return flame.validateSelect(p, false, '请选择用户节点');", winType = WinType.invoke), //
        }, //
        columns = { //
                @UIColumn(field = "rowId", checkbox = true), //
                @UIColumn(field = "identity", width = "220"), //
                @UIColumn(field = "fullName", width = "150"), //
                @UIColumn(field = "action", width = "60", align = "center"), // 操作图标列
                @UIColumn(field = "nodeType", width = "80", align = "center"), //
                @UIColumn(field = "createdStamp", width = "140", align = "center"), //
        } //
)
public class ContainerTeamTreeBuilder extends AbstractTreeComponentBuilder {

    @Override
    public List<?> getRootNode(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();

        Container container = (Container) commandBean.getPrimaryObj();
        if (container == null) {
            return result;
        }

        XContainerTeam team = container.getTeam();
        if (team == null) {
            return result;
        }

        String compId = this.generateCompId(commandBean.getGridId());
        List<XContainerRoleMap> roleMaps = TeamHelper.repository().findRoleMapsByTeam(team);

        for (XContainerRoleMap roleMap : roleMaps) {
            RoleRB role = roleMap.getRole();
            XGroup group = roleMap.getGroup();

            TreeComponentNode roleNode = TreeComponentNode.newTreeComponentNode(role);
            String roleRowId = this.assemRowId(roleMap.getOid(), role.getOid());
            roleNode.setRowId(roleRowId);
            roleNode.addAttribute("identity", new ArrayComponent(new IconBox("images/role.gif"), new TextDisplay(role.getDisplay(LocalizationHelper.getLocale()))));
            roleNode.addAttribute("fullName", role.getDisplay(LocalizationHelper.getLocale()));
            roleNode.addAttribute("nodeType", LocalizationHelper.get("role"));
            roleNode.addAttribute("createdStamp", role.getCreatedStamp());
            roleNode.addAttribute("action", this.createRoleActions(roleRowId, compId));
            result.add(roleNode);

            if (group != null) {
                List<Object> members = XGroupHelper.service().getMember(group);
                for (Object object : members) {
                    if (object instanceof XGroup) {
                        XGroup subGroup = (XGroup) object;
                        String groupRowId = this.assemRowId(roleRowId, new String[] {subGroup.getOid()});

                        TreeComponentNode groupNode = TreeComponentNode.newTreeComponentNode(subGroup);
                        groupNode.setRowId(groupRowId);
                        groupNode.addAttribute("identity", new ArrayComponent(new IconBox(subGroup.getIcon()), new TextDisplay(subGroup.getName())));
                        groupNode.addAttribute("fullName", subGroup.getFullName());
                        groupNode.addAttribute("action", createPrincipalActions(groupRowId, compId));
                        groupNode.addAttribute("nodeType", LocalizationHelper.get("group"));
                        groupNode.addAttribute("createdStamp", subGroup.getCreatedStamp());
                        roleNode.addChildren(groupNode);
                    } else if (object instanceof XUser) {
                        XUser user = (XUser) object;
                        String userRowId = this.assemRowId(roleRowId, new String[] {user.getOid()});

                        TreeComponentNode userNode = TreeComponentNode.newTreeComponentNode(user);
                        userNode.setRowId(userRowId);
                        userNode.addAttribute("identity", new ArrayComponent(new IconBox(user.getIcon()), new TextDisplay(user.getName())));
                        userNode.addAttribute("fullName", user.getFullName());
                        userNode.addAttribute("nodeType", LocalizationHelper.get("user"));
                        userNode.addAttribute("createdStamp", user.getCreatedStamp());
                        userNode.addAttribute("action", createPrincipalActions(userRowId, compId));
                        userNode.setLeaf(true);
                        roleNode.addChildren(userNode);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 展开树节点时获取子节点列表。
     *
     * <p>rowId 为组合结构，格式为 {@code {roleMapOid}~{roleOid}} 或
     * {@code {roleMapOid}~{roleOid}^{entityOid}}，其中 {@code ~} 为同级对象分隔符，
     * {@code ^} 为层级分隔符。需先拆分提取当前节点的实体 OID 再查询子成员。</p>
     *
     * @param commandBean 请求命令对象，{@code getRowId()} 为被展开节点的组合 rowId
     * @return 子节点列表
     */
    @Override
    public List<?> getNode(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();
        XUIRowId uiRowId = commandBean.getRowId();

        if (uiRowId != null) {
            String compId = this.generateCompId(commandBean.getGridId());
            // 从组合 rowId 中提取叶子实体 OID：先按 ^ 取最后一级，再按 ~ 取最后一个对象
            List<Object> members = XGroupHelper.service().getMember(uiRowId.getObjectId());
            for (Object object : members) {
                if (object instanceof XGroup) {
                    XGroup group = (XGroup) object;
                    String groupRowId = this.assemRowId(uiRowId.getValue(), new String[] {group.getOid()});

                    TreeComponentNode groupNode = TreeComponentNode.newTreeComponentNode(group);
                    groupNode.setRowId(this.assemRowId(uiRowId.getValue(), new String[] {group.getOid()}));
                    groupNode.addAttribute("identity", new ArrayComponent(new IconBox(group.getIcon()), new TextDisplay(group.getName())));
                    groupNode.addAttribute("fullName", group.getFullName());
                    groupNode.addAttribute("action", createPrincipalActions(groupRowId, compId));
                    groupNode.addAttribute("nodeType", LocalizationHelper.get("group"));
                    groupNode.addAttribute("createdStamp", group.getCreatedStamp());
                    result.add(groupNode);
                } else if (object instanceof XUser) {
                    XUser user = (XUser) object;
                    String userRowId = this.assemRowId(uiRowId.getValue(), new String[] {user.getOid()});

                    TreeComponentNode userNode = TreeComponentNode.newTreeComponentNode(user);
                    userNode.setRowId(userRowId);
                    userNode.addAttribute("identity", new ArrayComponent(new IconBox(user.getIcon()), new TextDisplay(user.getName())));
                    userNode.addAttribute("fullName", user.getFullName());
                    userNode.addAttribute("action", createPrincipalActions(userRowId, compId));
                    userNode.addAttribute("nodeType", LocalizationHelper.get("user"));
                    userNode.addAttribute("createdStamp", user.getCreatedStamp());
                    userNode.setLeaf(true);
                    result.add(userNode);
                }
            }
        }

        return result;
    }

    /** 角色行 action 图标：添加用户 + 移除角色 */
    private ActionBox createRoleActions(String rowId, String compId) {
        XUIAction addUser = new XUIAction();
        addUser.setName("addUser");
        addUser.setIcon("images/add16x16.gif");
        addUser.setDisplay("添加用户");
        addUser.setProcessor("xw.team.processor.AddTeamUserProcessor");
        addUser.setUrl("thymeleaf/team/addTeamUser.html");
        addUser.setWinType(WinType.popup);
        addUser.setStyle("width:680px;height:500px;padding:5px;");

        XUIAction removeRole = new XUIAction();
        removeRole.setName("removeRole");
        removeRole.setIcon("images/remove16x16.gif");
        removeRole.setDisplay("移除角色");
        removeRole.setProcessor("xw.team.processor.RemoveTeamRoleProcessor");
        removeRole.setWinType(WinType.invoke);

        ActionBox actionBox = new ActionBox(rowId);
        actionBox.setCompId(compId);
        return actionBox.add(addUser).add(removeRole);
    }

    /** 用户行 action 图标：移除用户 */
    private ActionBox createPrincipalActions(String rowId, String compId) {
        XUIAction removeUser = new XUIAction();
        removeUser.setName("removeUser");
        removeUser.setIcon("images/remove16x16.gif");
        removeUser.setDisplay("移除");
        removeUser.setProcessor("xw.team.processor.RemoveTeamUserProcessor");
        removeUser.setWinType(WinType.invoke);

        ActionBox actionBox = new ActionBox(rowId);
        actionBox.setCompId(compId);
        return actionBox.add(removeUser);
    }
}
