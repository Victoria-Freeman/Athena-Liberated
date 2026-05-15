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

package com.kin.athena.core.utils.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.net.Uri
import android.provider.Settings
import com.kin.athena.core.logging.Logger


val Context.notificationManagerCompat get() = NotificationManagerCompat.from(this)

fun Context.doIfNotificationsAllowed(block: NotificationManagerCompat.() -> Unit) = if (hasNotificationsPermission())
    block(notificationManagerCompat)
else
    Unit

@SuppressLint("InlinedApi")
fun Context.hasNotificationsPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
else
    true

fun Context.isFingerprintSupported(): Boolean {
    val biometricManager = BiometricManager.from(this)
    return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false // Hardware exists but no fingerprints enrolled
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> false
        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> false
        BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> false
        BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> false
        else -> false
    }
}

fun Context.requestDisableBatteryOptimization() {
    val powerManager = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:$packageName"))
            startActivity(intent)
        } else {
            Logger.info("Battery optimization is already disabled for this app.")
        }
    } else {
        Logger.error("Battery optimization is not supported on this device.")
    }
}