package com.flame.common.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.flame.action.IAction;
import com.flame.xui.XCommandBean;
import com.flame.orm.XObject;
import com.flame.thing.IModelManaged;
import com.flame.util.FlameUtils;
import com.thing.entity.XThingModel;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xw.action.service.XActionServiceHelper;

@Tag(name = "Common Interface")
@Controller
public class AppShellController {
	protected static final String COMMAND_BEAN = "commandBean";
	protected static final String THING_MODEL = "thingModel";
	protected static final String INFO_TABS = "infoTabs";
	protected static final String ACTION_MENU = "actionMenu";
	protected static final String ACTION_KEY = "actionKey";
	protected static final String RANDOM_UUID = "randomUUID"; //生成随机的常量
	protected static final Logger logger = LoggerFactory.getLogger(AppShellController.class);

	@GetMapping(value = { "/thymeleaf/**", "/jsp/**" })
	public ModelAndView thymeleaf(@RequestParam MultiValueMap<String, Object> multiMap, HttpServletRequest request, HttpServletResponse response) {
		String uiUri = request.getRequestURI().substring(request.getContextPath().length() + 1);

		ModelAndView modelView = new ModelAndView();
		XCommandBean commandBean = XCommandBean.newCommandBean(request, response, multiMap, new String[] {});
		modelView.addObject(COMMAND_BEAN, commandBean);
		modelView.addObject(RANDOM_UUID, FlameUtils.getRandomConst());

		XObject primaryObj = commandBean.getPrimaryObj();
		if (primaryObj instanceof XThingModel) {
			XThingModel thingModel = (XThingModel) primaryObj;
			modelView.addObject(THING_MODEL, thingModel);
			List<IAction> infoTabs = XActionServiceHelper.service().getSubActions("XWX-InfoTabs", XThingModel.class.getSimpleName());
			modelView.addObject(INFO_TABS, infoTabs);
		} else if (primaryObj instanceof IModelManaged) {
			IModelManaged entity = (IModelManaged) primaryObj;
			XThingModel thingModel = (XThingModel) entity.getThingModel();
			modelView.addObject(THING_MODEL, thingModel);
			List<IAction> infoTabs = getInfoTabItems(thingModel);
			modelView.addObject(INFO_TABS, infoTabs);
		}

		if ("".equals(uiUri)) {
			modelView.setViewName("thymeleaf/page/infoPage.html");
		} else if (uiUri.startsWith("jsp/")) {
			modelView.setViewName("forward:/" + uiUri + ".jsp");
		} else if (uiUri.startsWith("thymeleaf/")) {
			modelView.setViewName(uiUri);
		} else {
			modelView.setViewName(uiUri);
		}

		return modelView;
	}

	protected List<IAction> getInfoTabItems(XThingModel thingModel) {
		List<IAction> infoTabs = XActionServiceHelper.service().getSubActions("XWX-InfoTabs", thingModel.getModelKey());
		if (infoTabs.isEmpty()) {
			XThingModel baseModel = (XThingModel) thingModel.getThingModel();
			if (baseModel == null) {
				return infoTabs;
			} else {
				return getInfoTabItems(baseModel);
			}
		}
		return infoTabs;
	}

	protected List<IAction> getInfoTabItems(XObject xObject) {
		if (xObject == null)
			return new ArrayList<>();

		return XActionServiceHelper.service().getSubActions("XWX-InfoTabs", xObject.getClass().getSimpleName());
	}

	/**
	 * JQuery.post传递数组参数时，key都会加上“[]”后缀
	 *
	 * @param multiMap
	 * @return
	 */
	protected Map<String, Object> handleMultiMap(MultiValueMap<String, Object> multiMap) {
		Map<String, Object> params = new HashMap<>();
		for (Entry<String, List<Object>> entry : multiMap.entrySet()) {
			String key = entry.getKey();
			List<Object> list = entry.getValue();
			/**
			 * JQuery.post传递数组参数时，key都会加上“[]”后缀
			 */
			if (key.endsWith("[]")) {
				key = key.substring(0, key.length() - 2);
				params.put(key, list);
			} else {
				if (!list.isEmpty()) {
					if (list.size() == 1) {
						params.put(key, list.get(0));
					} else {
						params.put(key, list);
					}
				}
			}
		}

		return params;
	}
}
