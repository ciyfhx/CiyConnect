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

import com.ciyfhx.network.authenticate.AuthenticationManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends BaseServerClientModel{

	protected ExecutorService executorService = Executors.newSingleThreadExecutor();

	protected Socket socket;

	protected NetworkConnection networkConnection;


	protected Client(AuthenticationManager authenticationManager, PacketsFactory packetsFactory){
		super(authenticationManager, packetsFactory);
	}

	/**
	 * Connect to server
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public NetworkConnection connect(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);

		networkConnection = new NetworkConnection(this, socket.getInetAddress(),
				new DataOutputStream(socket.getOutputStream()), new DataInputStream(socket.getInputStream()), socket);
		networkConnection.setNetworkListener(networkListener);
		//networkConnection.setPipeLineStream(networkConnection.getPipeLineStream());

		// Authenticate
		if (authenticationManager != null) {
			if (authenticationManager.clientAuthenticate(networkConnection)) {

				authenticationManager.authenticationSuccess(networkConnection);

				executorService.submit(networkConnection.createNetworkInterface());

			} else {
				authenticationManager.authenticationFailed(networkConnection);
			}
		} else {
			executorService.submit(networkConnection.createNetworkInterface());
		}
		return networkConnection;
	}

	/**
	 * Async connect
	 * @param host
	 * @param port
	 * @return
	 */
	public CompletableFuture<NetworkConnection> connectAsync(String host, int port){
		return CompletableFuture.supplyAsync(() -> {
			try {
				return connect(host, port);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}


	public NetworkConnection getNetworkConnection(){
		return networkConnection;
	}


	public void sendPacket(Packet packet) throws Exception {
		networkConnection.getNetworkInterface().sendPacket(packet);
	}

	public void setPipeLineStream(PipeLineStream pipeLineStream) {
		networkConnection.setPipeLineStream(pipeLineStream);
	}

	public PipeLineStream getPipeLineStream() {
		return networkConnection.getPipeLineStream();
	}
}
