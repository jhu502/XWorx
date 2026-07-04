package mes.apl.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import mes.apl.XAssemblyLine;
import mes.apl.repos.AplRepository;
import mes.equipt.XEquipment;
import mes.equipt.XEquiptInstance;

@Service
public class XPLineStandardService {
	@Resource
	private AplRepository repository;

	public List<?> getUsedbyXPLine(XAssemblyLine xpline) {
		return repository.getUsedbyXPLine(xpline);
	}

	public List<?> getEquiptInstance(XEquipment equipment) {
		return repository.getEquiptInstance(equipment);
	}

	public List<?> getEquiptParameter(XEquiptInstance instance) {
		return repository.getEquiptParameter(instance);
	}
}
