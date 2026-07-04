package xw.flow.service;


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.flowable.engine.RuntimeService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.flame.config.basic.BasicConfiguration;
import com.flame.orm.PersistenceHelper;

import jakarta.annotation.Resource;
import xw.flow.XFlowRepositoryHelper;
import xw.flow.constants.FlowStatus;
import xw.flow.entity.XWorkTimer;

/**
 * @DisallowConcurrentExecution: 禁止并发执行相同Job.class的任务
 * @PersistJobDataAfterExecution: 持久化JobDetail中的JobDataMap(对trigger中的datamap无效)
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class XFlowTimerTriggerJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(XFlowTimerTriggerJob.class);
    private static final String lockPath = "/services/XFlow/lock-timeRobot";
    @Resource
    private RuntimeService runtimeService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        CuratorFramework framework = BasicConfiguration.getFramework();
        InterProcessMutex processMutex = new InterProcessMutex(framework, lockPath);
        try {
            if (processMutex.acquire(1, TimeUnit.SECONDS)) {
                List<XWorkTimer> workTimers = XFlowRepositoryHelper.repository().findXWorkTimer(FlowStatus.OPEN, new Timestamp(new Date().getTime()));
                for (XWorkTimer workTimer : workTimers) {
                    runtimeService.trigger(workTimer.getExecutionId());
                    workTimer.setStatus(FlowStatus.CLOSED);
                    PersistenceHelper.service().save(workTimer);
                }
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            } else {
                logger.error(e.getMessage());
            }
        } finally {
            try {
                processMutex.release();
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                } else {
                    logger.error(e.getMessage());
                }
            }
        }
    }
}
