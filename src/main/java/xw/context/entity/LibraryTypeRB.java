package xw.context.entity;

import org.springframework.cache.annotation.Cacheable;

import com.flame.config.JPAConfiguration;
import com.flame.localize.AbstractEnumerated;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Cacheable // 启用二级缓存
@Table(name = "LibraryTypeRB", uniqueConstraints = {})
public class LibraryTypeRB extends AbstractEnumerated<LibraryTypeRB> {
    private static final long serialVersionUID = 1L;

	public static LibraryTypeRB toLibraryTypeRB(String name) {
		return JPAConfiguration.toRBType(LibraryTypeRB.class, name);
	}
}
