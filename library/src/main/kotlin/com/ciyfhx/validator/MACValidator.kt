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

package com.ciyfhx.validator

import com.ciyfhx.network.PipeLine
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.util.*

/**
 *
 *
 * MAC - Message Authenticated Code
 *
 *
 *
 * Check if the message receive is not tampered by computing the checksum of SHA512
 * **Formula:** *Digest = (SHA512(MESSAGE | SECRET))*
 * **Note:** The MAC's hashed function used by this class is susceptible to length extension attack
 * @see com.ciyfhx.validator.HMACValidator
 *
 *
 * @author  Peh Zi Heng
 * @version 1.0
 * @since   2018-10-10
 */
open class MACValidator : PipeLine {

    var secret: ByteBuffer? = null
    var senderSecret: ByteBuffer? = null

    private val logger = LoggerFactory.getLogger(MACValidator::class.java)

    @Throws(Exception::class)
    override fun read(data: ByteBuffer): ByteBuffer {
        data.clear()

        val content = ByteArray(data.capacity() - 64)
        data.get(content)
        val hashed = ByteArray(64)
        data.get(hashed)

        val contentBB = ByteBuffer.wrap(content)
        if (secret == null) throw RuntimeException("Secret/Salt is not set")
        val computedHash = ValidatorUtils.getSHA512SecurePassword(contentBB, secret)

        if (Arrays.equals(computedHash?.array(), hashed)) {
            logger.trace("Computed hashed is correct")
            return contentBB
        } else
            throw InvalidHashException("Computed hash and the hash receive is not the same")
    }

    @Throws(Exception::class)
    override fun write(data: ByteBuffer): ByteBuffer {
        if (senderSecret == null) throw RuntimeException("Sender Secret/Salt is not set")

        val hashed = ValidatorUtils.getSHA512SecurePassword(data, senderSecret)
        logger.trace("Computed hashed {}", hashed!!.capacity())
        data.clear()
        hashed.clear()
        return ByteBuffer.allocate(data.capacity() + 64).put(data).put(hashed)
    }


    protected inner class InvalidHashException(msg: String) : Exception(msg)

}