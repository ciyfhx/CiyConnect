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

import java.util.concurrent.atomic.AtomicBoolean;

public class BaseServerClientModel {


    protected AtomicBoolean running = new AtomicBoolean(false);

    protected PacketsFactory packetsFactory = new PacketsFactory();

    protected AuthenticationManager authenticationManager = AuthenticationManager.getDefaultAuthenticationManager();

    protected NetworkListener networkListener;

    //Uses for creating the standard protocol
    protected Class networkInterfaceClass = DefaultInterfaceProtocol.class;

    private boolean useSSL  = false;

    protected void setUseSSL(boolean useSSL){
        this.useSSL = useSSL;
    }

    public boolean UseSSL(){
        return useSSL;
    }

    /**
     * Set the Network listener
     * @param networkListener
     */
    public void setNetworkListener(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

    /**
     * Returns the set Network listener
     * @return
     */
    public NetworkListener getNetworkListener() {
        return networkListener;
    }


    protected BaseServerClientModel(AuthenticationManager authenticationManager, PacketsFactory packetsFactory){
        this.authenticationManager = authenticationManager;
        this.packetsFactory = packetsFactory;
    }

    /**
     * This will set the default class used for creating new instance of the network interacting protocol
     * <b>Note:</b>make sure that inherited class of the NetworkInterfaceClass does not have any user defined constructor
     * @param networkInterfaceClass
     * @throws InvalidClassException
     */
    protected void setStandardNetworkInterfaceClass(Class networkInterfaceClass) throws InvalidClassException {
        if(networkInterfaceClass.isInstance(NetworkInterface.class)){
            networkInterfaceClass = networkInterfaceClass;
        }else throw new InvalidClassException(networkInterfaceClass.getName() + " is not instance of NetworkInterface class");
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
//
//    public void setPacketsFactory(PacketsFactory packetsFactory){
//        this.packetsFactory = packetsFactory;
//    }
//
    public PacketsFactory getPacketsFactory(){
        return packetsFactory;
    }

    /**
     * Is the connection alive
     * @return
     */
    public boolean isRunning() {
        return running.get();
    }


    public class InvalidClassException extends Throwable {

        public InvalidClassException(String msg){
            super(msg);
        }

    }



}
