package com.flame.common.controller;

import com.flame.xui.XCommandBean;
import com.flame.minio.MinioHelper;
import com.flame.orm.XObject;
import com.google.common.net.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xw.content.entity.XApplicationData;

import java.io.InputStream;

@Tag(name = "Common Interface")
@Controller
@RequestMapping(value = "/Servlet", method = {RequestMethod.POST, RequestMethod.GET})
public class XFlameServletController extends AppShellController {
    protected static final Logger logger = LoggerFactory.getLogger(XFlameServletController.class);

    /**
     * http://flame.xworx.cn/XWorx/Servlet/downloadContent?oid=OR:xw.content.XApplicationData:683
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/downloadContent")
    public ResponseEntity<Resource> downloadContent(HttpServletRequest request, HttpServletResponse response) {
        XCommandBean commandBean = XCommandBean.newCommandBean(request, response);
        XObject primaryObj = commandBean.getPrimaryObj();
        if (!(primaryObj instanceof XApplicationData)) {
            logger.error("参数Oid：" + commandBean.getPrimaryOid() + "的对象不存在.");
            return ResponseEntity.noContent().build();
        }

        XApplicationData appdata = (XApplicationData) primaryObj;
        String contentType = request.getServletContext().getMimeType(appdata.getFileName());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        InputStream inputStream = MinioHelper.service().downloadContent(appdata.getInnerName(), MinioHelper.XWORX_VAULT);
        InputStreamResource resource = new InputStreamResource(inputStream);
        return ResponseEntity.ok() //
                .contentType(MediaType.parseMediaType(contentType)) //
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=\"" + appdata.getFileName() + "\"") //
                .body(resource);
    }
}
