package mes.equipt;

import com.flame.annotations.XDefinition;
import com.thing.common.DefaultThing;
import xw.vc.VersionControlled;

import jakarta.persistence.*;

@Entity
@Table(name = "XEquiptInstance", uniqueConstraints = {})
@XDefinition(name = "XEquiptInstance", config = DefaultThing.class, icon = "images/equiptInstance.png", description = "XEquiptInstance", display = "Equipt Instance", en_US = "Equipt Instance", zh_CN = "设备")
public class XEquiptInstance extends VersionControlled<XEquiptInstanceMaster> {
    private static final long serialVersionUID = 1L;
    @ManyToOne(targetEntity = XEquiptInstanceMaster.class)
    @JoinColumn(name = "master_id", foreignKey = @ForeignKey(name = "MASTER_ID_FK"))
    protected XEquiptInstanceMaster master;

    public static XEquiptInstance newXEquiptInstance(String number, String name, String description) {
        XEquiptInstanceMaster master = new XEquiptInstanceMaster();
        XEquiptInstance equiptInstance = new XEquiptInstance(master);
        equiptInstance.setNumber(number);
        equiptInstance.setName(name);
        equiptInstance.setDescription(description);

        return equiptInstance;
    }

    public XEquiptInstance() {
    }

    private XEquiptInstance(XEquiptInstanceMaster master) {
        this.setMaster(master);
    }

    @Override
    public XEquiptInstanceMaster getMaster() {
        return this.master;
    }

    @Override
    public void setMaster(XEquiptInstanceMaster master) {
        this.master = master;
    }
}
