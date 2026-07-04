package plm.dynamic.repos;

import plm.dynamic.XCaseTable;
import plm.dynamic.XExpression;
import plm.part.XPart;
import plm.dynamic.XCharacteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DynamicRepository extends JpaRepository<XCharacteristic, Long> {
    @Query(value = "select a from XCharacteristic a where a.characted.xid = :#{#part.xid} order by a.sortNo asc")
    List<XCharacteristic> getSortedXCharacteristic(XPart part);

    @Query(value = "select max(a.sortNo) from XCharacteristic a where a.characted.xid = :#{#part.xid}")
    List<?> getMaxCharactSortNo(XPart part);

    @Query(value = "select a from XCharacteristic a where a.characted.xid = :#{#part.xid}")
    List<XCharacteristic> getRelatedCharacteristic(XPart part);

    @Query(value = "select a from XCharacteristic a where a.characted.xid = :#{#part.xid} and a.name = :name")
    List<XCharacteristic> getRelatedCharacteristic(XPart part, String name);

    @Query(value = "select a from XCaseTable a where a.part.xid = :#{#part.xid}")
    List<XCaseTable> getRelatedXCaseTable(XPart part);

    @Query(value = "select a from XExpression a where a.part.xid = :#{#part.xid}")
    List<XExpression> getRelatedXExpression(XPart part);

    @Query(value = "select a from XExpression a where a.part.xid = :#{#part.xid} and a.name = :name")
    List<XExpression> getRelatedXExpression(XPart part, String name);
}
