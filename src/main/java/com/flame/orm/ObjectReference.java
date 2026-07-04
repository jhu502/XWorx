package com.flame.orm;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.util.XException;

@Embeddable
public class ObjectReference<T extends XPersistable> implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Pattern PATTERN_OID = Pattern.compile("(OR:)?([a-z0-9A-Z]+(\\.))+[a-z0-9A-Z]+(:[0-9]+)");
	@Basic
	@Column(name = "id_a1")
	protected long id = -1;
	@Basic
	@Column(name = "classname_a1")
	protected String className = "";
	@JsonIgnore
	@Transient
	protected Class<T> objClass;
	@JsonIgnore
	@Transient
	protected T persist;

	public static ObjectReference<? extends XPersistable> newObjectReference(String oid) {
		return new ObjectReference<>(oid);
	}

	public static <T extends XPersistable> ObjectReference<T> newObjectReference(T persist) {
		return new ObjectReference<>(persist);
	}

	public ObjectReference() {
	}

	public ObjectReference(T persist) {
		this.id = persist.getXid();
		this.className = persist.getClass().getName();
	}

	@SuppressWarnings("unchecked")
	public ObjectReference(Class<? extends XPersistable> cls, long id) {
		this.id = id;
		this.className = cls.getName();
		this.objClass = (Class<T>) cls;
	}

	public ObjectReference(String oid) {
		String[] array = breakOid(oid);

		this.id = Long.parseLong(array[2]);
		this.className = array[1];
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		if (this.id < 0) {
			this.id = id;
		} else {
			throw new XException("Setting id manually is not allowed");
		}
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String classname) {
		this.className = classname;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getObjectClass() {
		if (this.persist != null) {
			return (Class<T>) this.persist.getClass();
		} else {
			try {
				return (Class<T>) Class.forName(this.className);
			} catch (ClassNotFoundException e) {
				throw new XException(e);
			}
		}
	}

	public String toString() {
		return "OR:" + this.className + ":" + this.id;
	}

	public T getObject() {
		if (this.persist != null) {
			return this.persist;
		} else {
			if (this.id > 0) {
				this.persist = PersistenceHelper.service().refresh(this);
			} else {
				try {
					Class<T> clazz = this.getObjectClass();
					Constructor<T> constructor = clazz.getConstructor();
					this.persist = constructor.newInstance();
				} catch (XException e) {
					throw e;
				} catch (Exception e) {
					throw new XException(e);
				}
			}
			return this.persist;
		}
	}

	public static boolean isOid(String oid) {
		if (oid == null)
			return false;

		Matcher matcher = PATTERN_OID.matcher(oid);
		if (matcher.matches())
			return true;
		else
			return false;
	}
    
	private static String[] breakOid(String oid) {
		if (oid == null)
			return new String[0];

		Matcher matcher = PATTERN_OID.matcher(oid);
		if (!matcher.matches())
			throw new XException("Oid(" + oid + ") is incorrect Oid format.");

		String[] splits = oid.split(":");
		if (splits.length == 2) {
			return new String[]{"OR", splits[0], splits[1]};
		} else {
			return splits;
		}
	}
}
