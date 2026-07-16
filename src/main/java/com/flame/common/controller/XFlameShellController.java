package com.flame.common.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.flame.minio.MinioHelper;
import com.google.common.net.HttpHeaders;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.flame.action.ActionKey;
import com.flame.action.IAction;
import com.flame.action.IActionModel;
import com.flame.common.FreeMarkerUtils;
import com.flame.xui.XCommandBean;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.config.basic.BasicConfiguration;
import com.flame.config.XUIConfiguration;
import com.flame.orm.XObject;
import com.flame.thing.IModelManaged;
import com.flame.util.FlameUtils;
import com.flame.xui.XUIAction;
import com.thing.entity.XThingModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xw.action.processor.DefaultActionMenuProcessor;
import xw.action.processor.XWXActionMenuProcessor;
import xw.action.service.XActionServiceHelper;
import xw.content.entity.XApplicationData;

@Tag(name = "Common Interface")
@Controller
@RequestMapping(value = "/XUI$", method = {RequestMethod.POST, RequestMethod.GET})
public class XFlameShellController extends AppShellController {
    private static final String FREEMARKER = "freemarker/";
    private static final String ACTION_MENU = "XWX-ActionMenu";
    private static final Pattern Pattern_Page = Pattern.compile("[a-zA-Z0-9]*");
    @Resource
    private XUIConfiguration xuiConfig;

    private XUIConfiguration getXUIConfig() {
        if (xuiConfig == null) {
            xuiConfig = BasicConfiguration.getBean(XUIConfiguration.class);
        }

        return xuiConfig;
    }

    @GetMapping(value = "/info/{page}")
    public ModelAndView getInfoPage(@PathVariable String page, @RequestParam MultiValueMap<String, Object> multiMap, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelView = new ModelAndView();
        modelView.addObject(RANDOM_UUID, FlameUtils.getRandomConst());
        XCommandBean commandBean = XCommandBean.newCommandBean(request, response, multiMap, new String[0]);
        modelView.addObject(COMMAND_BEAN, commandBean);

        if (FlameUtils.isBlank(page)) {
            modelView.setViewName("thymeleaf/page/infoPage.html");
        } else if (Pattern_Page.matcher(page).matches()) {
            modelView.setViewName("thymeleaf/page/" + FlameUtils.capitalise(page) + ".html");
        } else {
            modelView.setViewName(page);
        }
        if (page.startsWith(FREEMARKER)) {
            FreeMarkerUtils.initStatics(modelView);
        }

        // 添加到返回值中
        XObject primaryObj = commandBean.getPrimaryObj();
        if (primaryObj instanceof XThingModel) {
            XThingModel thingModel = (XThingModel) primaryObj;
            modelView.addObject(THING_MODEL, thingModel);
            List<IAction> infoTabs = XActionServiceHelper.service().getSubActions("XWX-InfoTabs", XThingModel.class.getSimpleName());
            modelView.addObject(ACTION_MENU, thingModel.getModelKey() + ":" + ACTION_MENU);
            modelView.addObject(INFO_TABS, infoTabs);
        } else if (primaryObj instanceof IModelManaged) {
            IModelManaged entity = (IModelManaged) primaryObj;
            XThingModel thingModel = (XThingModel) entity.getThingModel();
            modelView.addObject(THING_MODEL, thingModel);
            List<IAction> infoTabs = getInfoTabItems(thingModel);
            modelView.addObject(ACTION_MENU, thingModel.getModelKey() + ":" + ACTION_MENU);
            modelView.addObject(INFO_TABS, infoTabs);
        } else {
            List<IAction> infoTabs = getInfoTabItems(primaryObj);
            modelView.addObject(ACTION_MENU, primaryObj.getClass().getName() + ":" + ACTION_MENU);
            modelView.addObject(INFO_TABS, infoTabs);
        }
        return modelView;
    }

    @GetMapping(value = "/popup/{actionKey}")
    public ModelAndView popupWindow(@PathVariable String actionKey, @RequestParam MultiValueMap<String, Object> multiMap, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelView = new ModelAndView();
        modelView.addObject(RANDOM_UUID, FlameUtils.getRandomConst());
        XCommandBean commandBean = XCommandBean.newCommandBean(request, response, multiMap, new String[]{ACTION_KEY, actionKey});
        modelView.addObject(COMMAND_BEAN, commandBean);

        XUIAction action = commandBean.getAction();
        if (action == null)
            return modelView;

        String page = action.getUrl();
        if (StringUtils.isBlank(page))
            return modelView;

        modelView.setViewName(page);

        if (page.startsWith(FREEMARKER)) {
            FreeMarkerUtils.initStatics(modelView);
        }
        return modelView;
    }

