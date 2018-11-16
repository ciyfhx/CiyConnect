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

package com.ciyfhx.network


internal inline fun NetworkConnection.sendBytes(data: ByteArray) {
    this.dataOutputStream.also {
        it.writeInt(data?.size!!)
        it.write(data)
        it.flush()

    }
}

internal inline fun NetworkConnection.readBytes(): ByteArray {
    return this.dataInputStream.let{
        val data = ByteArray(it.readInt())
        it.read(data)
        return data
    }
}