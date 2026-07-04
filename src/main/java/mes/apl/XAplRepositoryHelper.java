package mes.apl;

import com.flame.config.basic.BasicConfiguration;
import mes.apl.repos.AplRepository;

public class XAplRepositoryHelper {
    private static AplRepository repository;

    public static AplRepository repository() {
        if (repository == null) {
            repository = BasicConfiguration.getBean(AplRepository.class);
        }

        return repository;
    }
}
