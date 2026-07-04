package plm.cad;

import com.flame.annotations.XDefinition;
import com.thing.common.DefaultThing;
import plm.dynamic.ICharacted;
import xw.vc.VersionControlled;

import jakarta.persistence.*;

@Entity
@Table(name = "CADDocument", uniqueConstraints = {})
@XDefinition(name = "CADDocument", config = DefaultThing.class, icon = "images/cad.gif", description = "CADDocument", display = "CADDocument", en_US = "CADDocument", zh_CN = "CADDocument")
public class CADDocument extends VersionControlled<CADDocumentMaster> implements ICharacted {
    private static final long serialVersionUID = 1L;
    @ManyToOne(targetEntity = CADDocumentMaster.class)
    @JoinColumn(name = "masterId", foreignKey = @ForeignKey(name = "MASTER_ID_FK"))
    protected CADDocumentMaster master;

    private CADDocument(CADDocumentMaster master) {
        this.setMaster(master);
    }

    public CADDocument() {
    }

    @Override
    public CADDocumentMaster getMaster() {
        return this.master;
    }

    @Override
    public void setMaster(CADDocumentMaster master) {
        this.master = master;
    }
}
