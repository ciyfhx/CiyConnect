package com.ciyfhx.network;

import java.nio.ByteBuffer;

public class Packet {

	protected ByteBuffer data;
	private int id;

	protected void initID(int id){
		this.id = id;
	}
	
	public Packet(int id, ByteBuffer data){
		this.data = data;
		initID(id);
	}
	
	protected Packet(ByteBuffer data){
		this(-1, data);
	}
	
	public ByteBuffer getData(){
		return data;
	}
	
	public int getPacketID(){
		return id;
	}
	
	
}
