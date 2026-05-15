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

package com.kin.athena.di.repository

import com.kin.athena.data.local.provider.DatabaseProvider
import com.kin.athena.data.repository.LogRepositoryImpl
import com.kin.athena.domain.repository.LogRepository
import com.kin.athena.domain.usecase.log.AddLog
import com.kin.athena.domain.usecase.log.DeleteLog
import com.kin.athena.domain.usecase.log.DeleteLogs
import com.kin.athena.domain.usecase.log.GetLog
import com.kin.athena.domain.usecase.log.GetLogs
import com.kin.athena.domain.usecase.log.LogUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LogModule {
    @Provides
    @Singleton
    fun provideLogRepository(provider: DatabaseProvider): LogRepository {
        return LogRepositoryImpl(provider)
    }

    @Provides
    @Singleton
    fun provideLogUseCases(repository: LogRepository): LogUseCases {
        return LogUseCases(
            getLogs = GetLogs(repository),
            deleteLog = DeleteLog(repository),
            getLog = GetLog(repository),
            addLog = AddLog(repository),
            deleteLogs = DeleteLogs(repository)
        )
    }
}