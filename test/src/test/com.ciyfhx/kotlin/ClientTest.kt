package com.ciyfhx.kotlin


fun main(args :Array<String>){

//    val factory = PacketsFactory()
//
//    val publisher = factory.registerId(PacketIDs.MESSAGING)
//    val toStringProcessor = Processors.ToStringProcessor
//
//    publisher.subscribe(toStringProcessor)
//    toStringProcessor.subscribe(PrintLineSubscriber())
//
//    val client = ClientBuilder.newInstance().withPacketsFactory(factory).build()
//
//    client.networkListener = object : NetworkListener {
//        override fun disconnected(disconnector: NetworkConnection) {
//            println("Disconnector: " + disconnector.address)
//        }
//
//        override fun connected(connector: NetworkConnection) {
//            println("Connector: " + connector.address)
//        }
//    }
//
//    client.connectAsync("localhost", 5555).thenAccept { b ->
//        println("Connected")
//        client.pipeLineStream.addPipeLine(CompressionPipeLine())
//    }


}