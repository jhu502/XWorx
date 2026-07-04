package xw.flow.entity;

import com.flame.orm.ObjectToObjectLink;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import xw.auths.entity.RoleRB;

@Entity
@Table(name = "XFlowRoleMap", uniqueConstraints = {})
public class XFlowRoleMap extends ObjectToObjectLink<XWorkInstance, RoleRB> {
}
