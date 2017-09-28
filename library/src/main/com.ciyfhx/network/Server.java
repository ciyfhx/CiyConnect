package com.ciyfhx.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server extends BaseServerClientModel{

	private Logger logger = LoggerFactory.getLogger(Server.class);

	protected ServerSocket server;

	protected ObservableMap<Integer, NetworkConnection> connections = FXCollections.observableHashMap();

	protected AtomicInteger idCounter = new AtomicInteger(-1);

	protected NetworkListener serverNetworkListener;

	protected int maxConnections = 3;

	private ExecutorService executorService = Executors.newFixedThreadPool(3);

	private AtomicBoolean init = new AtomicBoolean(false);

	/**
	 * Initialize server socket
	 * 
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public void init(int port) throws IllegalAccessException, IOException {
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
			public void connected(NetworkConnection connector) {
				if (networkListener != null)
					networkListener.connected(connector);
			}
		};

		server = new ServerSocket(port);

		init.set(true);
	}

	/**
	 * Accept Incoming connection
	 * 
	 * @throws IOException
	 */
	public void acceptIncomingConnection() throws IOException {
		if (!init.get())
			throw new IllegalStateException("Server not initialized!");

		running.set(true);
		while (isRunning()) {

			// accept connection
			Socket socket = server.accept();

			NetworkConnection networkConnection = createNetworkConnection(socket);
			networkConnection.setNetworkListener(serverNetworkListener);

			//Check whether there is too many connections
			if(connections.size()>=maxConnections){
				logger.warn("{} attempt to connect but there are too many connections", networkConnection.getAddress());
				networkConnection.close();
				continue;
			}

			// Check if authentication is null else we will just accept the
			// connection
			if (authenticationManager != null) {
				// Create a timeline for authentication (Only works in JavaFX)
//				Timeline authenticationTimer = new Timeline(
//						new KeyFrame(authenticationManager.getAuthenticationTimeOut(), ae -> {
//							// Authentication Timeout
//							authenticationManager.authenticationTimeOut(networkConnection);
//
//							try {
//								networkConnection.close();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//
//						}));
//				authenticationTimer.playFromStart();
				socket.setSoTimeout((int)authenticationManager.getAuthenticationTimeOut().toMillis());
				if (authenticationManager.serverAuthenticate(networkConnection)) {
//					authenticationTimer.stop();
					socket.setSoTimeout(0);
					authenticationManager.authenticationSuccess(networkConnection);


					executorService.submit(networkConnection.createNetworkInterface());

					addConnection(networkConnection);
				} else {
					// Authentication Failure
					authenticationManager.authenticationFailed(networkConnection);
				}
			} else {
				executorService.submit(networkConnection.createNetworkInterface());

				addConnection(networkConnection);
			}

		}
	}

	/**
	 * Calls the accept incoming connection return once the the server stop accepting connection(ASync)
	 * @return - future submitted
	 */
	public CompletableFuture<Boolean> acceptIncomingConnectionAsync() {
		ExecutorService service = Executors.newSingleThreadExecutor();
		CompletableFuture<Boolean> acceptConnection = new CompletableFuture<Boolean>();
		service.submit(() -> {

			try {
				acceptIncomingConnection();
				acceptConnection.complete(true);
			} catch (IOException e) {
				e.printStackTrace();
				acceptConnection.completeExceptionally(e);
			}

		});
		return acceptConnection;

	}

	public Stream<NetworkConnection> stream(){
		return connections.entrySet().stream().map(con -> con.getValue());
	}

	public void close() {
		if (!isRunning())
			throw new IllegalStateException("Server is not running!");

		running.set(false);

	}

	@Deprecated(since = "Long Long Time Ago :)")
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

	protected void addConnection(NetworkConnection networkConnection) throws IOException {
		if (!isRunning())
			throw new IllegalAccessError("Server not initialized!");

		connections.put(idCounter.incrementAndGet(), networkConnection);

	}

	protected void removeConnection(NetworkConnection networkConnection) throws IOException {
		if (!isRunning())
			throw new IllegalAccessError("Server not initialized!");
		networkConnection.getNetworkInterface().close();
		removeConnectionFromList(networkConnection);

	}

	protected void removeConnectionFromList(NetworkConnection networkConnection) {
		connections.values().remove(networkConnection);
		System.gc();
	}

	/**
	 * Set the maximum connections the server can handle at once
	 * @param maxConnections
	 */
	public void setMaxConnections(int maxConnections){
		this.maxConnections = maxConnections;
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
