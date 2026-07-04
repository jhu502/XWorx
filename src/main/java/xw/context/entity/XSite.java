package xw.context.entity;

import com.flame.annotations.XDefinition;
import com.thing.common.DefaultThing;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "XSite", uniqueConstraints = {})
@XDefinition(name = "XSite", config = DefaultThing.class, icon = "images/sitemgmt.png", description = "XSite", display = "Site", en_US = "Site", zh_CN = "站点")
public class XSite extends Container {
	private static final long serialVersionUID = 1L;
}
