package mes.equipt;

import com.flame.orm.ObjectToObjectLink;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "XEquiptInstanceLink", uniqueConstraints = {})
public class XEquiptInstanceLink extends ObjectToObjectLink<XEquipmentMaster, XEquiptInstanceMaster> {
    public static XEquiptInstanceLink newXEquiptInstanceLink(XEquipment equipment, XEquiptInstance instance) {
        XEquiptInstanceLink instanceLink = new XEquiptInstanceLink();
        instanceLink.setLeftObject(equipment.getMaster());
        instanceLink.setRightObject(instance.getMaster());
        return instanceLink;
    }
}
