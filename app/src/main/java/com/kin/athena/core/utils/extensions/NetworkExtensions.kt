/*
 * Copyright (C) 2025-2026 Vexzure
 * Copyright (C) 2026 Victoria Freeman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.kin.athena.core.utils.extensions

import com.kin.athena.core.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * Extension function to resolve an IP address to a hostname.
 *
 * @return The hostname as a String, or null if the resolution fails.
 */
suspend fun String.resolveIpToHostname(): String? {
    return withContext(Dispatchers.IO) {
        try {
            val address = InetAddress.getByName(this@resolveIpToHostname)
            if (address.hostName == this@resolveIpToHostname) {
                null
            } else {
                address.hostName
            }
        } catch (e: Exception) {
            null
        }
    }
}