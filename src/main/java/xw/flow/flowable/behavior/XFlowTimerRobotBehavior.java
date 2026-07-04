package xw.flow.flowable.behavior;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.flowable.bpmn.model.ReceiveTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.bpmn.behavior.ReceiveTaskActivityBehavior;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.context.Context;
import org.flowable.engine.impl.history.HistoryManager;

import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;

import xw.flow.XFlowDefinitionHelper;
import xw.flow.XFlowExecutionHelper;
import xw.flow.XFlowRepositoryHelper;
import xw.flow.bean.TimeRobotVO;
import xw.flow.entity.XWorkInstance;
import xw.flow.entity.XWorkTimer;

/**
 * XWorx的时间自动机是通过ReceiveTask实现,
 */
public class XFlowTimerRobotBehavior extends ReceiveTaskActivityBehavior {
	private static final long serialVersionUID = 1L;
	private ReceiveTask receiveTask;

    public XFlowTimerRobotBehavior(String receiveTaskId, String skipExpression) {
        super(receiveTaskId, skipExpression);
    }

    public void setReceiveTask(ReceiveTask receiveTask) {
        this.receiveTask = receiveTask;
    }

    public void execute(DelegateExecution execution) {
        // 在新版本中，ExecutionEntity可能已经被改为统一的Execution
        // 我们仍可以通过其接口使用，但不再使用实现类
        org.flowable.engine.impl.persistence.entity.ExecutionEntity executionEntity = (org.flowable.engine.impl.persistence.entity.ExecutionEntity) execution;
        XWorkTimer workTimer = this.getXWorkTimer(executionEntity);
        if (workTimer == null)
            throw new XException("XWorkTimer was not found.");

        TimeRobotVO vo = XFlowDefinitionHelper.getTimeRobot(receiveTask);

        long increment = year2ms(vo.getYears()) + month2ms(vo.getMonths()) + day2ms(vo.getDays()) + hour2ms(vo.getHours()) + minute2ms(vo.getMinutes()) + second2ms(vo.getSeconds());
        long timer = workTimer.getCreatedStamp().getTime() + increment;
        workTimer.setTimestamp(new Timestamp(timer));
        PersistenceHelper.service().save(workTimer);
    }

    private XWorkTimer getXWorkTimer(org.flowable.engine.impl.persistence.entity.ExecutionEntity execution) {
        XWorkTimer workTimer = (XWorkTimer) execution.getExternalObject();
        if (workTimer != null)
            return workTimer;

        ProcessEngineConfigurationImpl engineConfigurationImpl = Context.getProcessEngineConfiguration();
        
        // 从数据库去查询Execution的当前HistoricActivityInstance, 然后根据HistoricActivityInstance去查询XWorkTimer;
        HistoryManager historyManager = engineConfigurationImpl.getHistoryManager();
        HistoricActivityInstance hisActivityInstance = historyManager.findHistoricActivityInstance(execution, true);
        if (hisActivityInstance == null)
            return workTimer;

        // 从Execution的BusinessKey中去获取对应的XWorkInstance对象
        XWorkInstance workInstance = XFlowExecutionHelper.execution().getXWorkInstance(execution);
        if (workInstance == null)
            return workTimer;

        List<XWorkTimer> workTimers = XFlowRepositoryHelper.repository().findXWorkTimer(workInstance, hisActivityInstance);
        for (XWorkTimer timer : workTimers) {
            if (workTimer == null || timer.getCreatedStamp().after(workTimer.getCreatedStamp())) {
                workTimer = timer;
            }
        }
        if (workTimer != null) {
            execution.setExternalObject(workTimer);
        }

        return workTimer;
    }

    public static long year2ms(int year) {
        return year * 12 * 30 * 24 * 60 * 60 * 1000;
    }

    public static long month2ms(int month) {
        return month * 30 * 24 * 60 * 60 * 1000;
    }

    public static long day2ms(int day) {
        return day * 24 * 60 * 60 * 1000;
    }

    public static long hour2ms(int hour) {
        return hour * 60 * 60 * 1000;
    }

    public static long minute2ms(int minute) {
        return minute * 60 * 1000;
    }

    public static long second2ms(int second) {
        return second * 1000;
    }

    public static void main(String[] args) {
        int seconds = 10;
        int minutes = 10;
        int hours = 1;
        int days = 1;
        int months = 1;
        int years = 0;

        Date date = new Date();
        long time = date.getTime();
        long tgTime = time + year2ms(years) + month2ms(months) + day2ms(days) + hour2ms(hours) + minute2ms(minutes) + second2ms(seconds);
        System.out.println(date);
        System.out.println(new Date(tgTime));
    }
}
