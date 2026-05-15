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

package com.kin.athena.service.utils.manager

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.kin.athena.core.logging.Logger
import com.kin.athena.service.utils.notifications.NetworkSpeedMonitorService

object NetworkSpeedManager {

    fun start(context: Context) {
        try {
            if (!isServiceRunning(context)) {
                val intent = Intent(context, NetworkSpeedMonitorService::class.java).apply {
                    action = NetworkSpeedMonitorService.ACTION_START
                }
                ContextCompat.startForegroundService(context, intent)
                Logger.info("NetworkSpeedMonitorService started")
            } else {
                Logger.info("NetworkSpeedMonitorService is already running")
            }
        } catch (e: Exception) {
            Logger.error("Failed to start NetworkSpeedMonitorService: ${e.message}", e)
        }
    }

    fun stop(context: Context) {
        try {
            if (isServiceRunning(context)) {
                val intent = Intent(context, NetworkSpeedMonitorService::class.java).apply {
                    action = NetworkSpeedMonitorService.ACTION_STOP
                }
                context.startService(intent)
                Logger.info("NetworkSpeedMonitorService stop requested")
            } else {
                Logger.info("NetworkSpeedMonitorService is not running")
            }
        } catch (e: Exception) {
            Logger.error("Failed to stop NetworkSpeedMonitorService: ${e.message}", e)
        }
    }

    fun isServiceRunning(context: Context): Boolean {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)
            runningServices.any { service ->
                service.service.className == NetworkSpeedMonitorService::class.java.name
            }
        } catch (e: Exception) {
            Logger.error("Failed to check if NetworkSpeedMonitorService is running: ${e.message}", e)
            false
        }
    }

    fun toggle(context: Context) {
        if (isServiceRunning(context)) {
            stop(context)
        } else {
            start(context)
        }
    }

}