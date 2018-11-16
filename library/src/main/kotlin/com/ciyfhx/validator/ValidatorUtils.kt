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
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class ValidatorUtils {
    companion object {

        val logger = LoggerFactory.getLogger(ValidatorUtils::class.java)!!

        @JvmStatic
        internal fun getSHA512SecurePassword(data: ByteBuffer?, secret: ByteBuffer?): ByteBuffer? {
            try {
                val md = MessageDigest.getInstance("SHA-512")

                return md?.run {
                    update(secret)
                    val finalData = concat(secret, data)
                    val bytes = digest(finalData?.array())
                    ByteBuffer.wrap(bytes)
                }

            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                logger.error("Unable to compute hash")
            }

            return null
        }

        @JvmStatic
        internal fun concat(b1: ByteBuffer?, b2: ByteBuffer?): ByteBuffer? {
            if (b1 != null && b2 != null) {
                val finalData = ByteBuffer.allocate(b1.capacity() + b2.capacity())
                finalData.put(b1)
                finalData.put(b2)
                return finalData
            }
            return null
        }



    }

}

