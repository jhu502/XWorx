package xw.context;

import xw.context.repos.ContextRepository;
import com.flame.config.basic.BasicConfiguration;
import xw.context.service.XContextService;

public class ContextHelper {
	private static ContextRepository repository;
	private static XContextService service;

	public static ContextRepository repository() {
		if (repository == null) {
			repository = BasicConfiguration.getBean(ContextRepository.class);
		}

		return repository;
	}

	public static XContextService service() {
		if (service == null) {
			service = BasicConfiguration.getBean(XContextService.class);
		}

		return service;
	}
}
