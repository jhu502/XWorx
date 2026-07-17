package xw.content.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import xw.content.ContentItem;

@Entity
@Table(name = "XResourceData", uniqueConstraints = {})
public class XResourceData extends ContentItem {
	private static final long serialVersionUID = 1L;

}
