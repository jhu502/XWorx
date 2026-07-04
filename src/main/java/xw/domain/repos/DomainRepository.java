package xw.domain.repos;

import xw.domain.entity.XAdminDomain;
import com.flame.orm.ObjectReference;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EntityScan(basePackages = {"xw.domain"})
public interface DomainRepository extends JpaRepository<XAdminDomain, Long> {
    @Query(value = "select a from XAdminDomain a where a.number='/'")
    XAdminDomain getRootDomain();

    @Query(value = "select a from XAdminDomain a where a.number=:number")
    List<XAdminDomain> getDomainByNumber(String number);

    @Query(value = "select a from XAdminDomain a where a.adminDomain.xid=:#{#upper.xid}")
    List<XAdminDomain> queryChildDomain(XAdminDomain upper);

    @Query(value = "select a from XAdminDomain a where a.adminDomain.xid=:#{#upper.xid} and a.number=:domainNo")
    XAdminDomain queryChildDomain(XAdminDomain upper, String domainNo);

    @Query(value = "select a from XAdminDomain a where a.adminDomain.xid=:#{#domainRef.id}")
    List<XAdminDomain> queryChildDomain(ObjectReference<XAdminDomain> domainRef);
}
