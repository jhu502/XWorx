package xw.action.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flame.action.IActionModel;
import com.flame.orm.XObject;

import xw.action.entity.AbstractAction;
import xw.action.entity.XActionFavorite;
import xw.action.entity.XActionItem;
import xw.action.entity.XActionModel;
import xw.auths.entity.XUser;

@Repository
public interface XActionRepository extends JpaRepository<XActionItem, Long> {
    @Query(value = "select a from XActionFavorite a where a.creator.xid = :#{#xuser.xid}")
    List<XActionFavorite> listXFavoriteObjects(XUser xuser);

    @Query(value = "select a from XActionFavorite a where a.favorite.id = :#{#object.xid} and a.creator.xid = :#{#xuser.xid}")
    XActionFavorite getXFavoriteObject(XObject object, XUser xuser);

    @Query(value = "select a from XActionItem a where a.name like :name and a.display like :display")
    List<XActionItem> findXActionItems(@Param("name") String name, @Param("display") String display);

    @Query(value = "select a from XActionModel a where a.name like :name and a.display like :display")
    List<XActionModel> findXActionModels(@Param("name") String name, @Param("display") String display);

    @Query(value = "select a from XActionItem a where a.name like :name and a.type like :type")
    XActionItem getXActionItem(@Param("name") String name, @Param("type") String type);

    @Query(value = "select a from XActionModel a where a.name like :name and a.type like :type")
    XActionModel getXActionModel(@Param("name") String name, @Param("type") String type);

    @Query(value = "select a from XActionModel a where a.type = :type")
    List<XActionModel> queryXActionModels(@Param("type") String type);

    @Query(value = "select c from XActionItemLink b, XActionItem c where b.left.id = :#{#model.xid} and b.right.id = c.xid")
    List<XActionItem> getChildItems(IActionModel model);

    @Query(value = "select c from XActionItemLink b, XActionModel c where b.left.id = :#{#model.xid} and b.right.id = c.xid")
    List<XActionModel> getChildModels(IActionModel model);

    @Query(value = "select c, b from XActionModel a, XActionItemLink b, XActionItem c where a.type = :#{#model.type} and a.name = :#{#model.name} and a.xid = b.left.id and b.right.id = c.xid")
    List<Object[]> getXActionItemAndLink(IActionModel model);

    @Query(value = "select c, b from XActionModel a, XActionItemLink b, XActionModel c where a.type = :#{#model.type} and a.name = :#{#model.name} and a.xid = b.left.id and b.right.id = c.xid")
    List<Object[]> getXActionModelAndLink(IActionModel model);

    @Query(value = "select a, b from XActionModel a, XActionItemLink b where a.xid = b.left.id and b.right.id = :#{#action.xid}")
    List<Object[]> findUsedByModelAndLink(AbstractAction action);
}
