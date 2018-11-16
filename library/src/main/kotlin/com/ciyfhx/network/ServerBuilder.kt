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

package com.ciyfhx.network

import com.ciyfhx.network.authenticate.AuthenticationManager
import com.ciyfhx.network.dispatcher.FixedServerConnectionDispatcher
import com.ciyfhx.network.dispatcher.ServerConnectionDispatcher
import java.net.InetAddress
import javax.net.ssl.SSLContext

fun ServerBuilder.build(authenticationManager: AuthenticationManager = AuthenticationManager.getDefaultAuthenticationManager(),
                        packetsFactory: PacketsFactory = PacketsFactory(),
                        dispatcher: ServerConnectionDispatcher = FixedServerConnectionDispatcher(3),
                        port: Int = 5555, backlog: Int = 50, address: InetAddress = InetAddress.getLocalHost()): Server {
    val server = Server(authenticationManager, packetsFactory, dispatcher)

    server.init(port, backlog, address, null)

    return server
}

fun SSLServerBuilder.build(authenticationManager: AuthenticationManager = AuthenticationManager.getDefaultAuthenticationManager(),
                        packetsFactory: PacketsFactory = PacketsFactory(),
                        dispatcher: ServerConnectionDispatcher = FixedServerConnectionDispatcher(3),
                        port: Int = 5555, backlog: Int = 50, address: InetAddress = InetAddress.getLocalHost(),
                                         sslContext: SSLContext = SSLContext.getDefault()): Server {
    val server = Server(authenticationManager, packetsFactory, dispatcher)

    server.init(port, backlog, address, sslContext)

    return server
}
