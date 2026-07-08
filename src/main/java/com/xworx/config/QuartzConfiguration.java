package com.xworx.config;

import org.springframework.context.annotation.Configuration;

/**
 * 在Quartz中的主要概念：
 *     Scheduler：调度任务的主要API
 *     ScheduleBuilder：用于构建Scheduler，例如其简单实现类SimpleScheduleBuilder
 *     Job：调度任务执行的接口，也即定时任务执行的方法
 *     JobDetail：定时任务作业的实例
 *     JobBuilder：关联具体的Job，用于构建JobDetail
 *     Trigger：定义调度执行计划的组件，即定时执行
 *     TriggerBuilder：构建Trigger
 */
@Configuration
public class QuartzConfiguration {
}
