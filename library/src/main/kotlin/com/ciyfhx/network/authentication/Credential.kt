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

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.util.*

data class Credential(val username: String, val password: CharArray){

    val usernameByteArray: ByteArray get() = username.toByteArray()
    val passwordByteArray: ByteArray get() = Charset.forName("UTF-8").encode(CharBuffer.wrap(password)).array()

    override fun equals(other: Any?): Boolean {
        return if(other is Credential){
            other.username == username && Arrays.equals(password, other.password)
        } else false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

infix fun String.credential(password: CharArray): Credential{
    return Credential(this, password)
}


infix fun String.credential(passwordByteArray: ByteArray): Credential{
    return Credential(this, Charset.forName("UTF-8").decode(ByteBuffer.wrap(passwordByteArray)).array())
}
