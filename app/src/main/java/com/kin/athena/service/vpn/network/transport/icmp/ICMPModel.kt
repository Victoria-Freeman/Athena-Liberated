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

package com.kin.athena.service.vpn.network.transport.icmp

data class ICMPModel(
    val type: Int,
    val code: Int,
    val checksum: Int,
    val identifier: Int,
    val sequenceNumber: Int,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ICMPModel

        return type == other.type &&
                code == other.code &&
                checksum == other.checksum &&
                identifier == other.identifier &&
                sequenceNumber == other.sequenceNumber &&
                data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        return listOf(type, code, checksum, identifier, sequenceNumber, data.contentHashCode()).fold(0) { acc, i -> 31 * acc + i }
    }
}
