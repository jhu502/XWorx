package xw.flow.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.util.FlameUtils;
import com.flame.util.JsonUtils;
import com.flame.util.XException;

import xw.flow.XFlowExecutionHelper;
import xw.flow.entity.XFlowDefinition;
import xw.flow.entity.XWorkInstance;

/**
 * 启动流程处理器 —— 根据选中的 XPart 创建 XWorkInstance 并启动流程。
 *
 * <p>从父页面获取选中的 XFlowDefinition，从 popup 表单获取选中的 XPart OID 列表，
 * 为每个 XPart 调用 {@link xw.flow.service.XFlowExecutionService#startProcessInstance} 创建流程实例。</p>
 */
public class StartFlowProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);

        XFlowDefinition definition = (XFlowDefinition) commandBean.getPrimaryObj();
        if (definition == null) {
            throw new XException("请先选择流程定义");
        }

        List<String> selectedOids = getSelectedOids(commandBean);
        if (selectedOids.isEmpty()) {
            throw new XException("请选择部件");
        }

        List<XWorkInstance> instances = new ArrayList<>();
        for (String oid : selectedOids) {
        	XObject primaryObj = (XObject) PersistenceHelper.service().find(oid);
            if (primaryObj == null) {
                continue;
            }
            Map<String, Object> variables = new HashMap<>();
            XWorkInstance workInstance = XFlowExecutionHelper.execution().startProcessInstance(definition, primaryObj, variables);
            instances.add(workInstance);
        }

        formResult.setMessage("成功启动 " + instances.size() + " 个流程实例");
        return formResult;
    }

    /**
     * 从请求参数中获取选中的 XPart OID 列表。
     * 优先从 {@code selectedOids} 参数读取（popup 右侧面板已选列表的 JSON 数组），
     * 回退到 {@code commandBean.getRowIds()}（勾选行），兼容直接调用场景。
     *
     * @param commandBean 请求命令对象
     * @return 选中的 OID 列表，不会为 null
     */
    private List<String> getSelectedOids(XCommandBean commandBean) {
        List<String> oidList = new ArrayList<>();

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

        return oidList;
    }
}
