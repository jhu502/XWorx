package mes.apl;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flame.vc.Master;

@Entity
@Table(name = "XAssemblyLineMaster", uniqueConstraints = {})
public class XAssemblyLineMaster extends Master {
	private static final long serialVersionUID = 1L;
}
