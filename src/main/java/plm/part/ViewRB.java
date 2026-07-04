package plm.part;

import java.util.List;

import com.flame.config.JPAConfiguration;
import com.flame.localize.AbstractEnumerated;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "ViewRB", uniqueConstraints = {})
public class ViewRB extends AbstractEnumerated<ViewRB> {
	private static final long serialVersionUID = 1L;

	public static ViewRB[] getViewRBSet() {
		List<ViewRB> resultList = JPAConfiguration.toRBTypeList(ViewRB.class);
		return resultList.toArray(new ViewRB[0]);
	}

	public static ViewRB toViewRB(String name) {
		return JPAConfiguration.toRBType(ViewRB.class, name);
	}
}
