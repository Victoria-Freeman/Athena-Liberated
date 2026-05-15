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

package com.kin.athena.presentation.components

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.kin.athena.core.utils.constants.AppConstants

fun defaultScreenEnterAnimation(): EnterTransition {
    return fadeIn(animationSpec = tween(AppConstants.AnimationConstants.FADE_DURATION)) +
            scaleIn(
                initialScale = AppConstants.AnimationConstants.INITIAL_SCALE,
                animationSpec = tween(AppConstants.AnimationConstants.SCALE_DURATION)
            )
}

fun defaultScreenExitAnimation(): ExitTransition {
    return fadeOut(animationSpec = tween(AppConstants.AnimationConstants.FADE_DURATION)) +
            scaleOut(
                targetScale = AppConstants.AnimationConstants.INITIAL_SCALE,
                animationSpec = tween(AppConstants.AnimationConstants.SCALE_DURATION)
            )
}

fun slideScreenEnterAnimation(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(AppConstants.AnimationConstants.SLIDE_DURATION)
    )
}

fun slideScreenExitAnimation(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(AppConstants.AnimationConstants.SLIDE_DURATION)
    )
}

fun fastScreenEnterAnimation(): EnterTransition {
    return fadeIn(animationSpec = tween(150)) +
            scaleIn(
                initialScale = 0.98f,
                animationSpec = tween(180)
            )
}

fun fastScreenExitAnimation(): ExitTransition {
    return fadeOut(animationSpec = tween(120)) +
            scaleOut(
                targetScale = 0.98f,
                animationSpec = tween(150)
            )
}
