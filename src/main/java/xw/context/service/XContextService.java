package xw.context.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flame.auths.SessionHelper;
import com.flame.orm.PersistenceHelper;

import xw.auths.entity.XUser;
import xw.context.entity.LibraryTypeRB;
import xw.context.entity.XLibrary;
import xw.domain.DomainHelper;
import xw.domain.entity.XAdminDomain;
import xw.team.entity.XContainerTeam;

@Service("XContextService")
public class XContextService {
    @Transactional
    public XLibrary createXLibraryContext(String number, String name, String description, LibraryTypeRB libType) {
        XAdminDomain adminDomain = DomainHelper.service().createDomain4Library(number.toUpperCase(), name, libType);

        XUser xUser = (XUser) SessionHelper.getCurrentUser();
        XLibrary xLibrary = XLibrary.newLibrary(number.toUpperCase(), name, description, xUser.getOrganization());
        xLibrary.setLibraryType(libType);
        xLibrary.setAdminDomain(adminDomain);
        XContainerTeam team = XContainerTeam.newInstance();
        team = PersistenceHelper.service().save(team);
        xLibrary.setTeam(team);

        return PersistenceHelper.service().save(xLibrary);
    }
}
