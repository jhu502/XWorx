package mes.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flame.orm.XObject;

@Repository
public interface MesRepository extends JpaRepository<XObject, Long> {
}