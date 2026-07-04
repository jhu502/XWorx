package xw.action.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.flame.annotations.XDefinition;
import com.flame.config.basic.BasicConfiguration;
import com.flame.orm.XObject;
import com.flame.util.FlameUtils;

import xw.action.entity.AbstractAction;
import xw.action.entity.XActionItemLink;
import xw.action.repos.XActionRepository;

public class XActionServiceHelper {
	private static XActionService service;
	private static XActionRepository repository;

	public static XActionService service() {
		if (service == null) {
			service = BasicConfiguration.getBean(XActionService.class);
		}

		return service;
	}

	public static XActionRepository repository() {
		if (repository == null) {
			repository = BasicConfiguration.getBean(XActionRepository.class);
		}

		return repository;
	}

	public static XDefinition getXDefinition(XObject xObject) {
		if (xObject == null)
			return null;

		Class<? extends XObject> clazz = xObject.getClass();
		return getXDefinition(clazz);
	}

	public static XDefinition getXDefinition(Class<? extends XObject> clazz) {
		if (clazz == null)
			return null;

		return clazz.getAnnotation(XDefinition.class);
	}

	public static List<Map.Entry<AbstractAction, XActionItemLink>> sorted(List<Map.Entry<AbstractAction, XActionItemLink>> sortedList) {
		Collections.sort(sortedList, new Comparator<>() {

			@Override
			public int compare(Map.Entry<AbstractAction, XActionItemLink> left, Map.Entry<AbstractAction, XActionItemLink> right) {
				AbstractAction leftAction = left.getKey();
				XActionItemLink leftLink = left.getValue();
				String leftKey = FlameUtils.isBlank(leftLink.getSort()) ? "-" : leftLink.getSort() + ":" + leftAction.getXid();

				AbstractAction rightAction = right.getKey();
				XActionItemLink rightLink = right.getValue();
				String rightKey = FlameUtils.isBlank(rightLink.getSort()) ? "-" : rightLink.getSort() + ":" + rightAction.getXid();

				int result = leftKey.compareTo(rightKey);
				if (result == 0) {
					return leftAction.getName().compareTo(rightAction.getName());
				} else {
					return result;
				}
			}

		});

		return sortedList;
	}
}
