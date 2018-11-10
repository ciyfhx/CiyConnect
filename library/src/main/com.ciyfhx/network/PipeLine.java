package com.ciyfhx.network;

import java.nio.ByteBuffer;

public interface PipeLine {
	
	ByteBuffer read(ByteBuffer data) throws Exception;
	ByteBuffer write(ByteBuffer data) throws Exception;
	
}
