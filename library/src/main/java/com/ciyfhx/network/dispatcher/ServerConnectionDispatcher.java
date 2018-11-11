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



package com.ciyfhx.network.dispatcher;

import com.ciyfhx.network.NetworkInterface;
import com.ciyfhx.network.Server;

public interface ServerConnectionDispatcher {

    /**
     * Dispatch runnable connection to thread
     * @param server
     * @param networkInterface
     * @return is the connection added
     */
    boolean dispatchConnection(Server server, NetworkInterface networkInterface);


}
