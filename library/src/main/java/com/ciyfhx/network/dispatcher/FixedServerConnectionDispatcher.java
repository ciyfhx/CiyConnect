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

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 * FixedServerConnectionDispatcher uses Java implemented Executors.newFixedThreadPool() to create new thread
 * @see java.util.concurrent.ExecutorService
 *</p>
 * <p>
 * Upon receiving new connection request, FixedServerConnectionDispatcher will spawn new thread as long as the
 * max connections is not reached
 * <br>
 * <b>Note:</b><p>Max connection is set to 3 by default, to change this see FixedServerConnectionDispatcher.setMaxConnections(int setMaxConnections)</p>
 * </p>
 * @author  Peh Zi Heng
 * @version 1.0
 * @since   2018-10-10
 */
public class FixedServerConnectionDispatcher implements ServerConnectionDispatcher {


    private org.slf4j.Logger logger = LoggerFactory.getLogger(FixedServerConnectionDispatcher.class);

    protected int maxConnections = 3;


	private ExecutorService executorService = Executors.newFixedThreadPool(3);

    /**
     *  Dispatch new connection to the pool thread
     *  automatically closes connection when max connection is reached
     * @param server
     * @param networkInterface
     * @return is the connection request accepted
     */
    @Override
    public boolean dispatchConnection(Server server, NetworkInterface networkInterface) {
        //Max connections reached
        if(server.getConnectionsCount() >= maxConnections){
            try{
                networkInterface.close();
            }catch(IOException e){
                logger.error("Unable to close connection " + networkInterface.toString());
            }
            return false;
        }else {
            executorService.submit(networkInterface);
            return true;
        }
    }

    /**
     * Set the maximum connections the server can handle at once
     * @param maxConnections
     */
    public void setMaxConnections(int maxConnections){
        this.maxConnections = maxConnections;
    }


}
