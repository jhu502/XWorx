package xw.action.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flame.orm.ObjectToObjectLink;

@Entity
@Table(name = "xaction_item_link", uniqueConstraints = {})
public class XActionItemLink extends ObjectToObjectLink<XActionModel, AbstractAction> {
	private static final long serialVersionUID = 937566960567282485L;
	
	@Basic
	@Column(name = "sort")
	private String sort = "00";

	public static XActionItemLink newActionItemLink(XActionModel m, AbstractAction a) {
		XActionItemLink itemLink = new XActionItemLink();
		itemLink.setLeftObject(m);
		itemLink.setRightObject(a);

		return itemLink;
	}

	public static XActionItemLink newActionItemLink(XActionModel m, AbstractAction a, String sort) {
		XActionItemLink itemLink = new XActionItemLink();
		itemLink.setLeftObject(m);
		itemLink.setRightObject(a);
		itemLink.setSort(sort);

		return itemLink;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
}
