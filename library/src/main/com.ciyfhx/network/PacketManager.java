package com.ciyfhx.network;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public abstract class PacketManager<P extends Packet> {


	private SubmissionPublisher<PacketEvent<P>> publisher = new SubmissionPublisher<PacketEvent<P>>();

	abstract public void readPacket(PacketEvent packetEvent);
	//public void writePacket(Packet packet){}

	public void subscribe(Flow.Subscriber<PacketEvent<P>> subscriber){
		publisher.subscribe(subscriber);
	}

	protected void submit(PacketEvent<P> packetEvent){
		publisher.submit(packetEvent);
	}
	
	
}
