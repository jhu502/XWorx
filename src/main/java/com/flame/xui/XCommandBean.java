package com.flame.xui;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.flame.auths.ISession;
import com.flame.auths.IUser;
import com.flame.config.FlameConfiguration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flame.action.ActionKey;
import com.flame.action.IActionItem;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.common.form.ObjectFormProcessor;
import com.flame.localize.LocalizationHelper;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.util.FlameUtils;
import com.flame.util.JsonUtils;
import com.flame.util.XException;
import com.flame.annotations.UIDefinition;

/**
 * XUI 框架的命令 Bean，封装一次前端请求的完整上下文信息。
 *
 * <p>作为前端 UI 组件与后端处理逻辑之间的数据传输对象，XCommandBean 承载了：</p>
 * <ul>
 *   <li>HTTP 请求/响应对象（{@link HttpServletRequest} / {@link HttpServletResponse}）</li>
 *   <li>请求参数映射（从 QueryString、FormData、Multipart 等多种来源聚合）</li>
 *   <li>当前操作对应的 {@link ActionKey}、{@link XUIAction} 及处理器（Processor）</li>
 *   <li>Grid 组件选中的行标识（{@link XUIRowId}）及其对应的持久化对象</li>
 *   <li>关联的打开者选中项（openerSelected）</li>
 * </ul>
 *
 * <p>生命周期：每个请求由静态工厂方法 {@code newCommandBean} 创建实例，
 * 在请求处理完成后即被丢弃，不跨请求复用。</p>
 *
 * @see XUIAction
 * @see XUIRowId
 * @see ActionKey
 */
