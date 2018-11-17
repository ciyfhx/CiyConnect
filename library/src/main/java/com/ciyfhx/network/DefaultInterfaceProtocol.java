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


package com.ciyfhx.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java8.util.concurrent.SubmissionPublisher;

public class DefaultInterfaceProtocol extends NetworkInterface{

    private long maxPacketSize = 5048;
    private int delay = 100;//In milliseconds

    private Logger logger = LoggerFactory.getLogger(NetworkInterface.class);

    protected DefaultInterfaceProtocol(NetworkConnection networkConnection, BaseServerClientModel model) {
        super(networkConnection, model);
    }

    @Override
    protected Packet readProtocol() {

        DataInputStream input = networkConnection.getDataInputStream();

        synchronized (input){
            try{
                Thread.sleep(delay);
                int id = input.readInt();
                    int packetSize = input.readInt();
                    if (packetSize >= maxPacketSize) {
                        logger.warn("Packet size exceed limit: {}, id: {}", packetSize, id);
                        stop();
                        return null;
                    }
                    ByteBuffer buffer = ByteBuffer.allocate(packetSize);
                    input.readFully(buffer.array(), 0, packetSize);

                    return new Packet(id, buffer);
            }catch (Exception e) {
                e.printStackTrace();
                logger.debug("Connection reset {}", networkConnection.getAddress());
                stop();
                return null;

            }
        }

    }



    @Override
    protected void writeProtocol(Packet packet) throws Exception {
        DataOutputStream output = networkConnection.getDataOutputStream();
        synchronized(output){
            output.writeInt(packet.getPacketID());
            output.flush();

            ByteBuffer data = packet.getData();

            output.writeInt(data.capacity());
            output.flush();

            output.write(data.array());
            output.flush();
        }
    }

    /**
     * Set the interval each packet can be read in milliseconds
     * @param delay
     */
    public void setDelay(int delay){
        this.delay = delay;
    }

    /**
     * Set the maximum packet size the interface can accept
     * @param size
     */
    public void setMaxPacketSize(long size) {
        this.maxPacketSize = size;
    }



}