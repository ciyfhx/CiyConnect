package com.ciyfhx.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class NetworkConnection {

	private InetAddress address;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	
	private Socket socket;
	
	private NetworkInterface networkInterface;
	
	private PipeLineStream pipeLineStream;
	
	private NetworkListener networkListener;

	private BaseServerClientModel model;

	public NetworkConnection(BaseServerClientModel model, InetAddress address, DataOutputStream dataOutputStream, DataInputStream dataInputStream, Socket socket) {
		this.model = model;
		this.address = address;
		this.dataOutputStream = dataOutputStream;
		this.dataInputStream = dataInputStream;
		this.socket = socket;
	}
	
	protected void setNetworkListener(NetworkListener networkListener){
		this.networkListener = networkListener;
	}

	/**
	 * Module method
	 * Creates new connection used for connecting
	 * @return
	 */
	protected NetworkInterface createNetworkInterface(){
		networkInterface = new NetworkInterface(this, model);
		return this.networkInterface;
	}

	public void setSoTimeout(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
	}

	public InetAddress getAddress() {
		return address;
	}

	public DataOutputStream getDataOutputStream() {
		return dataOutputStream;
	}

	public DataInputStream getDataInputStream() {
		return dataInputStream;
	}


	public Socket getSocket(){
		return socket;
	}
	
	public NetworkInterface getNetworkInterface(){
		return networkInterface;
	}
	
	public NetworkListener getNetworkListener(){
		return networkListener;
	}
	
	public void setPipeLineStream(PipeLineStream pipeLineStream){
		this.pipeLineStream = pipeLineStream;
	}

	public PipeLineStream getPipeLineStream(){
		return pipeLineStream;
	}
	
	public void close() throws IOException{
		dataInputStream.close();
		dataOutputStream.close();
		socket.close();
	}
	
}
