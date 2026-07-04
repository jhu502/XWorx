package com.flame.thing;


import java.util.List;

import com.flame.orm.XPersistable;

/**
 * @author ph
 * @version 1.0
 * @created 29-10月-2019 22:20:04
 */
public interface IPropertyProvider extends XPersistable {
	List<IPropertyDefinition> getPropertyDefinitions();

	IPropertyDefinition getPropertyDefinition(String name);
}