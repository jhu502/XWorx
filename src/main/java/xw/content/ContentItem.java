package xw.content;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import com.flame.orm.XObject;

@MappedSuperclass
public class ContentItem extends XObject {
	private static final long serialVersionUID = 1L;

	@Basic
	@Column(name = "comments", length = 1000)
	private String comments = "";

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
