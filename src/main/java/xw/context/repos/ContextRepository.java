package xw.context.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import xw.auths.entity.XUser;
import xw.context.entity.Container;
import xw.context.entity.XFolder;
import xw.context.entity.XLibrary;
import xw.context.entity.XOrganization;
import xw.context.entity.XSite;

@Repository
public interface ContextRepository extends JpaRepository<XFolder, Long> {
    @Query(value = "select a from XSite a")
    XSite getXSite();

    @Query(value = "select a from XOrganization a where a.container.xid = :#{#site.xid}")
    List<XOrganization> getXOrganization(XSite site);

    @Query(value = "select a from XOrganization a where a.number = :number")
    XOrganization getXOrganization(String number);

    @Query(value = "select a from XFolder a where a.container.id = :#{#container.xid}")
    List<XFolder> listFolder(Container container);

    @Query(value = "select a from XFolder a where a.folder.xid = :#{#folder.xid}")
    List<XFolder> listFolder(XFolder folder);

    @Query(value = "select a from XLibrary a")
    List<XLibrary> allLibrary();

    @Query(value = "select a from XLibrary a where a.container.xid = :#{#user.organization.xid}")
    List<XLibrary> listLibrary(XUser user);

    @Query(value = "select a from XLibrary a where a.container.xid = :#{#xorg.xid}")
    List<XLibrary> listLibrary(XOrganization xorg);
}
