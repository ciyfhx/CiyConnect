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


import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

public class SSLServerBuilder extends ServerBuilder{

    private SSLServerBuilder() throws UnknownHostException, NoSuchAlgorithmException {
        super();
        sslContext = SSLContext.getDefault();
    }

    private SSLContext sslContext;

    /**
     * Create the instance used to build a com.ciyfhx.network.Server (With SSL)
     * Returns null if unable to build
     * @return
     */
    public static SSLServerBuilder newInstance(){
        try {
            return new SSLServerBuilder();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


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

        server.setTimeout(timeout);
        server.init(port, backlog, bindAddress, sslContext);

        return server;
    }



}
