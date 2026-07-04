package com.flame.orm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.persister.entity.EntityPersister;
import org.springframework.transaction.PlatformTransactionManager;

public interface XPersistenceService {
	List<?> query(String hql, Object[][] params);
	
	<T> List<T> query(Class<T> clazz, Object[][] params);

	List<?> nativeQuery(String sql, Class<?> resultClass, Object[][] params);

	<T extends XPersistable> T find(Class<T> clazz, long id);

	<T extends XPersistable> T find(Class<T> clazz, long id, Map<String, Object> properties);

	<T extends XPersistable> T find(String oid);

	<T extends XPersistable> T refresh(ObjectReference<T> reference);

	<T extends XPersistable> T refresh(T persist);

	<T extends XPersistable> T update(T persist);

	<T extends XPersistable> T insert(T persist);

	<T extends XPersistable> T save(T persist);

	void remove(XPersistable persist);
	
	<T extends XPersistable> void remove(List<T> persists);
	
	Class<? extends XPersistable> getEntityClass(String name);
	
	Set<Class<? extends XPersistable>> getEntityClass(Class<? extends XPersistable> clazz);

	EntityPersister getEntityPersister(String name);
	
	PlatformTransactionManager getTransactionManager();
}
