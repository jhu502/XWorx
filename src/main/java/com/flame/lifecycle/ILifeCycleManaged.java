package com.flame.lifecycle;

import com.flame.orm.XPersistable;

/**
 * @author ph
 * @version 1.0
 * @created 29-10月-2019 22:20:05
 */
public interface ILifeCycleManaged extends XPersistable {

    LifeCycleState getState();

    void setState(LifeCycleState state);

}