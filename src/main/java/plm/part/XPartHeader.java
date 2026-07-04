package plm.part;

import com.flame.auths.ICreatorInfo;
import com.flame.lifecycle.ILifeCycleManaged;
import com.flame.lifecycle.LifeCycleState;
import com.flame.vc.CheckOutInfo;
import com.flame.vc.IAttributes;

import com.flame.vc.IVersioned;
import com.thing.entity.ModeledEntity;
import jakarta.persistence.*;
import xw.auths.entity.XUser;

public class XPartHeader extends ModeledEntity implements ICreatorInfo<XUser>, IVersioned<XPartMaster>, ILifeCycleManaged, IAttributes {
    private static final long serialVersionUID = 1L;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private LifeCycleState state;
    @Basic
    @Column(name = "latest")
    private boolean latest = false;
    @Basic
    @Column(name = "version", length = 10)
    private String version;
    @ManyToOne(fetch = FetchType.EAGER) // EAGER：立即加载(也可设为LAZY懒加载)
    @JoinColumn(name = "view", referencedColumnName = "name", nullable = false)
    private ViewRB view;
    @Column(name = "checkout_info")
    @Enumerated(EnumType.STRING)
    private CheckOutInfo checkOutInfo = CheckOutInfo.ci;
    @ManyToOne(targetEntity = XPartMaster.class)
    @JoinColumn(name = "masterId", foreignKey = @ForeignKey(name = "MASTER_ID_FK"))
    protected XPartMaster master;

    @Override
    public CheckOutInfo getCheckOutInfo() {
        return this.checkOutInfo;
    }

    @Override
    public void setCheckOutInfo(CheckOutInfo cio) {
        this.checkOutInfo = cio;
    }

    @Override
    public boolean isLatest() {
        return this.latest;
    }

    @Override
    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public XPartMaster getMaster() {
        return this.master;
    }

    public void setMaster(XPartMaster master) {
        this.master = master;
    }

    public ViewRB getView() {
        return view;
    }

    public void setView(ViewRB view) {
        this.view = view;
    }

    public String getPartNumber() {
        return this.getMaster().getNumber();
    }

    public void setPartNumber(String number) {
        this.getMaster().setNumber(number);
    }

    public String getPartName() {
        return this.getMaster().getName();
    }

    public void setPartName(String name) {
        this.getMaster().setName(name);
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public LifeCycleState getState() {
        return this.state;
    }

    @Override
    public void setState(LifeCycleState state) {
        this.state = state;
    }
}
