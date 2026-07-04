package xw.flow.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XFlowTimeRobotDelegate implements JavaDelegate {
    protected static final Logger logger = LoggerFactory.getLogger(XFlowTimeRobotDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("-------------------------------:::" + execution);
    }
}
