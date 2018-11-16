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

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import com.ciyfhx.network.*;
import com.ciyfhx.network.dispatcher.CachedServerConnectionDispatcher;
import com.ciyfhx.processors.Processors;
import com.ciyfhx.processors.TransformProcessor;

import com.ciyfhx.test.packet.MessagingPacket;
import com.ciyfhx.test.packet.PacketIDs;
import java8.util.concurrent.SubmissionPublisher;
import org.slf4j.impl.SimpleLogger;

public class ServerTest {

	public static void main(String[] args) throws IllegalAccessException, IOException, InterruptedException, PacketsFactory.NotFoundException {

		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

		PacketsFactory factory = new PacketsFactory();

		factory.registerIds(Arrays.asList(PacketIDs.MESSAGING));
		SubmissionPublisher<PacketEvent<Packet>> publisher = factory.getPublisher(PacketIDs.MESSAGING);

		TransformProcessor<PacketEvent, String> stringTransformProcessor = Processors.ToStringProcessor;

		publisher.subscribe(stringTransformProcessor);
		stringTransformProcessor.subscribe(new PrintLineSubscriber());
		Server server = ServerBuilder.newInstance().withPort(5555).withPacketsFactory(factory)
				.withServerConnectionDispatcher(new CachedServerConnectionDispatcher()).build();

		server.setNetworkListener(new NetworkListener() {

			@Override
			public void disconnected(NetworkConnection disconnector) {
				System.out.println("Disconnector: " + disconnector.getAddress());
			}

			@Override
			public void preConnection(NetworkConnection connector) {

			}

			@Override
			public void connected(NetworkConnection connector) {
				//connector.getPipeLineStream().addPipeLine(new CompressionPipeLine());
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


		server.acceptIncomingConnectionAsync().thenAccept(b -> {
			b.getPipeLineStream().addPipeLine(new CompressionPipeLine());
			b.getSession().set("hello", "test");
		});



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
