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

package com.kin.athena.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kin.athena.core.utils.constants.AppConstants
import com.kin.athena.domain.model.Application
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(application: Application)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(applications: List<Application>)

    @Update
    suspend fun update(application: Application)

    @Delete
    suspend fun delete(application: Application)

    @Query("SELECT * FROM applications ORDER BY display_name COLLATE NOCASE")
    suspend fun getAllApplications(): List<Application>

    @Query("SELECT * FROM applications WHERE package_id = :packageId")
    suspend fun getApplicationByID(packageId: String): Application?

    @Query("SELECT * FROM applications WHERE package_id = :packageId")
    fun observeApplicationByID(packageId: String): Flow<Application?>

    @Query("SELECT EXISTS(SELECT 1 FROM applications WHERE package_id = :packageId)")
    suspend fun isPackageIdExists(packageId: String): Boolean

    @Query("SELECT package_id FROM applications WHERE package_id IN (:packageIds)")
    suspend fun getExistingPackageIds(packageIds: List<String>): List<String>

    @Query("""
        SELECT * FROM applications
        WHERE (:showSystemPackages = 1 OR system_app = 0)
        AND (:showOfflinePackages = 1 OR requires_network = 1)
        AND package_id != 'com.kin.athena'
        AND (
            :searchQuery = '' OR
            display_name LIKE '%' || :searchQuery || '%' OR
            package_id LIKE '%' || :searchQuery || '%' OR
            CAST(uid AS TEXT) LIKE '%' || :searchQuery || '%'
        )
        ORDER BY is_pinned DESC, display_name COLLATE NOCASE
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getFilteredApplications(
        showSystemPackages: Boolean,
        showOfflinePackages: Boolean,
        searchQuery: String,
        limit: Int,
        offset: Int
    ): List<Application>

    @Query("""
        SELECT COUNT(*) FROM applications
        WHERE (:showSystemPackages = 1 OR system_app = 0)
        AND (:showOfflinePackages = 1 OR requires_network = 1)
        AND package_id != 'com.kin.athena'
        AND (
            :searchQuery = '' OR
            display_name LIKE '%' || :searchQuery || '%' OR
            package_id LIKE '%' || :searchQuery || '%' OR
            CAST(uid AS TEXT) LIKE '%' || :searchQuery || '%'
        )
    """)
    suspend fun getFilteredApplicationsCount(
        showSystemPackages: Boolean,
        showOfflinePackages: Boolean,
        searchQuery: String
    ): Int

    @Query("UPDATE applications SET is_pinned = :isPinned WHERE package_id = :packageId")
    suspend fun updatePinnedStatus(packageId: String, isPinned: Boolean)
}