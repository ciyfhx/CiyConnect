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


package com.ciyfhx.test;

import com.ciyfhx.network.*;
import com.ciyfhx.processors.Processors;
import com.ciyfhx.processors.TransformProcessor;
import com.ciyfhx.test.packet.MessagingPacket;
import com.ciyfhx.test.packet.PacketIDs;
import java8.util.concurrent.Flow;
import java8.util.concurrent.SubmissionPublisher;
import org.testng.annotations.BeforeClass;
import org.junit.Test;
import org.slf4j.impl.SimpleLogger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class BasicTest {

    static Server server;
    static Client client;

    @BeforeClass
    public static void setupConnection(){
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

        //Server
        PacketsFactory serverFactory = new PacketsFactory();

        serverFactory.registerIds(Arrays.asList(PacketIDs.MESSAGING));
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

        clientFactory.registerIds(Arrays.asList(PacketIDs.MESSAGING));
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