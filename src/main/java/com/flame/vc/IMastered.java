package com.flame.vc;

import com.flame.orm.XPersistable;

public interface IMastered extends XPersistable {

    String getNumber();

    void setNumber(String number);

    String getName();

    void setName(String name);

}
