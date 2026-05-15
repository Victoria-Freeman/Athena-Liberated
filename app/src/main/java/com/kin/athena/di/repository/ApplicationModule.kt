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

import android.app.Application
import com.kin.athena.data.local.provider.DatabaseProvider
import com.kin.athena.data.repository.ApplicationRepositoryImpl
import com.kin.athena.domain.repository.ApplicationRepository
import com.kin.athena.domain.usecase.application.AddApplication
import com.kin.athena.domain.usecase.application.AddApplications
import com.kin.athena.domain.usecase.application.ApplicationUseCases
import com.kin.athena.domain.usecase.application.CheckApplicationExists
import com.kin.athena.domain.usecase.application.DeleteApplication
import com.kin.athena.domain.usecase.application.GetApplication
import com.kin.athena.domain.usecase.application.GetApplications
import com.kin.athena.domain.usecase.application.GetExistingPackageIds
import com.kin.athena.domain.usecase.application.GetFilteredApplications
import com.kin.athena.domain.usecase.application.ObserveApplication
import com.kin.athena.domain.usecase.application.UpdateApplication
import com.kin.athena.domain.usecase.application.UpdatePinnedStatus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideDatabaseProvider(application: Application): DatabaseProvider {
        return DatabaseProvider(application)
    }


    @Provides
    @Singleton
    fun provideApplicationRepository(provider: DatabaseProvider): ApplicationRepository {
        return ApplicationRepositoryImpl(provider)
    }

    @Provides
    @Singleton
    fun provideApplicationUseCases(repository: ApplicationRepository): ApplicationUseCases {
        return ApplicationUseCases(
            addApplication = AddApplication(repository),
            addApplications = AddApplications(repository),
            deleteApplication = DeleteApplication(repository),
            getApplication = GetApplication(repository),
            observeApplication = ObserveApplication(repository),
            getApplications = GetApplications(repository),
            updateApplication = UpdateApplication(repository),
            checkApplicationExists = CheckApplicationExists(repository),
            getExistingPackageIds = GetExistingPackageIds(repository),
            getFilteredApplications = GetFilteredApplications(repository),
            updatePinnedStatus = UpdatePinnedStatus(repository)
        )
    }
}