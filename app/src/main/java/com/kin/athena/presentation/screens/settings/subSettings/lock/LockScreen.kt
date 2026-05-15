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

package com.kin.athena.presentation.screens.settings.subSettings.lock

import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.kin.athena.presentation.components.material.MaterialScaffold
import com.kin.athena.presentation.screens.settings.subSettings.lock.components.ActionType
import com.kin.athena.presentation.screens.settings.subSettings.lock.components.FingerprintLock
import com.kin.athena.presentation.screens.settings.subSettings.lock.components.PasscodeLock
import com.kin.athena.presentation.screens.settings.subSettings.lock.components.PatternLock
import com.kin.athena.presentation.screens.settings.viewModel.SettingsViewModel

@Composable
fun LockScreen(
    settingsViewModel: SettingsViewModel,
    navController: NavController,
    action: ActionType?,
) {
    MaterialScaffold {
        if (action != null) {
            when (action) {
                ActionType.PASSCODE -> PasscodeLock(settingsViewModel, navController)
                ActionType.FINGERPRINT -> FingerprintLock(settingsViewModel = settingsViewModel, navController = navController)
                ActionType.PATTERN -> PatternLock(settingsViewModel = settingsViewModel, navController = navController)
            }
        } else {
            when {
                settingsViewModel.settings.value.pattern != null -> PatternLock(settingsViewModel = settingsViewModel, navController = navController)
                settingsViewModel.settings.value.fingerprint -> FingerprintLock(settingsViewModel = settingsViewModel, navController = navController)
                settingsViewModel.settings.value.passcode != null -> PasscodeLock(settingsViewModel, navController)
            }
        }
    }
}