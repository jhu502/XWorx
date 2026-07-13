package plm.part.repos;

import plm.part.XPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<XPart, Long> {
    @Query(value = "select a, b from XPartUsageLink a, XPart b where a.left.id = :#{#part.xid} and a.right.id = b.master.xid")
    List<?> getUsedbyXPart(XPart part);

    @Query(value = "select p from XPart p where p.master.number like :keyword or p.master.name like :keyword")
    List<XPart> findPartFuzzy(String keyword);
}
