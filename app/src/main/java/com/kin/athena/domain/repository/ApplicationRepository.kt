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

import com.kin.athena.domain.model.Application
import kotlinx.coroutines.flow.Flow


interface ApplicationRepository {

    suspend fun getAllApplications(): List<Application>

    suspend fun insertApplication(application: Application)

    suspend fun insertApplications(applications: List<Application>)

    suspend fun updateApplication(application: Application)

    suspend fun deleteApplication(application: Application)

    suspend fun getApplicationByID(packageId: String): Application?

    fun observeApplicationByID(packageId: String): Flow<Application?>

    suspend fun isPackageIdExists(packageId: String): Boolean
    
    suspend fun getExistingPackageIds(packageIds: List<String>): List<String>
    
    suspend fun getFilteredApplications(
        showSystemPackages: Boolean,
        showOfflinePackages: Boolean,
        searchQuery: String,
        limit: Int,
        offset: Int
    ): List<Application>
    
    suspend fun getFilteredApplicationsCount(
        showSystemPackages: Boolean,
        showOfflinePackages: Boolean,
        searchQuery: String
    ): Int

    suspend fun updatePinnedStatus(packageId: String, isPinned: Boolean)
}