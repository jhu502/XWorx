package com.flame.test;

import com.flame.orm.AbstractEntity;
import com.flame.vc.CheckOutInfo;
import com.flame.vc.Iterated;
import jakarta.persistence.*;

@Entity
@Table(name = "WTPart", uniqueConstraints = {})
public class WTPart extends AbstractEntity implements Iterated<WTPartMaster> {
    private static final long serialVersionUID = 1L;
    @Basic
    @Column(name = "latest")
    private boolean latest = false;
    @Basic
    @Column(name = "previous")
    private long previous = -1;
    @Column(name = "checkout_info")
    @Enumerated(EnumType.STRING)
    private CheckOutInfo checkOutInfo = CheckOutInfo.ci;
    @ManyToOne(targetEntity = ViewRB.class)
    @JoinColumn(name = "viewId", foreignKey = @ForeignKey(name = "VIEW_ID_FK"))
    private ViewRB view;
    @ManyToOne(targetEntity = WTPartMaster.class)
    @JoinColumn(name = "masterId", foreignKey = @ForeignKey(name = "MASTER_ID_FK"))
    protected WTPartMaster master;

    public static WTPart newInstance(String number, String name) {
        WTPartMaster master = new WTPartMaster();
        master.setGenericType(GenericType.Dynamic);
        WTPart part = new WTPart(master);
        part.setNumber(number);
        part.setName(name);

        return part;
    }

    public WTPart() {
    }

    public WTPart(WTPartMaster master) {
        this.master = master;
    }

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

    public WTPartMaster getMaster() {
        return this.master;
    }

    @Override
    public void setMaster(WTPartMaster master) {
        this.master = master;
    }

    public ViewRB getView() {
        return this.view;
    }

    @Override
    public String getNumber() {
        return this.getMaster().getNumber();
    }

    @Override
    public void setNumber(String number) {
        this.getMaster().setNumber(number);
    }

    @Override
    public String getName() {
        return this.getMaster().getName();
    }

    @Override
    public void setName(String name) {
        this.getMaster().setName(name);
    }

    public void setView(ViewRB view) {
        this.view = view;
    }
}
