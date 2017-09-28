package com.ciyfhx.network;

import java.nio.ByteBuffer;

public interface PipeLine {
	
	abstract public ByteBuffer read(ByteBuffer data) throws Exception;
	abstract public ByteBuffer write(ByteBuffer data) throws Exception;
	
}
