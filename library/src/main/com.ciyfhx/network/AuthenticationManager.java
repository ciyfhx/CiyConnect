package com.ciyfhx.network;

import javafx.util.Duration;

public abstract class AuthenticationManager {
	
	private Duration authenticationTimeOut = Duration.millis(5000);

	abstract public boolean serverAuthenticate(NetworkConnection connection);

	abstract public boolean clientAuthenticate(NetworkConnection connection);

	abstract public void authenticationSuccess(NetworkConnection connection);

	abstract public void authenticationFailed(NetworkConnection connection);
	
	abstract public void authenticationTimeOut(NetworkConnection connection);

	public static RSAWithAESAuthentication getDefaultAutenticationManager() {
		return new RSAWithAESAuthentication();
	}

	public void setAuthenticationTimeOut(Duration authenticationTimeOut){
		this.authenticationTimeOut = authenticationTimeOut;
	}
	
	public Duration getAuthenticationTimeOut(){
		return authenticationTimeOut;
	}
	
}
