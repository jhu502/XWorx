package xw.auths.loader;

import com.flame.loader.AbstractDataLoader;
import com.flame.loader.FlameDataLoad;
import com.flame.orm.PersistenceHelper;

import xw.auths.entity.RoleRB;
import xw.auths.entity.RoleRB.RoleType;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * RoleRB数据加载器 - 加载角色枚举数据
 *
 * <p>
 * 专门处理RoleRB实体的数据加载，支持roleType和responsibility等扩展字段。
 * </p>
 *
 * @see RoleRB
 * @see RoleType
 */
public class RoleRBLoader extends AbstractDataLoader {

    @Override
    public void executeLoad(FlameDataLoad dataLoad) throws Exception {
        for (FlameDataLoad.LoadObject data : dataLoad.getData()) {
            List<?> list = this.queryObject(data);
            RoleRB roleRB = null;

            if (!list.isEmpty()) {
                roleRB = (RoleRB) list.get(0);
            } else {
                Constructor<?> constructor = data.getClazz().getConstructor();
                roleRB = (RoleRB) constructor.newInstance();
            }

            if (roleRB == null) {
                continue;
            }

            // 设置基础枚举字段
            roleRB.setName(data.getAttribute("name"));
            roleRB.setDisplay(data.getAttribute("display"));
            roleRB.setEn_US(data.getAttribute("en_US"));
            roleRB.setZh_CN(data.getAttribute("zh_CN"));
            roleRB.setDescription(data.getAttribute("description"));

            // 设置roleType字段
            String roleTypeStr = data.getAttribute("roleType");
            if (roleTypeStr != null && !roleTypeStr.isEmpty()) {
                RoleType roleType = parseRoleType(roleTypeStr);
                if (roleType != null) {
                    roleRB.setRoleType(roleType);
                }
            }

            // 设置responsibility字段
            String responsibility = data.getAttribute("responsibility");
            if (responsibility != null && !responsibility.isEmpty()) {
                responsibility = responsibility.replace("            ", "");
                roleRB.setResponsibility(responsibility);
            }

            PersistenceHelper.service().save(roleRB);
        }
    }

    /**
     * 解析角色类型字符串
     *
     * @param roleTypeStr 角色类型字符串
     * @return RoleType枚举值，若无法解析则返回null
     */
    private RoleType parseRoleType(String roleTypeStr) {
        try {
            return RoleType.valueOf(roleTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 尝试按名称匹配
            for (RoleType type : RoleType.values()) {
                if (type.getName().equalsIgnoreCase(roleTypeStr)) {
                    return type;
                }
            }
            return null;
        }
    }
}