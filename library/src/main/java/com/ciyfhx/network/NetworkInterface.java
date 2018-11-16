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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import java8.util.concurrent.SubmissionPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NetworkInterface implements Runnable {

	private AtomicBoolean connected = new AtomicBoolean(false);

	protected NetworkConnection networkConnection;
	protected BaseServerClientModel model;

	private Logger logger = LoggerFactory.getLogger(NetworkInterface.class);

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

			while (connected.get()) {
				this.readProtocol();
			}

			//Close connection
			try {
				closePublishers();
				close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			connected.set(false);
			if (networkListener != null)
				networkListener.disconnected(networkConnection);
	}

	/**
	 * Transform or decode the incoming data from the predefine pipeline
	 * <b>Note:</b>This is similar to @see #transformPipelineWrite but does the transformation in reverse order
	 * @param publisher - the publisher which the will call closeExceptionally on
	 * @param buffer - buffer to be transform
	 * @return
	 * @throws Exception
	 */
	protected ByteBuffer transformPipelineRead(SubmissionPublisher<PacketEvent<Packet>> publisher, ByteBuffer buffer) throws Exception{
		ByteBuffer tmpBuffer = buffer;

		PipeLineStream pipeLineStream = networkConnection.getPipeLineStream();
		try {
			if (pipeLineStream != null)tmpBuffer = pipeLineStream.streamRead(buffer);
		}catch(Exception e){
			e.printStackTrace();
			logger.warn("Unable to decode data");
			publisher.closeExceptionally(e);
		}
		return tmpBuffer;
	}

	/**
	 * Transform or encode the outgoing data from the predefine pipeline
	 * @param buffer - buffer to be transform
	 * @return
	 * @throws Exception
	 */
	protected ByteBuffer transformPipelineWrite(ByteBuffer buffer) throws Exception{
		ByteBuffer tmpBuffer = buffer;

		PipeLineStream pipeLineStream = networkConnection.getPipeLineStream();
		if (pipeLineStream != null)
			tmpBuffer = pipeLineStream.streamWrite(buffer);
		return tmpBuffer;
	}

	abstract protected void readProtocol();
	abstract protected void writeProtocol(Packet packet) throws Exception ;

	/**
	 * Set the atomic connected boolean value
	 * Setting this to false will escape from the runnable loop but does not terminate existing network read calls
	 * @param connected
	 */
	protected void setConnected(boolean connected){
		this.connected.set(connected);
	}

	@Override
	public String toString(){
		return String.format("IP: %s", networkConnection.getAddress().getHostAddress());
	}

	/**
	 * Close all publishers
	 */
	private void closePublishers(){
		model.getPacketsFactory().getPublishers().forEach(SubmissionPublisher::close);
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
	public void sendPacket(Packet packet) throws Exception {
			writeProtocol(packet);
	}

	/**
	 * Send packet async
	 * @param packet
	 */
	public void sendPacketAsync(Packet packet){
		CompletableFuture.runAsync(() -> {
			try {
				sendPacket(packet);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}


}
