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

public interface NetworkListener {

	/**
	 * Called before the connection is authenticated
	 * @param connector
	 */
	void preConnection(NetworkConnection connector);

	/**
	 * Called when there is new connection after authenticated
	 * @param connector
	 */
	void connected(NetworkConnection connector);

	/**
	 * Called when there is a disconnection
	 * @param disconnector
	 */
	void disconnected(NetworkConnection disconnector);




	
}
