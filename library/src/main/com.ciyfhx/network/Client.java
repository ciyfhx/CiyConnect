package com.ciyfhx.network;

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


	/**
	 * Connect to server
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public void connect(String host, int port) throws UnknownHostException, IOException {
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

	}

	/**
	 * Async connect
	 * @param host
	 * @param port
	 * @return
	 */
	public CompletableFuture<Boolean> connectAync(String host, int port){

		CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
		executorService.submit(() -> {

			try {
				connect(host, port);
				future.complete(true);
			} catch (IOException e) {
				e.printStackTrace();
				future.completeExceptionally(e);
			}

		});
		return future;
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
