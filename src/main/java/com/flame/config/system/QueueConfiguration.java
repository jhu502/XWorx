package com.flame.config.system;

import com.flame.config.basic.BasicConfiguration;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import xw.flow.service.XFlowTimerTriggerJob;

/**
 * 配置后台执行队列, 使用Quartz进行轮训进行队列任务处理;
 * 0 0 10,14,16 * * ?           每天10点/14点/4点
 * 0 0/30 9-17 * * ?            朝九晚五工作时间内每半小时
 * 0 0 12 ? * WED               表示每个星期三中午12点
 * 0 0 12 * * ?                 每天12点触发
 * 0 15 10 ? * *                每天10:15触发
 * 0 15 10 * * ?                每天10:15触发
 * 0 15 10 * * ? *              每天10:15触发
 * 0 15 10 * * ? 2005           2005年的每天10:15触发
 * 0 * 14 * * ?                 在每天14点到14:59期间的每1分钟触发
 * 0 0/5 14 * * ?               在每天14点到14:55期间的每5分钟触发
 * 0 0/5 14,18 * * ?            在每天14点到14:55期间和下午6点到6:55期间的每5分钟触发
 * 0 0-5 14 * * ?               在每天14点到14:05期间的每1分钟触发
 * 0 10,44 14 ? 3 WED           每年三月的星期三的下午14:10和14:44触发
 * 0 15 10 ? * MON-FRI          周一至周五的10:15触发
 * 0 15 10 15 * ?               每月15日10:15触发
 * 0 15 10 L * ?                每月最后一日的10:15触发
 * 0 15 10 ? * 6L               每月的最后一个星期五10:15触发
 * 0 15 10 ? * 6L 2002-2005     2002年至2005年的每月的最后一个星期五10:15触发
 * 0 15 10 ? * 6#3              每月的第三个星期五10:15触发
 * 30 * * * * ?                 每半分钟触发任务
 * 30 10 * * * ?                每小时的10分30秒触发任务
 * 30 10 1 * * ?                每天1点10分30秒触发任务
 * 30 10 1 20 * ?               每月20号1点10分30秒触发任务
 * 30 10 1 20 10 ? *            每年10月20号1点10分30秒触发任务
 * 30 10 1 20 10 ? 2011         2011年10月20号1点10分30秒触发任务
 * 30 10 1 ? 10 * 2011          2011年10月每天1点10分30秒触发任务
 * 30 10 1 ? 10 SUN 2011        2011年10月每周日1点10分30秒触发任务
 * 15,30,45 * * * * ?           每15秒，30秒，45秒时触发任务
 * 15-45 * * * * ?              15到45秒内，每秒都触发任务
 * 15/5 * * * * ?               每分钟的每15秒开始触发，每隔5秒触发一次
 * 15-30/5 * * * * ?            每分钟的15秒到30秒之间开始触发，每隔5秒触发一次
 * 0 0/3 * * * ?                每小时的第0分0秒开始，每三分钟触发一次
 * 0 15 10 ? * MON-FRI          星期一到星期五的10点15分0秒触发任务
 * 0 15 10 L * ?                每个月最后一天的10点15分0秒触发任务
 * 0 15 10 LW * ?               每个月最后一个工作日的10点15分0秒触发任务
 * 0 15 10 ? * 5L               每个月最后一个星期四的10点15分0秒触发任务
 * 0 15 10 ? * 5#3              每个月第三周的星期四的10点15分0秒触发任务
 */
@Configuration
@ConditionalOnClass({Scheduler.class, SchedulerFactoryBean.class, PlatformTransactionManager.class})
public class QueueConfiguration implements SchedulerFactoryBeanCustomizer {
    @Value("${com.xworx.queue.xflow-schedule:*/15 * * * * ?}")
    private String xflowSchedule; // corn表达式

    @Override
    public void customize(SchedulerFactoryBean schedulerFactoryBean) {
        // 延时5s启动定时任务，避免系统未完全启动却开始执行定时任务的情况
        schedulerFactoryBean.setStartupDelay(5);
        // 自动启动
        schedulerFactoryBean.setAutoStartup(true);
        // 覆盖已存在的任务,用于Quartz集群,QuartzScheduler启动会更新已存在的Job
        schedulerFactoryBean.setOverwriteExistingJobs(true);
    }

    @Bean(name = "flowScheduleJob")
    public JobDetail flowScheduleJob() {
        //指定具体的定时任务类
        return JobBuilder.newJob(XFlowTimerTriggerJob.class).withIdentity("XFlowSchedule").storeDurably().build();
    }

    @Bean(name = "flowScheduleTrigger")
    public Trigger flowScheduleTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(xflowSchedule);
        JobDetail jobDetail = (JobDetail) BasicConfiguration.getBean("flowScheduleJob");
        //返回任务触发器
        return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity("XFlowSchedule").withSchedule(scheduleBuilder).build();
    }
}
