package com.flame.xui;

import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.util.FlameUtils;

/**
 * Flame UI 组件中带有层级关系的结构化行标识符。
 *
 * <h3>值格式</h3>
 * <pre>
 *     [父级rowId]{@value #LEVEL_SEP}[当前rowId]
 *     rowId = [oid1]{@value #OBJECT_SEP}[oid2]{@value #OBJECT_SEP}...
 * </pre>
 *
 * <p>{@code >} 为层级分隔符，{@code ~} 为同行多对象分隔符。
 * 叶子节点不含 {@code >}，此时整个 value 即为当前行 rowId。</p>
 *
 * <h3>不可变性</h3>
 * <p>本类为值对象：通过 {@link #newInstance(String)} 构造后不可变，
 * 所有 getter 返回派生的新对象或基本类型。</p>
 *
 * @author Hujin
 * @see XCommandBean
 */
public class XUIRowId {

    /** 层级分隔符，分隔父级行标识与当前行标识。 */
    public static final String LEVEL_SEP = ">";

    /** 同行多对象 ID 分隔符。 */
    public static final String OBJECT_SEP = "~";

    /** 完整的行标识字符串，编码层级关系和同行多对象信息。构造后不可变。 */
    private String value;

    /**
     * 以指定值创建新实例（唯一构造入口）。
     *
     * @param value 行标识字符串，可为 null 或空（此时各 getter 返回安全的默认值）。
     * @return 新的 XUIRowId 实例。
     */
    public static XUIRowId newInstance(String value) {
        XUIRowId uiRowId = new XUIRowId();
        uiRowId.value = value;
        return uiRowId;
    }

    /** @return 完整的行标识字符串，可能为 null。 */
    public String getValue() {
        return this.value;
    }

    /**
     * 当前行末尾对象的 ID。
     *
     * <p>先通过 {@link #getRowId()} 剥离父级前缀，再取最后一个 {@value #OBJECT_SEP} 之后的部分。
     * 无分隔符时返回整个 rowId。</p>
     *
     * @return 末尾对象 ID，rowId 为空时返回空字符串。
     */
    public String getObjectId() {
        String rowId = this.getRowId();
        if (FlameUtils.isBlank(rowId))
            return "";

        String[] objectIds = rowId.split(OBJECT_SEP);
        return objectIds[objectIds.length - 1];
    }

    /**
     * 当前行指定位置的对象 ID。
     *
     * @param i 对象索引（0-based），超出范围返回空字符串。
     * @return 第 i 个对象 ID。
     */
    public String getObjectId(int i) {
        String rowId = this.getRowId();
        if (FlameUtils.isBlank(rowId))
            return "";

        String[] objectIds = rowId.split(OBJECT_SEP);
        if (i >= objectIds.length - 1) {
            return "";
        }

        return objectIds[i];
    }

    /**
     * 当前行末尾对象的 {@link ObjectReference}。
     *
     * @return 若末尾对象 ID 为合法 OID 则返回对应引用，否则返回 null。
     */
    public ObjectReference<?> getObjectRef() {
        String objectId = this.getObjectId();
        if (ObjectReference.isOid(objectId)) {
            return ObjectReference.newObjectReference(objectId);
        } else {
            return null;
        }
    }

    /**
     * 当前行指定位置对象的 {@link ObjectReference}。
     *
     * @param i 对象索引（0-based）。
     * @return 若对象 ID 为合法 OID 则返回对应引用，否则返回 null。
     */
    public ObjectReference<?> getObjectRef(int i) {
        String objectId = this.getObjectId(i);
        if (FlameUtils.isBlank(objectId))
            return null;

        if (ObjectReference.isOid(objectId)) {
            return ObjectReference.newObjectReference(objectId);
        } else {
            return null;
        }
    }

    /**
     * 从数据库加载当前行末尾对象的最新实例。
     *
     * @return 持久化实体，若 OID 无效或对象不存在则返回 null。
     */
    public Object getRowObject() {
        ObjectReference<?> objectRef = this.getObjectRef();
        if (objectRef == null) {
            return null;
        } else {
            return PersistenceHelper.service().refresh(objectRef);
        }
    }

    /**
     * 从数据库加载当前行指定位置对象的最新实例。
     *
     * @param i 对象索引（0-based）。
     * @return 持久化实体，若 OID 无效或对象不存在则返回 null。
     */
    public Object getRowObject(int i) {
        ObjectReference<?> objectRef = this.getObjectRef(i);
        if (objectRef == null) {
            return null;
        } else {
            return PersistenceHelper.service().refresh(objectRef);
        }
    }

    /**
     * 当前行所有对象的 ID 数组。
     *
     * <p>先通过 {@link #getRowId()} 剥离父级前缀，再以 {@value #OBJECT_SEP} 拆分。</p>
     *
     * @return 对象 ID 数组，rowId 为空时返回空数组（绝不返回 null）。
     */
    public String[] getObjectIds() {
        String rowId = this.getRowId();
        if (FlameUtils.isBlank(rowId))
            return new String[0];

        return rowId.split(OBJECT_SEP);
    }

    /**
     * 父级行标识。
     *
     * <p>在 value 中查找第一个 {@value #LEVEL_SEP}，提取之前的部分作为父级 rowId。
     * 若 value 中无层级分隔符（已是顶层）则返回 null。</p>
     *
     * @return 父级的 XUIRowId，顶层行返回 null。
     */
    public XUIRowId getParent() {
        if (FlameUtils.isBlank(value))
            return null;

        int index = this.value.lastIndexOf(LEVEL_SEP);
        if (index > 0) {
            String pRowId = this.value.substring(0, index);
            if (FlameUtils.isBlank(pRowId))
                return null;
            else
                return XUIRowId.newInstance(pRowId);
        } else {
            return null;
        }
    }

    /**
     * 根级行标识。
     *
     * <p>在 value 中查找第一个 {@value #LEVEL_SEP}，返回最顶层的父级。
     * 与 {@link #getParent()} 的区别：仅取第一个 {@code >} 之前的部分。</p>
     *
     * @return 根级的 XUIRowId，顶层行返回 null。
     */
    public XUIRowId getRoot() {
        if (FlameUtils.isBlank(value))
            return null;

        int index = this.value.indexOf(LEVEL_SEP);
        if (index > 0) {
            String pRowId = this.value.substring(0, index);
            if (FlameUtils.isBlank(pRowId))
                return null;
            else
                return XUIRowId.newInstance(pRowId);
        } else {
            return null;
        }
    }

    /**
     * 剥离父级前缀后的当前层级 rowId。
     *
     * <p>取最后一个 {@value #LEVEL_SEP} 之后的部分。无分隔符时返回整个 value。</p>
     *
     * @return 当前层级 rowId 字符串，value 为空时返回空字符串。
     */
    public String getRowId() {
        if (FlameUtils.isBlank(this.value))
            return "";

        int index = this.value.lastIndexOf(LEVEL_SEP);
        return this.value.substring(index + 1);
    }

    /** @return 内部 value 字符串，与 {@link #getValue()} 一致。 */
    public String toString() {
        return this.value;
    }
}
