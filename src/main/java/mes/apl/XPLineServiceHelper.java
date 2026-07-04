package mes.apl;

import com.flame.config.basic.BasicConfiguration;
import mes.apl.service.XPLineStandardService;

public class XPLineServiceHelper {
    private static XPLineStandardService service;

    private XPLineServiceHelper() {
    }

    public static XPLineStandardService service() {
        if (service == null) {
            service = BasicConfiguration.getBean(XPLineStandardService.class);
        }

        return service;
    }
}
