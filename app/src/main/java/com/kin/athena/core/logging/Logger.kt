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

package com.kin.athena.core.logging

import android.util.Log

object Logger {
    enum class LogLevel { DEBUG, INFO, WARN, ERROR }

    private var currentLogLevel: LogLevel = LogLevel.DEBUG
    fun debug(message: String) = log(LogLevel.DEBUG, message)
    fun info(message: String) = log(LogLevel.INFO, message)
    fun warn(message: String) = log(LogLevel.WARN, message)
    fun warning(message: String) = log(LogLevel.WARN, message)
    fun error(message: String, throwable: Throwable? = null) = log(LogLevel.ERROR, message, throwable)

    private fun log(level: LogLevel, message: String, throwable: Throwable? = null) {
        if (currentLogLevel <= level) {
            when (level) {
                LogLevel.DEBUG -> Log.d("AthenaLiberated", "[ + ] $message", throwable)
                LogLevel.INFO -> Log.i("AthenaLiberated", "[ + ] $message", throwable)
                LogLevel.WARN -> Log.w("AthenaLiberated", "[ ! ] $message", throwable)
                LogLevel.ERROR -> Log.e("AthenaLiberated", "[ ! ] $message", throwable)
            }
        }
    }
}