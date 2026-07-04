package com.flame.common.controller;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flame.auths.SessionHelper;
import com.flame.loader.AbstractDataLoader;
import com.flame.thing.ThingModelHelper;
import com.thing.entity.ConnectableEntity;
import com.thing.entity.ModeledEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import xw.auths.entity.XUser;

@RestController
@RequestMapping(value = "/Flame/LoadFile", produces = MediaType.APPLICATION_JSON_VALUE)
public class XFlameLoadController {
    @Operation(summary = "XWord Load Tool")
    @Transactional
    @PostMapping(value = "/systemInit")
    public void initFlameConfig() throws Exception {
        AbstractDataLoader.load("loadFiles/xw/domain/Basic-XAdminDomain.xml");

        XUser xuser = (XUser) SessionHelper.getCurrentUser();
        if (xuser == null || xuser.getXid() <= 0) {
            AbstractDataLoader.load("loadFiles/xw/auths/Basic-XGroup.xml");
            AbstractDataLoader.load("loadFiles/xw/auths/Basic-XUser.xml");
            ThingModelHelper.manager().registerThingModel(ConnectableEntity.class);
        }
    }

    @Operation(summary = "Register ThingModel", parameters = {
            @Parameter(name = "className", description = "指定model package", required = true)
    })
    @Transactional
    @PostMapping(value = "/registerModel")
    public void registerModel(String className) throws Exception {
        @SuppressWarnings("unchecked")
        Class<? extends ModeledEntity> regClass = (Class<? extends ModeledEntity>) Class.forName(className);
        ThingModelHelper.manager().registerThingModel(regClass);
    }

    @Operation(summary = "指定Load文件的相对路径，例如:loadFiles/plm/product/Menu-FTProduct.xml", parameters = {
            @Parameter(name = "xmlPath", description = "指定loadXML的相对路径", required = true)
    })
    @Transactional
    @PostMapping(value = "/loadXML")
    public void loadXMLFile(String xmlPath) throws Exception {
        AbstractDataLoader.load(xmlPath);
    }
}
