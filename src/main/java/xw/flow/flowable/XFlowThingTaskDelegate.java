package xw.flow.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XFlowThingTaskDelegate implements JavaDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(XFlowThingTaskDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        LOGGER.info("-----------------------Delegate:" + execution);

    }
}
