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

import com.kin.athena.presentation.navigation.routes.HomeRoutes
import com.kin.athena.service.vpn.network.util.NetworkConstants

data class Settings(
    // Hidden
    val isFirstTimeRunning: Boolean = true,

    //Privacy
    var screenProtection: Boolean = false,
    val blockPort80: Boolean = false,

    //Lock
    var defaultRoute: String = HomeRoutes.Home.route,
    var passcode: String? = null,
    var fingerprint: Boolean = false,
    var pattern: String? = null,
    val lockImmediately: Boolean = true,

    //Behaviour
    val logs: Boolean = false,
    val showSystemPackages: Boolean = false,
    val showOfflinePackages: Boolean = false,
    val wiFiDefault: Boolean = true,
    val cellularDefault: Boolean = true,
    val startOnBoot: Boolean = true,

    //Network
    val ipv4: String = NetworkConstants.VPN_ADDRESS,
    val ipv6: String = NetworkConstants.VPN6_ADDRESS,
    val allowLocal: Boolean = true,
    val blockWifiWhenScreenOff: Boolean = false,
    val blockCellularWhenScreenOff: Boolean = false,
    val permanentNotification: Boolean = false,
    val dnsServer1: String = NetworkConstants.DNS_SERVERS[0].ipv4Primary,
    val dnsServer2: String = NetworkConstants.DNS_SERVERS[0].ipv4Secondary,
    val dnsServer1v6: String = NetworkConstants.DNS_SERVERS[0].ipv6Primary,
    val dnsServer2v6: String = NetworkConstants.DNS_SERVERS[0].ipv6Secondary,
    val networkSpeedMonitor: Boolean = false,

    //Root
    val useRootMode: Boolean? = null,
    
    //Shizuku
    val useShizukuMode: Boolean? = null,

    // Themes
    val automaticTheme: Boolean = true,
    val darkTheme: Boolean = false,
    val useDynamicIcons: Boolean = false,
    var dynamicTheme: Boolean = false,
    var amoledTheme: Boolean = false,
    var customColor: Int = -7896468, // Default Material 3 accent color (0xFF5a5e6c - original primaryLight)
    var showDialog: Boolean = true,

    // Notifications
    val sendNotificationOnInstall: Boolean = false,

    // Nflog
    val PID: Int = 0,

    // DNS Blocking
    val malwareProtection: Boolean = false,
    val adBlocker: Boolean = false,
    val trackerProtection: Boolean = false,
    val autoUpdateEnabled: Boolean = true,
    val autoUpdateInterval: Long = 24 * 60 * 60 * 1000L,

    val dontShowHelp: Boolean = false
)