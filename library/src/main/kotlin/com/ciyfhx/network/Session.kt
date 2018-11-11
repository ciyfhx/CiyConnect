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

import java.util.concurrent.ConcurrentHashMap

open class Session protected constructor(val connection: NetworkConnection) {

    val session = ConcurrentHashMap<String, Any?>()

    /**
     * Put the new object inside the session
     * @param key
     * @param sessionObj
     */
    operator fun set(key: String, sessionObj: Any){
        session.put(key, sessionObj)
    }
    /**
     * Get the session stored object with the key
     * @param key
     */
    operator fun get(key: String): Any? {
        return session.get(key)
    }

    /**
     * Clears all session stored object
     */
    fun clear() {
        session.clear()
    }


}