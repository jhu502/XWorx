package com.thing.email;

import com.flame.annotations.XDefinition;
import com.thing.runtime.ConnectableThing;

@XDefinition(name = "FTEmail", config = FTEmail.class, icon = "images/email.png", description = "FTEmail", display = "Email")
public class FTEmailThing extends ConnectableThing<FTEmail, EMailConnection> {

	public FTEmailThing(FTEmail target) {
		super(target);
	}
}