    @GetMapping(value = "/action/{actionKey}")
    public ModelAndView actionWindow(@PathVariable String actionKey, @RequestParam MultiValueMap<String, Object> multiMap, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelView = new ModelAndView();
        modelView.addObject(RANDOM_UUID, FlameUtils.getRandomConst());
        XCommandBean commandBean = XCommandBean.newCommandBean(request, response, multiMap, new String[]{ACTION_KEY, actionKey});
        modelView.addObject(COMMAND_BEAN, commandBean);

        XUIAction action = commandBean.getAction();
        if (action == null)
            return modelView;

        String page = action.getUrl();
        if (StringUtils.isBlank(page))
            return modelView;

        modelView.setViewName(page);

        if (page.startsWith(FREEMARKER)) {
            FreeMarkerUtils.initStatics(modelView);
        }
        return modelView;
    }

    /**
     * @param processor
     * @param multiMap  MultiValueMap才能够接收多值的参数，Map只会接收到多值参数的第一个值
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/download/{processor}")
    public void downloadData(@PathVariable String processor, @RequestParam MultiValueMap<String, Object> multiMap, HttpServletRequest request, HttpServletResponse response) {
        XCommandBean commandBean = XCommandBean.newCommandBean(request, response, multiMap, new String[]{"processor", processor});
        FormResult formResult = commandBean.executeProcessor();
        logger.trace(formResult.toString());
    }

    /**
     * http://flame.xworx.cn/XWorx/Servlet/downloadContent?oid=OR:xw.content.XApplicationData:683
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/downloadContent")
    public ResponseEntity<org.springframework.core.io.Resource> downloadContent(HttpServletRequest request, HttpServletResponse response) {
        XCommandBean commandBean = XCommandBean.newCommandBean(request, response);
        XObject primaryObj = commandBean.getPrimaryObj();
        if (!(primaryObj instanceof XApplicationData)) {
            logger.error("参数Oid：{}的对象不存在.", commandBean.getPrimaryOid());
            return ResponseEntity.noContent().build();
        }

        XApplicationData appdata = (XApplicationData) primaryObj;
        String contentType = request.getServletContext().getMimeType(appdata.getFileName());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        InputStream inputStream = MinioHelper.service().downloadContent(appdata.getInnerName(), MinioHelper.XWORX_VAULT);
        InputStreamResource resource = new InputStreamResource(inputStream);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)) //
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + appdata.getFileName() + "\"") //
                .body(resource);
    }

    @ResponseBody
    @GetMapping(value = "/get/{processor}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getProcessor(@PathVariable String processor, @RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        XCommandBean commandBean = XCommandBean.newCommandBean(request, response, params, new String[]{"processor", processor});
        return commandBean.executeProcessor();
    }

    @Transactional //Post Processor需要添加事务，以控制当前处理的完整性
    @ResponseBody
    @PostMapping(value = "/post/{processor}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object postProcessor(@PathVariable String processor, @RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        XCommandBean commandBean = XCommandBean.newCommandBean(request, response, params, new String[]{"processor", processor});
        return commandBean.executeProcessor();
    }

    /**
     * @param processor
     * @param multiMap  MultiValueMap才能够接收多值的参数，Map只会接收到多值参数的第一个值
     * @param request   若enctype='multipart/form-data'上传文件，则MultipartHttpServletRequest
     * @param response
     * @return
     */
    @Transactional //Form Processor需要添加事务，以控制当前处理的完整性
    @ResponseBody
    @PostMapping(value = "/form/{processor}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object formProcessor(@PathVariable String processor, @RequestParam MultiValueMap<String, Object> multiMap, HttpServletRequest request, HttpServletResponse response) {
        XCommandBean commandBean = XCommandBean.newCommandBean(request, response, multiMap, new String[]{"processor", processor});
        return commandBean.executeProcessor();
    }

    @ResponseBody
    @GetMapping(value = "/ui/{uibuilder}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object genComponentConfig(@PathVariable String uibuilder, @RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        XCommandBean commandBean = XCommandBean.newCommandBean(request, response, (HashMap<String, Object>) params);
        try {
            return getXUIConfig().genComponentConfig(uibuilder, commandBean);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return FormResult.newFormResult(FormStatus.FAILURE, e.getMessage());
        }
    }

