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

package com.kin.athena.presentation.screens.settings.subSettings.lock.components

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.kin.athena.R
import com.kin.athena.core.utils.extensions.popUpToTop
import com.kin.athena.presentation.navigation.routes.HomeRoutes
import com.kin.athena.presentation.navigation.routes.SettingRoutes
import com.kin.athena.presentation.screens.settings.viewModel.SettingsViewModel
import java.util.concurrent.Executor

@Composable
fun FingerprintLock(
    settingsViewModel: SettingsViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    BackHandler {
        if (settingsViewModel.settings.value.fingerprint) {
            (context as? ComponentActivity)?.finish()
        } else {
            navController.navigateUp()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.1f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Fingerprint,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(2f)
        )
        LaunchedEffect(Unit) {
            customizedPrompt(context, settingsViewModel, navController)
        }
    }
}

fun customizedPrompt(
    context: Context,
    settingsViewModel: SettingsViewModel,
    navController: NavController
) {
    showBiometricPrompt(
        context,
        context as AppCompatActivity,
        onAuthError = {
            customizedPrompt(context, settingsViewModel, navController)
        },
        onAuthSuccess = {
            settingsViewModel.update(
                settingsViewModel.settings.value.copy(
                    passcode = null,
                    fingerprint = true,
                    pattern = null
                )
            )
            settingsViewModel.updateDefaultRoute(SettingRoutes.LockScreen.createRoute(null),)
            navController.navigate(HomeRoutes.Home.route) { popUpToTop(navController) }
        }
    )
}

fun showBiometricPrompt(
    context: Context,
    activity: AppCompatActivity,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit
) {
    val executor: Executor = ContextCompat.getMainExecutor(activity)

    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onAuthError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onAuthError("Authentication failed. Please try again.")
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(context.getString(R.string.lock_fingerprint))
        .setSubtitle(context.getString(R.string.app_name))
        .setNegativeButtonText(context.getString(R.string.common_cancel))
        .setConfirmationRequired(true)
        .build()

    biometricPrompt.authenticate(promptInfo)
}