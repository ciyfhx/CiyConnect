package com.ciyfhx.network.authenticate;

import com.ciyfhx.network.NetworkConnection;

public abstract class AuthenticationManager {
	
	private long authenticationTimeOut = 5000;

	abstract public boolean serverAuthenticate(NetworkConnection connection);

	abstract public boolean clientAuthenticate(NetworkConnection connection);

	abstract public void authenticationSuccess(NetworkConnection connection);

	abstract public void authenticationFailed(NetworkConnection connection);
	
	abstract public void authenticationTimeOut(NetworkConnection connection);

	public static RSAWithAESAuthentication getDefaultAutenticationManager() {
		return new RSAWithAESAuthentication();
	}

	public void setAuthenticationTimeOut(long authenticationTimeOut){
		this.authenticationTimeOut = authenticationTimeOut;
	}
	
	public long getAuthenticationTimeOut(){
		return authenticationTimeOut;
	}
	
}
