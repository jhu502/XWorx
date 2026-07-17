package xw.content.repos;

import org.springframework.data.jpa.repository.Query;
import xw.content.ContentItem;
import xw.content.IContentHolder;
import xw.content.entity.XApplicationData;
import xw.content.entity.XResourceData;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EntityScan(basePackages = {"xw.content"})
public interface ContentRepository extends JpaRepository<ContentItem, Long> {

	@Query(value = "select a from XApplicationData a, XHolderToContent b where a.xid = b.right.id and b.left.id = :#{#holder.xid}")
	List<XApplicationData> queryXApplicationData(IContentHolder holder);

	@Query(value = "select a from XResourceData a, XHolderToContent b where a.xid = b.right.id and b.left.id = :#{#holder.xid}")
	List<XResourceData> queryXResourceData(IContentHolder holder);

	@Query(value = "select a from XResourceData a, XHolderToContent b where a.xid = b.right.id and b.left.id = :#{#holder.xid} and a.uploadedPath = :uploadedPath")
	XResourceData getXResourceData(IContentHolder holder, String uploadedPath);
}
