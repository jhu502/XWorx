package com.thing.repos;

import java.util.List;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.flame.thing.IPropertyDefinition;
import com.flame.thing.IPropertyLayout;
import com.flame.thing.IServiceDefinition;
import com.flame.thing.IThingModel;
import com.flame.thing.LayoutType;
import com.thing.entity.XThingModel;

@Repository
@EntityScan(basePackages = {"com.thing"})
public interface ThingRepository extends JpaRepository<XThingModel, Long> {
    @Query(value = "select a from XThingModel a where a.number=:number")
    IThingModel getThingModel(String number);

    @Query(value = "select a from XThingModel a where a.thingModel is null")
    List<IThingModel> getRootModel();

    @Query(value = "select a from XThingModel a where a.thingModel.xid=:#{#model.xid}")
    List<IThingModel> getChildModel(IThingModel model);

    @Query(value = "select a from XPropertyDefinition a where a.propertyProvider =:#{#model}")
    List<IPropertyDefinition> getPropertyDefinition(IThingModel model);

    @Query(value = "select a from XPropertyDefinition a where a.propertyProvider=:#{#model} and a.name=:name")
    IPropertyDefinition getPropertyDefinition(IThingModel model, String name);

    @Query(value = "select a from XServiceDefinition a where a.serviceProvider =:#{#model}")
    List<IServiceDefinition> getServiceDefinition(IThingModel model);

    @Query(value = "select a from XPropertyLayout a where a.propertyProvider =:#{#model}")
    List<IPropertyLayout> getPropertyLayout(IThingModel model);

    @Query(value = "select a from XPropertyLayout a where a.propertyProvider =:#{#model} and a.layoutType = :type")
    List<IPropertyLayout> getPropertyLayout(IThingModel model, LayoutType type);
}
