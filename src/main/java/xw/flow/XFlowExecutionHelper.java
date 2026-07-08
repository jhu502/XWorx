package xw.flow;

import com.flame.xui.XCommandBean;
import com.flame.config.basic.BasicConfiguration;
import com.flame.orm.XObject;
import xw.flow.entity.XWorkActivity;
import xw.flow.entity.XWorkTask;
import xw.flow.service.XFlowExecutionService;
import org.flowable.engine.ProcessEngine;

public class XFlowExecutionHelper {
    private static XFlowExecutionService execution;

    public static XFlowExecutionService execution() {
        if (execution == null) {
            execution = BasicConfiguration.getBean(XFlowExecutionService.class);
        }

        return execution;
    }

    public static ProcessEngine getProcessEngine() {
        return execution().getProcessEngine();
    }

    public static String getTaskForm(XCommandBean commandBean) {
        XObject primary = commandBean.getPrimaryObj();
        if (primary instanceof XWorkTask) {
            XWorkTask workTask = (XWorkTask) primary;
            return workTask.getActivity().getTaskForm();
        } else if (primary instanceof XWorkActivity) {
            XWorkActivity workActivity = (XWorkActivity) primary;
            return workActivity.getTaskForm();
        } else {
            return null;
        }
    }
}
