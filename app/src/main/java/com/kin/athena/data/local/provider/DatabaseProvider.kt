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
package com.kin.athena.data.local.provider

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kin.athena.core.utils.constants.AppConstants
import com.kin.athena.data.local.dao.ApplicationDao
import com.kin.athena.data.local.dao.LogDao
import com.kin.athena.data.local.dao.NetworkFilterDao
import com.kin.athena.data.database.dao.CustomDomainDao
import com.kin.athena.data.local.database.AppDatabase

class DatabaseProvider(private val application: Application) {

    @Volatile
    private var database: AppDatabase? = null

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add the uses_google_play_services column to applications table
            database.execSQL("ALTER TABLE applications ADD COLUMN uses_google_play_services INTEGER NOT NULL DEFAULT 0")
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add new columns for professional architecture
            database.execSQL("ALTER TABLE applications ADD COLUMN display_name TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE applications ADD COLUMN last_updated INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
            database.execSQL("ALTER TABLE applications ADD COLUMN requires_network INTEGER NOT NULL DEFAULT 1")
            
            // Update display_name for existing apps using package_id as fallback
            database.execSQL("UPDATE applications SET display_name = package_id WHERE display_name = ''")
            // Set last_updated to current time for all existing apps
            database.execSQL("UPDATE applications SET last_updated = ${System.currentTimeMillis()}")
            
            // Create indexes for better performance
            database.execSQL("CREATE INDEX IF NOT EXISTS index_applications_display_name ON applications(display_name)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_applications_package_id ON applications(package_id)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_applications_uid ON applications(uid)")
        }
    }
    
    private val MIGRATION_4_8 = object : Migration(4, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Fix any remaining empty display names (from MIGRATION_4_5)
            database.execSQL("UPDATE applications SET display_name = package_id WHERE display_name = '' OR display_name IS NULL")

            // Add bypass_vpn column for VPN tunnel exclusion (from MIGRATION_5_6)
            database.execSQL("ALTER TABLE applications ADD COLUMN bypass_vpn INTEGER NOT NULL DEFAULT 0")

            // Create custom_domains table for domain management (from MIGRATION_6_7 & 7_8 combined)
            database.execSQL("DROP TABLE IF EXISTS custom_domains")
            database.execSQL("""
                CREATE TABLE custom_domains (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    domain TEXT NOT NULL,
                    description TEXT NOT NULL,
                    is_regex INTEGER NOT NULL,
                    is_allowlist INTEGER NOT NULL,
                    created_at INTEGER NOT NULL,
                    is_enabled INTEGER NOT NULL
                )
            """)
        }
    }

    private val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add is_pinned column for pinned apps feature
            database.execSQL("ALTER TABLE applications ADD COLUMN is_pinned INTEGER NOT NULL DEFAULT 0")
        }
    }

    @Synchronized
    fun instance(): AppDatabase {
        return database ?: synchronized(this) {
            database ?: buildDatabase().also { database = it }
        }
    }

    private fun buildDatabase(): AppDatabase {
        return Room.databaseBuilder(application.applicationContext,
            AppDatabase::class.java,
            AppConstants.DatabaseConstants.DATABASE_NAME)
            .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_8, MIGRATION_8_9)
            .build()
    }

    @Synchronized
    fun close() {
        database?.close()
        database = null
    }

    fun applicationDao(): ApplicationDao {
        return instance().packageDao()
    }

    fun logDao(): LogDao {
        return instance().logDao()
    }

    fun networkFilterDao(): NetworkFilterDao {
        return instance().blockedDao()
    }

    fun customDomainDao(): CustomDomainDao {
        return instance().customDomainDao()
    }
}
