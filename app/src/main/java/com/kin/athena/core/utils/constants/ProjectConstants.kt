/*
 * Copyright (C) 2025-2026 Vexzure
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

package com.kin.athena.core.utils.constants

object ProjectConstants {
    val SHA_256_SIGNING = setOf(
        "3dac74ea57cbf9d5dd10ac2f443036afd38581588dd4dafa02d230bddb4250dd", // Play Store signature
        "f8388448de6d3eb4831a60aa4194a0524cbb443fe5806090c1a403565f49a06b",  // Github signature
        "628704bf1c86130eff49ad991c1b4dc9006700112e5ebe692da3da43f074d8fd" // Build
    )
    const val DEVELOPER = "Victoria Freeman"

    const val PROJECT_DOWNLOADS = "https://github.com/Kin69/Athena"
    const val PROJECT_SOURCE_CODE = "https://github.com/Kin69/Athena"
    const val LICENSE_URL = "https://www.gnu.org/licenses/gpl-3.0.en.html"
}
