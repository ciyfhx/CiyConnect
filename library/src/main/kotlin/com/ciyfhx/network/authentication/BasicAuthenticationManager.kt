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

package com.ciyfhx.network.authentication

import com.ciyfhx.network.NetworkConnection
import com.ciyfhx.network.Packet
import com.ciyfhx.network.authenticate.AuthenticationManager
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.util.*

class BasicAuthenticationManager(private val credential: Credential) : AuthenticationManager() {

    val logger = LoggerFactory.getLogger(BasicAuthenticationManager::class.java)!!

    override fun serverAuthenticate(connection: NetworkConnection): Boolean {
        logger.trace("Receiving credentials")
        val clientCredential = Credential.base64(connection.networkInterface.readDirect().data.array())

        val same = clientCredential == credential
        //Send ACK
        val packet = Packet(0, ByteBuffer.allocate(1).put(same.toByte()))
        connection.networkInterface.sendPacket(packet)

        return same
    }

    override fun clientAuthenticate(connection: NetworkConnection): Boolean {
        logger.trace("Sending credentials")

        val packet = Packet(0, ByteBuffer.wrap(credential.base64()))
        connection.networkInterface.sendPacket(packet)

        //Read ACK
        val samePacket = connection.networkInterface.readDirect()
        val same = (samePacket.data.get().toInt() == 1)
        logger.trace("ACK from server is {}", same)

        return same
    }

    override fun authenticationSuccess(connection: NetworkConnection) {
        logger.info("Basic authentication is successful")
    }

    override fun authenticationFailed(connection: NetworkConnection) {
        logger.warn("Basic authentication is unsuccessful")
    }

}

fun Boolean.toByte(): Byte{
    return if (this) (1.toByte()) else (0.toByte())
}

/**
 * Format the username and password to base64 and returns the byte array
 * @return the formatted byte array of &ltusername&gt:&ltpassword&gt in base64
 */
fun Credential.base64(): ByteArray{

    val concat = this.username.toCharArray().copyOf(this.username.length + this.password.size + 1) // The size of username and password with colon
    concat[this.username.length] = ':'
    this.password.copyInto(concat, this.username.length+1)
    return Base64.getEncoder().encode(Charset.forName("UTF-8").encode(CharBuffer.wrap(concat))).array()
}

/**
 * Return the username and password of &ltusername&gt:&ltpassword&gt in base64 from byte array
 * @return the credential
 */
fun Credential.Companion.base64(data: ByteArray): Credential{
    val concat = Charset.forName("UTF-8").decode(ByteBuffer.wrap(Base64.getDecoder().decode(data))).array()
    val username = String(concat.takeWhile { it != ':' }.toCharArray())
    val password = concat.takeLast(concat.size-username.length-1).toCharArray()
    return username credential password
}