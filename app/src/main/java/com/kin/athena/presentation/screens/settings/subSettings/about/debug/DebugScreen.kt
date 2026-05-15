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

package com.kin.athena.presentation.screens.settings.subSettings.about.debug

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.kin.athena.R
import com.kin.athena.core.logging.Logger
import com.kin.athena.core.utils.runRootCommand
import com.kin.athena.presentation.components.PermissionModal
import com.kin.athena.presentation.screens.settings.components.SettingType
import com.kin.athena.presentation.screens.settings.components.SettingsBox
import com.kin.athena.presentation.screens.settings.components.SettingsScaffold
import com.kin.athena.presentation.screens.settings.components.settingsContainer
import com.kin.athena.presentation.screens.settings.subSettings.dns.root.HostsManager
import com.kin.athena.presentation.screens.settings.viewModel.SettingsViewModel

@Composable
fun DebugScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
) {
    val context = LocalContext.current
    
    SettingsScaffold(
        settings = settingsViewModel,
        title = stringResource(id = R.string.debug_title),
        onBackNavClicked = { navController.navigateUp() }
    ) {
          settingsContainer {
              SettingsBox(
                  title = stringResource(R.string.debug_delete_iptables),
                  actionType = SettingType.CUSTOM,
                  customAction = { onExit ->
                        ClearChainSheet(onExit)
                  }
              )
              SettingsBox(
                  title = stringResource(R.string.debug_remove_hosts),
                  actionType = SettingType.CUSTOM,
                  customAction = { onExit ->
                        ClearHostsSheet(context, onExit)
                  }
              )
          }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClearChainSheet(onExit: () -> Unit) {
    val disableState = rememberModalBottomSheetState()


    PermissionModal(
        permissionName = stringResource(id = R.string.home_firewall_terminated),
        permissionDescription = stringResource(id = R.string.firewall_terminated_description),
        permissionRequest = stringResource(id = R.string.common_clear),
        onDismiss = {
            onExit()
        },
        onPermissionRequest = {
            runRootCommand(commands = "iptables -F\n")
            onExit()
        },
        sheetState = disableState,
        permissionAlternative = stringResource(id = R.string.common_cancel),
        onPermissionAlternativeRequest = {
            onExit()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClearHostsSheet(context: android.content.Context, onExit: () -> Unit) {
    val disableState = rememberModalBottomSheetState()

    PermissionModal(
        permissionName = stringResource(R.string.debug_clear_hosts_title),
        permissionDescription = stringResource(R.string.debug_clear_hosts_desc),
        permissionRequest = stringResource(id = R.string.common_clear),
        onDismiss = {
            onExit()
        },
        onPermissionRequest = {
            try {
                val hostsManager = HostsManager(context, emptyList())
                hostsManager.revertToDefault()
                Logger.info("Debug: Successfully cleared hosts file")
            } catch (e: Exception) {
                Logger.error("Debug: Failed to clear hosts file: ${e.message}", e)
            }
            onExit()
        },
        sheetState = disableState,
        permissionAlternative = stringResource(id = R.string.common_cancel),
        onPermissionAlternativeRequest = {
            onExit()
        }
    )
}