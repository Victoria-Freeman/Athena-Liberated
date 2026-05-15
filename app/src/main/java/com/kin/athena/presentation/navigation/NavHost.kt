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

package com.kin.athena.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kin.athena.core.utils.extensions.animatedComposable
import com.kin.athena.core.utils.extensions.safeNavigate
import com.kin.athena.core.utils.extensions.slideInComposable
import com.kin.athena.core.utils.extensions.fastAnimatedComposable
import com.kin.athena.presentation.navigation.routes.HomeRoutes
import com.kin.athena.presentation.navigation.routes.LogRoutes
import com.kin.athena.presentation.navigation.routes.SettingRoutes
import com.kin.athena.presentation.navigation.routes.settingScreens
import com.kin.athena.presentation.screens.settings.subSettings.about.debug.DebugScreen
import com.kin.athena.presentation.screens.details.DetailsScreen
import com.kin.athena.presentation.screens.home.HomeScreen
import com.kin.athena.presentation.screens.home.viewModel.HomeViewModel
import com.kin.athena.presentation.screens.packet.PacketScreen
import com.kin.athena.presentation.screens.settings.subSettings.logs.LogsScreen
import com.kin.athena.presentation.screens.settings.SettingsScreen
import com.kin.athena.presentation.screens.settings.subSettings.lock.LockScreen
import com.kin.athena.presentation.screens.settings.subSettings.lock.components.ActionType
import com.kin.athena.presentation.screens.settings.subSettings.network.ips.IpScreen
import com.kin.athena.presentation.screens.settings.viewModel.SettingsViewModel

@Composable
fun AppNavHost(
    settings: SettingsViewModel,
    navController: NavHostController,
    startDestination: String,
    homeViewModel: HomeViewModel,
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        fastAnimatedComposable(HomeRoutes.Home.route) {
            HomeScreen(
                onLogsClicked = {  navController.safeNavigate(LogRoutes.Logs.route) },
                onSettingsClicked = { navController.safeNavigate(SettingRoutes.Settings.route) },
                onApplicationClicked = { navController.safeNavigate(HomeRoutes.Details.createRoute(it)) },
                settingsViewModel = settings,
                homeViewModel = homeViewModel
            )
        }

        animatedComposable(HomeRoutes.Details.route) {
            DetailsScreen(
                settings = settings,
                navController = navController,
                homeViewModel = homeViewModel,
                onBack = { navController.navigateUp() }
            )
        }

        animatedComposable(LogRoutes.Logs.route) {
            LogsScreen(
                onBackNavClicked = { navController.navigateUp() },
                navController = navController
            )
        }

        animatedComposable(LogRoutes.Packet.route) {
            PacketScreen(onBack = { navController.navigateUp() })
        }

        animatedComposable(SettingRoutes.LockScreen.route) { backStackEntry ->
            val actionString = backStackEntry.arguments?.getString("type")
            val action = if (actionString == "null" || actionString == null) {
                null
            } else {
                ActionType.valueOf(actionString)
            }
            LockScreen(
                settingsViewModel = settings,
                navController = navController,
                action = action
            )
        }

        slideInComposable(SettingRoutes.Settings.route) {
            SettingsScreen(
                navController = navController,
                settings = settings,
            )
        }

        animatedComposable(HomeRoutes.Debug.route) {
            DebugScreen(navController, settings)
        }

        animatedComposable(LogRoutes.Ips.route) {
            IpScreen(navController)
        }

        settingScreens.forEach { (route, screen) ->
            if (route == SettingRoutes.Settings.route) {
                slideInComposable(SettingRoutes.Settings.route) {
                    screen(settings, navController, homeViewModel)
                }
            } else {
                animatedComposable(route) {
                    screen(settings, navController, homeViewModel)
                }
            }
        }
    }
}