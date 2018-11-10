package com.ciyfhx.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

public class CompressionPipeLine implements PipeLine{


	@Override
	public ByteBuffer read(ByteBuffer data) throws IOException, DataFormatException {
		return ByteBuffer.wrap(CompressionUtils.decompress(data.array()));
	}

	@Override
	public ByteBuffer write(ByteBuffer data) throws IOException {
		return ByteBuffer.wrap(CompressionUtils.compress(data.array()));
	}


}
