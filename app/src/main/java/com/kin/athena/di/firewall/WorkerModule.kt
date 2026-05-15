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

package com.kin.athena.di.firewall

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.kin.athena.presentation.screens.settings.subSettings.dns.hosts.RuleDatabaseUpdateWorker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.MapKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import kotlin.reflect.KClass

@MapKey
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@AssistedFactory
interface RuleDatabaseUpdateWorkerFactory {
    fun create(@Assisted context: Context, @Assisted params: WorkerParameters): RuleDatabaseUpdateWorker
}

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(RuleDatabaseUpdateWorker::class)
    abstract fun bindRuleDatabaseUpdateWorker(
        factory: RuleDatabaseUpdateWorkerFactory
    ): RuleDatabaseUpdateWorkerFactory
}
