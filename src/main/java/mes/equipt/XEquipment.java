package mes.equipt;

import com.thing.common.DefaultThing;
import com.flame.annotations.XDefinition;
import xw.vc.VersionControlled;

import jakarta.persistence.*;

@Entity
@Table(name = "XEquipment", uniqueConstraints = {})
@XDefinition(name = "XEquipment", config = DefaultThing.class, icon = "images/equipt.png", description = "XEquipment", display = "Equipment", en_US = "Equipment", zh_CN = "设备")
public class XEquipment extends VersionControlled<XEquipmentMaster> {
    private static final long serialVersionUID = 1L;
    @ManyToOne(targetEntity = XEquipmentMaster.class)
    @JoinColumn(name = "master_id", foreignKey = @ForeignKey(name = "MASTER_ID_FK"))
    protected XEquipmentMaster master;

    public static XEquipment newXEquipment(String number, String name, String description) {
        XEquipmentMaster master = new XEquipmentMaster();
        XEquipment xequipt = new XEquipment(master);
        xequipt.setNumber(number);
        xequipt.setName(name);
        xequipt.setDescription(description);

        return xequipt;
    }

    public XEquipment() {
    }

    private XEquipment(XEquipmentMaster master) {
        this.setMaster(master);
    }

    @Override
    public XEquipmentMaster getMaster() {
        return this.master;
    }

    @Override
    public void setMaster(XEquipmentMaster master) {
        this.master = master;
    }
}
