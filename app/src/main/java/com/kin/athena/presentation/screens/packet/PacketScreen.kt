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

package com.kin.athena.presentation.screens.packet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.NetworkCheck
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kin.athena.R
import com.kin.athena.core.utils.extensions.getAppNameFromPackage
import com.kin.athena.core.utils.extensions.toFormattedDateTime
import com.kin.athena.core.utils.extensions.uidToApplication
import com.kin.athena.domain.model.Ip
import com.kin.athena.domain.model.Log
import com.kin.athena.presentation.components.material.MaterialBar
import com.kin.athena.presentation.components.material.MaterialScaffold
import com.kin.athena.presentation.screens.packet.viewModel.PacketViewModel
import com.kin.athena.presentation.screens.settings.components.IconType
import com.kin.athena.presentation.screens.settings.components.SettingType
import com.kin.athena.presentation.screens.settings.components.SettingsBox
import com.kin.athena.presentation.screens.settings.components.settingsContainer
import com.kin.athena.service.firewall.utils.Port
import java.util.Locale

@Composable
fun PacketScreen(
    packetViewModel: PacketViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val log = packetViewModel.log.value

    MaterialScaffold(
        topBar = {
            MaterialBar(
                title = stringResource(id = R.string.packet_log_title),
                onBackNavClicked = { onBack() }
            )
        },
        content = {
            log?.let {
                PacketDetails(log = log, packetViewModel = packetViewModel)
            }
        }
    )
}

@Composable
private fun PacketDetails(log: Log, packetViewModel: PacketViewModel) {
    val context = LocalContext.current
    val application = context.uidToApplication(log.packageID)

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        settingsContainer {
            SettingsBox(
                icon = IconType.VectorIcon(Icons.Rounded.Check),
                title = stringResource(id = R.string.packet_firewall_status),
                description = log.packetStatus.toString(),
                actionType = SettingType.TEXT
            )
            SettingsBox(
                icon = IconType.VectorIcon(Icons.Rounded.Block),
                title = stringResource(id = R.string.packet_block),
                description = stringResource(id = R.string.packet_block_desc, log.destinationIP),
                actionType = SettingType.SWITCH,
                variable = packetViewModel.blockedIps.value.contains(Ip(ip = log.destinationIP)),
                onSwitchEnabled = {
                    if (it) {
                        packetViewModel.addIp(Ip(ip = log.destinationIP))
                    } else {
                        packetViewModel.deleteIp(log.destinationIP)
                    }
                }
            )
        }
        settingsContainer {
            SettingsBox(
                icon = IconType.VectorIcon(Icons.Rounded.NetworkCheck),
                title = stringResource(id = R.string.packet_protocol),
                description = log.protocol,
                actionType = SettingType.TEXT
            )
            if (application != null) {
                SettingsBox(
                    icon = IconType.VectorIcon(Icons.Rounded.Apps),
                    title = stringResource(id = R.string.packet_sender),
                    description = context.getAppNameFromPackage(application.packageID),
                    actionType = SettingType.TEXT
                )
            }
        }
        settingsContainer {
            SettingsBox(
                icon = IconType.VectorIcon(Icons.Rounded.LocationOn),
                title = stringResource(id = R.string.packet_source),
                description = "${log.sourceIP}:${log.sourcePort}",
                actionType = SettingType.TEXT
            )
            SettingsBox(
                icon = IconType.VectorIcon(Icons.Rounded.LocationOn),
                title = stringResource(id = R.string.packet_destination),
                description = "${log.destinationIP}:${log.destinationPort}",
                actionType = SettingType.TEXT
            )
        }
        settingsContainer {
            log.destinationAddress?.let {
                SettingsBox(
                    icon = IconType.VectorIcon(Icons.Rounded.Storage),
                    title = stringResource(id = R.string.packet_dest_address),
                    description = log.destinationAddress,
                    actionType = SettingType.TEXT
                )
            }
            val port = Port.getServiceName(log.destinationPort.toInt())
            port?.let {
                SettingsBox(
                    icon = IconType.VectorIcon(Icons.Rounded.Lan),
                    title = stringResource(id = R.string.packet_dest_type),
                    description = port.uppercase(Locale.ROOT),
                    actionType = SettingType.TEXT
                )
            }
        }
        settingsContainer {
            SettingsBox(
                icon = IconType.VectorIcon(Icons.Rounded.Timer),
                title = stringResource(id = R.string.packet_time),
                description = log.time.toFormattedDateTime(),
                actionType = SettingType.TEXT
            )
        }
    }
}