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

import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec




/**
 *
 *
 * HMAC - keyed-hash message authentication code or hash-based message authentication code
 *
 *
 * Check if the message receive is not tampered by computing the checksum of SHA512
 * **Formula:** *Digest = (SHA512(SHA512(MESSAGE | SECRET) | SECRET2))*
 *
 * @author  Peh Zi Heng
 * @version 1.0
 * @since   2018-10-10
 */
class HMACValidator : MACValidator() {
    private val logger = LoggerFactory.getLogger(HMACValidator::class.java)

    override fun read(data: ByteBuffer): ByteBuffer {
        data.clear()

        val content = ByteArray(data.capacity() - 64)
        data.get(content)
        val hashed = ByteArray(64)
        data.get(hashed)

        val contentBB = ByteBuffer.wrap(content)
        if (secret == null) throw RuntimeException("Secret/Salt is not set")
        val computedHash = getHMACHashed(ByteBuffer.wrap(content), secret)

        if (Arrays.equals(computedHash.array(), hashed)) {
            logger.trace("Computed hashed is correct")
            return contentBB
        } else
            throw InvalidHashException("Computed hash and the hash receive is not the same")
    }

    override fun write(data: ByteBuffer): ByteBuffer {
        if (senderSecret == null) throw RuntimeException("Sender Secret/Salt is not set")

        val hashed = getHMACHashed(data, senderSecret)
        logger.trace("Computed HMAC hashed {}", hashed.capacity())
        data.clear()
        hashed.clear()
        return ByteBuffer.allocate(data.capacity() + 64).put(data).put(hashed)
    }

    companion object {

        private const val ALGORITHM = "HmacSHA512"

        private fun getHMACHashed(data: ByteBuffer?, key: ByteBuffer?): ByteBuffer{
            val signingKey = SecretKeySpec(key?.array(), ALGORITHM)
            val mac = Mac.getInstance(ALGORITHM)
            mac.init(signingKey)
            return ByteBuffer.wrap(mac.doFinal(data?.array()))
        }
    }

}