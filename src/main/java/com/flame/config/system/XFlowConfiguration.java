package com.flame.config.system;

import java.util.ArrayList;
import java.util.List;

import org.flowable.app.engine.AppEngineConfiguration;
import org.flowable.app.spring.SpringAppEngineConfiguration;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.scripting.BeansResolverFactory;
import org.flowable.common.engine.impl.scripting.ResolverFactory;
import org.flowable.engine.configurator.ProcessEngineConfigurator;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.scripting.VariableScopeResolverFactory;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.ProcessEngineAutoConfiguration;
import org.flowable.spring.boot.app.AppEngineAutoConfiguration;
import org.flowable.validation.ProcessValidatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import jakarta.annotation.PostConstruct;
import xw.flow.flowable.XFlowFlowableEventListener;
import xw.flow.flowable.XFlowValidatorSetFactory;
import xw.flow.flowable.resolver.XFlowScriptResolverFactory;

/**
 * @author hujin
 * EngineConfigurator流程引擎初始化过程的接口, 允许开发者在引擎启动阶段介入配置流程, 实现个性化定制.
 * a.该接口定义了两个核心方法：beforeInit() 和 configure()它们在引擎初始化的不同阶段被调用;
 */
@Configuration
@EntityScan({"xw.flow.entity"})
@EnableJpaRepositories({"xw.flow.repos"})
@ComponentScan({"xw.flow.service"})
//@ConditionalOnMissingBean(ProcessEngineConfiguration.class)
@AutoConfigureAfter(name = {"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration", "org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration"})
public class XFlowConfiguration extends ProcessEngineConfigurator {
    private static final Logger LOGGER = LoggerFactory.getLogger(XFlowConfiguration.class);

    @PostConstruct
    public void initXProcessEngine() {
        SpringProcessEngineConfiguration x;
        ProcessEngineAutoConfiguration y;
        SpringAppEngineConfiguration z;
        AppEngineAutoConfiguration v;
    }

    /**
     * 在流程引擎核心组件初始化之前执行. 此时ProcessEngineConfiguration对象已被创建,但引擎的核心服务(如RepositoryService、RuntimeService等)尚未初始化.
     *
     * @param engineConfiguration
     */
    @Override
    public void beforeInit(AbstractEngineConfiguration engineConfiguration) {
        LOGGER.info("XFlow configuration beforeInit:{}", engineConfiguration.getClass().getName());
        List<FlowableEventListener> listenerList = engineConfiguration.getEventListeners();
        /**
         * 增加全局事件监听器, 监听所有的Flowable Node的事件
         */
        if (listenerList == null) {
            engineConfiguration.setEventListeners(List.of(new XFlowFlowableEventListener()));
        } else {
            listenerList.add(new XFlowFlowableEventListener());
        }

        super.beforeInit(engineConfiguration);
    }

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        LOGGER.info("XFlow configuration configure:{}", engineConfiguration.getClass().getName());
        if (engineConfiguration instanceof ProcessEngineConfigurationImpl) {
            ProcessEngineConfigurationImpl engineConfigurationImpl = (ProcessEngineConfigurationImpl) engineConfiguration;

            ProcessValidatorImpl processValidatorImpl = new ProcessValidatorImpl();
            processValidatorImpl.addValidatorSet(XFlowValidatorSetFactory.createExecutableProcessValidatorSet());
            engineConfigurationImpl.setProcessValidator(processValidatorImpl);
        } else if (engineConfiguration instanceof AppEngineConfiguration) {
        }
        
        super.configure(engineConfiguration);
    }

    /**
     * 为ScriptingEngine注册XFlowScriptResolverFactory, 以实现ScriptTask通过XFlowContextVarResolver获取XFlowContext中的变量
     *
     * @param engineConfigurationImpl
     */
    public void initScriptingEngines(ProcessEngineConfigurationImpl engineConfigurationImpl) {
        List<ResolverFactory> resolverFactories = engineConfigurationImpl.getResolverFactories();
        if (resolverFactories == null) {
            resolverFactories = new ArrayList<>();
            engineConfigurationImpl.setResolverFactories(resolverFactories);
            resolverFactories.add(new VariableScopeResolverFactory());
            resolverFactories.add(new BeansResolverFactory());
            resolverFactories.add(new XFlowScriptResolverFactory());
        } else {
            resolverFactories.add(new XFlowScriptResolverFactory());
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
