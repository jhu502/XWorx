package xw.content.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import xw.content.ContentItem;
import xw.content.IContentHolder;
import com.flame.orm.ObjectToObjectLink;

@Entity
@Table(name = "XHolderToContent", uniqueConstraints = {})
public class XHolderToContent extends ObjectToObjectLink<IContentHolder, ContentItem> {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "number", length = 100)
	private String number = "";

	@Basic
	@Column(name = "description", length = 1000)
	private String description = "";

	public static XHolderToContent newInstance(IContentHolder holder, ContentItem item) {
		XHolderToContent holderContent = new XHolderToContent(holder, item);

		return holderContent;
	}

	public XHolderToContent() {
	}

	public XHolderToContent(IContentHolder holder, ContentItem item) {
		this.setLeftObject(holder);
		this.setRightObject(item);
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setContentItem(ContentItem item) {
		this.setRightObject(item);
	}
}
