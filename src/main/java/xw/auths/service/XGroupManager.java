package xw.auths.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;

import jakarta.annotation.Resource;
import xw.auths.entity.*;
import xw.auths.repos.XAuthRepository;

@Service
public class XGroupManager {
    protected static final Logger logger = LoggerFactory.getLogger(XGroupManager.class);
    @Resource
    private XAuthRepository repository;

    public List<Object> getMember(String oid) {
        if (oid == null)
            throw new XException("参数:oid是空");

        List<Object> result = new ArrayList<Object>();
        List<?> groupList = repository.getGroupMember(ObjectReference.newObjectReference(oid));
        List<?> userList = repository.getUserMember(ObjectReference.newObjectReference(oid));
        result.addAll(groupList);
        result.addAll(userList);
        return result;
    }

    public List<Object> getMember(XGroup group) {
        if (group == null)
            throw new XException("参数:group是空");

        List<Object> result = new ArrayList<Object>();
        List<?> groupList = repository.getGroupMember(group);
        List<?> userList = repository.getUserMember(group);
        result.addAll(groupList);
        result.addAll(userList);
        return result;
    }

    public Set<XGroup> listParentGroup(String oid) {
        Set<XGroup> result = new HashSet<XGroup>();
        if (oid == null || "".equals(oid.trim())) {
            return result;
        }

        if (oid.indexOf("XUser") > 0) {
            List<XGroup> list = repository.getUserParentGroup(ObjectReference.newObjectReference(oid));
            result.addAll(list);
        } else if (oid.indexOf("XGroup") > 0) {
            List<XGroup> list = repository.getGroupParentGroup(ObjectReference.newObjectReference(oid));
            result.addAll(list);
        }
        return result;
    }

    /**
     * 从组中删除子组或用户（同时删除成员实体本身）。
     *
     * <h4>与 {@link #removeMember(XGroup, List)} 的区别</h4>
     * <ul>
     *   <li>{@code removeGroup}：删除链接关系 <b>并删除</b> 子组实体。若子组下还有成员则拒绝删除。</li>
     *   <li>{@code removeMember}：<b>仅删除</b> 链接关系，不删除成员实体（用户或子组）。</li>
     * </ul>
     *
     * @param pid  父组 OID
     * @param oids 待删除的子组/用户 OID 列表
     */
    @Transactional
    public void removeGroup(String pid, List<?> oids) {
        ObjectReference<XGroup> pidRef = new ObjectReference<XGroup>(pid);
        XGroup group = PersistenceHelper.service().refresh(pidRef);
        if (group == null) {
            throw new XException("对象不存在!");
        }

        for (Object _oid : oids) {
            String oid = (String) _oid;

            if (oid.contains("XGroup")) {
                List<Object> result = getMember(oid);
                if (!result.isEmpty())
                    throw new XException("存在下层对象，不允许删除!");

                ObjectReference<XGroup> oidRef = new ObjectReference<XGroup>(oid);
                List<?> list = PersistenceHelper.service().query("select a from XGroupGroupLink a where a.left.id = :pid and a.right.id = :oid", new Object[][] { { "pid", pidRef.getId() }, { "oid", oidRef.getId() } });
                for (Object obj : list) {
                    PersistenceHelper.service().remove((XGroupGroupLink) obj);
                }
                XGroup _group = PersistenceHelper.service().refresh(oidRef);
                if (_group != null) {
                    PersistenceHelper.service().remove(_group);
                }
            } else if (oid.contains("XUser")) {
                ObjectReference<XGroup> oidRef = new ObjectReference<XGroup>(oid);
                List<?> list = PersistenceHelper.service().query("select a from XGroupUserLink a where a.left.id = :pid and a.right.id = :oid", new Object[][] { { "pid", pidRef.getId() }, { "oid", oidRef.getId() } });
                for (Object obj : list) {
                    PersistenceHelper.service().remove((XGroupUserLink) obj);
                }
            }

        }

    }

    /**
     * 删除指定的组，先清理所有关联的 {@link XGroupGroupLink} 和 {@link XGroupUserLink}，
     * 再删除 {@link XGroup} 实体本身。
     *
     * @param group 待删除的组实体
     * @throws XException 若 group 为空
     */
    @Transactional
    public void removeGroup(XGroup group) {
        if (group == null)
            throw new XException("参数:group是空");

        // 删除该组作为父组的所有子组链接
        List<XGroupGroupLink> childLinks = repository.findGroupGroupLinksByParent(group);
        for (XGroupGroupLink link : childLinks) {
            PersistenceHelper.service().remove(link);
        }

        // 删除该组作为子组的所有父组链接
        List<XGroupGroupLink> parentLinks = repository.findGroupGroupLinksByChild(group);
        for (XGroupGroupLink link : parentLinks) {
            PersistenceHelper.service().remove(link);
        }

        // 删除该组下的所有用户链接
        List<XGroupUserLink> userLinks = repository.findGroupUserLinks(group);
        for (XGroupUserLink link : userLinks) {
            PersistenceHelper.service().remove(link);
        }

        // 删除组实体本身
        PersistenceHelper.service().remove(group);
    }

    /**
     * 从组中移除成员（仅删除链接关系，不删除成员实体本身）。
     *
     * <h4>核心逻辑</h4>
     * <ol>
     *   <li>校验父组是否为空</li>
     *   <li>遍历待移除的成员列表</li>
     *   <li>若成员为 {@link XGroup}（子组）→ 删除对应的 {@link XGroupGroupLink} 链接</li>
     *   <li>若成员为 {@link XUser}（用户）→ 删除对应的 {@link XGroupUserLink} 链接</li>
     * </ol>
     *
     * <h4>与 {@link #removeGroup(String, List)} 的区别</h4>
     * <p>本方法仅解除组与成员之间的关联关系，不会删除任何成员实体。
     * 适用于"将用户/子组从当前组中踢出但不删除其账号/子组"的场景。</p>
     *
     * <h4>示例</h4>
     * <pre>
     *     // 从组 A 中移除用户 U1 和子组 G2（用户和子组本身保留）
     *     removeMember(groupA, List.of(user1, group2));
     * </pre>
     *
     * @param group   父组实体
     * @param members 待移除的成员列表（{@link XUser} 或 {@link XGroup} 实例）
     * @throws XException 若 group 为空
     */
    @Transactional
    public void removeMember(XGroup group, List<XPrincipal> members) {
        if (group == null)
            throw new XException("参数:group是空");

        for (XPrincipal member : members) {
            if (member instanceof XGroup) {
                XGroup childGroup = (XGroup) member;
                List<?> list = PersistenceHelper.service().query("select a from XGroupGroupLink a where a.left.id = :pid and a.right.id = :oid", new Object[][] { { "pid", group.getXid() }, { "oid", childGroup.getXid() } });
                for (Object obj : list) {
                    PersistenceHelper.service().remove((XGroupGroupLink) obj);
                }
            } else if (member instanceof XUser) {
                XUser user = (XUser) member;
                List<?> list = PersistenceHelper.service().query("select a from XGroupUserLink a where a.left.id = :pid and a.right.id = :oid", new Object[][] { { "pid", group.getXid() }, { "oid", user.getXid() } });
                for (Object obj : list) {
                    PersistenceHelper.service().remove((XGroupUserLink) obj);
                }
            }
        }
    }
}
