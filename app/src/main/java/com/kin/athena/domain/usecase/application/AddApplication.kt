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

class AddApplication @Inject constructor(
    private val packageRepository: ApplicationRepository
) {
    suspend fun execute(application: Application): Result<Unit, Error> {
        return try {
            packageRepository.insertApplication(application)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(Error.ServerError(e.message ?: "Error while adding package ${application.packageID}"))
        }
    }
}
