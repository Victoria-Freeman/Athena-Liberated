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

package com.kin.athena.core.utils.extensions

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.kin.athena.core.logging.Logger
import com.kin.athena.presentation.components.defaultScreenEnterAnimation
import com.kin.athena.presentation.components.defaultScreenExitAnimation
import com.kin.athena.presentation.components.slideScreenEnterAnimation
import com.kin.athena.presentation.components.slideScreenExitAnimation
import com.kin.athena.presentation.components.fastScreenEnterAnimation
import com.kin.athena.presentation.components.fastScreenExitAnimation
import com.kin.athena.presentation.navigation.routes.HomeRoutes
import com.kin.athena.presentation.navigation.routes.SettingRoutes

fun NavController.safeNavigate(route: String) {
    val currentDestinationRoute = currentBackStackEntry?.destination?.route?.split("/")?.firstOrNull()

    if (currentDestinationRoute != null) {
        val segmented = route.split("/")
        val targetFirstSegment = segmented.firstOrNull()

        if (currentDestinationRoute != targetFirstSegment || segmented.size > 1 && segmented[1] == "lock") {
            navigate(route)
        }
    } else {
        Logger.error("Current destination route is null. Navigating to $route.")
        navigate(route)
    }
}


fun NavOptionsBuilder.popUpToTop(navController: NavController) {
    popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
        inclusive =  true
    }
}


fun NavGraphBuilder.animatedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = { defaultScreenEnterAnimation() },
        exitTransition = { defaultScreenExitAnimation() },
        popEnterTransition = { defaultScreenEnterAnimation() },
        popExitTransition = { defaultScreenExitAnimation() },
        content = content
    )
}

fun NavGraphBuilder.slideInComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = { slideScreenEnterAnimation() },
        exitTransition = { defaultScreenExitAnimation() },
        popEnterTransition = { defaultScreenEnterAnimation() },
        popExitTransition = { slideScreenExitAnimation() },
        content = content
    )
}

fun NavGraphBuilder.fastAnimatedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = { fastScreenEnterAnimation() },
        exitTransition = { fastScreenExitAnimation() },
        popEnterTransition = { fastScreenEnterAnimation() },
        popExitTransition = { fastScreenExitAnimation() },
        content = content
    )
}
