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

import com.ciyfhx.network.authenticate.AuthenticationManager;
import com.ciyfhx.network.dispatcher.FixedServerConnectionDispatcher;
import com.ciyfhx.network.dispatcher.ServerConnectionDispatcher;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

public class ServerBuilder {
    private AuthenticationManager authenticationManager = AuthenticationManager.getDefaultAuthenticationManager();
    private int port = 5555;

    private PacketsFactory packetsFactory = new PacketsFactory();

    private ServerConnectionDispatcher dispatcher = new FixedServerConnectionDispatcher(3);

    private int backlog = 50;
    private InetAddress bindAddress = InetAddress.getLocalHost();

    private ServerBuilder() throws UnknownHostException {}

    /**
     * Create the instance used to build a com.ciyfhx.network.Server
     * Returns null if unable to build
     * @return
     */
    public static ServerBuilder newInstance(){
        try {
            return new ServerBuilder();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ServerBuilder withPort(int port){
        this.port = port;
        return this;
    }

    /**
     * Set the number of clients that will be in a queue when initialing a connection before dropping
     * @param backlog
     */
    public void withBacklog(int backlog){
        this.backlog = backlog;
    }


    /**
     * Set the authentication manager
     * @see AuthenticationManager
     * @param authenticationManger
     * @return
     */
    public ServerBuilder withAuthenticationManager(AuthenticationManager authenticationManger){
        this.authenticationManager = authenticationManger;
        return this;
    }

    /**
     * Set the packets factory
     * @see PacketsFactory
     * @param packetsFactory
     * @return
     */
    public ServerBuilder withPacketsFactory(PacketsFactory packetsFactory){
        this.packetsFactory = packetsFactory;
        return this;
    }

    /**
     * Set the server connection dispatcher
     * @see ServerConnectionDispatcher
     * @param dispatcher
     * @return
     */
    public ServerBuilder withServerConnectionDispatcher(ServerConnectionDispatcher dispatcher){
        this.dispatcher = dispatcher;
        return this;
    }

    /**
     * Create the server object with the given fields
     * @return Created server object
     * @throws IOException
     * @throws IllegalAccessException
     */
    public Server build() throws IOException, IllegalAccessException {
        Server server = new Server(authenticationManager, packetsFactory, dispatcher);

        server.init(port, backlog, bindAddress, null);

        return server;
    }


    public class SSLServerBuilder extends ServerBuilder{

        private SSLServerBuilder() throws UnknownHostException, NoSuchAlgorithmException {
            super();
            sslContext = SSLContext.getDefault();
        }

        private SSLContext sslContext;

        /**
         * Set the SSLContext used for creating SSL Socket
         *
         * <b>Note: </b> By default, the SSLContext.getDefault() will be used if not set
         *
         * @param sslContext
         */
        public void withSSLContext(SSLContext sslContext){
            this.sslContext = sslContext;
        }

        @Override
        public Server build() throws IOException, IllegalAccessException {
            Server server = new Server(authenticationManager, packetsFactory, dispatcher);

            server.init(port, backlog, bindAddress, sslContext);

            return server;
        }



    }





}
