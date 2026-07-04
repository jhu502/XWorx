package com.flame.orm;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.flame.logs.XPrintStream;
import com.flame.test.GenericType;
import com.flame.test.ViewRB;
import com.flame.test.WTPart;
import com.flame.test.WTPartMaster;

@SpringBootTest
@ActiveProfiles("Test")
public class XWorxJPApplicationTest {
    public static final String XWORX_HOME = "xworx.home";
    public static final String XWORX_JVM_ID = "xworx.jvm.id";

    static {
        if (!(System.out instanceof XPrintStream))
            System.setOut(new XPrintStream(System.out));

        System.setProperty(XWORX_HOME, "D:/SourceSpace/SpaceFlame/XServer");
        System.setProperty(XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
    }

    @Test
    public void createWTPart() {
        WTPart part = WTPart.newInstance("XX000001", "Test");
        part.setLatest(true);
        part.setDescription("Test");
        ViewRB viewRB = ViewRB.toViewRB("Design");
        part.setView(viewRB);
        WTPartMaster master = part.getMaster();
        master.setEndItem(true);
        master.setCollapsible(true);
        master.setPhantom(true);
        master.setGenericType(GenericType.Standard);
        PersistenceHelper.service().save(part);
    }

    @Test
    public void queryWTPart() {
        WTPart part = (WTPart) PersistenceHelper.getPersistable("OR:com.flame.test.WTPart:402");
        System.out.println(part);
        ViewRB viewRB = part.getView();
        System.out.println(viewRB.getDisplay(Locale.CHINA));
    }

    @Test
    public void testJDBC() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/xworx", "ftadmin", "ftadmin");
        PreparedStatement statement = connection.prepareStatement("select * from WTPart");
        statement.execute();
        //System.out.println(statement.getMoreResults());
        System.out.println(statement.getUpdateCount() != -1);
        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()) {
            System.out.println(resultSet.getString(2));
        }
    }
}
