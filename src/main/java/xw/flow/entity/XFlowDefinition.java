package xw.flow.entity;

import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.repository.ProcessDefinition;
import org.hibernate.annotations.ColumnTransformer;

import com.flame.annotations.XDefinition;
import com.flame.orm.JsonArrayConverter;
import com.flame.orm.XConstant;
import com.thing.common.DefaultThing;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import xw.vc.IterationControlled;
import xw.flow.IFlowVariable;
import xw.flow.bean.FlowVariable;

@Entity
@Table(name = "XFlowDefinition", uniqueConstraints = {})
@XDefinition(name = "XFlowDefinition", config = DefaultThing.class, icon = "images/flow/flowdef.gif", description = "XFlowDefinition",
        display = "Flow Template", en_US = "Flow Definition", zh_CN = "流程定义")
public class XFlowDefinition extends IterationControlled<XFlowDefinitionMaster> implements IFlowVariable {
    private static final long serialVersionUID = 1L;
    @Basic
    @Column(name = "processDefId")
    private String processDefId;
    @Basic
    @Column(name = "enabled")
    private boolean enabled = true;
    @Basic
    @Column(name = "deployed")
    private boolean deployed = false;
    @ColumnTransformer(write = "?::jsonb")
    @Convert(converter = JsonArrayConverter.class)
    @Column(name = "variables", columnDefinition = XConstant.JSONB)
    private List<FlowVariable> variables = new ArrayList<>();
    @ManyToOne(targetEntity = XFlowDefinitionMaster.class)
    @JoinColumn(name = "masterId", foreignKey = @ForeignKey(name = "MASTER_ID_FK"))
    protected XFlowDefinitionMaster master;

    public static XFlowDefinition newXFlowDefinition(String number, String name) {
        XFlowDefinitionMaster master = new XFlowDefinitionMaster();
        XFlowDefinition definition = new XFlowDefinition();
        definition.setMaster(master);
        definition.setNumber(number);
        definition.setName(name);

        return definition;
    }

    public XFlowDefinition() {
    }

    @Override
    public XFlowDefinitionMaster getMaster() {
        return this.master;
    }

    @Override
    public void setMaster(XFlowDefinitionMaster master) {
        this.master = master;
    }

    public String getInstructions() {
        return this.getMaster().getInstructions();
    }

    public void setInstructions(String instructions) {
        this.getMaster().setInstructions(instructions);
    }

    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefinition(ProcessDefinition processDefinition) {
        this.processDefId = processDefinition.getId();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

    public List<FlowVariable> getVariables() {
        return variables;
    }

    public void setVariables(List<FlowVariable> variables) {
        this.variables = variables;
    }

    public void addVariable(FlowVariable variable) {
        this.variables.add(variable);
    }
}
