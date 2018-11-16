

package com.ciyfhx.test

import com.ciyfhx.network.ClientBuilder
import com.ciyfhx.network.Packet
import com.ciyfhx.network.PacketsFactory
import com.ciyfhx.network.authenticate.AuthenticationManager
import com.ciyfhx.network.authentication.AuthenticationManagerList
import com.ciyfhx.network.authentication.SimpleAuthenticationManager
import com.ciyfhx.network.authentication.credential
import com.ciyfhx.network.build
import com.ciyfhx.processors.Processors
import java8.util.concurrent.Flow
import org.slf4j.impl.SimpleLogger
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

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

    System.setProperty("javax.net.ssl.trustStore", "D:\\keystore.jks")
    System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE")
    val packageFactory = PacketsFactory()
    val publisher = packageFactory.registerId(MESSAGE)
    val toStringProcessor = Processors.ToStringProcessor
    publisher.subscribe(toStringProcessor)
    toStringProcessor.subscribe(PrintLineSubscriber())

    val authenticationManager = AuthenticationManagerList(AuthenticationManager.getDefaultAuthenticationManager(), SimpleAuthenticationManager("hell1o" credential "123".toCharArray()))
    val client = ClientBuilder.newInstance().build(packetsFactory = packageFactory, authenticationManager = authenticationManager)
    client.connectAsync("192.168.99.1", 5555, SSLContext.getDefault()).thenAccept {
        println("Client Connected")
    }.exceptionally {
        it.printStackTrace()
        null
    }

    while(true){
        Thread.sleep(1000)
    }

}

