package com.ciyfhx.network;

public class PacketEvent<P extends Packet> {

	private NetworkConnection networkConnection;
	private P packet;

	protected PacketEvent(NetworkConnection networkConnection, P packet) {
		this.networkConnection = networkConnection;
		this.packet = packet;
	}

	public P getPacket() {
		return packet;
	}

	public NetworkConnection getSenderNetworkConnection() {
		return networkConnection;
	}

}
