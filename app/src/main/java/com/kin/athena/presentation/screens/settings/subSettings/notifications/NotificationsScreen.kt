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

package com.kin.athena.presentation.screens.settings.subSettings.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.InstallMobile
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.kin.athena.R
import com.kin.athena.presentation.screens.settings.components.IconType
import com.kin.athena.presentation.screens.settings.components.SettingType
import com.kin.athena.presentation.screens.settings.components.SettingsBox
import com.kin.athena.presentation.screens.settings.components.SettingsScaffold
import com.kin.athena.presentation.screens.settings.components.settingsContainer
import com.kin.athena.presentation.screens.settings.viewModel.SettingsViewModel

@Composable
fun NotificationsScreen(
    navController: NavController,
    settings: SettingsViewModel,
) {
    SettingsScaffold(
        settings = settings,
        title = stringResource(id = R.string.settings_notifications),
        onBackNavClicked = { navController.navigateUp() }
    ) {
        settingsContainer {
            val notificationTitle = stringResource(id = R.string.behavior_notify_install)
            val notificationDescription = stringResource(id = R.string.behavior_notify_install_desc)
            
            SettingsBox(
                title = notificationTitle,
                description = notificationDescription,
                icon = IconType.VectorIcon(Icons.Rounded.InstallMobile),
                actionType = SettingType.SWITCH,
                variable = settings.settings.value.sendNotificationOnInstall,
                onSwitchEnabled = {
                    settings.update(settings.settings.value.copy(sendNotificationOnInstall = it))
                }
            )

            SettingsBox(
                title = stringResource(id = R.string.behavior_permanent_notification),
                description = stringResource(id = R.string.behavior_permanent_notification_desc),
                icon = IconType.VectorIcon(Icons.Rounded.NotificationsNone),
                actionType = SettingType.SWITCH,
                variable = settings.settings.value.permanentNotification,
                onSwitchEnabled = { 
                    settings.update(settings.settings.value.copy(permanentNotification = it)) 
                }
            )
        }
    }
}