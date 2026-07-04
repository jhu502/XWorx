package xw.flow.entity;

import java.util.ArrayList;
import java.util.List;

import com.flame.orm.XConstant;
import com.flame.orm.JsonArrayConverter;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;
import xw.flow.IFlowRoute;
import xw.flow.bean.FlowRoute;
import xw.flow.constants.FlowLanguage;
import xw.flow.constants.FlowNodeType;

@Entity
@Table(name = "XFlowScriptTask", uniqueConstraints = {})
public class XFlowScriptTask extends XFlowNode implements IFlowRoute {
    private static final long serialVersionUID = 1L;
    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private FlowLanguage language;
    @Basic
    @Column(name = "expression", length = 4000)
    private String expression = "";
    @ColumnTransformer(write = "?::jsonb")
    @Convert(converter = JsonArrayConverter.class)
    @Column(name = "routes", columnDefinition = XConstant.JSONB)
    private List<FlowRoute> routes = new ArrayList<>();

    public static XFlowScriptTask newInstance(String nodeId, XFlowDefinition definition) {
        XFlowScriptTask flowtask = new XFlowScriptTask();
        flowtask.setNodeType(FlowNodeType.scriptTask);
        flowtask.setXFlowDefinition(definition);
        flowtask.setNodeId(nodeId);

        return flowtask;
    }

    public FlowLanguage getLanguage() {
        return language;
    }

    public void setLanguage(FlowLanguage language) {
        this.language = language;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<FlowRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<FlowRoute> routes) {
        this.routes = routes;
    }

    public void addRoute(FlowRoute route) {
        this.routes.add(route);
    }
}
