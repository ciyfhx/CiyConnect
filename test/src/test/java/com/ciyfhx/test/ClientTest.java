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

import java.util.Arrays;


import com.ciyfhx.network.*;
import com.ciyfhx.network.validator.MACValidator;
import com.ciyfhx.processors.Processors;
import com.ciyfhx.processors.TransformProcessor;
import com.ciyfhx.test.packet.PacketIDs;
import java8.util.concurrent.SubmissionPublisher;
import org.slf4j.impl.SimpleLogger;

public class ClientTest {

	public static void main(String[] args) throws Exception, PacketsFactory.NotFoundException {
		
		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

		PacketsFactory factory = new PacketsFactory();

		factory.registerIds(Arrays.asList(PacketIDs.MESSAGING));
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

		client.connectAsync("localhost", 5555).thenAccept((con) -> {
			con.getPipeLineStream().addPipeLine(new CompressionPipeLine());
		});

		//Blocking
		while(true){

		}

		//client.sendPacket(new MessagingPacket("Hello"));

	}

}
