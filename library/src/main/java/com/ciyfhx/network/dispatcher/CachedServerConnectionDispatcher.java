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
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * CachedServerConnectionDispatcher uses Java implemented Executors.newCachedThreadPool() to create new thread
 * @see java.util.concurrent.ExecutorService
 *</p>
 * <p>
 * Upon receiving new connection request, CachedServerConnectionDispatcher will spawn new thread as needed when
 * no threads is available
 * <b>Useful when server need to constantly close and receive connection requests</b>
 * </p>
 * @author  Peh Zi Heng
 * @version 1.0
 * @since   2018-10-10
 */
public class CachedServerConnectionDispatcher implements  ServerConnectionDispatcher{


    private org.slf4j.Logger logger = LoggerFactory.getLogger(CachedServerConnectionDispatcher.class);

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public boolean dispatchConnection(Server server, NetworkInterface networkInterface) {
        executorService.submit(networkInterface);
        return true;
    }


}
