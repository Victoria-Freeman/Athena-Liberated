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

package com.kin.athena.service.firewall.model

import com.kin.athena.data.service.NetworkManager

enum class FirewallResult {
    ACCEPT(),
    DROP(),
    DNS_BLOCKED(),
}

data class FireWallModel(
    val destinationIP: String = "",
    val destinationPort: Int = 0,
    val sourceIP: String = "",
    val sourcePort: Int = 0,
    val protocol: Byte = 0,
    var uid: Int = 0,
    var shouldLog: Boolean = true,
    var networkType: NetworkManager.ConnectionType = NetworkManager.ConnectionType.WIFI
)

