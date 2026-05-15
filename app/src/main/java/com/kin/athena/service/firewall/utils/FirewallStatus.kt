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

package com.kin.athena.service.firewall.utils

import androidx.annotation.Nullable

sealed class FirewallStatus(val loaded: Float) {
    object OFFLINE : FirewallStatus(0f)
    data class LOADING(val progress: Float = 0f) : FirewallStatus(progress)
    object ONLINE : FirewallStatus(100f)
    object MAGISK_SYSTEMLESS_ERROR : FirewallStatus(0f)

    fun not(): FirewallStatus {
        return when (this) {
            MAGISK_SYSTEMLESS_ERROR -> OFFLINE
            OFFLINE -> ONLINE
            is LOADING -> OFFLINE
            ONLINE -> OFFLINE
        }
    }

    fun name(): String {
        return when (this) {
            MAGISK_SYSTEMLESS_ERROR -> "OFFLINE"
            OFFLINE -> "OFFLINE"
            is LOADING -> "LOADING"
            ONLINE -> "ONLINE"
        }
    }

    fun getRulesLoaded(): Float {
        try {
            return when (this) {
                MAGISK_SYSTEMLESS_ERROR -> 0.0f
                OFFLINE -> 0.0f
                is LOADING -> this.progress
                ONLINE -> 1.0f
            }
        } catch (e: NullPointerException) {
            return  0f
        }
    }
}