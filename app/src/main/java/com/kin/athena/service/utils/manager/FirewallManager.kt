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

package com.kin.athena.service.utils.manager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kin.athena.core.logging.Logger
import com.kin.athena.domain.model.Application
import com.kin.athena.service.firewall.utils.FirewallStatus
import com.kin.athena.service.root.service.RootConnectionService
import com.kin.athena.service.shizuku.ShizukuConnectionService
import com.kin.athena.service.vpn.service.VpnConnectionServer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class FirewallMode {
    ROOT,
    VPN,
    SHIZUKU,
}

interface FirewallService {
    fun updateRules(application: Application?)
    fun startService(context: Context)
    fun stopService(context: Context)
    fun updateLogs(enabled: Boolean)
    fun updateScreen(value: Boolean)
    suspend fun updateDomains(progressCallback: (suspend (Int) -> Unit)? = null)
    fun updateHttpSettings()
    fun setDnsBlocking(enabled: Boolean)
    fun isDnsBlockingEnabled(): Boolean
}

@Singleton
class FirewallStateManager @Inject constructor() {
    var mode: FirewallMode = FirewallMode.VPN
}

@Singleton
class FirewallManager @Inject constructor(
    private val firewallStateManager: FirewallStateManager,
    @ApplicationContext private val context: Context,
    private val rootService: FirewallService,
    private val vpnService: FirewallService,
    private val shizukuService: FirewallService
) {

    private val _rulesLoaded: MutableStateFlow<FirewallStatus> = MutableStateFlow(FirewallStatus.OFFLINE)
    val rulesLoaded: StateFlow<FirewallStatus> get() = _rulesLoaded.asStateFlow()


    var currentService: MutableState<FirewallService?> = mutableStateOf(null)


    init {
        updateService()
        bindService()
    }

    private fun updateService() {
        currentService.value = when(firewallStateManager.mode) {
            FirewallMode.ROOT -> rootService
            FirewallMode.VPN -> vpnService
            FirewallMode.SHIZUKU -> shizukuService
        }
    }

    private var isServiceBound: Boolean = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            currentService.value = when (firewallStateManager.mode) {
                FirewallMode.ROOT -> rootService
                FirewallMode.VPN -> vpnService
                FirewallMode.SHIZUKU -> shizukuService
            }
            isServiceBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            isServiceBound = false
        }
    }

    fun updateScreen(value: Boolean) {
        currentService.value?.updateScreen(value)
    }

    fun updateLogs(enabled: Boolean) {
        currentService.value?.updateLogs(enabled)
    }

    suspend fun updateDomains(progressCallback: (suspend (Int) -> Unit)? = null) {
        currentService.value?.updateDomains(progressCallback)
    }

    fun updateHttpSettings() {
        currentService.value?.updateHttpSettings()
    }

    fun update(state: FirewallStatus) {
        _rulesLoaded.value = state
    }

    private fun bindService() {
        try {
            val intent = when (firewallStateManager.mode) {
                FirewallMode.ROOT -> Intent(context, RootConnectionService::class.java)
                FirewallMode.VPN -> Intent(context, VpnConnectionServer::class.java)
                FirewallMode.SHIZUKU -> Intent(context, ShizukuConnectionService::class.java)
            }
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: IllegalArgumentException) {
            Logger.debug("Cannot bind service: ${e.message}")
        }
    }

    private fun unbindService() {
        if (isServiceBound) {
            context.unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    fun updateFirewallRules(application: Application?) {
        currentService.value?.updateRules(application) ?: Logger.warn("Cannot update rules: Service not available.")
    }

    fun setFirewallMode(mode: FirewallMode) {
        updateService()
        firewallStateManager.mode = mode
    }

    fun startFirewall() {
        updateService()
        bindService()
        currentService.value?.startService(context) ?: Logger.warn("Service not available for start.")
    }

    fun stopFirewall() {
        updateService()
        unbindService()
        currentService.value?.stopService(context) ?: Logger.warn("Service not available for stop.")
    }
    
    fun setDnsBlocking(enabled: Boolean) {
        if (isServiceBound) {
            currentService.value?.setDnsBlocking(enabled) ?: Logger.warn("Cannot set DNS blocking: Service is not bound.")
        } else {
            Logger.warn("Cannot set DNS blocking: Service is not bound.")
        }
    }
    
    fun isDnsBlockingEnabled(): Boolean {
        return if (isServiceBound) {
            currentService.value?.isDnsBlockingEnabled() ?: false
        } else {
            Logger.warn("Cannot get DNS blocking status: Service is not bound.")
            false
        }
    }
}
