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
import com.ciyfhx.test.BasicTest.server
import com.ciyfhx.test.packet.MessagingPacket
import com.ciyfhx.test.packet.PacketIDs
import java8.util.concurrent.Flow
import java.nio.ByteBuffer


const val MESSAGING = 0x02

data class Message(val message: String) : Packet(MESSAGING, ByteBuffer.wrap(message.toByteArray()))


class PrintLineSubscriber : Flow.Subscriber<String> {

    lateinit var subscription: Flow.Subscription
    override fun onSubscribe(subscription: Flow.Subscription) {
        this.subscription = subscription
        println("Subscribe to $subscription")
        subscription.request(1)
    }

    override fun onNext(item: String) {
        println("Message: $item")
        subscription.request(1)
    }

    override fun onError(throwable: Throwable) =
        subscription.cancel()

    override fun onComplete() =
        println("Done")
}


fun main(args :Array<String>){

    val factory = PacketsFactory()

    val publisher = factory.registerId(PacketIDs.MESSAGING)
    val toStringProcessor = Processors.ToStringProcessor

    publisher.subscribe(toStringProcessor)
    toStringProcessor.subscribe(PrintLineSubscriber())

    val client = ClientBuilder.newInstance().withPacketsFactory(factory).build()

    client.networkListener = object : NetworkListener {
        override fun disconnected(disconnector: NetworkConnection) {
            println("Disconnector: " + disconnector.address)
        }

        override fun connected(connector: NetworkConnection) {
            println("Connector: " + connector.address)
        }
    }



    client.connectAsync("localhost", 5555).thenAccept {
        println("Connected")
        it.pipeLineStream.addPipeLine(CompressionPipeLine())
    }


    val server = ServerBuilder.newInstance().withPort(5555).withPacketsFactory(factory).build()
    server.acceptIncomingConnectionAsync()

    server.stream().forEach {
        it.networkInterface.sendPacket(MessagingPacket("test"))
    }

    server.stream().foreach{
        try {
            it.getNetworkInterface().sendPacket(MessagingPacket("test"));
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    server.stream().foreach{
        it.
    }

}