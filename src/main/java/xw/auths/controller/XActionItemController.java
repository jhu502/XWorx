package xw.auths.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.flame.action.IAction;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.orm.XPersistable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import xw.action.entity.XActionItem;
import xw.action.entity.XActionModel;
import xw.action.service.XActionService;

@RestController
@RequestMapping(value = "/ActionController", produces = MediaType.APPLICATION_JSON_VALUE)
public class XActionItemController {
	@Resource
	private XActionService actionService;

	@Operation(summary = "创建菜单", parameters = { @Parameter(name = "oid", required = true), @Parameter(name = "type", required = true) })
	@RequestMapping(value = "createActionMenu", method = RequestMethod.POST)
	public void createActionMenu(String oid, String type) {

	}

	@Operation(summary = "获取菜单项", parameters = { @Parameter(name = "name", required = true), @Parameter(name = "type", required = true) })
	@RequestMapping(value = "getActionItems", method = RequestMethod.GET)
	public List<IAction> getActionItems(String name, String type) {
		return actionService.getSubActions(name, type);
	}

	@Operation(summary = "获取菜单项", parameters = { @Parameter(name = "name", required = true), @Parameter(name = "oid", required = true) })
	@RequestMapping(value = "getActions", method = RequestMethod.GET)
	public List<IAction> getActions(String name, String oid) {
		XPersistable persist = PersistenceHelper.service().refresh(new ObjectReference<XPersistable>(oid));
		return actionService.getActions(name, persist);
	}

	@Operation(summary = "获取上下文菜单", parameters = { @Parameter(name = "name", required = true), @Parameter(name = "type", required = true), @Parameter(name = "oid", required = true) })
	@RequestMapping(value = "getContextMenu", method = RequestMethod.GET)
	public ModelAndView getContextMenu(String name, String type, String oid) {
		XObject persist = (XObject) PersistenceHelper.service().find(oid);
		ModelAndView model = new ModelAndView("freemarker/auths/contextMenu");
		// 添加到返回值中
		model.addObject("persist", persist);
		model.addObject("name", name);
		model.addObject("type", type);
		List<IAction> menuList = actionService.getSubActions(name, type);
		model.addObject("list", menuList);

		return model;
	}

	@Operation(summary = "搜索菜单项", parameters = { @Parameter(name = "name", required = true), @Parameter(name = "value", required = true) })
	@RequestMapping(value = "searchMenuItems", method = RequestMethod.GET)
	public List<Map<String, Object>> searchMenuItems(String name, String value) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (name == null || name.trim().equals(""))
			return result;
		if (value == null || value.trim().equals(""))
			return result;

		List<Object> list = actionService.searchActions(name, value);
		for (Object obj : list) {
			if (obj instanceof XActionItem) {
				XActionItem action = (XActionItem) obj;
				Map<String, Object> rowData = new HashMap<String, Object>();
				rowData.put("oid", action.getOid());
				rowData.put("name", action.getName());
				if (action.getIcon() == null || "".equals(action.getIcon())) {
					rowData.put("display", action.getDisplay());
				} else {
					rowData.put("display", "<img src='" + action.getIcon() + "'/> " + action.getDisplay());
				}
				rowData.put("type", action.getType());
				rowData.put("icon", action.getIcon());
				rowData.put("supported_type", action.getSupportedType());
				rowData.put("url", action.getUrl());
				rowData.put("object", "FTAction");
				rowData.put("createdStamp", action.getCreatedStamp().toString());
				rowData.put("modifiedStamp", action.getModifiedStamp().toString());
				rowData.put("state", "open");
				result.add(rowData);
			} else if (obj instanceof XActionModel) {
				XActionModel model = (XActionModel) obj;
				Map<String, Object> rowData = new HashMap<String, Object>();
				rowData.put("oid", model.getOid());
				rowData.put("name", model.getName());
				if (model.getIcon() == null || "".equals(model.getIcon())) {
					rowData.put("display", model.getDisplay());
				} else {
					rowData.put("display", "<img src='" + model.getIcon() + "'/> " + model.getDisplay());
				}
				rowData.put("type", model.getType());
				rowData.put("icon", model.getIcon());
				rowData.put("supported_type", model.getSupportedType());
				rowData.put("url", model.getUrl());
				rowData.put("object", "XActionModel");
				rowData.put("createdStamp", model.getCreatedStamp().toString());
				rowData.put("modifiedStamp", model.getModifiedStamp().toString());
				rowData.put("state", "open");
				List<Map<String, String>> _result = new ArrayList<Map<String, String>>();
				for (IAction action : actionService.getSubActions(model)) {
					if (action instanceof XActionItem) {
						XActionItem _action = (XActionItem) action;
						Map<String, String> _rowData = new HashMap<String, String>();
						_rowData.put("oid", _action.getOid());
						_rowData.put("name", _action.getName());
						if (_action.getIcon() == null || "".equals(_action.getIcon())) {
							_rowData.put("display", _action.getDisplay());
						} else {
							_rowData.put("display", "<img src='" + _action.getIcon() + "'/> " + _action.getDisplay());
						}
						_rowData.put("type", _action.getType());
						_rowData.put("icon", _action.getIcon());
						_rowData.put("supported_type", _action.getSupportedType());
						_rowData.put("url", _action.getUrl());
						_rowData.put("object", "FTAction");
						_rowData.put("createdStamp", _action.getCreatedStamp().toString());
						_rowData.put("modifiedStamp", _action.getModifiedStamp().toString());
						_result.add(_rowData);
					} else if (action instanceof XActionModel) {
						XActionModel _model = (XActionModel) action;
						Map<String, String> _rowData = new HashMap<String, String>();
						_rowData.put("oid", _model.getOid());
						_rowData.put("name", _model.getName());
						if (_model.getIcon() == null || "".equals(_model.getIcon())) {
							_rowData.put("display", _model.getDisplay());
						} else {
							_rowData.put("display", "<img src='" + _model.getIcon() + "'/> " + _model.getDisplay());
						}
						_rowData.put("type", _model.getType());
						_rowData.put("icon", _model.getIcon());
						_rowData.put("supported_type", _model.getSupportedType());
						_rowData.put("url", _model.getUrl());
						_rowData.put("object", "XActionModel");
						_rowData.put("createdStamp", _model.getCreatedStamp().toString());
						_rowData.put("modifiedStamp", _model.getModifiedStamp().toString());
						_result.add(_rowData);
					}
				}
				rowData.put("children", _result);
				result.add(rowData);
			}
		}

		return result;
	}
}
