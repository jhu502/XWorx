package xw.flow.processor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flame.orm.AbstractEntity;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.orm.XPersistable;
import com.flame.thing.IModeledEntity;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.util.FlameUtils;

import xw.vc.VersionControlled;

/**
 * 通用对象搜索处理器 —— 根据对象类型和关键字进行模糊搜索。
 *
 * <p>接收前端传入的 objectType（对象类型全限定类名）和 searchKey（搜索关键字）参数，
 * 根据对象类型动态构建 JPQL 查询，对实体编号/名称进行 LIKE 模糊匹配，
 * 返回包含 oid / number / name / display / icon 的 Map 列表，供前端 datagrid 渲染。</p>
 *
 * <p>支持所有 ThingModel 中注册的实体类型，根据实体是否继承 {@link VersionControlled}
 * 自动选择对应的 JPQL 查询路径（{@code master.number/name} 或 {@code number/name}）。</p>
 */
public class QueryObjectProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        String objectType = commandBean.getTextParameter("objectType");
        String searchKey = commandBean.getTextParameter("searchKey");

        if (FlameUtils.isBlank(objectType)) {
            formResult.setMessage("请选择对象类型");
            return formResult;
        }
        if (FlameUtils.isBlank(searchKey)) {
            formResult.setMessage("请输入搜索关键字");
            return formResult;
        }

        try {
            List<Map<String, String>> resultData = searchByEntityType(objectType, searchKey);
            formResult.setData(resultData);
        } catch (Exception e) {
            logger.error("搜索失败: objectType={}, searchKey={}", objectType, searchKey, e);
            formResult.setMessage("搜索失败: " + e.getMessage());
        }

        return formResult;
    }

    /**
     * 根据实体类型和关键字进行模糊搜索。
     *
     * <p>通过 {@link Class#forName(String)} 加载实体类，根据实体是否继承
     * {@link VersionControlled} 选择相应的 JPQL 查询路径，
     * 使用 {@link PersistenceHelper} 执行查询并提取通用字段。</p>
     *
     * @param objectType 实体类型全限定类名
     * @param searchKey  搜索关键字
     * @return 搜索结果行列表
     */
    private List<Map<String, String>> searchByEntityType(String objectType, String searchKey) throws ClassNotFoundException {
        Class<?> entityClass = Class.forName(objectType);
        String simpleName = entityClass.getSimpleName();
        String keyword = "%" + searchKey.trim() + "%";

        String jpql;
        if (VersionControlled.class.isAssignableFrom(entityClass)) {
            jpql = "select e from " + simpleName + " e where e.master.number like :keyword or e.master.name like :keyword";
        } else {
            jpql = "select e from " + simpleName + " e where e.number like :keyword or e.name like :keyword";
        }

        List<?> results = PersistenceHelper.service().query(jpql, new Object[][]{{"keyword", keyword}});

        List<Map<String, String>> resultData = new ArrayList<>();
        for (Object obj : results) {
            Map<String, String> row = new HashMap<>();
            if (obj instanceof XPersistable persist) {
                row.put("oid", persist.getOid());
            }
            if (obj instanceof AbstractEntity ae) {
                row.put("number", emptyIfNull(ae.getNumber()));
                row.put("name", emptyIfNull(ae.getName()));
                row.put("display", emptyIfNull(ae.getDisplay()));
            }
            if (obj instanceof IModeledEntity me) {
                row.put("icon", emptyIfNull(me.getIcon()));
            } else {
                row.put("icon", "");
            }
            if (obj instanceof VersionControlled<?> vc) {
                row.put("version", emptyIfNull(vc.getVersion()));
            } else {
                row.put("version", "");
            }
            if (obj instanceof XObject xo) {
                Timestamp createdStamp = (Timestamp) xo.getCreatedStamp();
                Timestamp modifiedStamp = (Timestamp) xo.getModifiedStamp();
                row.put("createdStamp", createdStamp != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdStamp) : "");
                row.put("modifiedStamp", modifiedStamp != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(modifiedStamp) : "");
            }
            resultData.add(row);
        }

        return resultData;
    }

    private static String emptyIfNull(String value) {
        return value != null ? value : "";
    }
}
