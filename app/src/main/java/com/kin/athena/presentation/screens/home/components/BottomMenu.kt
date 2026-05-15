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

package com.kin.athena.presentation.screens.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kin.athena.R
import com.kin.athena.presentation.screens.settings.components.IconType
import com.kin.athena.presentation.screens.settings.components.SettingType
import com.kin.athena.presentation.screens.settings.components.SettingsBox
import com.kin.athena.presentation.screens.settings.components.settingsContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomMenu(
    sheetState: SheetState,
    onLogsClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onExit: () -> Unit,
) {
    ModalBottomSheet(
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = { onExit() }
    ) {
        LazyColumn(
            modifier = Modifier.padding(20.dp, 0.dp, 20.dp, 20.dp)
        ) {
            settingsContainer {
                SettingsBox(
                    size = 8.dp,
                    isCentered = true,
                    title = stringResource(R.string.common_logs),
                    icon = IconType.VectorIcon(Icons.Rounded.Code),
                    actionType = SettingType.CUSTOM,
                    customAction = {
                        onLogsClicked()
                        onExit()
                    }
                )
                SettingsBox(
                    title = stringResource(R.string.common_settings),
                    size = 8.dp,
                    isCentered = true,
                    icon = IconType.VectorIcon(Icons.Rounded.Settings),
                    actionType = SettingType.CUSTOM,
                    customAction = {
                        onSettingsClicked()
                        onExit()
                    }
                )
            }
        }
    }
}