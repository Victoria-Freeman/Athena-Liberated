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
import com.kin.athena.domain.model.Log
import com.kin.athena.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogRepositoryImpl @Inject constructor(
    private val provider: DatabaseProvider
) : LogRepository {
    override suspend fun insertLog(log: Log) {
        provider.logDao().insertLog(log)
    }

    override suspend fun deleteLogById(id: Int) {
        provider.logDao().deleteLogById(id)
    }

    override suspend fun deleteLogs() {
        provider.logDao().deleteLogs()
    }

    override suspend fun getLogById(id: Int): Log? {
        return provider.logDao().getLogById(id)
    }

    override suspend fun getAllLogs(): Flow<List<Log>> {
        return provider.logDao().getAllLogs()
    }
}