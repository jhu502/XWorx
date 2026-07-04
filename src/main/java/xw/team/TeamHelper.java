package xw.team;

import com.flame.config.basic.BasicConfiguration;

import xw.team.repos.TeamRepository;

public class TeamHelper {
    private static TeamRepository repository;

    public static TeamRepository repository() {
        if (repository == null) {
            repository = BasicConfiguration.getBean(TeamRepository.class);
        }
        return repository;
    }
}
