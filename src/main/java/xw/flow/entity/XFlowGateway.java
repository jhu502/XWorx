package xw.flow.entity;

import java.util.ArrayList;
import java.util.List;

import com.flame.orm.XConstant;
import com.flame.orm.JsonArrayConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "XFlowGateway", uniqueConstraints = {})
public class XFlowGateway extends XFlowNode {
    private static final long serialVersionUID = 1L;
    @ColumnTransformer(write = "?::jsonb")
    @Convert(converter = JsonArrayConverter.class)
    @Column(name = "arguments", columnDefinition = XConstant.JSONB)
    private List<String> arguments = new ArrayList<>();

    public static XFlowGateway newInstance(String nodeId, XFlowDefinition definition) {
        XFlowGateway gateway = new XFlowGateway();
        gateway.setXFlowDefinition(definition);
        gateway.setNodeId(nodeId);

        return gateway;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}
