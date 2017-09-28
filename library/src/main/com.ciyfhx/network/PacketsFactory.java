package com.ciyfhx.network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Stream;

public class PacketsFactory {

	private Map<Integer, SubmissionPublisher<PacketEvent<Packet>>> registeredPublishers = new HashMap<Integer, SubmissionPublisher<PacketEvent<Packet>>>();

//	public static void registerPacketManager(PacketManager<Packet> packetManager, int id) {
//		registeredPacketManager.put(id, packetManager);
//	}
	
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

	public void registerIds(List<Integer> list){
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
