package com.ciyfhx.network;

import java.io.IOException;

public class NetworkResetException extends IOException{

	private static final long serialVersionUID = 1L;
	
	public NetworkResetException(NetworkConnection connection){
		super(String.format("Connection Reset for %s\n", connection.getAddress()));
	}
	

}
