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

package com.kin.athena.presentation.screens.settings.subSettings.network

import android.content.Intent
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMissedOutgoing
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.filled.CallMissedOutgoing
import androidx.compose.material.icons.filled.MultipleStop
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kin.athena.R
import com.kin.athena.core.utils.extensions.safeNavigate
import com.kin.athena.presentation.navigation.routes.LogRoutes
import com.kin.athena.presentation.screens.settings.components.IconType
import com.kin.athena.presentation.screens.settings.components.IpDialog
import com.kin.athena.presentation.screens.settings.components.SettingType
import com.kin.athena.presentation.screens.settings.components.SettingsBox
import com.kin.athena.presentation.screens.settings.components.SettingsScaffold
import com.kin.athena.presentation.screens.settings.components.settingsContainer
import com.kin.athena.presentation.screens.settings.subSettings.network.viewModel.IpDialogViewModel
import com.kin.athena.presentation.screens.settings.viewModel.SettingsViewModel
import com.kin.athena.service.utils.manager.FirewallManager
import com.kin.athena.service.utils.manager.NetworkSpeedManager
import com.kin.athena.core.utils.isDeviceRooted
import com.kin.athena.core.utils.ShizukuUtils
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.AdminPanelSettings
import kotlinx.coroutines.runBlocking

