package xw.action.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.flame.action.ActionKey;
import com.flame.action.IAction;
import com.flame.action.IActionItem;
import com.flame.action.IActionManager;
import com.flame.action.IActionModel;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XPersistable;

import jakarta.annotation.Resource;
import xw.action.entity.AbstractAction;
import xw.action.entity.XActionItem;
import xw.action.entity.XActionItemLink;
import xw.action.entity.XActionModel;
import xw.action.repos.XActionRepository;

@Service("ActionService")
public class XActionService implements IActionManager {
	@Resource
	private XActionRepository repository;

	public IActionItem getActionItem(String name, String type) {
		return repository.getXActionItem(name, type);
	}

	public IActionModel getActionModel(String name, String type) {
		return repository.getXActionModel(name, type);
	}
	
	public IActionModel getActionModel(ActionKey actionKey) {
	    return repository.getXActionModel(actionKey.getName(), actionKey.getType());
	}

	public IAction getAction(String name, String type) {
		IActionModel model = repository.getXActionModel(name, type);
		if (model != null) {
			return model;
		} else {
			return repository.getXActionItem(name, type);
		}
	}

	public List<IAction> getActions(String identity, XPersistable persist) {
		List<IAction> result = new ArrayList<>();
		String[] params = identity.split(":");
		IActionModel actionModel = this.getActionModel(params[1], params[0]);
		if (actionModel == null)
			return result;

		List<Entry<AbstractAction, XActionItemLink>> sortedList = new ArrayList<>();
		List<Object[]> itemList = repository.getXActionItemAndLink(actionModel);

		for (Object[] objs : itemList) {
			XActionItem action = (XActionItem) objs[0];
			String supports = action.getSupportedType();
			if (supports == null || "".equals(supports)) {
				sortedList.add(new SimpleEntry<>((AbstractAction) objs[0], (XActionItemLink) objs[1]));
			} else {
				if (persist == null)
					continue;

				for (String s : supports.split(",")) {
					Class<?> cls = PersistenceHelper.service().getEntityClass(s);
					if (cls.isAssignableFrom(persist.getClass())) {
						sortedList.add(new SimpleEntry<>((AbstractAction) objs[0], (XActionItemLink) objs[1]));
						break;
					}
				}
			}
		}
		List<Object[]> modelList = repository.getXActionModelAndLink(actionModel);
		for (Object[] objs : modelList) {
			XActionModel model = (XActionModel) objs[0];
			String supports = model.getSupportedType();
			if (supports == null || "".equals(supports)) {
				sortedList.add(new SimpleEntry<>((AbstractAction) objs[0], (XActionItemLink) objs[1]));
			} else {
				if (persist == null)
					continue;

				for (String type : supports.split(",")) {
					Class<?> typeCls = PersistenceHelper.service().getEntityClass(type);
					if (typeCls != null && typeCls.isAssignableFrom(persist.getClass())) {
						sortedList.add(new SimpleEntry<>((AbstractAction) objs[0], (XActionItemLink) objs[1]));
						break;
					}
				}
			}
		}
		XActionServiceHelper.sorted(sortedList);
		return sortedList.stream().map(e -> e.getKey()).collect(Collectors.toList());
	}

	public List<IAction> getSubActions(String name, String type) {
		IAction action = this.getAction(name, type);
		if (action instanceof IActionModel) {
			return this.getSubActions((IActionModel) action);
		}

		return new ArrayList<>();
	}

	public List<IAction> getSubActions(IActionModel model) {
		if (model == null)
			return new ArrayList<>();

		List<Entry<AbstractAction, XActionItemLink>> sortedList = new ArrayList<>();

		for (Object[] entry : repository.getXActionModelAndLink(model)) {
			sortedList.add(new SimpleEntry<>((AbstractAction) entry[0], (XActionItemLink) entry[1]));
		}

		for (Object[] entry : repository.getXActionItemAndLink(model)) {
			sortedList.add(new SimpleEntry<>((AbstractAction) entry[0], (XActionItemLink) entry[1]));
		}
		XActionServiceHelper.sorted(sortedList);

		return sortedList.stream().map(e -> e.getKey()).collect(Collectors.toList());
	}

	public List<Object[]> getActionAndLink(IActionModel actionModel) {
		List<Object[]> result = new ArrayList<>();
		if (actionModel == null)
			return result;

		result.addAll(repository.getXActionModelAndLink(actionModel));
		result.addAll(repository.getXActionItemAndLink(actionModel));

		return result;
	}

	public List<Object> searchActions(String name, String value) {
		List<Object> result = new ArrayList<Object>();
		List<?> list = PersistenceHelper.service().query("select a from XActionItem a where a." + name + " like :value", new Object[][] { { "value", "%" + value + "%" } });
		result.addAll(list);
		list = PersistenceHelper.service().query("select a from XActionModel a where a." + name + " like :value", new Object[][] { { "value", "%" + value + "%" } });
		result.addAll(list);

		return result;
	}
}