    @ResponseBody
    @PostMapping(value = "/data/{uibuilder}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object genComponentData(@PathVariable String uibuilder, @RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        XCommandBean commandBean = XCommandBean.newCommandBean(request, response, (HashMap<String, Object>) params);

        try {
            return getXUIConfig().genComponentData(uibuilder, commandBean);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return FormResult.newFormResult(FormStatus.FAILURE, e.getMessage());
        }
    }

    @Operation(summary = "获取菜单项", parameters = {
            @Parameter(name = "actionKey", in = ParameterIn.PATH, required = true)
    })
    @ResponseBody
    @GetMapping(value = "/menu/actionMenu", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object genActionMenu(HttpServletRequest request, HttpServletResponse response) {
        XCommandBean commandBean = XCommandBean.newCommandBean(request, response);
        ActionKey actionKey = commandBean.getActionKey(ACTION_MENU);
        if (actionKey == null) {
            commandBean.setProcessor(XWXActionMenuProcessor.class.getName());
        } else {
            IActionModel actionModel = XActionServiceHelper.service().getActionModel(actionKey);
            if (actionModel == null) {
                commandBean.setProcessor(XWXActionMenuProcessor.class.getName());
            } else {
                String processor = actionModel.getProcessor();
                if (FlameUtils.isBlank(processor)) {
                    commandBean.setProcessor(XWXActionMenuProcessor.class.getName());
                } else {
                    commandBean.setProcessor(processor);
                }
            }
        }

        return commandBean.executeProcessor();
    }

    @Operation(summary = "获取菜单项", parameters = {
            @Parameter(name = "type", in = ParameterIn.PATH, required = true)
    })
    @ResponseBody
    @GetMapping(value = "/menu/{type}/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getActionItems(@PathVariable String type, @PathVariable String name, HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> paramMap = Map.of(ACTION_KEY, ActionKey.newActionKey(name, type).toString());

            // XWX-ActionMenu 通用操作菜单：不依赖数据库中的ActionModel记录，对所有持久化类型直接使用XWXActionMenuProcessor
            if (ACTION_MENU.equals(name)) {
                XCommandBean commandBean = XCommandBean.newCommandBean(request, response, paramMap, new String[]{"processor", XWXActionMenuProcessor.class.getName()});
                return commandBean.executeProcessor();
            }

            IActionModel actionModel = XActionServiceHelper.service().getActionModel(name, type);
            if (actionModel != null && actionModel.getProcessor() != null) {
                XCommandBean commandBean = XCommandBean.newCommandBean(request, response, paramMap, new String[]{"processor", actionModel.getProcessor()});
                return commandBean.executeProcessor();
            } else {
                XCommandBean commandBean = XCommandBean.newCommandBean(request, response, paramMap, new String[]{"processor", DefaultActionMenuProcessor.class.getName()});
                return commandBean.executeProcessor();
            }
        } finally {
        }
    }

    @Operation(summary = "获取菜单项", parameters = {
            @Parameter(name = "actionKey", in = ParameterIn.PATH, required = true)
    })
    @ResponseBody
    @GetMapping(value = "/menu/{actionKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getActionMenu(@PathVariable String actionKey, HttpServletRequest request, HttpServletResponse response) {
        try {
            ActionKey actKey = ActionKey.newActionKey(actionKey);
            Map<String, Object> paramMap = Map.of(ACTION_KEY, actionKey);

            // XWX-ActionMenu 通用操作菜单：不依赖数据库中的ActionModel记录
            if (ACTION_MENU.equals(actKey.getName())) {
                XCommandBean commandBean = XCommandBean.newCommandBean(request, response, paramMap, new String[]{"processor", XWXActionMenuProcessor.class.getName()});
                return commandBean.executeProcessor();
            }

            IActionModel actionModel = XActionServiceHelper.service().getActionModel(actKey.getName(), actKey.getType());
            if (actionModel != null && actionModel.getProcessor() != null) {
                XCommandBean commandBean = XCommandBean.newCommandBean(request, response, paramMap, new String[]{"processor", actionModel.getProcessor()});
                return commandBean.executeProcessor();
            } else {
                XCommandBean commandBean = XCommandBean.newCommandBean(request, response, paramMap, new String[]{"processor", DefaultActionMenuProcessor.class.getName()});
                return commandBean.executeProcessor();
            }
        } finally {
        }
    }
}
