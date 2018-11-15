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

package com.ciyfhx.network.authenticate

import com.ciyfhx.network.NetworkConnection
import com.ciyfhx.network.plusAssign
import com.ciyfhx.validator.HMACValidator
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer


class RSAWithAESAuthenticationWithHMACValidator : RSAWithAESAuthenticationWithMACValidator() {

    init {validator = HMACValidator()}

    val logger = LoggerFactory.getLogger(RSAWithAESAuthenticationWithHMACValidator::class.java)!!

    override fun serverAuthenticate(connection: NetworkConnection): Boolean {
        return if(super.serverAuthenticate(connection)){

            val salt = generateSaltAndSend(connection)
            validator.secret2 = ByteBuffer.wrap(salt!!)
            logger.debug("Sent validator salt 2")

            val senderSalt = receiveSalt(connection)
            validator.senderSecret2 = ByteBuffer.wrap(senderSalt!!)
            logger.debug("Done receiving validator salt 2")

            true
        }else false
    }

    override fun clientAuthenticate(connection: NetworkConnection): Boolean {
        val success = super.clientAuthenticate(connection)
        return if (success) {
            val senderSalt = receiveSalt(connection)
            validator.senderSecret2 = ByteBuffer.wrap(senderSalt!!)
            logger.debug("Done receiving validator salt 2")

            val salt = generateSaltAndSend(connection)
            validator.secret2 = ByteBuffer.wrap(salt)
            logger.debug("Sent validator salt 2")

            true
        } else
            false
    }


    override fun authenticationSuccess(connection: NetworkConnection?) {
        super.authenticationSuccess(connection)
        connection?.pipeLineStream?.addPipeLine(validator)
    }

}
