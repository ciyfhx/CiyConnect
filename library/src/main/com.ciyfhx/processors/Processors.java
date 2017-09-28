package com.ciyfhx.processors;

import com.ciyfhx.network.PacketEvent;

public class Processors {

     public static TransformProcessor<PacketEvent, String> ToStringProcessor = new TransformProcessor<PacketEvent, String>(p -> new String(p.getPacket().getData().array()));

}
