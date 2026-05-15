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

import com.kin.athena.core.logging.Logger
import java.io.BufferedReader
import java.io.InputStreamReader

class ShizukuFirewallUserService : IShizukuFirewallService.Stub() {

    override fun destroy() {
        Logger.info("ShizukuFirewallUserService: destroy() called")
        System.exit(0)
    }

    override fun enableFirewallChain(): Boolean {
        Logger.info("ShizukuFirewallUserService: enableFirewallChain() called")
        
        return try {
            val command = "cmd connectivity set-chain3-enabled true"
            Logger.debug("Executing command: $command")
            val result = executeCommand(command)
            Logger.debug("Command result: $result")
            
            val success = !result.contains("error") && !result.contains("failed") && !result.contains("Unknown command")
            Logger.info("ShizukuFirewallUserService: enableFirewallChain() result: $success")
            success
        } catch (e: Exception) {
            Logger.error("ShizukuFirewallUserService: enableFirewallChain() failed: ${e.message}")
            false
        }
    }

    override fun disableFirewallChain(): Boolean {
        Logger.info("ShizukuFirewallUserService: disableFirewallChain() called")
        
        return try {
            // First disable the firewall chain to immediately stop blocking
            Logger.debug("Disabling firewall chain first...")
            val disableCommand = "cmd connectivity set-chain3-enabled false"
            Logger.debug("Executing command: $disableCommand")
            val disableResult = executeCommand(disableCommand)
            Logger.debug("Disable command result: $disableResult")
            
            // Then unblock all previously blocked apps by enabling networking for all packages
            Logger.debug("Unblocking all apps after disabling firewall chain...")
            unblockAllApps()
            
            val success = !disableResult.contains("error") && !disableResult.contains("failed") && !disableResult.contains("Unknown command")
            Logger.info("ShizukuFirewallUserService: disableFirewallChain() result: $success")
            success
        } catch (e: Exception) {
            Logger.error("ShizukuFirewallUserService: disableFirewallChain() failed: ${e.message}")
            false
        }
    }

    private fun unblockAllApps() {
        try {
            Logger.debug("Getting list of all installed packages...")
            val listPackagesCommand = "pm list packages"
            val packagesResult = executeCommand(listPackagesCommand)
            
            if (packagesResult.isNotEmpty()) {
                val packages = packagesResult.lines()
                    .filter { it.startsWith("package:") && it.length > 8 }
                    .map { it.substring(8).trim() } // Remove "package:" prefix and trim whitespace
                    .filter { it.isNotBlank() }
                
                Logger.info("Found ${packages.size} packages, unblocking all...")
                
                var successCount = 0
                var errorCount = 0
                
                packages.forEach { packageName ->
                    try {
                        val unblockCommand = "cmd connectivity set-package-networking-enabled true $packageName"
                        val result = executeCommand(unblockCommand)
                        
                        if (result.contains("Enabled networking") || result.isEmpty()) {
                            successCount++
                            Logger.debug("✓ Unblocked $packageName")
                        } else {
                            errorCount++
                            Logger.debug("✗ Failed to unblock $packageName: $result")
                        }
                    } catch (e: Exception) {
                        errorCount++
                        Logger.debug("✗ Exception unblocking $packageName: ${e.message}")
                    }
                }
                
                Logger.info("Finished unblocking packages: $successCount successful, $errorCount failed")
            } else {
                Logger.warn("No packages found to unblock")
            }
        } catch (e: Exception) {
            Logger.error("Failed to unblock all apps: ${e.message}")
        }
    }

    override fun isFirewallChainEnabled(): Boolean {
        Logger.debug("ShizukuFirewallUserService: isFirewallChainEnabled() called")
        
        return try {
            val command = "cmd connectivity get-chain3-enabled"
            Logger.debug("Executing command: $command")
            val result = executeCommand(command)
            Logger.debug("Command result: $result")
            
            val enabled = result.contains("true") && !result.contains("Unknown command")
            Logger.debug("ShizukuFirewallUserService: isFirewallChainEnabled() result: $enabled")
            enabled
        } catch (e: Exception) {
            Logger.error("ShizukuFirewallUserService: isFirewallChainEnabled() failed: ${e.message}")
            false
        }
    }

    override fun setPackageNetworking(packageName: String, enabled: Boolean): Boolean {
        Logger.info("ShizukuFirewallUserService: setPackageNetworking($packageName, $enabled) called")
        
        return try {
            val command = "cmd connectivity set-package-networking-enabled $enabled $packageName"
            Logger.debug("Executing command: $command")
            val result = executeCommand(command)
            Logger.debug("Command result: $result")
            
            val success = !result.contains("error") && !result.contains("failed") && !result.contains("Unknown command")
            Logger.info("ShizukuFirewallUserService: setPackageNetworking($packageName, $enabled) result: $success")
            success
        } catch (e: Exception) {
            Logger.error("ShizukuFirewallUserService: setPackageNetworking($packageName, $enabled) failed: ${e.message}")
            false
        }
    }

    override fun getPackageNetworking(packageName: String): Boolean {
        Logger.debug("ShizukuFirewallUserService: getPackageNetworking($packageName) called")
        
        return try {
            val command = "cmd connectivity get-package-networking-enabled $packageName"
            Logger.debug("Executing command: $command")
            val result = executeCommand(command)
            Logger.debug("Command result: $result")
            
            val enabled = result.contains("true") && !result.contains("Unknown command")
            Logger.debug("ShizukuFirewallUserService: getPackageNetworking($packageName) result: $enabled")
            enabled
        } catch (e: Exception) {
            Logger.error("ShizukuFirewallUserService: getPackageNetworking($packageName) failed: ${e.message}")
            true // Default to enabled on error
        }
    }

    override fun executeCommand(command: String): String {
        Logger.debug("ShizukuFirewallUserService: executeCommand($command) called")
        
        return try {
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            
            val output = StringBuilder()
            var line: String?
            
            // Read stdout
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            
            // Read stderr
            while (errorReader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            
            val exitCode = process.waitFor()
            reader.close()
            errorReader.close()
            
            val result = output.toString().trim()
            Logger.debug("ShizukuFirewallUserService: executeCommand result (exit: $exitCode): $result")
            result
        } catch (e: Exception) {
            Logger.error("ShizukuFirewallUserService: executeCommand failed: ${e.message}")
            ""
        }
    }
}