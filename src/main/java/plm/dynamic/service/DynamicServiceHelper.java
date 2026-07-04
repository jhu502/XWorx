package plm.dynamic.service;

import plm.dynamic.repos.DynamicRepository;
import plm.part.XPart;
import com.flame.config.basic.BasicConfiguration;

import java.util.List;

public class DynamicServiceHelper {
	private static DynamicRepository repository;
	private static DynamicStandardService service;

	public static DynamicRepository repository() {
		if (repository == null) {
			repository = BasicConfiguration.getBean(DynamicRepository.class);
		}

		return repository;
	}

	public static DynamicStandardService service() {
		if (service == null) {
			service = BasicConfiguration.getBean(DynamicStandardService.class);
		}

		return service;
	}

	public static int getNextCharactSortNo(XPart part) {
		List<?> list = repository().getMaxCharactSortNo(part);
		if (list == null || list.isEmpty())
			return 0;

		Object object = list.get(0);
		if (object == null)
			return 0;

		int sortNo = (int) object;
		return sortNo + 1;
	}

	private DynamicServiceHelper() {
	}
}
