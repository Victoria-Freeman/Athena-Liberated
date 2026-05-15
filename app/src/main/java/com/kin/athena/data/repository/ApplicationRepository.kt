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

package com.kin.athena.data.repository

import com.kin.athena.data.local.provider.DatabaseProvider
import com.kin.athena.domain.model.Application
import com.kin.athena.domain.repository.ApplicationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ApplicationRepositoryImpl @Inject constructor(
    private val provider: DatabaseProvider
) : ApplicationRepository {

    override suspend fun getAllApplications(): List<Application> {
        return provider.applicationDao().getAllApplications()
    }

    override suspend fun insertApplication(application: Application) {
        provider.applicationDao().insert(application)
    }

    override suspend fun insertApplications(applications: List<Application>) {
        provider.applicationDao().insertAll(applications)
    }

    override suspend fun updateApplication(application: Application) {
        provider.applicationDao().update(application)
    }

    override suspend fun deleteApplication(application: Application) {
        provider.applicationDao().delete(application)
    }

    override suspend fun getApplicationByID(packageId: String): Application? {
        return provider.applicationDao().getApplicationByID(packageId)
    }

    override fun observeApplicationByID(packageId: String): Flow<Application?> {
        return provider.applicationDao().observeApplicationByID(packageId)
    }

    override suspend fun isPackageIdExists(packageId: String): Boolean {
        return provider.applicationDao().isPackageIdExists(packageId)
    }

    override suspend fun getExistingPackageIds(packageIds: List<String>): List<String> {
        return provider.applicationDao().getExistingPackageIds(packageIds)
    }

    override suspend fun getFilteredApplications(
        showSystemPackages: Boolean,
        showOfflinePackages: Boolean,
        searchQuery: String,
        limit: Int,
        offset: Int
    ): List<Application> {
        val results = provider.applicationDao().getFilteredApplications(
            showSystemPackages, showOfflinePackages, searchQuery, limit, offset
        )
        return results
    }

    override suspend fun getFilteredApplicationsCount(
        showSystemPackages: Boolean,
        showOfflinePackages: Boolean,
        searchQuery: String
    ): Int {
        return provider.applicationDao().getFilteredApplicationsCount(
            showSystemPackages, showOfflinePackages, searchQuery
        )
    }

    override suspend fun updatePinnedStatus(packageId: String, isPinned: Boolean) {
        provider.applicationDao().updatePinnedStatus(packageId, isPinned)
    }
}