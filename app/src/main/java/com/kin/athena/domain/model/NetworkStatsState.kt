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

/**
 * Data class representing the current network statistics state for the logging screen.
 * Contains counts of allowed, blocked, and total network requests along with firewall status.
 */
data class NetworkStatsState(
    /**
     * Number of network requests that were allowed by the firewall
     */
    val allowedCount: Long = 0L,
    
    /**
     * Number of network requests that were blocked by the firewall
     */
    val blockedCount: Long = 0L,
    
    /**
     * Total number of network requests processed (allowedCount + blockedCount)
     */
    val totalCount: Long = 0L,
    
    /**
     * Whether the firewall is currently active and processing requests
     */
    val isFirewallActive: Boolean = false,
    
    /**
     * Timestamp when the current firewall session started, null if no session is active
     */
    val sessionStartTime: Long? = null
)