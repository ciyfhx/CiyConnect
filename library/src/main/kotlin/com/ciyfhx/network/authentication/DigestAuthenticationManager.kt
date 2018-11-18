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
import com.ciyfhx.network.authenticate.RSAWithAESAuthentication
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*

/*
* HA1 = MD5(username:realm:password)
* response = MD5(HA1:nonce)
<b>Note:</b> DigestAuthenticationManager does not follow any specification
 */
class DigestAuthenticationManager(private val credential: Credential, val realm: String) : AuthenticationManager() {

    val logger = LoggerFactory.getLogger(DigestAuthenticationManager::class.java)!!


    override fun serverAuthenticate(connection: NetworkConnection): Boolean {

        val nonce = SecureGenerator.randomSecureBytes(64)
        val packet = Packet(0, ByteBuffer.wrap(nonce))
        logger.trace("Sending challenge")
        connection.networkInterface.sendPacket(packet)

        val serverResponse = calculateResponse(nonce)
        val clientResponse = connection.networkInterface.readDirect()

        //Send ACK
        val same = Arrays.equals(serverResponse, clientResponse.data.array())
        val ackPacket = Packet(0, ByteBuffer.allocate(1).put(same.toByte()))
        connection.networkInterface.sendPacket(ackPacket)

        return same

    }

    override fun clientAuthenticate(connection: NetworkConnection): Boolean {
        val nonce = connection.networkInterface.readDirect()

        val clientResponse = calculateResponse(nonce.data.array())
        logger.trace("Sending back completed challenge")
        connection.networkInterface.sendPacket(Packet(0, ByteBuffer.wrap(clientResponse)))

        //Read ACK
        val samePacket = connection.networkInterface.readDirect()
        val same = (samePacket.data.get().toInt() == 1)
        logger.trace("ACK from server is {}", same)

        return same
    }

    override fun authenticationSuccess(connection: NetworkConnection) {
        logger.info("Digest authentication is successful")
    }

    override fun authenticationFailed(connection: NetworkConnection) {
        logger.warn("Digest authentication is unsuccessful")
    }

    private fun calculateResponse(nonce: ByteArray): ByteArray{

        val a = getMD5Hash(ByteBuffer.wrap("test".toByteArray()))

        val usernameAndRealm = credential.username + ":" + realm + ":"
        val HA1content = CharArray(usernameAndRealm.length + credential.password.size)
        usernameAndRealm.toCharArray(HA1content)
        credential.password.copyInto(HA1content, usernameAndRealm.length)
        val HA1 = getMD5Hash(Charset.forName("UTF-8").encode(CharBuffer.wrap(HA1content)))

        val responseContent = ByteBuffer.allocate(HA1.size + nonce.size + 1)
                .put(HA1).put(':'.toByte()).put(nonce)
        responseContent.flip()

        val response = getMD5Hash(responseContent)

        return response

    }

    private fun getMD5Hash(data: ByteBuffer): ByteArray{
        val md = MessageDigest.getInstance("MD5")
        md.update(data)
        return md.digest()
    }

}
