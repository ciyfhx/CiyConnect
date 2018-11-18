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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import com.ciyfhx.network.authenticate.AuthenticationFailure;
import com.ciyfhx.network.authenticate.AuthenticationManager;
import com.ciyfhx.network.dispatcher.ServerConnectionDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

public class Server extends BaseServerClientModel{

	private Logger logger = LoggerFactory.getLogger(Server.class);

	protected ServerSocket server;

	protected ConcurrentHashMap<Long, NetworkConnection> connections = new ConcurrentHashMap<Long, NetworkConnection>();

	protected long idCounter = -1;

	protected NetworkListener serverNetworkListener;


	private ServerConnectionDispatcher dispatcher;

	private AtomicBoolean init = new AtomicBoolean(false);

	protected Server(AuthenticationManager authenticationManager, PacketsFactory packetsFactory, ServerConnectionDispatcher dispatcher) {
		super(authenticationManager, packetsFactory);
		this.dispatcher = dispatcher;
	}

	/**
	 * Initialize server socket
	 *
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	protected void init(int port, int backLog, InetAddress address, SSLContext sslContext) throws IllegalAccessException, IOException {
		if (isRunning())
			throw new IllegalAccessException("Server is already running");

		serverNetworkListener = new NetworkListener() {

			@Override
			public void disconnected(NetworkConnection disconnector) {
				removeConnectionFromList(disconnector);
				if (networkListener != null)
					networkListener.disconnected(disconnector);
			}

			@Override
			public void preConnection(NetworkConnection connector) {
				if (networkListener != null)
					networkListener.preConnection(connector);
			}

			@Override
			public void connected(NetworkConnection connector) {
				if (networkListener != null)
					networkListener.connected(connector);
			}
		};

		if(sslContext != null){
			ServerSocketFactory sslSocketFactory = sslContext.getServerSocketFactory();
			server = sslSocketFactory.createServerSocket(port, backLog, address);
		}else{
			server = new ServerSocket(port, backLog, address);
		}


		init.set(true);
	}

	/**
	 * Accept Incoming connection
	 * 
	 * @throws IOException
	 */
	public NetworkConnection acceptIncomingConnection() throws Exception {
		if (!init.get())
			throw new IllegalStateException("Server not initialized!");

		running.set(true);
		if (isRunning()) {

			// accept connection
			Socket socket = server.accept();

			NetworkConnection networkConnection = createNetworkConnection(socket);
			networkConnection.setNetworkListener(serverNetworkListener);
			NetworkInterface networkInterface = networkConnection.createNetworkInterface();

			//Call the pre connection listener
			serverNetworkListener.preConnection(networkConnection);


			// Check if authentication is null else we will just accept the
			// connection
			if (authenticationManager != null) {
				socket.setSoTimeout((int)authenticationManager.getAuthenticationTimeOut());
				if (authenticationManager.serverAuthenticate(networkConnection)) {
					socket.setSoTimeout(0);//Reset timeout
					authenticationManager.authenticationSuccess(networkConnection);

					dispatcher.dispatchConnection(this, networkInterface);

					addConnection(networkConnection);
				} else {
					// Authentication Failure
					authenticationManager.authenticationFailed(networkConnection);
					throw new AuthenticationFailure("Unable to authenticate");

				}
			} else {
				//If no authentication protocol is specified
				dispatcher.dispatchConnection(this, networkConnection.createNetworkInterface());

				addConnection(networkConnection);
			}
			return networkConnection;
		}
		return null;
	}

	/**
	 * Calls the accept incoming connection return once the the server stop accepting connection(ASync)
	 * @return - future submitted
	 */
	public CompletableFuture<NetworkConnection> acceptIncomingConnectionAsync() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return acceptIncomingConnection();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

	}

	public Stream<NetworkConnection> stream(){
		return connections.entrySet().stream().map(con -> con.getValue());
	}

	/**
	 * Close the server and all the existing connections
	 */
	public void close() {
		if (!isRunning())
			throw new IllegalStateException("Server is not running!");


		this.stream().forEach((con) -> {
			try {
				removeConnection(con);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Error closing network for {}", con);
			}
		});
		running.set(false);
	}

	@Deprecated()
	public void broadcast(Packet packet) throws NetworkResetException{
		Iterator<NetworkConnection> networkConnections = connections.values().iterator();
		while (networkConnections.hasNext()) {
			NetworkConnection networkConnection = networkConnections.next();
			try {
				networkConnection.getNetworkInterface().sendPacket(packet);
			} catch (Exception e) {
				removeConnectionFromList(networkConnection);
				throw new NetworkResetException(networkConnection);
			}
		}
	}

	protected NetworkConnection createNetworkConnection(Socket socket) throws IOException {
		return new NetworkConnection(this, socket.getInetAddress(), new DataOutputStream(socket.getOutputStream()),
				new DataInputStream(socket.getInputStream()), socket);
	}

	/**
	 * Add a new connection
	 * @param networkConnection
	 * @throws IOException
	 */
	protected void addConnection(NetworkConnection networkConnection) throws IOException {
		if (!isRunning())
			throw new IllegalAccessError("Server not initialized!");

		connections.put((idCounter+1), networkConnection);

	}

	/**
	 * Forcefully remove an existing connection
	 * @param networkConnection
	 * @throws IOException
	 */
	protected void removeConnection(NetworkConnection networkConnection) throws IOException {
		if (!isRunning())
			throw new IllegalAccessError("Server not initialized!");
		networkConnection.getNetworkInterface().close();
		removeConnectionFromList(networkConnection);

	}

	/**
	 * Forcefully remove an existing connection from list
	 * @param networkConnection
	 * @throws IOException
	 */
	protected void removeConnectionFromList(NetworkConnection networkConnection) {
		connections.values().remove(networkConnection);
		System.gc();
	}

	/**
	 * The total amount of connections
	 * @return
	 */
	public int getConnectionsCount(){
		return connections.size();
	}

	/**
	 * Check if server is accepting connection
	 *
	 * @return
	 */
	@Override
	public boolean isRunning() {
		return super.isRunning();
	}


}
