package com.ciyfhx.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkInterface implements Runnable {

	private long maxPacketSize = 5048;
	private int delay = 100;//In milliseconds

	private AtomicBoolean connected = new AtomicBoolean(false);

	private NetworkConnection networkConnection;

	private Logger logger = LoggerFactory.getLogger(NetworkInterface.class);

	private BaseServerClientModel model;

	protected NetworkInterface(NetworkConnection networkConnection, BaseServerClientModel model) {
		this.networkConnection = networkConnection;
		this.model = model;
	}

	@Override
	public void run() {

		connected.set(true);
		NetworkListener networkListener = networkConnection.getNetworkListener();
		if (networkListener != null)
			networkListener.connected(networkConnection);

		DataInputStream input = networkConnection.getDataInputStream();

		try {
			while (connected.get()) {

				int id = input.readInt();
				logger.trace("Received packet id: {} from: {}", id, networkConnection.getAddress());
				SubmissionPublisher<PacketEvent<Packet>> publisher = model.getPacketsFactory().checkPacket(id);
				if (publisher != null) {
					int packetSize = input.readInt();
					if (packetSize >= maxPacketSize) {
						logger.warn("Packet size exceed limit: {}", packetSize);
						break;
					}
					ByteBuffer buffer = ByteBuffer.allocate(packetSize);
					input.readFully(buffer.array(), 0, packetSize);

					// Through pipeline
					PipeLineStream pipeLineStream = networkConnection.getPipeLineStream();
					try {
						if (pipeLineStream != null)buffer = pipeLineStream.streamRead(buffer);
					}catch(Exception e){
						e.printStackTrace();
						logger.warn("Unable to decode data");
						break;
					}


					publisher.submit(new PacketEvent(networkConnection, new Packet(buffer)));
				} else {
					logger.warn("Unknown packet id: {}", id);
					break;
				}
				Thread.sleep(delay);
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.debug("Connection reset {}", networkConnection.getAddress());
		} finally {
			try {
				close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			connected.set(false);
			if (networkListener != null)
				networkListener.disconnected(networkConnection);
		}

	}

	@Override
	public String toString(){
		return String.format("IP: %s", networkConnection.getAddress().getHostAddress());
	}

	/**
	 * Set the interval each packet can be read in milliseconds
	 * @param delay
	 */
	public void setDelay(int delay){
		this.delay = delay;
	}
	
	/**
	 * Set the maximum packet size the interface can accept
	 * @param size
	 */
	public void setMaxPacketSize(long size) {
		this.maxPacketSize = size;
	}

	/**
	 * Close the current socket
	 * @throws IOException
	 */
	public void close() throws IOException {
		networkConnection.close();
	}

	/**
	 * Send packet to this network interface
	 * @param packet - packet to send
	 * @throws Exception
	 */
	public synchronized void sendPacket(Packet packet) throws Exception {
		DataOutputStream output = networkConnection.getDataOutputStream();
		output.writeInt(packet.getPacketID());
		output.flush();

		ByteBuffer data = packet.getData();

		// Through pipeline
		PipeLineStream pipeLineStream = networkConnection.getPipeLineStream();
		if (pipeLineStream != null)
			data = pipeLineStream.streamWrite(data);

		output.writeInt(data.capacity());
		output.flush();
		output.write(data.array());
		output.flush();
	}

//	/**
//	 * Send packet async
//	 * @param packet
//	 */
//	public void sendPacketAsync(Packet packet){
//		Executors.
//		CompletableFuture.runAsync(() -> {
//			try {
//				sendPacket(packet);
//			} catch (Exception e) {
//				exceptionFunction.OnThrowback(e);
//			}
//		});
//	}


}
