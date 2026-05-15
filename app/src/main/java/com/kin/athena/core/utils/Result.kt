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

package com.kin.athena.core.utils

import com.kin.athena.core.logging.Logger

sealed class Result<out T, out E> {
    data class Success<out T>(val data: T) : Result<T, Nothing>()
    data class Failure<out E>(val error: E) : Result<Nothing, E>()

    inline fun <R> fold(
        ifFailure: (E) -> R = { defaultFailureHandler(it) },
        ifSuccess: (T) -> R
    ): R = when (this) {
        is Success -> ifSuccess(data)
        is Failure -> ifFailure(error)
    }

    companion object {
        inline fun <E> defaultFailureHandler(error: E): Nothing {
            if (error is Throwable) {
                Logger.error(error.stackTraceToString())
            } else {
                Logger.error(error.toString())
            }
            throw RuntimeException("Unhandled Result Failure: $error")
        }
    }
}