package xw.team.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import xw.auths.entity.RoleRB;
import xw.team.entity.XContainerRoleMap;
import xw.team.entity.XContainerTeam;

@Repository
public interface TeamRepository extends JpaRepository<XContainerTeam, Long> {

    @Query("select a from XContainerRoleMap a where a.left.id = :#{#team.xid}")
    List<XContainerRoleMap> findRoleMapsByTeam(XContainerTeam team);

    @Query("select a from XContainerRoleMap a where a.left.id = :#{#team.xid} and a.right.id = :#{#role.xid}")
    List<XContainerRoleMap> findRoleMapsByTeamAndRole(XContainerTeam team, RoleRB role);
}
