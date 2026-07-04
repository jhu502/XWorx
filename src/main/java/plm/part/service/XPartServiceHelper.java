package plm.part.service;

import xw.context.entity.Container;
import plm.part.XPart;
import plm.part.repos.PartRepository;
import com.flame.config.basic.BasicConfiguration;
import com.flame.orm.PersistenceHelper;

import java.util.List;

public class XPartServiceHelper {
	private static PartRepository repository;
	private static XPartStandardService service;

	private XPartServiceHelper() {
	}

	public static PartRepository repository() {
		if (repository == null) {
			repository = BasicConfiguration.getBean(PartRepository.class);
		}

		return repository;
	}

	public static XPartStandardService service() {
		if (service == null) {
			service = BasicConfiguration.getBean(XPartStandardService.class);
		}

		return service;
	}

	public static List<XPart> queryXPartByContainer(Container container) {
		return PersistenceHelper.service().query(XPart.class, new Object[][] { { "containerRef.id", container.getXid() } });
	}
}
