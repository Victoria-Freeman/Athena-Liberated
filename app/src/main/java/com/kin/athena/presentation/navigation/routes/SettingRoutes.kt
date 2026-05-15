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

package com.kin.athena.presentation.navigation.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.kin.athena.presentation.screens.home.viewModel.HomeViewModel
import com.kin.athena.presentation.screens.settings.subSettings.about.AboutScreen
import com.kin.athena.presentation.screens.settings.subSettings.behavior.BehaviorScreen
import com.kin.athena.presentation.screens.settings.subSettings.colors.ColorsScreen
import com.kin.athena.presentation.screens.settings.subSettings.dns.DnsScreen
import com.kin.athena.presentation.screens.settings.subSettings.dns.CustomBlocklistManagementScreen
import com.kin.athena.presentation.screens.settings.subSettings.dns.DomainManagementScreen
import com.kin.athena.presentation.screens.settings.subSettings.network.NetworkScreen
import com.kin.athena.presentation.screens.settings.subSettings.notifications.NotificationsScreen
import com.kin.athena.presentation.screens.settings.subSettings.privacy.PrivacyScreen
import com.kin.athena.presentation.screens.settings.subSettings.proxy.ProxyScreen
import com.kin.athena.presentation.screens.settings.SettingsScreen
import com.kin.athena.presentation.screens.settings.subSettings.lock.components.ActionType
import com.kin.athena.presentation.screens.settings.viewModel.SettingsViewModel

sealed class SettingRoutes(val route: String) {
    data object Settings : SettingRoutes("settings")
    data object Colors : SettingRoutes("subSettings/colors")
    data object Behavior: SettingRoutes("subSettings/behavior")
    data object Network: SettingRoutes("subSettings/network")
    data object Notifications: SettingRoutes("subSettings/notifications")
    data object Proxy: SettingRoutes("subSettings/proxy")
    data object Dns: SettingRoutes("subSettings/dns")
    data object CustomBlocklist: SettingRoutes("subSettings/dns/customBlocklist")
    data object DomainManagement: SettingRoutes("subSettings/dns/domainManagement")
    data object Privacy: SettingRoutes("subSettings/privacy")
    data object About: SettingRoutes("subSettings/about")
    data object LockScreen : SettingRoutes("subSettings/lock/{type}") {
        fun createRoute(action: ActionType?) = "subSettings/lock/$action"
    }
}

val settingScreens = mapOf<String, @Composable (settingsViewModel: SettingsViewModel, navController : NavController, homeViewModel: HomeViewModel) -> Unit>(
    SettingRoutes.Settings.route to { settingsViewModel, navController, homeViewModel -> SettingsScreen(navController, settingsViewModel) },
    SettingRoutes.Colors.route to { settingsViewModel, navController, homeViewModel -> ColorsScreen(navController, settingsViewModel, homeViewModel) },
    SettingRoutes.Behavior.route to { settingsViewModel, navController, homeViewModel -> BehaviorScreen(navController, settingsViewModel) },
    SettingRoutes.Network.route to { settingsViewModel, navController, homeViewModel -> NetworkScreen(navController, settingsViewModel) },
    SettingRoutes.Notifications.route to { settingsViewModel, navController, homeViewModel -> NotificationsScreen(navController, settingsViewModel) },
    SettingRoutes.Proxy.route to { settingsViewModel, navController, homeViewModel -> ProxyScreen(navController, settingsViewModel) },
    SettingRoutes.Dns.route to { settingsViewModel, navController, homeViewModel -> DnsScreen(navController, settingsViewModel) },
    SettingRoutes.CustomBlocklist.route to { settingsViewModel, navController, homeViewModel -> CustomBlocklistManagementScreen(navController, settingsViewModel) },
    SettingRoutes.DomainManagement.route to { settingsViewModel, navController, homeViewModel -> DomainManagementScreen(navController, settingsViewModel) },
    SettingRoutes.Privacy.route to { settingsViewModel, navController, homeViewModel -> PrivacyScreen(navController, settingsViewModel) },
    SettingRoutes.About.route to { settingsViewModel, navController, homeViewModel -> AboutScreen(navController, settingsViewModel) }
)