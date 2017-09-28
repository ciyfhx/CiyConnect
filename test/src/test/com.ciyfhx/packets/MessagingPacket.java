package com.ciyfhx.packets;

import com.ciyfhx.network.Packet;

import java.nio.ByteBuffer;

public class MessagingPacket extends Packet {
    public MessagingPacket(String message) {
        super(PacketIDs.MESSAGING, ByteBuffer.wrap(message.getBytes()));
    }
}
