package xw.flow.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flame.vc.Master;

@Entity
@Table(name = "XFlowDefinitionMaster", uniqueConstraints = {})
public class XFlowDefinitionMaster extends Master {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "instructions")
	private String instructions = "";

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
}
