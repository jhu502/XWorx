package xw.context.entity;

import xw.context.IContained;

import jakarta.persistence.*;

@Entity
@Table(name = "XCompany", uniqueConstraints = {})
public class XCompany extends Container implements IContained {
    private static final long serialVersionUID = 1L;
    @ManyToOne(targetEntity = XSite.class)
    @JoinColumn(name = "container_id", foreignKey = @ForeignKey(name = "CONTAINER_ID_FK"))
    private Container container;

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }
}
