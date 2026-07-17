package xw.content;

import java.lang.reflect.Constructor;

import com.flame.util.XException;

import xw.content.entity.XApplicationData;
import xw.content.entity.XResourceData;

public enum ContentType {
	PRIMARY(XApplicationData.class, "Primary"), //
	SECONDARY(XApplicationData.class, "Secondary"), //
	THUMBNAIL3D(XApplicationData.class, "Thumbnail3D"), //
	RESOURCE(XResourceData.class, "resource"); //
	
	private Class<? extends ContentItem> entityClass;
	private String display;

	ContentType(Class<? extends ContentItem> clazz, String display) {
		this.entityClass = clazz;
		this.display = display;
	}
	
	public ContentItem newContentItem() throws XException {
		try {
			Constructor<? extends ContentItem> constructor = entityClass.getConstructor();
			ContentItem contentItem = constructor.newInstance();
			contentItem.setContentType(this);
			return contentItem;
		} catch (XException e) {
			throw e;
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	public String getDisplay() {
		return display;
	}
}
