package plm.part;

import com.flame.orm.ObjectReference;
import com.flame.vc.ObjectUsageLink;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;

import java.util.UUID;

public class XHeaderUsageLink  extends ObjectUsageLink<XPartHeader, XPartMaster> {
    private static final long serialVersionUID = 1L;

    @Basic
    @Column(name = "componentId")
    private String componentId = "";

    @Basic
    @Column(name = "lineNumber", length = 10)
    private String lineNumber = "";

    @Basic
    @Column(name = "quantity")
    private double quantity = 0;

    @Basic
    @Column(name = "unit")
    private String unit = "ea";

    @Basic
    @Column(name = "inclusionExpression", length = 1000)
    private String inclusionExpression = "";

    @Basic
    @Column(name = "quantityExpression", length = 1000)
    private String quantityExpression = "";

    public static XHeaderUsageLink newInstance(XPartHeader header, XPartMaster master) {
        XHeaderUsageLink usageLink = new XHeaderUsageLink();
        usageLink.setComponentId(UUID.randomUUID().toString());
        usageLink.setLeft(new ObjectReference<>(header));
        usageLink.setRight(new ObjectReference<>(master));
        return usageLink;
    }

    public static XHeaderUsageLink newInstance(XPartHeader header, XPartUsageLink link) {
        XHeaderUsageLink usageLink = new XHeaderUsageLink();
        usageLink.setLeft(new ObjectReference<>(header));
        usageLink.setRight(link.getRight());
        usageLink.setQuantity(link.getQuantity());
        usageLink.setUnit(link.getUnit());
        usageLink.setComponentId(link.getComponentId());
        usageLink.setLineNumber(link.getLineNumber());
        usageLink.setInclusionExpression(link.getInclusionExpression());
        usageLink.setQuantityExpression(link.getQuantityExpression());
        return usageLink;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getInclusionExpression() {
        return inclusionExpression;
    }

    public void setInclusionExpression(String inclusionExpression) {
        this.inclusionExpression = inclusionExpression;
    }

    public String getQuantityExpression() {
        return quantityExpression;
    }

    public void setQuantityExpression(String quantityExpression) {
        this.quantityExpression = quantityExpression;
    }
}
