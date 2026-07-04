package mes;

import com.flame.logs.XPrintStream;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.management.ManagementFactory;

@SpringBootTest
@ActiveProfiles("Test")
class XWorxMESApplicationTest {
    protected Logger logger = LoggerFactory.getLogger(XWorxMESApplicationTest.class);
    public static final String XWORX_HOME = "xworx.home";
    public static final String XWORX_JVM_ID = "xworx.jvm.id";

    static {
        if (!(System.out instanceof XPrintStream))
            System.setOut(new XPrintStream(System.out));

        System.setProperty(XWORX_HOME, "D:/SourceSpace/SpaceFlame/XServer");
        System.setProperty(XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
    }

    @Test
    void test() {

    }
}
