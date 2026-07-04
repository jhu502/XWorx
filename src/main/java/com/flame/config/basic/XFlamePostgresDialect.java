package com.flame.config.basic;

import org.hibernate.dialect.PostgresPlusDialect;

public class XFlamePostgresDialect extends PostgresPlusDialect {
	public XFlamePostgresDialect() {
		super();
		// this.registerColumnType(Types.JAVA_OBJECT, XConstant.JSONB); //使JPA支持jsonb
		//	this.registerHibernateType(Types.ARRAY, StringType.class.getName());
		//	this.registerHibernateType(Types.OTHER, StringType.class.getName());
	}

	//	@Override
	//	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor descriptor) {
	//		switch (descriptor.getSqlType()) {
	//		case Types.CLOB:
	//			return VarcharTypeDescriptor.INSTANCE;
	//		case Types.BLOB:
	//			return VarcharTypeDescriptor.INSTANCE;
	//		case 1111:
	//			return VarcharTypeDescriptor.INSTANCE;
	//		}
	//		return super.remapSqlTypeDescriptor(descriptor);
	//	}
}
