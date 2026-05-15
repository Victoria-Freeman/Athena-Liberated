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

import com.kin.athena.domain.model.Application
import com.kin.athena.domain.repository.ApplicationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveApplication @Inject constructor(
    private val packageRepository: ApplicationRepository
) {
    fun execute(applicationID: String): Flow<Application?> {
        return packageRepository.observeApplicationByID(applicationID)
    }
}
