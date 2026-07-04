package xw.action.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flame.action.IActionModel;

@Entity
@Table(name = "XActionModel", uniqueConstraints = {})
public class XActionModel extends AbstractAction implements IActionModel {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "processor")
	private String processor = "";
	
	public XActionModel() {
		
	}
	
	public XActionModel(String name, String type, String display, String icon, String style) {
		this.setName(name);
		this.setType(type);
		this.setDisplay(display);
		this.setIcon(icon);
		this.setStyle(style);
	}

	public String getProcessor() {
		return this.processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}
}
