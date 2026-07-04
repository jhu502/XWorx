package xw.domain;

import xw.domain.repos.DomainRepository;
import com.flame.config.basic.BasicConfiguration;
import xw.domain.service.XDomainService;

public class DomainHelper {
	private static DomainRepository repository;
	private static XDomainService service;

	public static DomainRepository repository() {
		if (repository == null) {
			repository = BasicConfiguration.getBean(DomainRepository.class);
		}

		return repository;
	}

	public static XDomainService service() {
		if (service == null) {
			service = BasicConfiguration.getBean(XDomainService.class);
		}

		return service;
	}
}
