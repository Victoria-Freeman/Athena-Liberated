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

package com.kin.athena.data.cache

import com.kin.athena.core.logging.Logger
import com.kin.athena.presentation.screens.settings.subSettings.dns.hosts.RuleDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DomainCacheService @Inject constructor(
    private val ruleDatabase: RuleDatabase
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized
    
    private var lastInitializationTime = 0L
    private val CACHE_VALIDITY_MS = 5 * 60 * 1000L // 5 minutes
    
    fun initializeGlobally() {
        val currentTime = System.currentTimeMillis()

        // Skip if recently initialized and still valid
        if (_isInitialized.value && (currentTime - lastInitializationTime) < CACHE_VALIDITY_MS) {
            Logger.debug("DomainCache: Using cached data (${(currentTime - lastInitializationTime) / 1000}s old)")
            return
        }

        if (_isLoading.value) {
            Logger.debug("DomainCache: Already loading, skipping duplicate request")
            return
        }

        _isLoading.value = true
        Logger.info("DomainCache: Starting background domain initialization")
        serviceScope.launch {
            try {
                ruleDatabase.initialize()
                _isInitialized.value = true
                lastInitializationTime = currentTime
                Logger.info("DomainCache: Background initialization completed")
            } catch (e: Exception) {
                Logger.error("DomainCache: Failed to initialize domains", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun invalidateCache() {
        Logger.info("DomainCache: Cache invalidated, will reload on next access")
        _isInitialized.value = false
        lastInitializationTime = 0L
    }
    
    fun forceRefresh() {
        Logger.info("DomainCache: Force refresh requested")
        _isInitialized.value = false
        lastInitializationTime = 0L
        initializeGlobally()
    }
}