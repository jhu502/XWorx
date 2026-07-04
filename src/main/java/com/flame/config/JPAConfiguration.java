package com.flame.config;

import java.util.List;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.flame.auths.ISession;
import com.flame.auths.IUser;
import com.flame.localize.IEnumeratedType;
import com.flame.orm.PersistenceHelper;
import com.flame.util.StringUtil;

import jakarta.annotation.Resource;

@Configuration
@ComponentScan({ "com.flame.orm" })
@EntityScan({ "com.flame.orm" })
@EnableJpaRepositories({ "com.flame.orm" })
public class JPAConfiguration {
    private static JPAConfiguration jpaConfiguration;
	private static ApplicationContext applicationContext;
	private static ISession isession;

	//@Cacheable(value = "CacheRB", key = "'MBA:' + #enumType.getName() + '|' + #name")
	public <T extends IEnumeratedType<T>> List<T> getEnumeratedType(Class<T> enumType, String field, Object value) {
		if (enumType == null || StringUtil.isBlank(field)) {
			return null;
		}

		return PersistenceHelper.service().query(enumType, new Object[][] { { field, value } });
	}

	@Resource
	public void setApplicationContext(ApplicationContext appContext) {
		applicationContext = appContext;
	}

	@Resource
	public void setISession(ISession session) {
		isession = session;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}
    
    public static JPAConfiguration instance() {
        if (jpaConfiguration == null) {
            jpaConfiguration = JPAConfiguration.getBean(JPAConfiguration.class);
        }
        
        return jpaConfiguration;
    }

	public static <T> T getBean(Class<T> clazz) {
		if (applicationContext == null)
			return null;

		return applicationContext.getBean(clazz);
	}

	public static <T> T getBean(String name, Class<T> clazz) {
		return applicationContext.getBean(name, clazz);
	}

	public static IUser currentUser() {
		return isession.currentUser();
	}

    public static <T extends IEnumeratedType<T>> T toRBType(Class<T> clazz, String name) {
		List<T> resultList = JPAConfiguration.instance().getEnumeratedType(clazz, "name", name);
		if (resultList == null || resultList.isEmpty()) {
			return null;
		} else {
			return resultList.get(0);
		}
    }

    public static <T extends IEnumeratedType<T>> List<T> toRBTypeList(Class<T> clazz) {
        return PersistenceHelper.service().query(clazz, new Object[0][0]);
    }

    public static <T extends IEnumeratedType<T>> List<T> toRBTypeList(Class<T> clazz, String field, Object value) {
        return JPAConfiguration.instance().getEnumeratedType(clazz, field, value);
    }
}
