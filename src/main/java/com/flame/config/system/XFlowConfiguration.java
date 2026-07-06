package com.flame.config.system;

import java.util.ArrayList;
import java.util.List;

import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.scripting.BeansResolverFactory;
import org.flowable.common.engine.impl.scripting.ResolverFactory;
import org.flowable.engine.impl.scripting.VariableScopeResolverFactory;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.validation.ProcessValidatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import jakarta.annotation.PostConstruct;
import xw.flow.flowable.XFlowFlowableEventListener;
import xw.flow.flowable.XFlowValidatorSetFactory;
import xw.flow.flowable.behavior.XFlowActivityBehaviorFactory;
import xw.flow.flowable.operation.XFlowEngineAgenda;
import xw.flow.flowable.resolver.XFlowScriptResolverFactory;

/**
 * XFlow 流程引擎配置 —— 在 Spring Bean 初始化阶段注入自定义行为。
 *
 * <p>通过 {@code @Autowired} 获取 {@link SpringProcessEngineConfiguration}，
 * 在 {@code @PostConstruct} 中一次性完成所有自定义配置，
 * 执行时机早于 Flowable 引擎构建（早于 BpmnParser 创建）。</p>
 *
 * <h3>配置项：</h3>
 * <ol>
 *   <li>{@link XFlowActivityBehaviorFactory} —— 替换全局 ActivityBehavior 工厂</li>
 *   <li>{@link XFlowFlowableEventListener} —— 注册全局事件监听器</li>
 *   <li>ProcessValidator —— 注册自定义流程校验器</li>
 *   <li>{@link XFlowScriptResolverFactory} —— 注册脚本变量解析器</li>
 * </ol>
 *
 * @author hujin
 */
@Configuration
@EntityScan({"xw.flow.entity"})
@EnableJpaRepositories({"xw.flow.repos"})
@ComponentScan({"xw.flow.service"})
@AutoConfigureAfter(name = {"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration", "org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration"})
public class XFlowConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(XFlowConfiguration.class);
    @Autowired
    private SpringProcessEngineConfiguration processEngineConfiguration;

    @PostConstruct
    public void init() {
        LOGGER.info("XFlow configuration initializing via @PostConstruct");

        // ① 替换 ActivityBehavior 工厂
        processEngineConfiguration.setActivityBehaviorFactory(new XFlowActivityBehaviorFactory());
        processEngineConfiguration.setAgendaFactory(new XFlowEngineAgenda.XFlowEngineAgendaFactory());

        // ② 注册全局事件监听器
        List<FlowableEventListener> listeners = processEngineConfiguration.getEventListeners();
        if (listeners == null) {
            processEngineConfiguration.setEventListeners(List.of(new XFlowFlowableEventListener()));
        } else {
            listeners.add(new XFlowFlowableEventListener());
        }

        // ③ 注册自定义流程校验器
        ProcessValidatorImpl validator = new ProcessValidatorImpl();
        validator.addValidatorSet(XFlowValidatorSetFactory.createExecutableProcessValidatorSet());
        processEngineConfiguration.setProcessValidator(validator);

        // ④ 注册脚本引擎变量解析器
        List<ResolverFactory> resolvers = processEngineConfiguration.getResolverFactories();
        if (resolvers == null) {
            resolvers = new ArrayList<>();
            processEngineConfiguration.setResolverFactories(resolvers);
            resolvers.add(new VariableScopeResolverFactory());
            resolvers.add(new BeansResolverFactory());
        }
        resolvers.add(new XFlowScriptResolverFactory());

        LOGGER.info("XFlow configuration initialized");
    }
}
