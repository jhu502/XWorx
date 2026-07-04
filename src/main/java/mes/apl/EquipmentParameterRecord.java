package mes.apl;

import com.flame.orm.XObject;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "XEquipParameterRecord", uniqueConstraints = {})
public class EquipmentParameterRecord extends XObject {
    private static final long serialVersionUID = 1L;
    @Basic
    @Column(name = "name", length = 200)
    private String name = "";
    @Basic
    @Column(name = "parameter", length = 100)
    private String parameter = "";
    @Basic
    @Column(name = "value", length = 100)
    private String value = "";
    @Basic
    @Column(name = "source", length = 100)
    private String source = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
