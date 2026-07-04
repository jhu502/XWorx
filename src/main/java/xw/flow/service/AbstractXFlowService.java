package xw.flow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractXFlowService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractXFlowService.class);

    public static String oid2NCName(String oid) {
        return oid.replace(':', '-');
    }
}
