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

import java8.util.concurrent.SubmissionPublisher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class PacketsFactory {

	private ConcurrentHashMap<Integer, SubmissionPublisher<PacketEvent<Packet>>> registeredPublishers = new ConcurrentHashMap<Integer, SubmissionPublisher<PacketEvent<Packet>>>();

	
	public void registerPublisher(SubmissionPublisher<PacketEvent<Packet>> publisher, int id) throws AlreadyInsertedException {
		registeredPublishers.put(id, publisher);
	}

	protected SubmissionPublisher<PacketEvent<Packet>> checkPacket(int id) {

		for (int idCheck : registeredPublishers.keySet()) {
			if (idCheck == id)
				return registeredPublishers.get(idCheck);
		}
		return null;

	}

	public void registerIds(Collection<Integer> list){
		list.stream().distinct().filter(id -> !registeredPublishers.keySet().contains(id)).forEach(id -> registeredPublishers.put(id, new SubmissionPublisher<PacketEvent<Packet>>()));
	}

	/**
	 *
	 *
	 * @param id - Packet ID
	 * @return Flow.Publisher - Subscribe to receive packets from the given packet ID
	 * @throws AlreadyInsertedException
	 */
	public SubmissionPublisher<PacketEvent<Packet>> registerId(int id) throws AlreadyInsertedException {
		if(registeredPublishers.keySet().contains(id))throw new AlreadyInsertedException("Duplicated Ids");

		SubmissionPublisher<PacketEvent<Packet>> publisher = new SubmissionPublisher<PacketEvent<Packet>>();
		registeredPublishers.put(id, publisher);
		return publisher;
	}

	public SubmissionPublisher<PacketEvent<Packet>> getPublisher(int id) throws NotFoundException {
		SubmissionPublisher<PacketEvent<Packet>> publisher = registeredPublishers.get(id);
		if(publisher==null)throw new NotFoundException("Publisher is not found!");
		return publisher;
	}

	public Stream<SubmissionPublisher<PacketEvent<Packet>>> getPublishers(){
		return registeredPublishers.entrySet().stream().map(p -> p.getValue());
	}

	public class AlreadyInsertedException extends Throwable{

		public AlreadyInsertedException(String msg){
			super(msg);
		}

	}

	public class NotFoundException extends Throwable {

		public NotFoundException(String msg){
			super(msg);
		}

	}
}
