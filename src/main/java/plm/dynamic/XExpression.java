package plm.dynamic;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import plm.part.XPart;
import com.flame.orm.ItemEntity;

@Entity
@Table(name = "XExpression", uniqueConstraints = {})
public class XExpression extends ItemEntity {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "expression")
	private String expression = "";

	@ManyToOne(targetEntity = XPart.class)
	@JoinColumn(name = "part_xid", foreignKey = @ForeignKey(name = "XPART_XEXPRESS_ID_FK"))
	private XPart part;

	public static XExpression newXExpression(XPart part) {
		XExpression express = new XExpression();
		express.part = part;

		return express;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public XPart getPart() {
		return this.part;
	}

	public void setPart(XPart part) {
		this.part = part;
	}
}
