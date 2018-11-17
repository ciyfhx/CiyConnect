/*
 * Copyright (c) 2018.
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.ciyfhx.network;

import java.nio.ByteBuffer;

public class Packet {

	//Data containing the data to be send
	protected ByteBuffer data;
	//The Packet ID number register with the packet factory
	protected int id;

	protected void initID(int id){
		this.id = id;
	}
	protected void initData(ByteBuffer data){this.data = data;}


	/**
	 * Set both the packet id and the data
	 * @param id
	 * @param data
	 */
	public Packet(int id, ByteBuffer data){
		this.data = data;
		initID(id);
	}

	/**
	 * Set the packet id but set the data to null
	 * <b>Note: </b> Remember to set the data afterward
	 * @param id
	 */
	public Packet(int id){
		this(id, null);
	}

	/**
	 * Set the data but set the packet id to -1
	 * <b>Note: </b> Remember to set the packet id afterward
	 * @param data
	 */
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
