package com.flame.orm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.SessionFactory;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import com.flame.config.JPAConfiguration;
import com.flame.auths.ICreatorInfo;
import com.flame.auths.IUser;
import com.flame.thing.IModelManaged;
import com.flame.thing.IThingModel;
import com.flame.thing.IThingModelManager;
import com.flame.vc.Iterated;
import com.flame.vc.Master;
import com.flame.util.XException;

import jakarta.annotation.Resource;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class XPersistenceServiceImpl implements XPersistenceService {
	private static Logger logger = LoggerFactory.getLogger(XPersistenceService.class);
	private final Map<String, Class<? extends XPersistable>> entityMap = new HashMap<>();
	private final Map<String, EntityPersister> persistMap = new HashMap<>();
	private final Map<Class<?>, Set<Class<? extends XPersistable>>> superClassMap = new HashMap<>();
	private IThingModelManager modelManager;
	@PersistenceContext
	private EntityManager entityManager;
	@Resource
	private PlatformTransactionManager transManager;
	@Resource
	private ApplicationContext applicationContext;

	private IThingModelManager getModelManager() {
		if (modelManager == null) {
			modelManager = applicationContext.getBean(IThingModelManager.class);
		}

		return modelManager;
	}

	private Query setParameter(Query query, Object[][] params) {
		if (query == null || params == null)
			return query;

		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				Object[] o = params[i];
				query.setParameter((String) o[0], o[1]);
			}
		}

		return query;
	}

	@Transactional
	public List<?> query(String hql, Object[][] params) {
		Query query = entityManager.createQuery(hql);
		return this.setParameter(query, params).getResultList();
	}

	public <T> List<T> query(Class<T> clazz) {
		return this.query(clazz, new Object[0][0]);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> query(Class<T> clazz, Object[][] params) {
		if (params == null) {
			return (List<T>) this.query(clazz);
		}

		StringBuilder hql = new StringBuilder();
		hql.append("select a from ").append(clazz.getSimpleName()).append(" a ");

		List<Object[]> resultList = new ArrayList<>();
		if (params.length > 0) {
			hql.append(" where ");
			boolean bool = true;
			int i = 0;
			for (Object[] objs : params) {
				String field = (String) objs[0];
				Object value = objs[1];

				if (value == null) {
					if (bool) {
						hql.append("a.").append(field).append(" is null");
					} else {
						hql.append(" and ").append("a.").append(field).append(" is null");
					}
					bool = false;
				} else {
					String param = "A" + i;
					resultList.add(new Object[] { param, value });
					if (bool) {
						hql.append("a.").append(field).append("=").append(":").append(param);
					} else {
						hql.append(" and ").append("a.").append(field).append("=").append(":").append(param);
					}
					bool = false;
					i = i + 1;
				}
			}
		}
		return (List<T>) this.query(hql.toString(), resultList.toArray(new Object[0][0]));
	}

	public List<?> nativeQuery(String sql, Class<?> resultClass, Object[][] p) {
		Query q = entityManager.createNativeQuery(sql, resultClass);
		return this.setParameter(q, p).getResultList();
	}

	@Override
	public <T extends XPersistable> T find(String oid) {
		if (oid == null || "".equals(oid))
			return null;

		if (ObjectReference.isOid(oid)) {
			ObjectReference<?> objectRef = ObjectReference.newObjectReference(oid);
			@SuppressWarnings("unchecked")
			Class<T> clazz = (Class<T>) objectRef.getObjectClass();
			Long id = objectRef.getId();
			if (XPersistable.class.isAssignableFrom(clazz)) {
				return entityManager.find(clazz, id);
			}
		}
		return null;
	}

	@Override
	public <T extends XPersistable> T find(Class<T> clazz, long id) {
		return entityManager.find(clazz, id);
	}

	@Override
	public <T extends XPersistable> T find(Class<T> clazz, long id, Map<String, Object> properties) {
		return entityManager.find(clazz, id, properties);
	}

	public <T extends XPersistable> T refresh(ObjectReference<T> reference) {
		if (reference == null)
			return null;

		return entityManager.find(reference.getObjectClass(), reference.getId());
	}

	public <T extends XPersistable> T refresh(T persistable) {
		if (entityManager.contains(persistable)) {
			entityManager.refresh(persistable);
			return persistable;
		}
		return entityManager.find((Class<T>) persistable.getClass(), persistable.getXid());
	}

	public boolean isPersist(XPersistable persist) {
		if (persist == null)
			throw new XException("Input parameter is null.");

		return persist.getXid() > 0;
	}

	@Transactional
	public <T extends XPersistable> T insert(T persist) {
		if (persist == null) {
			XException.throwException("Input parameter is null.");
		}
		if (persist instanceof XObject) {
			XObject xObject = (XObject) persist;
			xObject.setCreatedStamp(new Timestamp((new Date()).getTime()));
			xObject.setModifiedStamp(new Timestamp((new Date()).getTime()));
		}
		entityManager.persist(persist);

        logger.trace("Succeed to insert object {}", persist);
		return persist;
	}

	@Transactional
	public <T extends XPersistable> T update(T persist) {
		if (persist == null)
			XException.throwException("Input parameter is null.");
		if (persist instanceof XObject) {
			XObject cobj = (XObject) persist;
			cobj.setModifiedStamp(new Timestamp((new Date()).getTime()));
		}
		return entityManager.merge(persist);
	}

	/**
	 * 保存对象到数据库，判断传入对象是否已经持久化，已经持久化调用update方法，否则调用insert方法；
	 * 1、判断对象是否实现了Iterated接口，若是，首先insert对应的Master；
	 * 2、判断对象是否继承于ModelEntity，若是，判断是否设置了ThingModel属性，若否，获取对应默认的ThingModel对象；
	 */
	@Transactional
	public <T extends XPersistable> T save(T persist) {
		if (persist == null)
			XException.throwException("Input parameter is null.");
		if (PersistenceHelper.isPersistent(persist)) {
			this.update(persist);
			return persist;
		} else {
			if (this.isPersist(persist)) {
				return this.update(persist);
			} else {
				if (persist instanceof Iterated) {
					@SuppressWarnings("unchecked")
					Iterated<Master> iterated = (Iterated<Master>) persist;
					Master master = iterated.getMaster();
					if (!PersistenceHelper.isPersistent(master)) {
						master = this.save(master);
						iterated.setMaster(master);
					}
				}
				if (persist instanceof IModelManaged) {
					IModelManaged typeManaged = (IModelManaged) persist;
					IThingModel thingModel = typeManaged.getThingModel();
					if (thingModel == null) {
						thingModel = this.getModelManager().getThingModel(persist.getClass());
						typeManaged.setThingModel(thingModel);
					}
				}
				if (persist instanceof ICreatorInfo) {
					@SuppressWarnings("unchecked")
					ICreatorInfo<IUser> creatorInfo = (ICreatorInfo<IUser>) persist;

					IUser user = creatorInfo.getCreator();
					if (user == null) {
						user = JPAConfiguration.currentUser();
						if (user == null)
							throw new XException("Missing identity information!");
						creatorInfo.setCreator(user);
					}
				}
				return this.insert(persist);
			}
		}
	}

	/**
	 * Hibernate object states有三种状态：Transient、Persistent、Detached, Detached instance是一个已经持久化的对象，但是它的Session已经关闭了，它的引用依然有效;
	 * 重新修改这个Detached Object就是绑定到当前的Sesssion，etEntityManager()提供merge方法实现
	 */
	@Transactional

	public void remove(XPersistable persist) {
		getEntityManager().remove(getEntityManager().merge(persist));
	}

	@Transactional
	public <T extends XPersistable> void remove(List<T> persists) {
		for (XPersistable persist : persists) {
			entityManager.remove(persist);
		}
	}

	public Set<Class<? extends XPersistable>> getEntityClass(Class<? extends XPersistable> clazz) {
		if (this.superClassMap.isEmpty()) {
			this.loadEntityMetaModelInfos();
		}
		return this.superClassMap.get(clazz);
	}

	@Override
	public Class<? extends XPersistable> getEntityClass(String name) {
		if (this.entityMap.isEmpty()) {
			this.loadEntityMetaModelInfos();
		}
		return this.entityMap.get(name);
	}

	@Override
	public EntityPersister getEntityPersister(String name) {
		if (this.persistMap.isEmpty()) {
			this.loadEntityMetaModelInfos();
		}
		return this.persistMap.get(name);
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public PlatformTransactionManager getTransactionManager() {
		return this.transManager;
	}

	/**
	 * 遍历EntityManager中管理的所有的实体类，收集@Entity&@MappedSuperclass的关系建立映射，以实现
	 * 可以通过@MappedSuperclass注解的类找到所有的@Entity注解的实体类；
	 * 例如：通过传入@MappedSuperclass注解类，可以动态生成多个@Entity的HQL进行合并查询以达到查询
	 */
	private void loadEntityMetaModelInfos() {
		EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Stream<EntityPersister> streamPersister = ((MappingMetamodel) sessionFactory.getMetamodel()).streamEntityDescriptors();
		Map<String, EntityPersister> persistMap = streamPersister.collect(Collectors.toMap(p -> p.getEntityName(), p -> p));
		for (Map.Entry<String, EntityPersister> entry : persistMap.entrySet()) {
			String entityClassName = entry.getKey();
			EntityPersister entityPersister = entry.getValue();
			/**
			 * 收集注解@MappedSuperclass类与对应注解@Entity类的映射关系
			 */
			@SuppressWarnings("unchecked")
			Class<? extends XPersistable> entityClass = (Class<? extends XPersistable>) entityPersister.getMappedClass();
			for (Class<?> clazz : this.getMappedSuperClass(entityClass)) {
				if (XPersistable.class.isAssignableFrom(clazz)) {
					Set<Class<? extends XPersistable>> entitySet = this.superClassMap.get(clazz);
					if (entitySet == null) {
						entitySet = new HashSet<>();
						this.superClassMap.put(clazz, entitySet);
					}
					entitySet.add(entityClass);
				}
			}
			String simpleName = entityClassName.substring(entityClassName.lastIndexOf(".") + 1);
			this.entityMap.put(simpleName, entityClass);
			this.persistMap.put(simpleName, entityPersister);
		}
	}

	/**
	 * 获取参数Class<?>的所有带有注解:@MappedSuperclass的父类
	 * @param clazz 入参类
	 * @return
	 */
	private Set<Class<?>> getMappedSuperClass(Class<?> clazz) {
		Set<Class<?>> superSet = new HashSet<>();
		Entity entity = clazz.getAnnotation(Entity.class);
		if (entity != null) {
			clazz = clazz.getSuperclass();
		}
		while (clazz != null) {
			MappedSuperclass mappedClass = clazz.getAnnotation(MappedSuperclass.class);
			if (mappedClass == null) {
				clazz = null;
			} else {
				superSet.add(clazz);
				clazz = clazz.getSuperclass();
			}
		}
		return superSet;
	}
}
