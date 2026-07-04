package com.thing.email;

import java.io.Closeable;
import java.io.IOException;

import com.thing.common.AbstractConnection;

public class EMailConnection extends AbstractConnection<FTEmail> implements Closeable {

	@Override
	public void close() throws IOException {
		
	}

}
