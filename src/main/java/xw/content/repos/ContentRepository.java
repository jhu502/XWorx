package xw.content.repos;

import xw.content.entity.XApplicationData;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@EntityScan(basePackages = {"xw.content"})
public interface ContentRepository extends JpaRepository<XApplicationData, Long> {
}
