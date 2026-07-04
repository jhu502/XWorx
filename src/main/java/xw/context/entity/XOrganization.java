package xw.context.entity;

import com.flame.annotations.XDefinition;
import com.thing.common.DefaultThing;
import xw.context.IContained;

import jakarta.persistence.*;

@Entity
@Table(name = "XOrganization", uniqueConstraints = {})
@XDefinition(name = "XOrganization", config = DefaultThing.class, icon = "images/navorg.png", description = "XOrganization", display = "Organization", en_US = "Organization", zh_CN = "组织")
public class XOrganization extends Container implements IContained<XSite> {
    private static final long serialVersionUID = 1L;
    @ManyToOne(targetEntity = XSite.class)
    @JoinColumn(name = "containerId", foreignKey = @ForeignKey(name = "CONTAINER_ID_FK"), nullable = false)
    private XSite container;


    @Override
    public XSite getContainer() {
        return container;
    }

    @Override
    public void setContainer(XSite container) {
        this.container = container;
    }
}
