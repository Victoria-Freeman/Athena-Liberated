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

package com.kin.athena.presentation.screens.settings.subSettings.privacy

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kin.athena.R

data class BackupOptions(
    val includeSettings: Boolean = true,
    val includeDomains: Boolean = true,
    val includeBlocklists: Boolean = true,
    val includeApplications: Boolean = true
)

@Composable
fun BackupSelectionDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (BackupOptions) -> Unit
) {
    var includeSettings by remember { mutableStateOf(true) }
    var includeDomains by remember { mutableStateOf(true) }
    var includeBlocklists by remember { mutableStateOf(true) }
    var includeApplications by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.backup_selection_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                BackupOptionItem(
                    icon = Icons.Rounded.Settings,
                    label = stringResource(id = R.string.backup_option_settings),
                    checked = includeSettings,
                    onCheckedChange = { includeSettings = it }
                )

                BackupOptionItem(
                    icon = Icons.Rounded.Block,
                    label = stringResource(id = R.string.backup_option_domains),
                    checked = includeDomains,
                    onCheckedChange = { includeDomains = it }
                )

                BackupOptionItem(
                    icon = Icons.Rounded.ListAlt,
                    label = stringResource(id = R.string.backup_option_blocklists),
                    checked = includeBlocklists,
                    onCheckedChange = { includeBlocklists = it }
                )

                BackupOptionItem(
                    icon = Icons.Rounded.Apps,
                    label = stringResource(id = R.string.backup_option_applications),
                    checked = includeApplications,
                    onCheckedChange = { includeApplications = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        BackupOptions(
                            includeSettings = includeSettings,
                            includeDomains = includeDomains,
                            includeBlocklists = includeBlocklists,
                            includeApplications = includeApplications
                        )
                    )
                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.common_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.common_cancel))
            }
        }
    )
}

@Composable
private fun BackupOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
