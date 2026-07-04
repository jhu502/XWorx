package mes.apl;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flame.vc.ObjectUsageLink;

import mes.equipt.XEquipmentMaster;

@Entity
@Table(name = "XAssemblyLineUsageLink", uniqueConstraints = {})
public class XAssemblyLineUsageLink extends ObjectUsageLink<XAssemblyLine, XEquipmentMaster> {
	private static final long serialVersionUID = 1L;
	
    public static XAssemblyLineUsageLink newXAssemblyLineUsageLink(XAssemblyLine xpline, XEquipmentMaster master) {
        XAssemblyLineUsageLink usageLink = new XAssemblyLineUsageLink();
        usageLink.setUsedBy(xpline);
        usageLink.setUses(master);
        return usageLink;
    }
}
