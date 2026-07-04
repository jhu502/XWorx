package com.thing.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.flame.annotations.XConfig;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.thing.IModelManaged;
import com.flame.thing.IThingModel;
import com.flame.type.IPrimitiveType;
import com.flame.util.XException;
import com.google.common.collect.Sets;
import com.thing.ThingUtilities;
import com.thing.entity.IModeledEntity;

import jakarta.persistence.Entity;

/**
 * 用来管理ConfigEntity，例如：XPrincipal、XUser、XGroup、FTDatabase、FTProject等等
 * 
 * @author hujin
 *
 */

@Service
public class ConfigEntityManager implements IEntityManager {
	private static final Logger logger = LoggerFactory.getLogger(ConfigEntityManager.class);

	/**
	 * 需要进行增强，特别是性能方面的考虑，因为每次远程调用都会涉及到ConfigEntity的查找
	 * @thingIdentity e.g.:(XUser:Guest)
	 * @return
	 */
	public IModelManaged findConfigEntity(String thingIdent) {
		if (!ThingUtilities.checkThingIdentity(thingIdent)) {
			logger.error("Thing identity:{} pattern error.", thingIdent);
			throw new XException("Thing identity(" + thingIdent + ") pattern error.");
		}

		String[] args = thingIdent.split(":");
		String type = args[0];
		String key = args[1];

		Class<?> typeCls = PersistenceHelper.service().getEntityClass(type);
		if (IModelManaged.class.isAssignableFrom(typeCls)) {
			try {
				String pfield = (String) typeCls.getMethod("getIdentityField").invoke(null);

				List<?> list = this.queryConfigEntity(type, pfield + "='" + key + "'");
				if (list.isEmpty()) {
					return null;
				} else {
					return (IModelManaged) list.get(0);
				}
			} catch (Exception e) {
				throw new XException(e);
			}
		} else {
			throw new XException(type + " does not inherit from IThingEntity.");
		}
	}

	public List<?> queryConfigEntity(IThingModel model, String field, String value) {
		Class<?> modelCls = model.getEntityClass();
		Entity entity = modelCls.getAnnotation(Entity.class);
		if (entity == null) {
			return Collections.EMPTY_LIST;
		}

		if (IThingModel.class.isAssignableFrom(modelCls)) {
			if (field != null && !"".equals(field.trim())) {
				String hql = "select a from " + modelCls.getSimpleName() + " a where a.id = :id and a." + field + " like :value";
				return PersistenceHelper.service().query(hql, new Object[][] { { "id", model.getXid() }, { "value", "%" + value + "%" } });
			} else {
				String hql = "select a from " + modelCls.getSimpleName() + " a where a.id = :id";
				return PersistenceHelper.service().query(hql, new Object[][] { { "id", model.getXid() } });
			}
		} else {
			if (field != null && !"".equals(field.trim())) {
				String hql = "select a from " + modelCls.getSimpleName() + " a where a.modelMasterId = :id and a." + field + " like :value";
				return PersistenceHelper.service().query(hql, new Object[][] { { "id", model.getXid() }, { "value", "%" + value + "%" } });
			} else {
				String hql = "select a from " + modelCls.getSimpleName() + " a where a.modelMasterId = :id";
				return PersistenceHelper.service().query(hql, new Object[][] { { "id", model.getXid() } });
			}
		}
	}

	public List<?> queryConfigEntity(String model, String condition) {
		return PersistenceHelper.service().query("select a from " + model + " a where a." + condition, new Object[][] {});
	}

	public IModelManaged createConfigEntity(IThingModel thgModel, Map<String, String> params) {
		Class<?> modelCls = thgModel.getEntityClass();

		if (IModeledEntity.class.isAssignableFrom(modelCls)) {
			try {
				Constructor<?> constructor = modelCls.getConstructor();
				IModeledEntity thingEntity = (IModeledEntity) constructor.newInstance();
				thingEntity.setNumber(params.get("number"));
				thingEntity.setName(params.get("name"));
				thingEntity.setDescription(params.get("description"));

				List<Field> list = ThingUtilities.getDeclaredFields(modelCls, Sets.newHashSet(new Class<?>[] { XObject.class, IModeledEntity.class }));
				for (Field field : list) {
					XConfig config = field.getAnnotation(XConfig.class);
					if (config != null && config.created()) {
						field.setAccessible(true);
						IPrimitiveType<?> primitiveType = config.baseType().getPrimitive(params.get(field.getName()));
						field.set(thingEntity, primitiveType.getValue());
					}
				}
				thingEntity.setThingModel(thgModel);

				return PersistenceHelper.service().save(thingEntity);
			} catch (XException e) {
				throw e;
			} catch (Exception e) {
				throw new XException(e);
			}
		}
		return null;
	}

	public IModelManaged updateConfigEntity(IModeledEntity entity, Map<String, String> params) {
		try {
			entity.setNumber(params.get("number"));
			entity.setName(params.get("name"));
			entity.setDescription(params.get("description"));

			List<Field> list = ThingUtilities.getDeclaredFields(entity.getClass(), Sets.newHashSet(new Class<?>[] { XObject.class, IModeledEntity.class }));
			for (Field field : list) {
				XConfig config = field.getAnnotation(XConfig.class);
				if (config != null && config.modified()) {
					field.setAccessible(true);
					IPrimitiveType<?> primitiveType = config.baseType().getPrimitive(params.get(field.getName()));
					field.set(entity, primitiveType.getValue());
				}
			}

			return PersistenceHelper.service().save(entity);
		} catch (XException e) {
			throw e;
		} catch (Exception e) {
			throw new XException(e);
		}
	}
}
