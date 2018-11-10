package com.ciyfhx.java;

import com.ciyfhx.network.*;
import com.ciyfhx.packets.MessagingPacket;
import com.ciyfhx.packets.PacketIDs;
import com.ciyfhx.processors.Processors;
import com.ciyfhx.processors.TransformProcessor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.impl.SimpleLogger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import static org.junit.Assert.*;

public class BasicTest {

    static Server server;
    static Client client;

    @BeforeClass
    public static void setupConnection(){
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

        //Server
        PacketsFactory serverFactory = new PacketsFactory();

        serverFactory.registerIds(List.of(PacketIDs.MESSAGING));
        SubmissionPublisher<PacketEvent<Packet>> serverPublisher = null;
        try {
            serverPublisher = serverFactory.getPublisher(PacketIDs.MESSAGING);
            TransformProcessor<PacketEvent, String> stringTransformProcessor = Processors.ToStringProcessor;

            serverPublisher.subscribe(stringTransformProcessor);
            stringTransformProcessor.subscribe(new PrintLineSubscriber());
            server = ServerBuilder.newInstance().withPort(5555).withPacketsFactory(serverFactory).build();
            server.acceptIncomingConnectionAsync().thenAccept((connection) -> {
                System.out.println("Server Connected");
                connection.getPipeLineStream().addPipeLine(new CompressionPipeLine());
            });



        } catch (PacketsFactory.NotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Client
        PacketsFactory clientFactory = new PacketsFactory();

        clientFactory.registerIds(List.of(PacketIDs.MESSAGING));
        try {
            SubmissionPublisher<PacketEvent<Packet>> clientPublisher = clientFactory.getPublisher(PacketIDs.MESSAGING);
            TransformProcessor<PacketEvent, String> stringTransformProcessor = Processors.ToStringProcessor;

            clientPublisher.subscribe(stringTransformProcessor);
            stringTransformProcessor.subscribe(new Flow.Subscriber<String>() {
                private Flow.Subscription subscription;
                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    this.subscription = subscription;
                    System.out.format("Subscribe to %s\n", subscription.toString());
                    subscription.request(1);
                }

                @Override
                public void onNext(String item) {
                    System.out.format("Message: %s\n", item);
                    assertEquals("Message",item);
                    subscription.request(1);
                }

                @Override
                public void onError(Throwable throwable) {
                    subscription.cancel();
                }

                @Override
                public void onComplete() {
                    System.out.println("Done");
                }
            });
            //stringTransformProcessor.subscribe(new PrintLineSubscriber());

            client = ClientBuilder.newInstance().withPacketsFactory(clientFactory).build();

            client.connectAsync("localhost", 5555).thenAccept((connection) -> {

                System.out.println("Client Connected");
                connection.getPipeLineStream().addPipeLine(new CompressionPipeLine());
            });
        } catch (PacketsFactory.NotFoundException e) {
            e.printStackTrace();
        }



    }

    @Test
    public void sendMessage(){
        server.stream().forEach(n -> n.getNetworkInterface().sendPacketAsync(new MessagingPacket("Message")));
    }


}