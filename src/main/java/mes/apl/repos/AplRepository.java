package mes.apl.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mes.apl.XAssemblyLine;
import mes.equipt.XEquipment;
import mes.equipt.XEquiptInstance;

@Repository
public interface AplRepository extends JpaRepository<XAssemblyLine, Long> {
	@Query(value = "select a, b from XAssemblyLineUsageLink a, XEquipment b where a.right.id = b.master.xid and a.left.id = :#{#xpline.xid}")
	List<?> getUsedbyXPLine(XAssemblyLine xpline);

	@Query(value = "select a, b from XEquiptInstanceLink a, XEquiptInstance b where a.right.id = b.master.xid and a.left.id = :#{#equipment.master.xid}")
	List<?> getEquiptInstance(XEquipment equipment);

	@Query(value = "select a from XEquiptParameter a where a.instanceRef.id = :#{#instance.xid}")
	List<?> getEquiptParameter(XEquiptInstance instance);
}
