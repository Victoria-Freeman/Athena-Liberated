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

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val settings: SettingsBackup,
    val customDomains: List<CustomDomainBackup>,
    val customBlocklists: List<CustomBlocklistBackup>,
    val applications: List<ApplicationBackup> = emptyList()
)

@Serializable
data class SettingsBackup(
    val automaticTheme: Boolean,
    val darkTheme: Boolean,
    val useDynamicIcons: Boolean,
    val dynamicTheme: Boolean,
    val amoledTheme: Boolean,
    val customColor: Int,
    val screenProtection: Boolean,
    val blockPort80: Boolean,
    val logs: Boolean,
    val showSystemPackages: Boolean,
    val showOfflinePackages: Boolean,
    val wiFiDefault: Boolean,
    val cellularDefault: Boolean,
    val startOnBoot: Boolean,
    val allowLocal: Boolean,
    val blockWifiWhenScreenOff: Boolean,
    val blockCellularWhenScreenOff: Boolean,
    val permanentNotification: Boolean,
    val networkSpeedMonitor: Boolean,
    val sendNotificationOnInstall: Boolean,
    val malwareProtection: Boolean,
    val adBlocker: Boolean,
    val trackerProtection: Boolean,
    val autoUpdateInterval: Long
)

@Serializable
data class CustomDomainBackup(
    val domain: String,
    val description: String,
    val isRegex: Boolean,
    val isAllowlist: Boolean,
    val isEnabled: Boolean
)

@Serializable
data class CustomBlocklistBackup(
    val title: String,
    val url: String,
    val state: String // IGNORE, DENY, ALLOW
)

@Serializable
data class ApplicationBackup(
    val packageID: String,
    val internetAccess: Boolean,
    val cellularAccess: Boolean,
    val bypassVpn: Boolean = false,
    val isPinned: Boolean = false
)
