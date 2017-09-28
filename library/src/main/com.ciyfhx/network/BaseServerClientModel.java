package com.ciyfhx.network;

import java.util.concurrent.atomic.AtomicBoolean;

public class BaseServerClientModel {


    protected AtomicBoolean running = new AtomicBoolean(false);

    protected PacketsFactory packetsFactory = new PacketsFactory();

    protected AuthenticationManager authenticationManager = AuthenticationManager.getDefaultAutenticationManager();

    protected NetworkListener networkListener;


    public void setNetworkListener(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

    public NetworkListener getNetworkListener() {
        return networkListener;
    }

    /**
     * Set the type of authentication manager
     *
     * @param authenticationManager
     */
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Get the current authentication manager
     *
     * @return
     */
    public AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    public void setPacketsFactory(PacketsFactory packetsFactory){
        this.packetsFactory = packetsFactory;
    }

    public PacketsFactory getPacketsFactory(){
        return packetsFactory;
    }

    public boolean isRunning() {
        return running.get();
    }




}
