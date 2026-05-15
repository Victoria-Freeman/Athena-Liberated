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

package com.kin.athena.domain.repository

import com.kin.athena.domain.model.CustomDomain
import kotlinx.coroutines.flow.Flow

interface CustomDomainRepository {
    
    fun getAllowlistDomains(): Flow<List<CustomDomain>>
    
    fun getBlocklistDomains(): Flow<List<CustomDomain>>
    
    fun getEnabledAllowlistDomains(): Flow<List<CustomDomain>>
    
    fun getEnabledBlocklistDomains(): Flow<List<CustomDomain>>
    
    fun getAllDomains(): Flow<List<CustomDomain>>
    
    suspend fun getDomainById(id: Long): CustomDomain?
    
    suspend fun isDomainExists(domain: String, isAllowlist: Boolean): Boolean
    
    suspend fun insertDomain(domain: CustomDomain): Long
    
    suspend fun insertDomains(domains: List<CustomDomain>)
    
    suspend fun updateDomain(domain: CustomDomain)
    
    suspend fun deleteDomain(domain: CustomDomain)
    
    suspend fun deleteDomainById(id: Long)
    
    suspend fun deleteAllDomainsByType(isAllowlist: Boolean)
    
    suspend fun updateDomainEnabled(id: Long, isEnabled: Boolean)
    
    suspend fun getAllowlistCount(): Int
    
    suspend fun getBlocklistCount(): Int
    
    suspend fun getEnabledAllowlistCount(): Int
    
    suspend fun getEnabledBlocklistCount(): Int
}