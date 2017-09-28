package com.ciyfhx.builder;

import com.ciyfhx.network.AuthenticationManager;
import com.ciyfhx.network.PacketsFactory;
import com.ciyfhx.network.Server;

import java.io.IOException;

public final class ServerBuilder {
    private AuthenticationManager authenticationManager = AuthenticationManager.getDefaultAutenticationManager();
    private int port = 5555;

    private int maxConnections = 3;

    private PacketsFactory packetsFactory = new PacketsFactory();

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

    public ServerBuilder withMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }


    public ServerBuilder withPacketsFactory(PacketsFactory packetsFactory){
        this.packetsFactory = packetsFactory;
        return this;
    }


    public Server build() throws IOException, IllegalAccessException {
        Server server = new Server();

        server.setMaxConnections(maxConnections);
        server.init(port);
        server.setAuthenticationManager(authenticationManager);
        server.setPacketsFactory(packetsFactory);


        return server;
    }



}
