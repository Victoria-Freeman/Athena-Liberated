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

package com.kin.athena.presentation.screens.settings.subSettings.network.ips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.NetworkPing
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kin.athena.R
import com.kin.athena.presentation.components.material.MaterialScaffold
import com.kin.athena.presentation.screens.home.components.SearchBar
import com.kin.athena.presentation.screens.settings.components.IpDialog
import com.kin.athena.presentation.screens.settings.components.SettingDialog
import com.kin.athena.presentation.screens.settings.components.SettingsBox
import com.kin.athena.presentation.screens.settings.components.SettingType
import com.kin.athena.presentation.screens.settings.components.IconType
import com.kin.athena.presentation.screens.settings.components.settingsContainer
import com.kin.athena.presentation.screens.settings.subSettings.network.ips.viewModel.IpViewModel

@Composable
fun IpScreen(
    navController: NavController,
    ipViewModel: IpViewModel = hiltViewModel()
) {
    var showAddIpDialog by remember { mutableStateOf(false) }

    MaterialScaffold(
        topBar = {
           SearchBar(
               text = stringResource(R.string.network_manage_ips),
               query = ipViewModel.query.value,
               onQueryChange = ipViewModel::onQueryChanged,
               onClearClick = ipViewModel::clearQuery,
               onBackClicked = { navController.navigateUp() },
               button = {
                   IconButton(onClick = { showAddIpDialog = true }) {
                       Icon(Icons.Rounded.Add, contentDescription = null)
                   }
               }
           )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (ipViewModel.ips.value.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NetworkPing,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.network_no_ips),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val filteredIps = remember(ipViewModel.ips.value, ipViewModel.query.value) {
                    if (ipViewModel.query.value.isBlank()) {
                        ipViewModel.ips.value
                    } else {
                        ipViewModel.ips.value.filter { ip ->
                            ip.ip.contains(ipViewModel.query.value, ignoreCase = true)
                        }
                    }
                }
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    settingsContainer {
                        filteredIps.forEach { ip ->
                            IpCard(
                                ip = ip.ip,
                                onDelete = { ipViewModel.deleteIP(ip.ip) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add IP Dialog
    if (showAddIpDialog) {
        IpDialog(
            onExit = { showAddIpDialog = false },
            textFieldValue = ipViewModel.dialogTextField.value,
            onValueChange = ipViewModel::updateDialogText,
            onFinished = {
                ipViewModel.addIP(ipViewModel.dialogTextField.value)
                ipViewModel.updateDialogText(TextFieldValue())
                showAddIpDialog = false
            }
        )
    }
}

@Composable
private fun IpCard(
    ip: String,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    val ipType = when {
        ip.contains(":") -> "IPv6 Address"
        ip.contains(".") -> "IPv4 Address"
        else -> "IP Address"
    }

    SettingsBox(
        title = ip,
        description = ipType,
        icon = IconType.VectorIcon(Icons.Rounded.NetworkPing),
        actionType = SettingType.CUSTOM,
        customButton = {
            IconButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    )
    
    // Delete confirmation dialog
    if (showDeleteConfirm) {
        SettingDialog(
            text = "Delete IP Address",
            onExit = { showDeleteConfirm = false }
        ) {
            Column {
                Text(
                    text = "Are you sure you want to delete this IP address?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = ip,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showDeleteConfirm = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(stringResource(R.string.common_cancel))
                    }
                    Button(
                        onClick = {
                            onDelete()
                            showDeleteConfirm = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Delete")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}