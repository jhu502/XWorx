package xw.flow.flowable;

import org.flowable.validation.validator.ValidatorSet;
import org.flowable.validation.validator.ValidatorSetNames;
import org.flowable.validation.validator.impl.*;

public class XFlowValidatorSetFactory {
    public static ValidatorSet createExecutableProcessValidatorSet() {
        ValidatorSet validatorSet = new ValidatorSet(ValidatorSetNames.FLOWABLE_EXECUTABLE_PROCESS);

        /**
         * 通常指用于验证 BPMN 模型中关联元素(Association)合法性的验证器, 作用是在流程部署或模型校验阶段, 检查这些关联是否符合业务规则或技术约束;
         * a. 关联元素是 BPMN中用于连接流程元素的辅助连接线(如连接任务到文本注释、或表示信息流向);
         */
        validatorSet.addValidator(new AssociationValidator());
        /**
         * 用于验证 BPMN 模型中信号元素(Signal)合法性的验证器, 作用是在流程部署或模型校验阶段, 确保信号的定义和引用符合规范;
         * a. 信号元素是BPMN中用于跨流程或流程内传递事件的全局/局部事件机制(如通过信号启动流程、触发边界事件等);
         */
        validatorSet.addValidator(new SignalValidator());
        /**
         * 用于验证 BPMN 模型中操作元素(Operation)的合法性, 作用是在流程部署或模型校验阶段, 确保这些操作的配置符合业务规则或技术约束，避免运行时因配置错误导致的异常
         * a. 操作元素主要出现在与外部系统交互的流程节点中(如服务任务、调用活动等), 用于定义具体的交互逻辑(如REST调用、SOAP 请求、数据库操作等);
         */
        validatorSet.addValidator(new OperationValidator());
        validatorSet.addValidator(new ErrorValidator());
        validatorSet.addValidator(new DataObjectValidator());

		/**
		 * 用于对整个 BPMN 模型(BpmnModel)进行全面合法性验证的核心组件.它不仅验证单个流程元素(如任务、事件、网关等),还会校验元素之间的关系、流程结构完整性、以及模型是否符合 BPMN 2.0 规范和业务规则,确保流程模型在部署和运行时的正确性
		 */
        validatorSet.addValidator(new BpmnModelValidator());
		/**
		 * 用于验证 BPMN 模型中所有流程元素（FlowElement） 合法性的通用验证器。FlowElement 是 BPMN 模型中所有具体流程元素（如任务、网关、事件、序列流等）的基类，
		 * 因此 FlowElementValidator 的作用是对这些元素的共性规则（如 ID 唯一性、属性完整性）和个性规则（如任务的处理人配置、网关的分支条件）进行全面校验，确保每个元素的配置符合规范
		 */
        validatorSet.addValidator(new FlowElementValidator());

		/**
		 * 用于验证 BPMN 模型中启动事件(Start Event)合法性的验证器, 作用是在流程部署或模型校验阶段, 确保启动事件的配置符合 BPMN 规范和业务规则, 避免因启动事件配置错误导致流程无法正常启动
		 * a. 启动事件是流程的起点, 决定了流程如何被触发(如手动启动、定时触发、消息驱动等);
		 */
        validatorSet.addValidator(new StartEventValidator());
        validatorSet.addValidator(new SequenceflowValidator());
        validatorSet.addValidator(new UserTaskValidator());
        validatorSet.addValidator(new XFlowServiceTaskValidator());
        validatorSet.addValidator(new ScriptTaskValidator());
        validatorSet.addValidator(new SendTaskValidator());
        validatorSet.addValidator(new ExclusiveGatewayValidator());
        validatorSet.addValidator(new EventGatewayValidator());
        validatorSet.addValidator(new SubprocessValidator());
        validatorSet.addValidator(new EventSubprocessValidator());
        validatorSet.addValidator(new BoundaryEventValidator());
        validatorSet.addValidator(new IntermediateCatchEventValidator());
        validatorSet.addValidator(new IntermediateThrowEventValidator());
        validatorSet.addValidator(new MessageValidator());
        validatorSet.addValidator(new EventValidator());
        validatorSet.addValidator(new EndEventValidator());

        validatorSet.addValidator(new ExecutionListenerValidator());
        validatorSet.addValidator(new FlowableEventListenerValidator());

        validatorSet.addValidator(new DiagramInterchangeInfoValidator());

        return validatorSet;
    }
}
