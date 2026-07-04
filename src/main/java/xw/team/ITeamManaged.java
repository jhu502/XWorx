package xw.team;

import com.flame.orm.XPersistable;
import xw.team.entity.IRoleHolder;

public interface ITeamManaged<T extends IRoleHolder> extends XPersistable {
    T getTeam();

    void setTeam(T team);
}
