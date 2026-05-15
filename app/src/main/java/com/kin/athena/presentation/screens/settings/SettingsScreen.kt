/*
 * Copyright (C) 2025-2026 Vexzure
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

package com.kin.athena.presentation.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import com.kin.athena.R
import com.kin.athena.core.utils.extensions.safeNavigate
import com.kin.athena.presentation.navigation.routes.SettingRoutes
import com.kin.athena.presentation.screens.settings.components.SectionBlock
import com.kin.athena.presentation.screens.settings.components.SettingsScaffold
import com.kin.athena.presentation.screens.settings.components.SettingSection
import com.kin.athena.presentation.screens.settings.components.settingsContainer
import com.kin.athena.presentation.screens.settings.viewModel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    settings: SettingsViewModel,
) {
    SettingsScaffold(
        settings = settings,
        title = stringResource(id = R.string.common_settings),
        onBackNavClicked = { navController.navigateUp() }
    ) {

        item {
            SectionBlock(
                listOf(
                    SettingSection(
                        title = stringResource(id = R.string.settings_colors),
                        features = listOf(
                            stringResource(id = R.string.settings_colors_option_theme),
                            stringResource(id = R.string.colors_dynamic_icons),
                        ),
                        icon = Icons.Rounded.Palette,
                        onClick = { navController.safeNavigate(SettingRoutes.Colors.route) }
                    ),
                    SettingSection(
                        title = stringResource(id = R.string.settings_behavior),
                        features = listOf(
                            stringResource(id = R.string.common_logs),
                            stringResource(id = R.string.settings_behavior_option_system),
                        ),
                        icon = Icons.Rounded.RuleFolder,
                        onClick = { navController.safeNavigate(SettingRoutes.Behavior.route) }
                    )
                )
            )
        }
        item {
            val isShizukuMode = settings.settings.value.useShizukuMode == true
            val sections = buildList {
                add(
                    SettingSection(
                        title = stringResource(id = R.string.settings_network),
                        features = listOf(
                            stringResource(id = R.string.settings_network_option_lockdown),
                            stringResource(id = R.string.network_kill_switch)
                        ),
                        icon = Icons.Rounded.Wifi,
                        onClick = { navController.safeNavigate(SettingRoutes.Network.route) }
                    )
                )
                if (!isShizukuMode) {
                    add(
                        SettingSection(
                            title = stringResource(id = R.string.settings_dns),
                            features = listOf(
                                stringResource(id = R.string.settings_dns_option_block),
                                stringResource(id = R.string.settings_dns_option_hostname),
                                stringResource(id = R.string.settings_dns_option_updates)
                            ),
                            icon = Icons.Rounded.Dns,
                            onClick = { navController.safeNavigate(SettingRoutes.Dns.route) }
                        )
                    )
                }
            }
            SectionBlock(sections)
        }
        item {
            SectionBlock(
                listOf(
                    SettingSection(
                        title = stringResource(id = R.string.settings_privacy),
                        features = listOf(
                            stringResource(id = R.string.settings_privacy_option_lock),
                            stringResource(id = R.string.settings_privacy_option_hide),
                        ),
                        icon = ImageVector.vectorResource(id = R.drawable.incognito),
                        onClick = { navController.safeNavigate(SettingRoutes.Privacy.route) }
                    ),
                    SettingSection(
                        title = stringResource(id = R.string.settings_notifications),
                        features = listOf(
                            stringResource(id = R.string.settings_notifications_install_alerts),
                            stringResource(id = R.string.settings_notifications_activity),
                        ),
                        icon = Icons.Rounded.Notifications,
                        onClick = { navController.safeNavigate(SettingRoutes.Notifications.route) }
                    )
                )
            )
        }
        item {
            SectionBlock(
                listOf(
                    SettingSection(
                        title = stringResource(id = R.string.about_title),
                        features = listOf(
                            stringResource(id = R.string.details_version),
                            stringResource(id = R.string.about_build_type),
                            stringResource(id = R.string.settings_license)
                        ),
                        icon = Icons.Rounded.Info,
                        onClick = { navController.safeNavigate(SettingRoutes.About.route) }
                    )
                )
            )
        }
    }
}