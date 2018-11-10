package com.ciyfhx.java;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.SubmissionPublisher;

import com.ciyfhx.network.*;
import com.ciyfhx.packets.MessagingPacket;
import com.ciyfhx.packets.PacketIDs;
import com.ciyfhx.processors.Processors;
import com.ciyfhx.processors.TransformProcessor;

import org.slf4j.impl.SimpleLogger;

public class ServerTest {

	public static void main(String[] args) throws IllegalAccessException, IOException, InterruptedException, PacketsFactory.NotFoundException {

		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

		PacketsFactory factory = new PacketsFactory();

		factory.registerIds(List.of(PacketIDs.MESSAGING));
		SubmissionPublisher<PacketEvent<Packet>> publisher = factory.getPublisher(PacketIDs.MESSAGING);

		TransformProcessor<PacketEvent, String> stringTransformProcessor = Processors.ToStringProcessor;

		publisher.subscribe(stringTransformProcessor);
		stringTransformProcessor.subscribe(new PrintLineSubscriber());


		Server server = ServerBuilder.newInstance().withPort(5555).withPacketsFactory(factory).build();

		server.setNetworkListener(new NetworkListener() {

			@Override
			public void disconnected(NetworkConnection disconnector) {
				System.out.println("Disconnector: " + disconnector.getAddress());
			}

			@Override
			public void connected(NetworkConnection connector) {
				connector.getPipeLineStream().addPipeLine(new CompressionPipeLine());
				System.out.println("Connector: " + connector.getAddress());


//				for(int i = 0; i < 100; i++){
//					String message = new String("Testing " + i);
//					server.stream().forEach(con -> {
//						try {
//							con.getNetworkInterface().sendPacket(new MessagingPacket(message));
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					});
//				}

			}
		});


		server.acceptIncomingConnectionAsync();



		Scanner scanner = new Scanner(System.in);

		//Long Running Thread
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			server.stream().forEach(n -> {
				try {
					n.getNetworkInterface().sendPacket(new MessagingPacket(line));
				} catch (Exception e) {
					e.printStackTrace();
				}

			});
			Thread.sleep(1000);
		}

	}
}
