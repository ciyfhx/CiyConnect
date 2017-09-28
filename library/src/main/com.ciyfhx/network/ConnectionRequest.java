package com.ciyfhx.network;

public class ConnectionRequest {

	private NetworkConnection networkConnection;

	public ConnectionRequest(NetworkConnection networkConnection) {
		this.networkConnection = networkConnection;
	}

	public NetworkConnection getNetworkConnection() {
		return networkConnection;
	}

}