@Composable
fun NetworkScreen(
    navController: NavController,
    settings: SettingsViewModel,
) {
    val context = LocalContext.current
    val networkViewModel = hiltViewModel<IpDialogViewModel>().apply {
        updateIpv4DialogText(TextFieldValue(settings.settings.value.ipv4 ?: ""))
        updateIpv6DialogText(TextFieldValue(settings.settings.value.ipv6 ?: ""))
    }

    SettingsScaffold(
        settings = settings,
        title = stringResource(id = R.string.settings_network),
        onBackNavClicked = { navController.navigateUp() }
    ) {
        val isShizukuMode = settings.settings.value.useShizukuMode == true
        
        settingsContainer {
            if (!isShizukuMode) {
                SettingsBox(
                    title = stringResource(id = R.string.network_manage_ips),
                    description = stringResource(id = R.string.network_manage_ips_desc),
                    icon = IconType.VectorIcon(Icons.Filled.MultipleStop),
                    actionType = SettingType.CUSTOM,
                    customAction = {
                        navController.safeNavigate(LogRoutes.Ips.route)
                    },
                )
                SettingsBox(
                    title = stringResource(id = R.string.network_always_allow),
                    description = stringResource(id = R.string.network_always_allow_desc),
                    icon = IconType.VectorIcon(Icons.AutoMirrored.Filled.CallMissedOutgoing),
                    actionType = SettingType.SWITCH,
                    variable = settings.settings.value.allowLocal,
                    onSwitchEnabled = { settings.update(settings.settings.value.copy(allowLocal = it)) },
                )
            }
            val speedMonitorTitle = stringResource(id = R.string.notification_network_speed_channel)
            val speedMonitorDescription = stringResource(id = R.string.notification_network_speed_channel_desc)
            
            SettingsBox(
                title = speedMonitorTitle,
                description = speedMonitorDescription,
                icon = IconType.VectorIcon(Icons.Filled.NetworkCheck),
                actionType = SettingType.SWITCH,
                variable = settings.settings.value.networkSpeedMonitor,
                onSwitchEnabled = { enabled ->
                    settings.update(settings.settings.value.copy(networkSpeedMonitor = enabled))
                    if (enabled) {
                        NetworkSpeedManager.start(context)
                    } else {
                        NetworkSpeedManager.stop(context)
                    }
                },
            )
        }
        if (!isShizukuMode) {
            settingsContainer {
                SettingsBox(
                    title = stringResource(R.string.network_interface_ipv4),
                description = stringResource(R.string.network_interface_ipv4_desc),
                icon = IconType.VectorIcon(Icons.Rounded.ArrowDropUp),
                actionType = SettingType.CUSTOM,
                isEnabled = !(settings.settings.value.useRootMode ?: false),
                customAction = { onExit ->

                    LaunchedEffect(Unit) {
                        networkViewModel.updateIpv4DialogText(
                            TextFieldValue(
                                settings.settings.value.ipv4 ?: ""
                            )
                        )
                    }
                    IpDialog(
                        onExit = {
                            networkViewModel.updateIpv4DialogText(TextFieldValue(settings.settings.value.ipv4?: ""))
                            onExit()
                        },
                        textFieldValue = networkViewModel.dialogIpv4TextField.value,
                        onValueChange = networkViewModel::updateIpv4DialogText,
                        onFinished = {
                            settings.update(settings.settings.value.copy(ipv4 = networkViewModel.dialogIpv4TextField.value.text))
                            onExit()
                        },
                        allowOnlyIPv4 = true,
                    )
                }
            )

            SettingsBox(
                title = stringResource(R.string.network_interface_ipv6),
                description = stringResource(R.string.network_interface_ipv6_desc),
                icon = IconType.VectorIcon(Icons.Rounded.ArrowDropDown),
                isEnabled = !(settings.settings.value.useRootMode ?: false),
                actionType = SettingType.CUSTOM,
                customAction = { onExit ->
                    LaunchedEffect(Unit) {
                        networkViewModel.updateIpv6DialogText(TextFieldValue(settings.settings.value.ipv6 ?: ""))
                    }

                    IpDialog(
                        onExit = {
                            networkViewModel.updateIpv6DialogText(TextFieldValue(settings.settings.value.ipv6 ?: ""))
                            onExit()
                        },
                        textFieldValue = networkViewModel.dialogIpv6TextField.value,
                        onValueChange = networkViewModel::updateIpv6DialogText,
                        onFinished = {
                            settings.update(settings.settings.value.copy(ipv6 = networkViewModel.dialogIpv6TextField.value.text))
                            onExit()
                        },
                        allowOnlyIPv6 = true,
                    )
                }
            )
            }
            settingsContainer {
                SettingsBox(
                    title = stringResource(R.string.network_block_wifi_screen_off),
                description = stringResource(R.string.network_block_wifi_screen_off_desc),
                icon = IconType.VectorIcon(Icons.Filled.WifiOff),
                actionType = SettingType.SWITCH,
                isEnabled = settings.settings.value.useRootMode != true,
                variable = settings.settings.value.blockWifiWhenScreenOff,
                onSwitchEnabled = {
                    runBlocking {
                        settings.update(settings.settings.value.copy(blockWifiWhenScreenOff = it)) {
                            networkViewModel.firewallManager.updateScreen(it)
                        }
                    }
                },
            )
            SettingsBox(
                title = stringResource(R.string.network_block_cellular_screen_off),
                description = stringResource(R.string.network_block_cellular_screen_off_desc),
                icon = IconType.VectorIcon(Icons.Filled.SignalCellularConnectedNoInternet0Bar),
                actionType = SettingType.SWITCH,
                isEnabled = settings.settings.value.useRootMode != true,
                variable = settings.settings.value.blockCellularWhenScreenOff,
                onSwitchEnabled = {
                    runBlocking {
                        settings.update(settings.settings.value.copy(blockCellularWhenScreenOff = it)) {
                            networkViewModel.firewallManager.updateScreen(it)
                        }
                    }
                },
            )
            }
            settingsContainer {
                SettingsBox(
                    title = stringResource(R.string.network_kill_switch),
                description = stringResource(R.string.network_kill_switch_desc),
                icon = IconType.VectorIcon(Icons.Filled.StopCircle),
                actionType = SettingType.CUSTOM,
                isEnabled = settings.settings.value.useRootMode != true,
                customAction = {
                    val intent = Intent(Settings.ACTION_VPN_SETTINGS)
                    LocalContext.current.startActivity(intent)
                }
            )
            }
        }
    }
}