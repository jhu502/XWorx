package plm;

import com.flame.logs.XPrintStream;
import com.flame.orm.PersistenceHelper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import plm.dynamic.XCaseTable;
import xw.auths.entity.GroupTypeRB;
import xw.auths.entity.RoleRB;
import xw.auths.entity.XGroup;
import xw.context.entity.XLibrary;
import xw.context.entity.XOrganization;
import xw.team.entity.XContainerTeam;
import xw.team.entity.XContainerRoleMap;

import java.lang.management.ManagementFactory;

@SpringBootTest
@ActiveProfiles("Test")
class XWorxPLMApplicationTest {
    protected Logger logger = LoggerFactory.getLogger(XWorxPLMApplicationTest.class);
    public static final String XWORX_HOME = "xworx.home";
    public static final String XWORX_JVM_ID = "xworx.jvm.id";

    static {
        if (!(System.out instanceof XPrintStream))
            System.setOut(new XPrintStream(System.out));

        System.setProperty(XWORX_HOME, "D:/SourceSpace/SpaceFlame/XServer");
        System.setProperty(XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
    }

    @Test
    void testXLibrary() {
        XLibrary library = (XLibrary) PersistenceHelper.getPersistable("OR:xw.context.entity.XLibrary:3313");
        XContainerTeam team = library.getTeam();
        XGroup xgroup = new XGroup();
        RoleRB roleRB = RoleRB.toRoleRB("PRODUCT_MANAGER");
        xgroup.setNumber(library.getNumber() + "-" + roleRB.getName());
        xgroup.setName(roleRB.getName());
        xgroup.setGroupType(GroupTypeRB.toGroupTypeRB("SYSTEM"));
        xgroup.setAdminDomain(library.getAdminDomain());
        xgroup = PersistenceHelper.service().save(xgroup);
        XContainerRoleMap xTeamRoleMap = XContainerRoleMap.newInstance(team, roleRB, xgroup);
        xTeamRoleMap = PersistenceHelper.service().save(xTeamRoleMap);
        System.out.println(team);
//        System.out.println(xLibrary.getLibraryType());

//        List<XLibrary> list = libraryMapper.selectAllLibrary();
//        for (XLibrary library : list) {
//            System.out.println(library);
//            System.out.println("Container:" + library.getContainer());
//            System.out.println("LibType:" + library.getLibraryType());
//            System.out.println("Creator:" + library.getCreator());
//        }
    }

    @Test
    void testXOrganization() {
        XOrganization organization = (XOrganization) PersistenceHelper.getPersistable("OR:xw.context.entity.XOrganization:3244");
        XContainerTeam team = organization.getTeam();
        if (team == null) {
            team =  XContainerTeam.newInstance();
            team = PersistenceHelper.service().save(team);
            organization.setTeam(team);
            organization = PersistenceHelper.service().save(organization);
        }
        XGroup xgroup = new XGroup();
        RoleRB roleRB = RoleRB.toRoleRB("ADMINISTRATORS");
        xgroup.setNumber(organization.getNumber() + "-" + roleRB.getName());
        xgroup.setName(roleRB.getName());
        xgroup.setGroupType(GroupTypeRB.toGroupTypeRB("SYSTEM"));
        xgroup.setAdminDomain(organization.getAdminDomain());
        xgroup = PersistenceHelper.service().save(xgroup);
        XContainerRoleMap xTeamRoleMap = XContainerRoleMap.newInstance(team, roleRB, xgroup);
        xTeamRoleMap = PersistenceHelper.service().save(xTeamRoleMap);
        System.out.println(team);
    }

    @Test
    void testXCaseTable() {
        XCaseTable caseTable = (XCaseTable) PersistenceHelper.getPersistable("OR:plm.dynamic.XCaseTable:656");
        System.out.println(caseTable.getRows().getData());
    }
}



