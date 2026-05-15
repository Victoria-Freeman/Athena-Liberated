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

package com.kin.athena.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kin.athena.core.utils.constants.AppConstants
import com.kin.athena.data.local.dao.ApplicationDao
import com.kin.athena.data.local.dao.LogDao
import com.kin.athena.data.local.dao.NetworkFilterDao
import com.kin.athena.data.database.dao.CustomDomainDao
import com.kin.athena.data.database.entity.CustomDomainEntity
import com.kin.athena.domain.model.Application
import com.kin.athena.domain.model.Ip
import com.kin.athena.domain.model.Log

@Database(
    entities = [Application::class, Log::class, Ip::class, CustomDomainEntity::class],
    version = AppConstants.DatabaseConstants.DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun packageDao(): ApplicationDao
    abstract fun logDao(): LogDao
    abstract fun blockedDao() : NetworkFilterDao
    abstract fun customDomainDao(): CustomDomainDao
}