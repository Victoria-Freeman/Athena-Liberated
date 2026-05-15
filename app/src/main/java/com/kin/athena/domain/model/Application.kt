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
import com.kin.athena.core.utils.constants.AppConstants

@Entity(
    tableName = "applications",
    indices = [Index(value = ["uid"]), Index(value = ["display_name"]), Index(value = ["package_id"])]
)

data class Application(
    @PrimaryKey
    @ColumnInfo(name = "package_id")
    val packageID: String,

    @ColumnInfo(name = "uid")
    val uid: Int,

    @ColumnInfo(name = "system_app")
    val systemApp: Boolean,

    @ColumnInfo(name = "internet_access")
    val internetAccess: Boolean = true,

    @ColumnInfo(name = "cellular_access")
    val cellularAccess: Boolean = true,

    @ColumnInfo(name = "bypass_vpn")
    val bypassVpn: Boolean = false,

    @ColumnInfo(name = "uses_google_play_services")
    val usesGooglePlayServices: Boolean = false,

    @ColumnInfo(name = "display_name")
    val displayName: String = "",

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "requires_network")
    val requiresNetwork: Boolean = true,

    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false
)