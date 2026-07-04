package com.flame.orm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.flame.config.JPAConfiguration;

public class PersistenceHelper {
	private static Pattern PATTERN_OID = Pattern.compile("(OR:)?([a-z0-9A-Z]+(\\.))+[a-z0-9A-Z]+(:[0-9]+)");
	private static XPersistenceService service;

	private PersistenceHelper() {
	}

	public static XPersistenceService service() {
		if (service == null) {
			service = JPAConfiguration.getBean(XPersistenceService.class);
		}

		return service;
	}

    public static <T extends XPersistable> T getPersistable(Class<T> clazz, long xid) {
        return service().find(clazz, xid);
    }

	public static XPersistable getPersistable(String oid) {
		return service().refresh(new ObjectReference<XPersistable>(oid));
	}

	public static boolean isPersistent(XPersistable persist) {
		if (persist == null)
			return false;
		else
			return persist.getXid() > 0;
	}

	public static boolean isOidFormat(String oid) {
		if (oid == null)
			return false;
		if (oid.trim().isEmpty())
			return false;
		Matcher matcher = PATTERN_OID.matcher(oid);
		if (matcher.matches())
			return true;
		else
			return false;
	}
}
