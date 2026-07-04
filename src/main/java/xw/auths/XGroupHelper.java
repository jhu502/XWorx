package xw.auths;

import xw.auths.service.XGroupManager;
import xw.auths.repos.XAuthRepository;
import com.flame.config.basic.BasicConfiguration;

public class XGroupHelper {
	private static XAuthRepository repository;
	private static XGroupManager service;

	public static XAuthRepository repository() {
		if (repository == null) {
			repository = BasicConfiguration.getBean(XAuthRepository.class);
		}

		return repository;
	}
	
	public static XGroupManager service() {
		if (service == null) {
			service = BasicConfiguration.getBean(XGroupManager.class);
		}
		
		return service;
	}
}
