package com.ciyfhx.network;

public interface NetworkListener {

	public void connected(NetworkConnection connector);
	public void disconnected(NetworkConnection disconnector);
	
}
