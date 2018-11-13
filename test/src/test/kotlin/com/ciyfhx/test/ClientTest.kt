

package com.ciyfhx.network

import com.ciyfhx.processors.Processors
import java8.util.concurrent.Flow
import java.nio.ByteBuffer

const val MESSAGE = 1

data class MessagePacket(val message: String) : Packet(MESSAGE, ByteBuffer.wrap(message.toByteArray()))

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

    override fun onError(throwable: Throwable) = subscription.cancel()

    override fun onComplete() =  println("Done")

}

fun main(args: Array<String>){
    val packageFactory = PacketsFactory()
    val publisher = packageFactory.registerId(MESSAGE)
    val toStringProcessor = Processors.ToStringProcessor
    publisher.subscribe(toStringProcessor)
    toStringProcessor.subscribe(PrintLineSubscriber())

    val client = ClientBuilder.newInstance().build(packetsFactory = packageFactory)
    client.connectAsync("localhost", 5555).thenAccept {
        println("Client Connected")
    }

    while(true){

    }

}

