package xw.content;

import java.util.Collections;
import java.util.List;

import xw.content.entity.XApplicationData;
import xw.content.entity.XResourceData;
import xw.content.repos.ContentRepository;
import xw.content.service.XWorxContentService;
import com.flame.config.basic.BasicConfiguration;

public class XContentHelper {
	private static XWorxContentService service;
	private static ContentRepository repository;

	public static XWorxContentService service() {
		if (service == null) {
			service = BasicConfiguration.getBean(XWorxContentService.class);
		}

		return service;
	}

	public static ContentRepository repository() {
		if (repository == null) {
			repository = BasicConfiguration.getBean(ContentRepository.class);
		}

		return repository;
	}

	public static ContentItem getThumb3DContentItem(Object object) {
		if (object == null)
			return null;

		if (object instanceof IContentHolder contentHolder) {
			List<XApplicationData> list = XContentHelper.service().getRelatedContentItem(contentHolder, ContentType.THUMBNAIL3D);
			if (list == null || list.isEmpty()) {
				return null;
			} else {
				return list.get(0);
			}
		}

		return null;
	}

	public static List<XApplicationData> getAllContentItems(Object object) {
		if (!(object instanceof IContentHolder contentHolder))
			return Collections.emptyList();
		return service().getAllContentItems(contentHolder);
	}

	public static List<XResourceData> getAllResourceItems(Object object) {
		if (!(object instanceof IContentHolder holder))
			return List.of();
		return repository().queryXResourceData(holder);
	}
}
