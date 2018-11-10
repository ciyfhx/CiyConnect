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

public abstract class AuthenticationManager {
	
	private long authenticationTimeOut = 5000;

	abstract public boolean serverAuthenticate(NetworkConnection connection);

	abstract public boolean clientAuthenticate(NetworkConnection connection);

	abstract public void authenticationSuccess(NetworkConnection connection);

	abstract public void authenticationFailed(NetworkConnection connection);
	
	abstract public void authenticationTimeOut(NetworkConnection connection);

	public static RSAWithAESAuthentication getDefaultAuthenticationManager() {
		return new RSAWithAESAuthentication();
	}

	public void setAuthenticationTimeOut(long authenticationTimeOut){
		this.authenticationTimeOut = authenticationTimeOut;
	}
	
	public long getAuthenticationTimeOut(){
		return authenticationTimeOut;
	}
	
}