public class XCommandBean implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 日志记录器 */
	protected static final Logger logger = LoggerFactory.getLogger(XCommandBean.class);

	// ── 请求参数键名常量 ──────────────────────────────────────────
	/** 对象唯一标识符参数名（"oid"） */
	private static final String OID = "oid";
	/** 打开者（父窗口）选中行参数名 */
	private static final String OPENER_SELECTED = "openerSelected";
	/** 主对象 OID 参数名 */
	private static final String PRIMARY_OID = "primaryOid";
	/** Action 键参数名 */
	private static final String ACTION_KEY = "actionKey";
	/** 表单处理器类名参数名 */
	private static final String PROCESSOR = "processor";
	/** Grid 组件 ID 参数名 */
	private static final String GRID_ID = "gridId";
	/** Grid 选中行 ID 列表参数名 */
	private static final String ROW_IDS = "rowIds";
	private static final String ROW_ID = "rowId";
	/** 操作标题参数名 */
	private static final String TITLE = "title";
	/** 模型类全限定名参数名 */
	private static final String ENTITY_TYPE = "entityType";
	private static final String MODEL_TYPE = "modelType";
	private static final String ID = "id";

	// ── 实例字段 ──────────────────────────────────────────────────
	/** 表单处理器全限定类名 */
	private String processor = "";
	/** 主对象的 OID 字符串 */
	private String primaryOid = "";
	/** 当前操作的 Grid 组件 ID */
	private String gridId = "";
	/** 操作标题（显示名称） */
	private String title = "";
	/** 当前请求的 Action 键，用于定位具体操作 */
	private ActionKey actionKey;
	/** HTTP 请求对象（transient，不参与序列化） */
	private transient HttpServletRequest request;
	/** HTTP 响应对象（transient，不参与序列化） */
	private transient HttpServletResponse response;
	/** 解析后的 XUI Action 定义（transient） */
	private transient XUIAction action;
	/** 当前会话接口（transient，延迟初始化） */
	private transient ISession isession;
	/** 主对象实例（transient，延迟加载） */
	private transient XObject primaryObject;
	/** 打开者（父窗口）选中的行 ID 数组 */
	private XUIRowId[] openerSelected = new XUIRowId[0];
	/** 当前 Grid 选中的行 ID 数组 */
	private XUIRowId[] selectedIds = new XUIRowId[0];
	/** 聚合后的请求参数映射（key 为参数名，value 为字符串/数组） */
	private Map<String, Object> parameterMap = new HashMap<>();

	/**
	 * 静态工厂方法：从 MultiValueMap 创建 XCommandBean。
	 *
	 * @param request  HTTP 请求对象
	 * @param response HTTP 响应对象
	 * @param multiMap Spring MVC 的 MultiValueMap 参数（用于处理多值参数）
	 * @param extra    额外的键值对参数，每个元素为 {@code [key, value]}
	 * @return 新创建的 XCommandBean 实例
	 */
	public static XCommandBean newCommandBean(HttpServletRequest request, HttpServletResponse response, MultiValueMap<String, Object> multiMap, String[]... extra) {
		return new XCommandBean(request, response, multiMap, extra);
	}

	/**
	 * 静态工厂方法：从普通 Map 创建 XCommandBean。
	 *
	 * @param request  HTTP 请求对象
	 * @param response HTTP 响应对象
	 * @param paramMap 参数映射
	 * @param extra    额外的键值对参数
	 * @return 新创建的 XCommandBean 实例
	 */
	public static XCommandBean newCommandBean(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramMap, String[]... extra) {
		return new XCommandBean(request, response, paramMap, extra);
	}

	/**
	 * 静态工厂方法：仅从 request/response 创建 XCommandBean（无额外参数映射）。
	 *
	 * @param request  HTTP 请求对象
	 * @param response HTTP 响应对象
	 * @param extra    额外的键值对参数
	 * @return 新创建的 XCommandBean 实例
	 */
	public static XCommandBean newCommandBean(HttpServletRequest request, HttpServletResponse response, String[]... extra) {
		return new XCommandBean(request, response, null, extra);
	}

	/**
	 * 构造函数：初始化 XCommandBean 并聚合所有请求参数。
	 *
	 * <p>参数聚合顺序：</p>
	 * <ol>
	 *   <li>从 {@code request.getParameterMap()} 读取标准 HTTP 参数</li>
	 *   <li>调用 {@link #handleMultiMap(Map)} 处理 MultiValueMap（用于 Multipart 等场景）</li>
	 *   <li>追加 {@code extra} 中的额外键值对</li>
	 *   <li>从参数中提取 actionKey、gridId 等标准字段</li>
	 *   <li>解析 rowIds 构建 {@link XUIRowId} 数组</li>
	 *   <li>解析 openerSelected 构建关联选中数组</li>
	 * </ol>
	 *
	 * @param request  HTTP 请求对象
	 * @param response HTTP 响应对象
	 * @param multiMap 多值参数映射（可为 null）
	 * @param extra    额外的键值对参数，每个元素为 {@code [key, value]}
	 */
	public XCommandBean(HttpServletRequest request, HttpServletResponse response, Map<String, ?> multiMap, String[]... extra) {
		this.request = request;
		this.response = response;
		// 1. 从 HTTP 请求中读取所有参数
		for (Entry<String, String[]> entry : this.request.getParameterMap().entrySet()) {
			String name = entry.getKey();
			String[] values = entry.getValue();

			if (values == null) {
				this.parameterMap.put(name, values);
			} else {
				if (values.length == 1) {
					this.parameterMap.put(name, values[0]);
				} else {
					this.parameterMap.put(name, values);
				}
			}
		}
		// 2. 处理 MultiValueMap 参数（Multipart 等场景）
		this.handleMultiMap(multiMap);

		// 3. 追加额外的键值对参数
		if (extra != null) {
			for (String[] ext : extra) {
				if (ext != null && ext.length >= 2) {
					this.parameterMap.put(ext[0], ext[1]);
				}
			}
		}

		// 4. 提取标准字段
		this.setActionKey((String) this.parameterMap.get(ACTION_KEY));
		this.setGridId((String) this.parameterMap.get(GRID_ID));
		String processor = (String) this.parameterMap.get(PROCESSOR);
		if (processor == null) {
			XUIAction xaction = this.getAction();
			if (xaction != null) {
				this.setProcessor(xaction.getProcessor());
			}
		} else {
			this.setProcessor(processor);
		}

		// 5. 从 rowIds 参数构建结构化 rowId 数组（唯一数据源）
		LinkedHashMap<String, XUIRowId> selectedIdMap = new LinkedHashMap<>();
		Object $rowIds = this.parameterMap.get(ROW_IDS);
		if ($rowIds instanceof String rowIds) {
			if (rowIds.startsWith("[")) {
				// JSON 数组格式
				ArrayNode arrayNode = JsonUtils.convertT(rowIds, ArrayNode.class);
				Iterator<JsonNode> it = arrayNode.elements();
				while (it.hasNext()) {
					JsonNode node = it.next();
					XUIRowId uiRowId = XUIRowId.newInstance(node.asText());
					selectedIdMap.put(uiRowId.getValue(), uiRowId);
				}
			} else {
				// 单个 rowId 字符串
				XUIRowId uiRowId = XUIRowId.newInstance(rowIds);
				selectedIdMap.put(uiRowId.getValue(), uiRowId);
			}
		} else if ($rowIds instanceof String[] rowIdArray) {
			// 字符串数组格式
			for (String rowId : rowIdArray) {
				XUIRowId uiRowId = XUIRowId.newInstance(rowId);
				selectedIdMap.put(uiRowId.getValue(), uiRowId);
			}
		}

		// 向后兼容：将 rowId 参数也作为 rowIds 的来源
		if (this.parameterMap.containsKey(ROW_ID)) {
			String rowId = (String) this.parameterMap.get(ROW_ID);
			if (FlameUtils.isNotBlank(rowId) && !selectedIdMap.containsKey(rowId)) {
				XUIRowId xuiRowId = XUIRowId.newInstance(rowId);
				selectedIdMap.putFirst(xuiRowId.getValue(), xuiRowId);
			}
		}

		//TreeGrid展开节点时传递的是id参数
		if (this.parameterMap.containsKey(ID)) {
			String id = (String) this.parameterMap.get(ID);
			if (FlameUtils.isNotBlank(id) && !selectedIdMap.containsKey(id)) {
				XUIRowId xuiRowId = XUIRowId.newInstance(id);
				selectedIdMap.putFirst(xuiRowId.getValue(), xuiRowId);
			}
		}

		this.selectedIds = selectedIdMap.values().toArray(new XUIRowId[0]);

		// 6. 解析 openerSelected（父窗口选中行）
		LinkedHashMap<String, XUIRowId> openerSelectedMap = new LinkedHashMap<>();
		Object $referIds = this.parameterMap.get(OPENER_SELECTED);
		if ($referIds instanceof String _referIds) {
			if (FlameUtils.isNotBlank(_referIds)) {
				for (String referId : _referIds.split(",")) {
					XUIRowId uiRowId = XUIRowId.newInstance(referId);
					openerSelectedMap.put(uiRowId.getValue(), uiRowId);
				}
			}
		}
		this.openerSelected = openerSelectedMap.values().toArray(new XUIRowId[0]);

		this.setTitle((String) this.parameterMap.get(TITLE));
	}

	/**
	 * 向参数映射中添加或覆盖一个键值对。
	 *
	 * @param name  参数名
	 * @param value 参数值
	 */
	public void addParameter(String name, Object value) {
		this.parameterMap.put(name, value);
	}

	/**
	 * 获取完整的请求参数映射。
	 *
	 * @return 不可变的外部参数映射引用
	 */
	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}

	/**
	 * 根据名称获取参数值。
	 *
	 * @param name 参数名
	 * @return 参数值（可能为 String、String[] 或 null）
	 */
	public Object getParameter(String name) {
		return this.parameterMap.get(name);
	}

	/**
	 * 获取参数的字符串表示。
	 *
	 * @param name 参数名
	 * @return 参数的 {@code toString()} 结果，参数不存在时返回 null
	 */
	public String getTextParameter(String name) {
		Object value = this.getParameter(name);
		if (value == null)
			return null;
		else
			return value.toString();
	}

	/**
	 * 获取主对象的 OID 字符串（延迟解析，优先从缓存读取）。
	 *
	 * <p>解析优先级：</p>
	 * <ol>
	 *   <li>已缓存的 {@code primaryOid} 字段</li>
	 *   <li>请求参数中的 {@code primaryOid}</li>
	 *   <li>请求参数中的 {@code oid}</li>
	 *   <li>从第一个 rowId 中提取的对象 ID</li>
	 * </ol>
	 *
	 * @return 主对象 OID 字符串，无可用值时返回空字符串
	 */
	public String getPrimaryOid() {
		if (FlameUtils.isNotBlank(this.primaryOid))
			return this.primaryOid;

		this.primaryOid = (String) parameterMap.get(PRIMARY_OID);
		if (FlameUtils.isBlank(this.primaryOid)) {
			String oid = (String) parameterMap.get(OID);
			if (FlameUtils.isNotBlank(oid)) {
				this.primaryOid = oid;
			} else {
				XUIRowId uiRowId = this.getRowId();
				if (uiRowId != null) {
					this.primaryOid = uiRowId.getObjectId();
				}
			}
		}
		return primaryOid;
	}

	/**
	 * 获取主对象的持久化实例（延迟加载，优先从缓存读取）。
	 *
	 * <p>支持标准 OID 格式和带 {@code ~} 分隔符的复合 OID 格式。
	 * 查询失败时返回 null 并记录错误日志。</p>
	 *
	 * @return 主 {@link XObject} 实例，无法解析时返回 null
	 */
	public XObject getPrimaryObj() {
		if (this.primaryObject != null) {
			return this.primaryObject;
		}
		if (FlameUtils.isBlank(this.getPrimaryOid())) {
			return null;
		} else {
			try {
				String primaryOid = this.getPrimaryOid();
				if (FlameUtils.isBlank(primaryOid)) {
					return null;
				} else if (ObjectReference.isOid(primaryOid)) {
					this.primaryObject = PersistenceHelper.service().find(primaryOid);
					return this.primaryObject;
				} else {
					primaryOid = FlameUtils.getLastSplit(primaryOid, "~");
					this.primaryObject = PersistenceHelper.service().find(primaryOid);
					return this.primaryObject;
				}
			} catch (XException e) {
				logger.error(e.getMessage(), e);
				return null;
			}
		}
	}

	/**
	 * 获取当前请求的会话接口（延迟初始化）。
	 *
	 * @return {@link ISession} 实例
	 */
	public ISession getSession() {
		if (this.isession == null) {
			this.isession = FlameConfiguration.getBean(ISession.class);
		}

		return this.isession;
	}

	/**
	 * 获取当前登录用户。
	 *
	 * @return 当前 {@link IUser} 实例
	 */
	public IUser getCurrentUser() {
		return this.getSession().currentUser();
	}

	/**
	 * 获取打开者（父窗口）选中的行 ID 数组。
	 *
	 * @return 打开者选中的 {@link XUIRowId} 数组，无选中时返回空数组
	 */
	public XUIRowId[] getOpenerSelected() {
		return this.openerSelected;
	}

	/**
	 * 执行当前 CommandBean 关联的表单处理器。
	 *
	 * <p>通过反射实例化 {@link ObjectFormProcessor} 并调用其
	 * {@link ObjectFormProcessor#doOperation} 方法。
	 * 若处理器类不存在或执行异常，返回 {@link FormStatus#FAILURE} 的 {@link FormResult}。</p>
	 *
	 * @return 表单处理结果
	 */
	public FormResult executeProcessor() {
		try {
			Class<?> formClass = Class.forName(this.getProcessor());
			Constructor<?> constructor = formClass.getConstructor(new Class<?>[0]);
			ObjectFormProcessor formProcessor = (ObjectFormProcessor) constructor.newInstance();
			return formProcessor.doOperation(this);
		} catch (ClassNotFoundException e) {
			FormResult formResult = new FormResult();
			formResult.setStatus(FormStatus.FAILURE);
			formResult.setMessage("Processor:" + e.getMessage() + " is not found.");

			return formResult;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			FormResult formResult = new FormResult();
			formResult.setStatus(FormStatus.FAILURE);
			formResult.setMessage(e.getMessage());

			return formResult;
		}
	}

	/**
	 * 处理多值参数映射，将其合并到 parameterMap 中。
	 *
	 * <p>处理逻辑：</p>
	 * <ul>
	 *   <li>对于以 {@code []} 结尾的 key（jQuery 数组参数），去除后缀并转为数组</li>
	 *   <li>对于 List 类型的值：空列表 → null，单元素 → 取值，多元素 → 转为数组</li>
	 *   <li>其他类型直接存入</li>
	 * </ul>
	 *
	 * @param multiMap 多值参数映射（可为 null）
	 */
	protected void handleMultiMap(Map<String, ?> multiMap) {
		if (multiMap == null)
			return;

		for (Entry<String, ?> entry : multiMap.entrySet()) {
			String key = entry.getKey();
			Object val = entry.getValue();
			/**
			 * JQuery.post 传递数组参数时，key 都会加上 "[]" 后缀
			 */
			if (key.endsWith("[]")) {
				key = key.substring(0, key.length() - 2);
				if (val instanceof List) {
					parameterMap.put(key, ((List<?>) val).toArray());
				} else {
					parameterMap.put(key, val);
				}
			} else {
				if (val instanceof List) {
					List<?> list = (List<?>) val;
					if (!list.isEmpty()) {
						if (list.size() == 1) {
							parameterMap.put(key, list.get(0));
						} else {
							parameterMap.put(key, list.toArray());
						}
					} else {
						parameterMap.put(key, null);
					}
				} else {
					parameterMap.put(key, val);
				}

			}
		}
	}

	/**
	 * 获取指定类上由 {@link UIDefinition} 标注的注解。
	 *
	 * <p>遍历类的所有注解，查找带有 {@code @UIDefinition} 元注解的注解实例。</p>
	 *
	 * @param clazz 要检查的类
	 * @return 带有 UIDefinition 元注解的注解实例，未找到时返回 null
	 */
	public Annotation getUIAnnotation(Class<?> clazz) {
		Annotation[] annotations = clazz.getAnnotations();
		for (Annotation annotation : annotations) {
			UIDefinition uiDef = annotation.annotationType().getAnnotation(UIDefinition.class);
			if (uiDef != null) {
				return annotation;
			}
		}

		return null;
	}

	/**
	 * 获取当前 Action 键。
	 *
	 * @return {@link ActionKey} 实例，未设置时返回 null
	 */
	public ActionKey getActionKey() {
		return actionKey;
	}

	/**
	 * 获取当前 Action 键，若未设置则尝试从主对象类型推断。
	 *
	 * <p>推断逻辑：从 primaryOid 中解析出类名 → 通过反射获取 Class →
	 * 使用默认名称和该类构造 ActionKey。</p>
	 *
	 * @param defaultName 默认 Action 名称（用于推断）
	 * @return {@link ActionKey} 实例，无法推断时返回 null
	 */
	public ActionKey getActionKey(String defaultName) {
		if (this.actionKey == null) {
			if (FlameUtils.isBlank(defaultName)) {
				return this.actionKey;
			}

			String primaryOid = this.getPrimaryOid();
			String[] array = FlameUtils.splitOid(primaryOid);
			if (array.length > 2) {
				String className = array[1];
				if (FlameUtils.isNotBlank(className)) {
					try {
						this.actionKey = ActionKey.newActionKey(defaultName, Class.forName(className));
					} catch (ClassNotFoundException e) {
						throw new XException(e);
					}
				}
			}
		}

		return this.actionKey;
	}

	/**
	 * 设置当前 Action 键。
	 *
	 * @param actionKey Action 键字符串，为空时忽略
	 */
	public void setActionKey(String actionKey) {
		if (FlameUtils.isNotBlank(actionKey)) {
			this.actionKey = ActionKey.newActionKey(actionKey);
		}
	}

	/**
	 * 获取解析后的 XUI Action 定义（延迟解析，优先从缓存读取）。
	 *
	 * <p>解析来源：</p>
	 * <ul>
	 *   <li>若 ActionKey 的 type 指向一个 Builder 类（通过 {@code @UIDefinition} 注解）：
	 *       从该 Grid 构建器的 actions 列表中匹配同名 Action</li>
	 *   <li>否则：从 {@link IActionItem} 注册表中查找并转换</li>
	 * </ul>
	 *
	 * @return {@link XUIAction} 实例，无法解析时返回 null
	 */
	public XUIAction getAction() {
		if (this.action != null)
			return this.action;

		String aKey = (String) parameterMap.get(ACTION_KEY);
		if (aKey == null)
			return null;

		ActionKey actionKey = ActionKey.newActionKey(aKey);
		if (FlameUtils.isBlank(actionKey.getType()) || FlameUtils.isBlank(actionKey.getName()))
			return null;

		if (actionKey.fromBuilder()) {
			// Builder 模式：从 @UIDefinition 注解的 Grid 组件中查找 Action
			String builder = actionKey.getType();
			try {
				Annotation annotation = this.getUIAnnotation(Class.forName(builder));
				if (annotation == null)
					return null;

				UIDefinition uiDefinition = annotation.annotationType().getAnnotation(UIDefinition.class);
				WidgetType compType = uiDefinition.component();
				Class<? extends IComponent> configClazz = compType.getWidget();
				if (configClazz == null || !GridComponent.class.isAssignableFrom(configClazz))
					return null;

				Constructor<? extends IComponent> constructor = configClazz.getConstructor(annotation.annotationType(), String.class);
				GridComponent compConfig = (GridComponent) constructor.newInstance(annotation, builder);
				for (XUIAction _action : compConfig.getActions()) {
					if (actionKey.getName().equals(_action.getName())) {
						if (FlameUtils.isBlank(_action.getDisplay())) {
							this.setTitle(_action.getDisplay());
						} else {
							this.setTitle(LocalizationHelper.get(_action.getName()));
						}
						this.action = _action;
						break;
					}
				}
			} catch (Exception e) {
				throw new XException(e);
			}
		} else {
			// 标准模式：从 IActionItem 注册表查找
			IActionItem actionItem = actionKey.getActionItem();
			if (actionItem != null) {
				this.action = XUIAction.toXUIAction(actionItem);
				this.setTitle(actionItem.getDisplay());
			}
		}

		return this.action;
	}

	/**
	 * 获取表单处理器类名。
	 *
	 * @return 处理器全限定类名
	 */
	public String getProcessor() {
		return processor;
	}

	/**
	 * 设置表单处理器类名。
	 *
	 * @param processor 处理器全限定类名，为 null 或空白时忽略
	 */
	public void setProcessor(String processor) {
		if (processor != null && !processor.trim().isEmpty()) {
			this.processor = processor;
		}
	}

	/**
	 * 获取当前操作的 Grid 组件 ID。
	 *
	 * @return Grid ID 字符串
	 */
	public String getGridId() {
		return gridId;
	}

	/**
	 * 设置当前操作的 Grid 组件 ID。
	 *
	 * @param gridId Grid ID 字符串，为 null 或空白时忽略
	 */
	public void setGridId(String gridId) {
		if (gridId != null && !gridId.trim().isEmpty()) {
			this.gridId = gridId;
		}
	}

	/**
	 * 将所有选中行 ID 用逗号拼接为单个字符串。
	 *
	 * @return 逗号分隔的 rowId 字符串，无选中时返回空字符串
	 */
	public String getRowIdsJoint() {
		StringBuilder builder = new StringBuilder();
		for (XUIRowId uiRowId : this.getRowIds()) {
			if (builder.length() == 0) {
				builder.append(uiRowId.getValue());
			} else {
				builder.append(",").append(uiRowId.getValue());
			}
		}

		return builder.toString();
	}

	/**
	 * 获取当前 Grid 选中的行 ID 数组。
	 *
	 * @return {@link XUIRowId} 数组，无选中时返回空数组
	 */
	public XUIRowId[] getRowIds() {
		return this.selectedIds;
	}

	/**
	 * 获取当前选中行对应的持久化对象列表。
	 *
	 * <p>遍历所有 rowId，通过 {@link PersistenceHelper} 加载对应的 {@link XObject}。
	 * 跳过空 rowId 和非标准 OID 格式的 rowId。</p>
	 *
	 * @return 持久化对象列表，无有效选中时返回空列表
	 */
	public List<Object> getRowObjects() {
		List<Object> result = new ArrayList<>();

		XUIRowId[] rowIds = this.getRowIds();
		for (XUIRowId uiRowId : rowIds) {
			if (FlameUtils.isBlank(uiRowId))
				continue;

			String objectId = uiRowId.getObjectId();
			if (!ObjectReference.isOid(objectId))
				continue;
			XObject rowObj = PersistenceHelper.service().refresh(new ObjectReference<>(objectId));
			if (rowObj != null) {
				result.add(rowObj);
			}
		}

		return result;
	}

	/**
	 * 获取第一个 rowId（结构化组合字符串）。
	 *
	 * <p>rowId 为组合结构，格式如 {@code {A}~{B}^{C}}，
	 * 其中 {@code ~} 为同级对象分隔符，{@code ^} 为层级分隔符。</p>
	 *
	 * @return 第一个 rowId，若无则返回 null
	 */
	public XUIRowId getRowId() {
		if (this.selectedIds.length > 0) {
			return this.selectedIds[0];
		}
		return null;
	}

	/**
	 * 获取第一个选中行对应的持久化对象。
	 *
	 * <p>等价于 {@code getRowObjects()} 的首个元素，但仅加载单个对象。</p>
	 *
	 * @return 第一个选中行的 {@link XObject}，无有效选中时返回 null
	 */
	public Object getRowObject() {
		if (FlameUtils.isBlank(this.getRowId()))
			return null;

		XUIRowId uiRowId = this.getRowId();
		String oid = uiRowId.getObjectId();
		if (!ObjectReference.isOid(oid))
			return null;
		XObject rowObject = PersistenceHelper.service().refresh(new ObjectReference<>(oid));
		if (rowObject == null) {
			return null;
		} else {
			return rowObject;
		}
	}

	/**
	 * 获取操作标题。
	 *
	 * @return 标题字符串
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 获取 HTTP 请求对象。
	 *
	 * @return {@link HttpServletRequest} 实例
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * 获取 HTTP 响应对象。
	 *
	 * @return {@link HttpServletResponse} 实例
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * 设置操作标题。
	 *
	 * @param title 标题字符串，为空时忽略
	 */
	public void setTitle(String title) {
		if (FlameUtils.isNotBlank(title)) {
			this.title = title;
		}
	}

	/**
	 * 获取模型类型（从请求参数 {@code class} 中读取）。
	 *
	 * @return 模型类全限定名，未提供时返回 null
	 */
	public String getEntityType() {
		return this.getTextParameter(ENTITY_TYPE);
	}
}
