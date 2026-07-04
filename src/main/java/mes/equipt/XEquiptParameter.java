package mes.equipt;

import com.flame.annotations.XDefinition;
import com.flame.orm.ObjectReference;
import com.flame.type.XBaseType;
import com.thing.common.DefaultThing;
import com.thing.entity.ModeledEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "XEquiptParameter", uniqueConstraints = {})
@XDefinition(name = "XEquiptParameter", config = DefaultThing.class, icon = "images/parameter.gif", description = "XEquiptParameter", display = "Equipt Parameter", en_US = "Equipt Parameter", zh_CN = "参数")
public class XEquiptParameter extends ModeledEntity {
	private static final long serialVersionUID = 1L;
	@Column(name = "baseType")
	@Enumerated(EnumType.STRING)
	private XBaseType baseType;
	@Basic
    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "equipId")), @AttributeOverride(name = "className", column = @Column(name = "equip_classname"))})
    protected ObjectReference<XEquiptInstance> instanceRef = null;
    @Basic
    @Column(name = "value")
    private String value = "";

    public static XEquiptParameter newXEquiptParameter(XEquiptInstance instance, String number, String name, String description) {
        XEquiptParameter xequipt = new XEquiptParameter(instance);
        xequipt.setNumber(number);
        xequipt.setName(name);
        xequipt.setDescription(description);

        return xequipt;
    }

    public XEquiptParameter() {
    }

    private XEquiptParameter(XEquiptInstance instance) {
        this.instanceRef = new ObjectReference<>(instance);
    }

    public ObjectReference<XEquiptInstance> getInstanceRef() {
        return instanceRef;
    }

    public void setInstanceRef(ObjectReference<XEquiptInstance> instanceRef) {
        this.instanceRef = instanceRef;
    }

	public XBaseType getBaseType() {
		return baseType;
	}

	public void setBaseType(XBaseType baseType) {
		this.baseType = baseType;
	}

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
