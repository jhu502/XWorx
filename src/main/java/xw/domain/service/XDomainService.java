package xw.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flame.auths.SessionHelper;
import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;

import xw.auths.entity.XUser;
import xw.context.entity.LibraryTypeRB;
import xw.context.entity.XOrganization;
import xw.domain.DomainHelper;
import xw.domain.entity.XAdminDomain;

@Service("XDomainService")
public class XDomainService {

    @Transactional
    public XAdminDomain createXAdminDomain(String number, String name, String description, XAdminDomain upper) {
        if (upper == null) {
            throw new XException("Please assign parent domain!");
        }
        XAdminDomain adminDomain = new XAdminDomain();
        adminDomain.setNumber(number);
        adminDomain.setName(name);
        adminDomain.setDescription(description);
        adminDomain.setAdminDomain(upper);
        adminDomain = PersistenceHelper.service().save(adminDomain);

        return adminDomain;

    }

    @Transactional
    public XAdminDomain createDomain4Library(String number, String name, LibraryTypeRB libType) {
        XUser xUser = (XUser) SessionHelper.getCurrentUser();
        XOrganization xOrg = xUser.getOrganization();
        XAdminDomain xDomain = xOrg.getAdminDomain();

        String dName = "PLM";
        if ("PRODUCT".equals(libType.getName())) {
            dName = "PLM";
        } else if ("PROJECT".equals(libType.getName())) {
            dName = "PJT";
        } else if ("LIBRARY".equals(libType.getName())) {
            dName = "LIB";
        }
        XAdminDomain pDomain = DomainHelper.repository().queryChildDomain(xDomain, dName);
        if (pDomain == null)
            throw new XException(String.format("Domain: %s isn't found under Org domain: %s.", dName, xDomain.getName()));

        return this.createXAdminDomain(number, name, "XLibrary Domain", pDomain);
    }
}
