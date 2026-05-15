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

package com.kin.athena.domain.usecase.application

import com.kin.athena.domain.repository.ApplicationRepository
import javax.inject.Inject
import com.kin.athena.core.utils.Result
import com.kin.athena.core.utils.Error
import com.kin.athena.domain.model.Application

data class FilteredApplicationResult(
    val applications: List<Application>,
    val totalCount: Int,
    val hasMore: Boolean
)

class GetFilteredApplications @Inject constructor(
    private val applicationRepository: ApplicationRepository
) {
    suspend fun execute(
        showSystemPackages: Boolean,
        showOfflinePackages: Boolean,
        searchQuery: String,
        limit: Int = Int.MAX_VALUE,
        offset: Int = 0
    ): Result<FilteredApplicationResult, Error> {
        return try {
            val applications = applicationRepository.getFilteredApplications(
                showSystemPackages, showOfflinePackages, searchQuery, limit, offset
            )
            val totalCount = applicationRepository.getFilteredApplicationsCount(
                showSystemPackages, showOfflinePackages, searchQuery
            )
            val hasMore = offset + applications.size < totalCount

            com.kin.athena.core.logging.Logger.info("GetFilteredApplications: offset=$offset, limit=$limit, got ${applications.size} apps, totalCount=$totalCount, hasMore=$hasMore")

            Result.Success(
                FilteredApplicationResult(
                    applications = applications,
                    totalCount = totalCount,
                    hasMore = hasMore
                )
            )
        } catch (e: Exception) {
            Result.Failure(Error.ServerError(e.message ?: "Error while retrieving filtered packages"))
        }
    }
}