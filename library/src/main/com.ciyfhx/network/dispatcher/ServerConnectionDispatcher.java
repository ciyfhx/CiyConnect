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
