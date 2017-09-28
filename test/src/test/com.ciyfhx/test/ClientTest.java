package com.ciyfhx.test;

import java.util.List;
import java.util.concurrent.SubmissionPublisher;


import com.ciyfhx.builder.ClientBuilder;
import com.ciyfhx.network.*;
import com.ciyfhx.packets.PacketIDs;
import com.ciyfhx.processors.Processors;
import com.ciyfhx.processors.TransformProcessor;
import org.slf4j.impl.SimpleLogger;

public class ClientTest {

	public static void main(String[] args) throws Exception, PacketsFactory.NotFoundException {
		
		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

		PacketsFactory factory = new PacketsFactory();

		factory.registerIds(List.of(PacketIDs.MESSAGING));
		SubmissionPublisher<PacketEvent<Packet>> publisher = factory.getPublisher(PacketIDs.MESSAGING);

		TransformProcessor<PacketEvent, String> stringTransformProcessor = Processors.ToStringProcessor;

		publisher.subscribe(stringTransformProcessor);
		stringTransformProcessor.subscribe(new PrintLineSubscriber());

		Client client = ClientBuilder.newInstance().withPacketsFactory(factory).build();

		client.setNetworkListener(new NetworkListener() {
			@Override
			public void disconnected(NetworkConnection disconnector) {
				System.out.println("Disconnector: " + disconnector.getAddress());
			}

			@Override
			public void connected(NetworkConnection connector) {
				System.out.println("Connector: " + connector.getAddress());
			}
		});

		client.connectAync("localhost", 5555).thenAccept((b) -> {

			System.out.println("Connected");
			client.getPipeLineStream().addPipeLine(new CompressionPipeLine());

		});



		//client.sendPacket(new MessagingPacket("Hello"));

	}

}
