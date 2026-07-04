package xw.flow;

import xw.flow.repos.XFlowRepository;
import com.flame.config.basic.BasicConfiguration;

public class XFlowRepositoryHelper {
    private static XFlowRepository repository;

    public static XFlowRepository repository() {
        if (repository == null) {
            repository = BasicConfiguration.getBean(XFlowRepository.class);
        }

        return repository;
    }
}
