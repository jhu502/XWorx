package mes.equipt;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flame.vc.Master;

@Entity
@Table(name = "XEquipmentMaster", uniqueConstraints = {})
public class XEquipmentMaster extends Master {
	private static final long serialVersionUID = 1L;
}
