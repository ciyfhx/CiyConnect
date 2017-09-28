package com.ciyfhx.builder;

import com.ciyfhx.network.*;

public final class ClientBuilder {

    private AuthenticationManager authenticationManager = AuthenticationManager.getDefaultAutenticationManager();

    private PacketsFactory packetsFactory = new PacketsFactory();


    public static ClientBuilder newInstance(){
        return new ClientBuilder();
    }

    public ClientBuilder withAuthenticationManager(AuthenticationManager authenticationManger){
        this.authenticationManager = authenticationManger;
        return this;
    }

    public ClientBuilder withPacketsFactory(PacketsFactory packetsFactory){
        this.packetsFactory = packetsFactory;
        return this;
    }

//    public Client connectAsync(String host, int port){
//        Client client = createClient();
//
//        return client.connectAsync(host, port);;
//    }
//
//    public Client connect(String host, int port) throws IOException, IllegalAccessException {
//        Client client = createClient();
//        client.connect(host, port);
//        return client;
//    }

    public Client build(){
        Client client = new Client();

        client.setAuthenticationManager(authenticationManager);
        client.setPacketsFactory(packetsFactory);

        return client;
    }

}
