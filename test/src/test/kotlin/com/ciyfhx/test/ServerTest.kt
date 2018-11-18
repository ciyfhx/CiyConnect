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

package com.ciyfhx.test

import com.ciyfhx.network.*
import com.ciyfhx.network.authenticate.AuthenticationManager
import com.ciyfhx.network.authentication.AuthenticationManagerList
import com.ciyfhx.network.authentication.BasicAuthenticationManager
import com.ciyfhx.network.authentication.DigestAuthenticationManager
import com.ciyfhx.network.authentication.credential
import com.ciyfhx.processors.Processors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.impl.SimpleLogger
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE")


    System.setProperty("javax.net.ssl.keyStore", "D:\\keystore.jks")
    System.setProperty("javax.net.ssl.keyStorePassword", "ciyfhx")

    val loc = "D:\\keystore.store"

    val packageFactory = PacketsFactory()
    val publisher = packageFactory.registerId(MESSAGE)
    val toStringProcessor = Processors.ToStringProcessor
    publisher.subscribe(toStringProcessor)
    toStringProcessor.subscribe(PrintLineSubscriber())
//    val server = ServerBuilder.newInstance().build(
//            port = 5555,
//            packetsFactory = packageFactory
//            )

    val authenticationManager = AuthenticationManagerList(AuthenticationManager.getDefaultAuthenticationManager(),
            DigestAuthenticationManager("hello" credential "123".toCharArray(), "com.1ciyfhx.test"))
    val server = SSLServerBuilder.newInstance().build(port = 5556, authenticationManager = authenticationManager)
    runBlocking(Dispatchers.IO){
        var b = false
        while(true){

            if(!b){
                server.acceptIncomingConnectionAsync().thenAccept {

                    val time = measureTimeMillis {
                        async {
                            server.stream().findFirst().map {
                                it.networkInterface.sendPacket(MessagePacket("HI"))
                            }
                        }
                        async {
                            server.stream().findFirst().map {
                                it.networkInterface.sendPacket(MessagePacket("HI2"))
                            }
                        }
                    }
                    b = false
                }.exceptionally {
                    it.printStackTrace()
                    b = false
                    null
                }
                b = true
            }


//            try {
//                server.acceptIncomingConnection()
//                val time = measureTimeMillis {
//                    async {
//                        server.stream().findFirst().map {
//                            it.networkInterface.sendPacket(MessagePacket("HI"))
//                        }
//                    }
//                    async {
//                        server.stream().findFirst().map {
//                            it.networkInterface.sendPacket(MessagePacket("HI2"))
//                        }
//                    }
//                }
//            }catch (e: Exception) {
//                println("Error")
//            }


        }

        //println(time)

    }
    while(true){
        Thread.sleep(1000)
    }

}