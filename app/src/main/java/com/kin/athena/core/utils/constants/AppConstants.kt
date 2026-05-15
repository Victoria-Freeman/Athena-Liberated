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


object AppConstants {
    object AnimationConstants {
        const val FADE_DURATION: Int = 200
        const val SCALE_DURATION: Int = 250
        const val SLIDE_DURATION: Int = 300
        const val INITIAL_SCALE: Float = 0.95f
    }

    object DatabaseConstants {
        const val DATABASE_NAME: String = "packages"
        const val DATABASE_VERSION: Int = 9
        const val SETTINGS_DATABASE_NAME: String = "settings"
    }

    object NetworkConstants {
        const val NFLOG_GROUP_ID: Int = 58
    }

    object DnsBlockLists {
        // Old blocklist URLs (for migration from v1.4 and earlier)
        object Legacy {
            const val MALWARE_PROTECTION = "https://raw.githubusercontent.com/DandelionSprout/adfilt/master/Alternate%20versions%20Anti-Malware%20List/AntiMalwareHosts.txt"
            const val AD_PROTECTION = "https://adaway.org/hosts.txt"
            const val PRIVACY_PROTECTION = "https://hostfiles.frogeye.fr/firstparty-trackers-hosts.txt"
            const val GAMBLING_PROTECTION = "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/gambling-only/hosts"
            const val ADULT_PROTECTION = "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/porn-only/hosts"
            const val SOCIAL_PROTECTION = "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/social-only/hosts"
        }

        val MALWARE_PROTECTION = listOf(
            // DandelionSprout: solid anti-malware coverage
            "https://raw.githubusercontent.com/DandelionSprout/adfilt/master/Alternate%20versions%20Anti-Malware%20List/AntiMalwareHosts.txt",
        )
        val AD_PROTECTION = listOf(
            "https://small.oisd.nl",
        )
        val ADS_EXTREME = listOf(
            "https://blokada.org/mirror/v5/goodbyeads/standard/hosts.txt"
        )
        val PRIVACY_PROTECTION = listOf(
            "https://hostfiles.frogeye.fr/firstparty-trackers-hosts.txt",
            "https://raw.githubusercontent.com/Perflyst/PiHoleBlocklist/master/android-tracking.txt",
        )
        val GAMBLING_PROTECTION = listOf(
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/gambling-only/hosts"
        )
        val ADULT_PROTECTION = listOf(
            "https://raw.githubusercontent.com/blocklistproject/Lists/master/porn.txt"
        )
        val SOCIAL_PROTECTION = listOf(
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/social-only/hosts",
        )
    }


}