package xw.auths.repos;

import com.flame.common.TwoEntry;
import com.flame.orm.ObjectReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import xw.auths.entity.RoleRB;
import xw.auths.entity.RoleRB.RoleType;
import xw.auths.entity.XGroup;
import xw.auths.entity.XGroupGroupLink;
import xw.auths.entity.XGroupUserLink;
import xw.auths.entity.XPrincipal;
import xw.auths.entity.XPrincipalFavorite;
import xw.auths.entity.XUser;

import java.util.List;

@Repository
public interface XAuthRepository extends JpaRepository<XUser, Long> {
    @Query(value = "select x from XUser x where x.name like ?1")
    List<XUser> getXUser(String name);

    @Query(value = "select a from XUser a where a.name = :name")
    List<XUser> findUser(String name);

    @Query(value = "select a from XUser a where a.name like CONCAT('%',:key,'%') or a.fullName like CONCAT('%',:key,'%') or a.englishName like CONCAT('%',:key,'%') or a.email like CONCAT('%',:key,'%')")
    List<XUser> findUserFuzzy(String key);

    @Query(value = "select a from XGroup a where a.name like CONCAT('%',:key,'%') or a.fullName like CONCAT('%',:key,'%') or a.englishName like CONCAT('%',:key,'%') or a.email like CONCAT('%',:key,'%')")
    List<XGroup> findGroupFuzzy(String key);

    XUser findByNameIgnoreCase(String name);

    Page<XUser> findAll(Specification<XUser> specification, Pageable pageable);

    @Query(value = "select a from XGroup a where a.name = :name")
    List<XGroup> getXGroupByName(String name);

    @Query(value = "select a from XGroup a, XGroupGroupLink b where a.xid = b.right.id and b.left.id = :#{#groupRef.id}")
    List<XGroup> getGroupMember(ObjectReference<?> groupRef);

    @Query(value = "select a from XGroup a, XGroupGroupLink b where a.xid = b.right.id and b.left.id = :#{#group.xid}")
    List<XGroup> getGroupMember(XGroup group);

    @Query(value = "select a from XGroup a where a.groupType.name='SYSTEM' and a.name='XROOT'")
    List<XGroup> getRootGroup();

    @Query(value = "select a from XUser a, XGroupUserLink b where a.xid = b.right.id and b.left.id = :#{#group.xid}")
    List<XUser> getUserMember(XGroup group);

    @Query(value = "select new com.flame.common.TwoEntry(a, b) from XUser a, XGroupUserLink b where a.xid = b.right.id and b.left.id = :#{#group.xid}")
    List<TwoEntry<XUser, XGroupUserLink>> getUserMembers(XGroup group);

    @Query(value = "select a from XUser a, XGroupUserLink b where a.xid = b.right.id and b.left.id = :#{#groupRef.id}")
    List<XUser> getUserMember(ObjectReference<?> groupRef);

    @Query(value = "select a from XGroup a, XGroupUserLink b where a.xid = b.left.id and b.right.id = :#{#user.xid}")
    List<XGroup> getUserParentGroup(XUser user);

    @Query(value = "select a from XGroup a, XGroupUserLink b where a.xid = b.left.id and b.right.id = :#{#user.id}")
    List<XGroup> getUserParentGroup(ObjectReference<?> user);

    @Query(value = "select a from XGroup a, XGroupGroupLink b where a.xid = b.left.id and b.right.id = :#{#group.xid}")
    List<XGroup> getGroupParentGroup(XGroup group);

    @Query(value = "select a from XGroup a, XGroupGroupLink b where a.xid = b.left.id and b.right.id = :#{#group.id}")
    List<XGroup> getGroupParentGroup(ObjectReference<?> group);

    @Query(value = "select a from XPrincipalFavorite a where a.favorite.id = :#{#principal.xid} and a.creator.xid = :#{#xuser.xid}")
    XPrincipalFavorite getXPrincipalFavorite(XPrincipal principal, XUser xuser);

    @Query(value = "select a from RoleRB a where a.roleType = :roleType")
    List<RoleRB> findByRoleType(RoleType roleType);

    @Query(value = "select a from XGroupUserLink a where a.left.id = :#{#group.xid}")
    List<XGroupUserLink> findGroupUserLinks(XGroup group);

    @Query(value = "select a from XGroupGroupLink a where a.left.id = :#{#group.xid}")
    List<XGroupGroupLink> findGroupGroupLinksByParent(XGroup group);

    @Query(value = "select a from XGroupGroupLink a where a.right.id = :#{#group.xid}")
    List<XGroupGroupLink> findGroupGroupLinksByChild(XGroup group);
}
