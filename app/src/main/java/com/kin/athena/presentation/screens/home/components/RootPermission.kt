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

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.kin.athena.R
import com.kin.athena.core.utils.grantRootAccess
import com.kin.athena.presentation.components.PermissionModal
import com.kin.athena.presentation.screens.home.viewModel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class rootResut {
    GRANTED,
    VPN,
    DENIED,
    CLOSED
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("InlinedApi")
@Composable
fun RootPermission(
    viewModel: HomeViewModel,
    onPermissionResult: (rootResut) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    fun onEnd() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                viewModel.updateRootPermissionStatus(false)
            }
        }
    }

    if (viewModel.rootPermissionRequested.value) {
        PermissionModal(
            sheetState = sheetState,
            permissionName = stringResource(id = R.string.permissions_root),
            permissionDescription = stringResource(id = R.string.root_permission_description),
            onPermissionRequest = {
                scope.launch(Dispatchers.Main) {
                    val result = grantRootAccess()
                    if (result) {
                        onEnd()
                        onPermissionResult(rootResut.GRANTED)
                    } else {
                        onEnd()
                        onPermissionResult(rootResut.DENIED)
                    }
                }
            },
            onPermissionAlternativeRequest = {
                onEnd()
                onPermissionResult(rootResut.VPN)
            },
            permissionAlternative = stringResource(id = R.string.permissions_deny_root),
            onDismiss = {
                onPermissionResult(rootResut.CLOSED)
                onEnd()
            }
        )
    }

    LaunchedEffect(true) {
        viewModel.updateRootPermissionStatus(true)
    }
}
