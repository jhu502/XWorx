package com.flame.xui;

import com.flame.config.FlameConfiguration;
import com.flame.orm.XPersistable;

import java.util.Map;
import java.util.Map.Entry;

public class HREFactory {
	private static String contextPath = null;

	private HREFactory() {
	}

	public static String getContextPath() {
		if (contextPath == null) {
			contextPath = FlameConfiguration.getContext();
		}

		return contextPath;
	}

	public static String getHREF(String uri) {
		return getHREF(uri, Map.of(), false);
	}

	public static String getHREF(String uri, Map<String, Object> paramMap) {
		return getHREF(uri, paramMap, false);
	}

	public static String getHREF(String uri, Map<String, Object> paramMap, boolean anchor) {
		StringBuilder hrefBuf = new StringBuilder();
		if (uri == null) {
			return hrefBuf.toString();
		}
		String baseHref = getBaseHREF();
		if (uri.startsWith(baseHref)) {
			hrefBuf.append(uri);
		} else {
			String context = getContextPath();
			String baseUri = "";
			if (context.endsWith("/")) {
				if (anchor)
					baseUri = context + "#";
				else
					baseUri = context;
			} else {
				if (anchor)
					baseUri = context + "/#";
				else
					baseUri = context + "/";
			}

			if (uri.startsWith(baseUri)) {
				hrefBuf.append(uri);
			} else {
				if (anchor) {
					if (uri.startsWith("/#")) {
						hrefBuf.append(baseUri).append(uri.substring(2));
					} else if (uri.startsWith("#")) {
						hrefBuf.append(baseUri).append(uri.substring(1));
					} else {
						hrefBuf.append(baseUri).append(uri);
					}
				} else {
					if (uri.startsWith("/")) {
						hrefBuf.append(baseUri).append(uri.substring(1));
					} else {
						hrefBuf.append(baseUri).append(uri);
					}
				}
			}
		}

		if (paramMap == null || paramMap.isEmpty())
			return hrefBuf.toString();

		boolean bool = hrefBuf.toString().contains("?");
		for (Entry<String, Object> entry : paramMap.entrySet()) {
			if (bool) {
				hrefBuf.append("&").append(entry.getKey()).append("=").append(entry.getValue().toString());
			} else {
				hrefBuf.append("?").append(entry.getKey()).append("=").append(entry.getValue().toString());
				bool = true;
			}
		}
		return hrefBuf.toString();
	}

	public static String getInfoPage(String query) {
		if (query == null)
			return "";

		StringBuilder urlBuf = new StringBuilder();
		urlBuf.append("XUI$/info/InfoPage?").append(query);

		return urlBuf.toString();
	}

	public static String getInfoPage(XPersistable persist) {
		if (persist == null)
			return "";

		return getInfoPage(Map.of("oid", persist.getOid()));
	}

	public static String hashInfoPage(XPersistable persist) {
		if (persist == null)
			return "";

		return hashInfoPage(Map.of("oid", persist.getOid()));
	}

	public static String getInfoPage(Map<String, Object> paramMap) {
		StringBuilder query = new StringBuilder();
		for (Entry<String, Object> entry : paramMap.entrySet()) {
			if (query.length() == 0) {
				query.append(entry.getKey()).append("=").append(entry.getValue().toString());
			} else {
				query.append("&").append(entry.getKey()).append("=").append(entry.getValue().toString());
			}
		}

		return getInfoPage(query.toString());
	}

	public static String hashInfoPage(Map<String, Object> paramMap) {
		StringBuilder query = new StringBuilder();
		for (Entry<String, Object> entry : paramMap.entrySet()) {
			if (query.length() == 0) {
				query.append(entry.getKey()).append("=").append(entry.getValue().toString());
			} else {
				query.append("&").append(entry.getKey()).append("=").append(entry.getValue().toString());
			}
		}

		return hashInfoPage(query.toString());
	}

	public static String hashInfoPage(String query) {
		if (query == null)
			return "";

		StringBuilder urlBuf = new StringBuilder();
		urlBuf.append("#XUI$/info/InfoPage?").append(query);

		return urlBuf.toString();
	}

	public static String getBaseHREF() {
		return FlameConfiguration.getScheme() + "://" + FlameConfiguration.getDomain() + FlameConfiguration.getContext() + "/";
	}
}
