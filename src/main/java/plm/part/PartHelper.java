package plm.part;

import plm.part.repos.PartRepository;
import com.flame.config.basic.BasicConfiguration;

public class PartHelper {
    private static PartRepository repository;

    public static PartRepository repository() {
        if (repository == null) {
            repository = BasicConfiguration.getBean(PartRepository.class);
        }

        return repository;
    }
}
