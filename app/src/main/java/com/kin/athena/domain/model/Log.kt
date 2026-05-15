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

package com.kin.athena.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.kin.athena.service.firewall.model.FirewallResult
import com.kin.athena.service.firewall.utils.FirewallStatus

@Entity(
    tableName = "logs",
    indices = [Index(value = ["uid"])]
)
data class Log(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "packet_time")
    val time: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "protocol")
    val protocol: String,

    @ColumnInfo(name = "uid")
    val packageID: Int,

    @ColumnInfo(name = "source_ip")
    val sourceIP: String,

    @ColumnInfo(name = "source_address")
    val destinationAddress: String?,

    @ColumnInfo(name = "source_port")
    val sourcePort: String,

    @ColumnInfo(name = "destination_ip")
    val destinationIP: String,

    @ColumnInfo(name = "destination_port")
    val destinationPort: String,

    @ColumnInfo(name = "packet_status")
    val packetStatus: FirewallResult,
)
