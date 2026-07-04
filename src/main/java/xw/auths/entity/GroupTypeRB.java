package xw.auths.entity;

import java.util.List;

import com.flame.config.JPAConfiguration;
import com.flame.localize.AbstractEnumerated;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "GroupTypeRB", uniqueConstraints = {})
public class GroupTypeRB extends AbstractEnumerated<GroupTypeRB> {
	private static final long serialVersionUID = 1L;

	public static GroupTypeRB[] getGroupTypeRBSet() {
		List<GroupTypeRB> resultList = JPAConfiguration.toRBTypeList(GroupTypeRB.class);
		return resultList.toArray(new GroupTypeRB[0]);
	}

	public static GroupTypeRB toGroupTypeRB(String name) {
		return JPAConfiguration.toRBType(GroupTypeRB.class, name);
	}
}
