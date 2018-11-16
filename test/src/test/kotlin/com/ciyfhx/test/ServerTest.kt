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
import com.ciyfhx.processors.Processors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.impl.SimpleLogger
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE")
    val packageFactory = PacketsFactory()
    val publisher = packageFactory.registerId(MESSAGE)
    val toStringProcessor = Processors.ToStringProcessor
    publisher.subscribe(toStringProcessor)
    toStringProcessor.subscribe(PrintLineSubscriber())
    val server = ServerBuilder.newInstance().build(
            port = 5555,
            packetsFactory = packageFactory
            )
    runBlocking(Dispatchers.IO){
        server.acceptIncomingConnection()

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

        //println(time)

    }
    while(true){
        Thread.sleep(1000)
    }

}