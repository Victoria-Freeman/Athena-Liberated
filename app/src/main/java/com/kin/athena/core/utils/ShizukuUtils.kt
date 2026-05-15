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

package com.kin.athena.core.utils

import com.kin.athena.core.logging.Logger
import rikka.shizuku.Shizuku

object ShizukuUtils {
    
    fun isShizukuAvailable(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
    }
    
    fun isShizukuPermissionGranted(): Boolean {
        return try {
            if (isShizukuAvailable()) {
                Shizuku.checkSelfPermission() == 0
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    fun requestShizukuPermission(onPermissionGranted: (() -> Unit)? = null) {
        try {
            if (isShizukuAvailable() && !isShizukuPermissionGranted()) {
                Logger.debug("Requesting Shizuku permission")
                
                // Add a one-time listener for permission result
                val listener = object : Shizuku.OnRequestPermissionResultListener {
                    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                        if (requestCode == 0) {
                            Logger.debug("Shizuku permission result: $grantResult")
                            if (grantResult == 0) { // Permission granted
                                onPermissionGranted?.invoke()
                            }
                            // Remove the listener after handling the result
                            Shizuku.removeRequestPermissionResultListener(this)
                        }
                    }
                }
                
                Shizuku.addRequestPermissionResultListener(listener)
                Shizuku.requestPermission(0)
            } else {
                Logger.debug("Shizuku permission request not needed. Available: ${isShizukuAvailable()}, Granted: ${isShizukuPermissionGranted()}")
                if (isShizukuPermissionGranted()) {
                    onPermissionGranted?.invoke()
                }
            }
        } catch (e: Exception) {
            Logger.error("Failed to request Shizuku permission: ${e.message}")
        }
    }
    
    fun isShizukuReady(): Boolean {
        return isShizukuAvailable() && isShizukuPermissionGranted()
    }
}