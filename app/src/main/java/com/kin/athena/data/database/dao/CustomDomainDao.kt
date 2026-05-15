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

package com.kin.athena.data.database.dao

import androidx.room.*
import com.kin.athena.data.database.entity.CustomDomainEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomDomainDao {
    
    @Query("SELECT * FROM custom_domains WHERE is_allowlist = :isAllowlist ORDER BY created_at DESC")
    fun getDomainsByType(isAllowlist: Boolean): Flow<List<CustomDomainEntity>>
    
    @Query("SELECT * FROM custom_domains WHERE is_allowlist = :isAllowlist AND is_enabled = 1 ORDER BY created_at DESC")
    fun getEnabledDomainsByType(isAllowlist: Boolean): Flow<List<CustomDomainEntity>>
    
    @Query("SELECT * FROM custom_domains ORDER BY created_at DESC")
    fun getAllDomains(): Flow<List<CustomDomainEntity>>
    
    @Query("SELECT * FROM custom_domains WHERE id = :id")
    suspend fun getDomainById(id: Long): CustomDomainEntity?
    
    @Query("SELECT EXISTS(SELECT 1 FROM custom_domains WHERE domain = :domain AND is_allowlist = :isAllowlist)")
    suspend fun isDomainExists(domain: String, isAllowlist: Boolean): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDomain(domain: CustomDomainEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDomains(domains: List<CustomDomainEntity>)
    
    @Update
    suspend fun updateDomain(domain: CustomDomainEntity)
    
    @Delete
    suspend fun deleteDomain(domain: CustomDomainEntity)
    
    @Query("DELETE FROM custom_domains WHERE id = :id")
    suspend fun deleteDomainById(id: Long)
    
    @Query("DELETE FROM custom_domains WHERE is_allowlist = :isAllowlist")
    suspend fun deleteAllDomainsByType(isAllowlist: Boolean)
    
    @Query("UPDATE custom_domains SET is_enabled = :isEnabled WHERE id = :id")
    suspend fun updateDomainEnabled(id: Long, isEnabled: Boolean)
    
    @Query("SELECT COUNT(*) FROM custom_domains WHERE is_allowlist = :isAllowlist")
    suspend fun getDomainCountByType(isAllowlist: Boolean): Int
    
    @Query("SELECT COUNT(*) FROM custom_domains WHERE is_allowlist = :isAllowlist AND is_enabled = 1")
    suspend fun getEnabledDomainCountByType(isAllowlist: Boolean): Int
}