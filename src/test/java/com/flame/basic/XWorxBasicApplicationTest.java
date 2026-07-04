package com.flame.basic;

import com.flame.auths.SessionHelper;
import com.flame.logs.XPrintStream;
import com.flame.orm.PersistenceHelper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import xw.auths.entity.XUser;
import xw.context.ContextHelper;
import xw.context.entity.LibraryTypeRB;
import xw.context.entity.XLibrary;

import java.lang.management.ManagementFactory;
import java.util.List;

@SpringBootTest
@ActiveProfiles("Test")
class XWorxBasicApplicationTest {
    protected Logger logger = LoggerFactory.getLogger(XWorxBasicApplicationTest.class);
    public static final String XWORX_HOME = "xworx.home";
    public static final String XWORX_JVM_ID = "xworx.jvm.id";

    static {
        if (!(System.out instanceof XPrintStream))
            System.setOut(new XPrintStream(System.out));

        System.setProperty(XWORX_HOME, "D:/SourceSpace/SpaceFlame/XServer");
        System.setProperty(XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
    }

    @Test
    void queryAllXLibrary() {
        XLibrary xLibrary = (XLibrary) PersistenceHelper.getPersistable("OR:xw.context.entity.XLibrary:3313");
        LibraryTypeRB typeRB = xLibrary.getLibraryType();
        System.out.println(typeRB + " -----CVC------ " + typeRB.getName());
        List<XLibrary> list = ContextHelper.repository().allLibrary();
        for (XLibrary library : list) {
            System.out.println(library + " -----AVA------ " + library.getLibraryType());
        }
    }
}
