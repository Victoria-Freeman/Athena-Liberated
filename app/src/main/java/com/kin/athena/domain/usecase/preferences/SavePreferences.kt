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

package com.kin.athena.domain.usecase.preferences

import com.kin.athena.core.utils.Result
import com.kin.athena.core.utils.Error
import com.kin.athena.domain.model.Settings
import com.kin.athena.domain.repository.SettingsRepository
import javax.inject.Inject

class SavePreferences @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun execute(settings: Settings): Result<Unit, Error> {
        return try {
            settingsRepository.saveSettings(settings)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(Error.ServerError(e.message ?: "Error while saving preferences"))
        }
    }
}