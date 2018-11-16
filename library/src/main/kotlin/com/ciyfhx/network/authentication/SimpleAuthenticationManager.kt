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
import com.ciyfhx.network.authenticate.AuthenticationManager
import com.ciyfhx.network.readBytes
import com.ciyfhx.network.sendBytes
import org.slf4j.LoggerFactory
import java.lang.Exception

class SimpleAuthenticationManager(private val credential: Credential) : AuthenticationManager() {

    val logger = LoggerFactory.getLogger(SimpleAuthenticationManager::class.java)!!

    override fun serverAuthenticate(connection: NetworkConnection): Boolean {
        logger.trace("Receiving username")
        val usernameByteArray = connection.readBytes()
        logger.trace("Receiving password")
        val passwordByteArray = connection.readBytes()
        val clientCredential = String(usernameByteArray) credential passwordByteArray

        val same = clientCredential == credential
        connection.dataOutputStream.writeBoolean(same)
        return same
    }

    override fun clientAuthenticate(connection: NetworkConnection): Boolean {
        logger.trace("Sending username")
        connection.sendBytes(credential.usernameByteArray)
        logger.trace("Sending password")
        connection.sendBytes(credential.passwordByteArray)

        val same = connection.dataInputStream.readBoolean()
        logger.trace("ACK from server is {}", same)

        return same
    }

    override fun authenticationSuccess(connection: NetworkConnection) {
        logger.info("Simple authentication is successful")
    }

    override fun authenticationFailed(connection: NetworkConnection) {
        logger.info("Simple authentication is unsuccessful")
        throw IncorrectCredential()
    }

    override fun authenticationTimeOut(connection: NetworkConnection) {
        logger.info("Simple authentication is unsuccessful (timeout)")
        throw IncorrectCredential()
    }

    inner class IncorrectCredential : Exception("Incorrect credentials")

}