package com.ciyfhx.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import com.ciyfhx.network.authenticate.AuthenticationManager;
import com.ciyfhx.network.dispatcher.ServerConnectionDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server extends BaseServerClientModel{

	private Logger logger = LoggerFactory.getLogger(Server.class);

	protected ServerSocket server;

	protected ConcurrentHashMap<Long, NetworkConnection> connections = new ConcurrentHashMap<Long, NetworkConnection>();
//
//	protected AtomicInteger idCounter = new AtomicInteger(-1);
	protected long idCounter = -1;

	protected NetworkListener serverNetworkListener;



//	protected int maxConnections = 3;

//
//	private ExecutorService executorService = Executors.newFixedThreadPool(3);

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
//			if(connections.size()>=maxConnections){
//				logger.warn("{} attempt to connect but there are too many connections", networkConnection.getAddress());
//				networkConnection.close();
//				continue;
//			}

			// Check if authentication is null else we will just accept the
			// connection
			if (authenticationManager != null) {
				socket.setSoTimeout((int)authenticationManager.getAuthenticationTimeOut());
				if (authenticationManager.serverAuthenticate(networkConnection)) {
					socket.setSoTimeout(0);
					authenticationManager.authenticationSuccess(networkConnection);

					dispatcher.dispatchConnection(this, networkConnection.createNetworkInterface());
					//executorService.submit(networkConnection.createNetworkInterface());

					addConnection(networkConnection);
				} else {
					// Authentication Failure
					authenticationManager.authenticationFailed(networkConnection);
				}
			} else {
				//If no authentication protocal is specified
				dispatcher.dispatchConnection(this, networkConnection.createNetworkInterface());
				//executorService.submit(networkConnection.createNetworkInterface());

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

	protected void addConnection(NetworkConnection networkConnection) throws IOException {
		if (!isRunning())
			throw new IllegalAccessError("Server not initialized!");

		connections.put((idCounter+1), networkConnection);

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
