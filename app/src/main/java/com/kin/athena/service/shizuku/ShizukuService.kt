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

package com.kin.athena.service.shizuku

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import com.kin.athena.BuildConfig
import com.kin.athena.core.logging.Logger
import com.kin.athena.domain.model.Application
import com.kin.athena.service.utils.manager.FirewallService
import rikka.shizuku.Shizuku
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuService @Inject constructor() : FirewallService {
    
    companion object {
        private const val REQUEST_CODE_SHIZUKU = 1001
    }
    
    private var isShizukuAvailable = false
    private var isShizukuPermissionGranted = false
    private var shizukuFirewallService: IShizukuFirewallService? = null
    private var isServiceBound = false
    
    private val serviceArgs by lazy {
        Shizuku.UserServiceArgs(
            ComponentName(BuildConfig.APPLICATION_ID, ShizukuFirewallUserService::class.java.name)
        )
            .processNameSuffix("firewall_service")
            .debuggable(BuildConfig.DEBUG)
            .version(BuildConfig.VERSION_CODE)
    }
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Logger.debug("Shizuku UserService connected")
            shizukuFirewallService = IShizukuFirewallService.Stub.asInterface(service)
            isServiceBound = true
            
            // Initialize firewall chain
            try {
                shizukuFirewallService?.enableFirewallChain()
                Logger.debug("Firewall chain enabled via Shizuku")
            } catch (e: Exception) {
                Logger.error("Failed to enable firewall chain: ${e.message}")
            }
        }
        
        override fun onServiceDisconnected(name: ComponentName) {
            Logger.debug("Shizuku UserService disconnected")
            shizukuFirewallService = null
            isServiceBound = false
        }
    }
    
    init {
        checkShizukuAvailability()
    }
    
    private fun checkShizukuAvailability() {
        try {
            isShizukuAvailable = Shizuku.pingBinder()
            if (isShizukuAvailable) {
                isShizukuPermissionGranted = Shizuku.checkSelfPermission() == 0
                Logger.debug("Shizuku available: $isShizukuAvailable, Permission granted: $isShizukuPermissionGranted")
            } else {
                Logger.warn("Shizuku service not available")
            }
        } catch (e: Exception) {
            Logger.error("Failed to check Shizuku availability: ${e.message}")
            isShizukuAvailable = false
            isShizukuPermissionGranted = false
        }
    }
    
    fun isShizukuReady(): Boolean {
        checkShizukuAvailability()
        return isShizukuAvailable && isShizukuPermissionGranted
    }
    
    fun requestShizukuPermission() {
        if (isShizukuAvailable && !isShizukuPermissionGranted) {
            try {
                Shizuku.requestPermission(REQUEST_CODE_SHIZUKU)
            } catch (e: Exception) {
                Logger.error("Failed to request Shizuku permission: ${e.message}")
            }
        }
    }
    
    private fun bindUserService() {
        if (!isShizukuReady()) {
            Logger.warn("Cannot bind UserService - Shizuku not ready")
            return
        }
        
        if (isServiceBound) {
            Logger.debug("UserService already bound")
            return
        }
        
        try {
            if (Shizuku.getVersion() >= 10) {
                Shizuku.bindUserService(serviceArgs, serviceConnection)
                Logger.debug("Binding Shizuku UserService")
            } else {
                Logger.error("Shizuku version too old (${Shizuku.getVersion()}), need >= 10")
            }
        } catch (e: Exception) {
            Logger.error("Failed to bind UserService: ${e.message}")
        }
    }
    
    private fun unbindUserService() {
        if (isServiceBound) {
            try {
                Shizuku.unbindUserService(serviceArgs, serviceConnection, true)
                Logger.debug("Unbound Shizuku UserService")
            } catch (e: Exception) {
                Logger.error("Failed to unbind UserService: ${e.message}")
            } finally {
                isServiceBound = false
                shizukuFirewallService = null
            }
        }
    }
    
    override fun updateRules(application: Application?) {
        if (!isShizukuReady()) {
            Logger.warn("Shizuku not ready, cannot update rules. Available: $isShizukuAvailable, Permission: $isShizukuPermissionGranted")
            return
        }
        
        application?.let { app ->
            try {
                // Determine if app should have network access
                // If either internet or cellular is blocked, block all network access
                val allowNetworkAccess = app.internetAccess && app.cellularAccess
                
                // Use UserService if bound, otherwise execute directly
                val success = if (isServiceBound && shizukuFirewallService != null) {
                    shizukuFirewallService?.setPackageNetworking(app.packageID, allowNetworkAccess) ?: false
                } else {
                    // Direct execution fallback
                    executeConnectivityCommand("cmd connectivity set-package-networking-enabled $allowNetworkAccess ${app.packageID}")
                }
                
                if (success) {
                    Logger.debug("Successfully updated network rules for ${app.packageID} (UID: ${app.uid}) to $allowNetworkAccess")
                } else {
                    Logger.warn("Failed to update network rules for ${app.packageID} (UID: ${app.uid})")
                }
            } catch (e: SecurityException) {
                Logger.error("Security error updating rules for ${app.packageID}: ${e.message}")
                checkShizukuAvailability()
            } catch (e: Exception) {
                Logger.error("Error updating rules for ${app.packageID}: ${e.message}")
            }
        } ?: run {
            Logger.warn("Application is null, cannot update rules")
        }
    }
    
    private fun executeConnectivityCommand(command: String): Boolean {
        return try {
            if (!isShizukuReady()) {
                Logger.error("Cannot execute command - Shizuku not ready: $command")
                return false
            }
            
            Logger.debug("Executing connectivity command via Shizuku: $command")
            
            // Execute shell command with elevated privileges through Shizuku
            val result = shizukuFirewallService?.executeCommand(command)
            if (result != null && !result.startsWith("Error")) {
                Logger.debug("Command executed successfully: $result")
                true
            } else {
                Logger.warn("Command failed: $result")
                false
            }
        } catch (e: Exception) {
            Logger.error("Error executing connectivity command: $command, Error: ${e.message}")
            false
        }
    }
    
    override fun startService(context: Context) {
        if (!isShizukuReady()) {
            Logger.warn("Shizuku not ready, cannot start service")
            return
        }
        
        Logger.info("Starting Shizuku firewall service")
        
        // Enable the firewall chain
        val chainEnabled = executeConnectivityCommand("cmd connectivity set-chain3-enabled true")
        if (chainEnabled) {
            Logger.info("Firewall chain enabled successfully")
        } else {
            Logger.warn("Failed to enable firewall chain")
        }
        
        // Try to bind UserService for better performance (optional)
        bindUserService()
        
        Logger.info("Shizuku firewall service started")
    }
    
    override fun stopService(context: Context) {
        Logger.info("Stopping Shizuku firewall service")
        
        try {
            // Disable the firewall chain when stopping
            val chainDisabled = if (isServiceBound && shizukuFirewallService != null) {
                shizukuFirewallService?.disableFirewallChain() ?: false
            } else {
                executeConnectivityCommand("cmd connectivity set-chain3-enabled false")
            }
            
            if (chainDisabled) {
                Logger.info("Firewall chain disabled successfully")
            } else {
                Logger.warn("Failed to disable firewall chain")
            }
        } catch (e: Exception) {
            Logger.error("Error disabling firewall chain: ${e.message}")
        }
        
        unbindUserService()
        Logger.info("Shizuku firewall service stopped")
    }
    
    override fun updateLogs(enabled: Boolean) {
        Logger.debug("Shizuku service: Log updates not implemented yet")
    }
    
    override fun updateScreen(value: Boolean) {
        Logger.debug("Shizuku service: Screen updates not implemented yet")
    }
    
    override suspend fun updateDomains(progressCallback: (suspend (Int) -> Unit)?) {
        Logger.debug("Shizuku service: Domain updates not implemented yet")
    }
    
    override fun updateHttpSettings() {
        Logger.debug("Shizuku service: HTTP settings updates not implemented yet")
    }
    
    override fun setDnsBlocking(enabled: Boolean) {
        Logger.debug("Shizuku service: DNS blocking not implemented yet")
    }
    
    override fun isDnsBlockingEnabled(): Boolean {
        return false
    }
}