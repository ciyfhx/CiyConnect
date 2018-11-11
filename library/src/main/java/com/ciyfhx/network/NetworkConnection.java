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
import java.lang.reflect.InvocationTargetException;
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
	private Session session;

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
		try {
			networkInterface = (NetworkInterface) model.networkInterfaceClass.getDeclaredConstructors()[0].newInstance(this, this.model);
			return this.networkInterface;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
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


	public Session getSession() {
		return session;
	}


	public void close() throws IOException{
		dataInputStream.close();
		dataOutputStream.close();
		socket.close();
	}
	
}
