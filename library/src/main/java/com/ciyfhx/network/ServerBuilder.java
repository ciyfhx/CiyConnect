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

import java.io.IOException;

public final class ServerBuilder {
    private AuthenticationManager authenticationManager = AuthenticationManager.getDefaultAuthenticationManager();
    private int port = 5555;

    private PacketsFactory packetsFactory = new PacketsFactory();

    private ServerConnectionDispatcher dispatcher = new FixedServerConnectionDispatcher(3);

    private ServerBuilder() {}


    public static ServerBuilder newInstance(){
        return new ServerBuilder();
    }

    public ServerBuilder withPort(int port){
        this.port = port;
        return this;
    }

    public ServerBuilder withAuthenticationManager(AuthenticationManager authenticationManger){
        this.authenticationManager = authenticationManger;
        return this;
    }


    public ServerBuilder withPacketsFactory(PacketsFactory packetsFactory){
        this.packetsFactory = packetsFactory;
        return this;
    }

    public ServerBuilder withServerConnectionDispatcher(ServerConnectionDispatcher dispatcher){
        this.dispatcher = dispatcher;
        return this;
    }


    public Server build() throws IOException, IllegalAccessException {
        Server server = new Server(authenticationManager, packetsFactory, dispatcher);

        server.init(port);

        return server;
    }



}
