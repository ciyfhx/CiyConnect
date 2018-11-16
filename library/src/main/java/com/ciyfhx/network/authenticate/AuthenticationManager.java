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


package com.ciyfhx.network.authenticate;

import com.ciyfhx.network.NetworkConnection;
import com.ciyfhx.network.authentication.RSAWithAESAuthenticationWithHMACValidator;

public abstract class AuthenticationManager {
	
	private long authenticationTimeOut = 5000;

	abstract public boolean serverAuthenticate(NetworkConnection connection) throws Exception;

	abstract public boolean clientAuthenticate(NetworkConnection connection) throws Exception;

	abstract public void authenticationSuccess(NetworkConnection connection) throws Exception;

	abstract public void authenticationFailed(NetworkConnection connection) throws Exception;
	
	abstract public void authenticationTimeOut(NetworkConnection connection) throws Exception;

	public static AuthenticationManager getDefaultAuthenticationManager() {
		return new RSAWithAESAuthenticationWithHMACValidator();
	}

	public void setAuthenticationTimeOut(long authenticationTimeOut){
		this.authenticationTimeOut = authenticationTimeOut;
	}
	
	public long getAuthenticationTimeOut(){
		return authenticationTimeOut;
	}
	
}
