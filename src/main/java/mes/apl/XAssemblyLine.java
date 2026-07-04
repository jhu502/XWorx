package mes.apl;

import com.flame.annotations.XDefinition;
import com.thing.common.DefaultThing;
import xw.vc.VersionControlled;

import jakarta.persistence.*;

@Entity
@Table(name = "XAssemblyLine", uniqueConstraints = {})
@XDefinition(name = "XAssemblyLine", config = DefaultThing.class, icon = "images/pline.png", description = "XAssemblyLine", display = "Assembly Line", en_US = "Assembly Line", zh_CN = "装配线")
public class XAssemblyLine extends VersionControlled<XAssemblyLineMaster> {
    private static final long serialVersionUID = 1L;
    @ManyToOne(targetEntity = XAssemblyLineMaster.class)
    @JoinColumn(name = "master_id", foreignKey = @ForeignKey(name = "MASTER_ID_FK"))
    protected XAssemblyLineMaster master;

    public static XAssemblyLine newAssemblyLine(String number, String name, String description) {
        XAssemblyLineMaster master = new XAssemblyLineMaster();
        XAssemblyLine xpline = new XAssemblyLine(master);
        xpline.setNumber(number);
        xpline.setName(name);
        xpline.setDescription(description);

        return xpline;
    }

    public XAssemblyLine() {
    }

    private XAssemblyLine(XAssemblyLineMaster master) {
        this.setMaster(master);
    }

    @Override
    public XAssemblyLineMaster getMaster() {
        return this.master;
    }

    @Override
    public void setMaster(XAssemblyLineMaster master) {
        this.master = master;
    }
}
