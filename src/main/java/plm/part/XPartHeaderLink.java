package plm.part;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flame.orm.ObjectReference;
import com.flame.orm.ObjectToObjectLink;

@Entity
@Table(name = "XPartHeaderLink", uniqueConstraints = {})
public class XPartHeaderLink extends ObjectToObjectLink<XPartHeader, XPartMaster> {
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
	@Basic
	@Column(name = "startStamp")
	private Timestamp startStamp = new Timestamp(new Date().getTime());
	@Basic
	@Column(name = "deadStamp")
	private Timestamp deadStamp = new Timestamp(new Date().getTime());
	@Basic
	@Column(name = "changeNumber")
	private String changeNumber = "";
	
	public static XPartHeaderLink newPartHeaderLink(XPartHeader header, XPartMaster master) {
		XPartHeaderLink headerlink = new XPartHeaderLink();
		headerlink.setComponentId(UUID.randomUUID().toString());
		headerlink.setLeft(new ObjectReference<XPartHeader>(header));
		headerlink.setRight(new ObjectReference<XPartMaster>(master));
		return headerlink;
	}

	public static XPartHeaderLink copyPartHeaderLink(XPartHeader header, XPartHeaderLink link) {
		XPartHeaderLink headerLink = new XPartHeaderLink();
		headerLink.setLeft(new ObjectReference<XPartHeader>(header));
		headerLink.setRight(link.getRight());
		headerLink.setQuantity(link.getQuantity());
		headerLink.setUnit(link.getUnit());
		headerLink.setComponentId(link.getComponentId());
		headerLink.setLineNumber(link.getLineNumber());
		headerLink.setInclusionExpression(link.getInclusionExpression());
		headerLink.setQuantityExpression(link.getQuantityExpression());
		headerLink.setStartStamp(link.getStartStamp());
		headerLink.setDeadStamp(link.getDeadStamp());
		return headerLink;
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

	public Timestamp getStartStamp() {
		return startStamp;
	}

	public void setStartStamp(Timestamp startStamp) {
		this.startStamp = startStamp;
	}

	public Timestamp getDeadStamp() {
		return deadStamp;
	}

	public void setDeadStamp(Timestamp deadStamp) {
		this.deadStamp = deadStamp;
	}

	public String getChangeNumber() {
		return changeNumber;
	}

	public void setChangeNumber(String changeNumber) {
		this.changeNumber = changeNumber;
	}
}
