package xw.team.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XPersistable;
import com.flame.util.FlameUtils;
import com.flame.util.JsonUtils;
import com.flame.util.XException;

import com.flame.xui.XUIRowId;
import xw.auths.entity.XGroup;
import xw.auths.entity.XGroupGroupLink;
import xw.auths.entity.XGroupUserLink;
import xw.auths.entity.XUser;
import xw.context.entity.Container;
import xw.team.entity.XContainerRoleMap;
import xw.team.entity.XContainerTeam;

public class AddTeamUserProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);

        Container container = (Container) commandBean.getPrimaryObj();
        XContainerTeam team = container.getTeam();
        if (team == null) {
            throw new XException("容器尚未初始化团队");
        }

        XUIRowId[] openerSelected = commandBean.getOpenerSelected();
        if (openerSelected == null || openerSelected.length == 0) {
            throw new XException("请先选中角色节点再添加参与者");
        }

        // 收集角色映射，过滤 null（用户可能选中了非角色行）
        List<XContainerRoleMap> containerRoleMaps = new ArrayList<>();
        for (XUIRowId openerRefId : openerSelected) {
            XContainerRoleMap roleMap = (XContainerRoleMap) openerRefId.getRowObject(0);
            if (roleMap != null) {
                containerRoleMaps.add(roleMap);
            }
        }
        if (containerRoleMaps.isEmpty()) {
            throw new XException("未找到对应的角色映射");
        }

        // 统一获取选中的用户/组 OID 列表，避免在外层循环中重复调用
        List<String> selectedOids = getSelectedOids(commandBean);
        if (selectedOids.isEmpty()) {
            throw new XException("未选择用户");
        }

        for (XContainerRoleMap containerRoleMap : containerRoleMaps) {
            XGroup roleGroup = containerRoleMap.getGroup();
            if (roleGroup == null) {
                throw new XException("角色未关联组");
            }

            for (String oid : selectedOids) {
                XPersistable obj = PersistenceHelper.service().find(oid);
                if (obj instanceof XUser user) {
                    XGroupUserLink link = XGroupUserLink.newGroupUserLink(roleGroup, user);
                    PersistenceHelper.service().save(link);
                } else if (obj instanceof XGroup subGroup) {
                    XGroupGroupLink link = XGroupGroupLink.newGroupGroupLink(roleGroup, subGroup);
                    PersistenceHelper.service().save(link);
                }
            }
        }

        formResult.setMessage("参与者添加成功");
        return formResult;
    }

    /**
     * 从请求参数中获取选中的用户/组 OID 列表。
     * 优先从 {@code selectedOids} 参数读取（popup 右侧面板已选列表的 JSON 数组），
     * 回退到 {@code commandBean.getRowIds()}（勾选行），兼容直接调用场景。
     *
     * @param commandBean 请求命令对象
     * @return 选中的 OID 列表，不会为 null
     */
    private List<String> getSelectedOids(XCommandBean commandBean) {
        List<String> oidList = new ArrayList<>();

        // 优先从 selectedOids 参数获取（popup 右侧面板已选列表，JSON 数组格式）
        String selectedOids = commandBean.getTextParameter("selectedOids");
        if (FlameUtils.isNotBlank(selectedOids)) {
            try {
                ArrayNode arrayNode = JsonUtils.convertT(selectedOids, ArrayNode.class);
                if (arrayNode != null) {
                    Iterator<JsonNode> it = arrayNode.elements();
                    while (it.hasNext()) {
                        oidList.add(it.next().asText());
                    }
                }
            } catch (Exception e) {
                logger.debug("解析 selectedOids JSON 失败: {}", e.getMessage());
            }
        }

        // 回退：从 rowIds 获取（checkbox 勾选的行）
        if (oidList.isEmpty()) {
            XUIRowId[] rowIds = commandBean.getRowIds();
            if (rowIds != null) {
                for (XUIRowId uiRowId : rowIds) {
                    String oid = uiRowId.getObjectId();
                    if (FlameUtils.isNotBlank(oid)) {
                        oidList.add(oid);
                    }
                }
            }
        }

        return oidList;
    }
}
