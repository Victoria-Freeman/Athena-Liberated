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

package com.kin.athena.presentation.screens.settings.subSettings.logs.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kin.athena.R
import com.kin.athena.core.utils.NumberFormatter
import com.kin.athena.domain.model.NetworkStatsState
import com.kin.athena.presentation.screens.settings.components.IconType
import com.kin.athena.presentation.screens.settings.components.SettingType
import com.kin.athena.presentation.screens.settings.components.SettingsBox
import com.kin.athena.presentation.screens.settings.components.settingsContainer

/**
 * Extension function for LazyListScope that adds network statistics section.
 * Shows allowed requests, blocked requests, and total activity counts.
 * 
 * @param networkStats The current network statistics state containing counts and firewall status
 */
fun LazyListScope.networkStatsSection(networkStats: NetworkStatsState) {
    settingsContainer {
        SettingsBox(
            icon = IconType.VectorIcon(Icons.Rounded.CheckCircle),
            title = stringResource(id = R.string.logs_allowed_requests),
            description = stringResource(
                id = R.string.logs_allowed_desc,
                NumberFormatter.formatCount(networkStats.allowedCount)
            ),
            actionType = SettingType.TEXT,
            customText = NumberFormatter.formatCount(networkStats.allowedCount)
        )
        
        SettingsBox(
            icon = IconType.VectorIcon(Icons.Rounded.Block),
            title = stringResource(id = R.string.logs_blocked_requests),
            description = stringResource(
                id = R.string.logs_blocked_desc,
                NumberFormatter.formatCount(networkStats.blockedCount)
            ),
            actionType = SettingType.TEXT,
            customText = NumberFormatter.formatCount(networkStats.blockedCount)
        )
        
        SettingsBox(
            icon = IconType.VectorIcon(Icons.Rounded.Analytics),
            title = stringResource(id = R.string.logs_total_activity),
            description = getSessionDescription(networkStats),
            actionType = SettingType.TEXT,
            customText = NumberFormatter.formatCount(networkStats.totalCount)
        )
    }
}

/**
 * Standalone composable component that displays network statistics in a card format.
 * Shows allowed requests, blocked requests, and total activity counts.
 * 
 * @param networkStats The current network statistics state containing counts and firewall status
 */
@Composable
fun NetworkStatsSection(networkStats: NetworkStatsState) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(32.dp))
    ) {
        SettingsBox(
            icon = IconType.VectorIcon(Icons.Rounded.CheckCircle),
            title = stringResource(id = R.string.logs_allowed_requests),
            description = stringResource(
                id = R.string.logs_allowed_desc,
                NumberFormatter.formatCount(networkStats.allowedCount)
            ),
            actionType = SettingType.TEXT,
            customText = NumberFormatter.formatCount(networkStats.allowedCount)
        )
        
        SettingsBox(
            icon = IconType.VectorIcon(Icons.Rounded.Block),
            title = stringResource(id = R.string.logs_blocked_requests),
            description = stringResource(
                id = R.string.logs_blocked_desc,
                NumberFormatter.formatCount(networkStats.blockedCount)
            ),
            actionType = SettingType.TEXT,
            customText = NumberFormatter.formatCount(networkStats.blockedCount)
        )
        
        SettingsBox(
            icon = IconType.VectorIcon(Icons.Rounded.Analytics),
            title = stringResource(id = R.string.logs_total_activity),
            description = getSessionDescription(networkStats),
            actionType = SettingType.TEXT,
            customText = NumberFormatter.formatCount(networkStats.totalCount)
        )
    }
}

/**
 * Generates a description for the total activity section based on firewall status and session time.
 * 
 * @param networkStats The current network statistics state
 * @return A formatted description string
 */
private fun getSessionDescription(networkStats: NetworkStatsState): String {
    return if (networkStats.isFirewallActive) {
        networkStats.sessionStartTime?.let { startTime ->
            val currentTime = System.currentTimeMillis()
            val sessionDuration = currentTime - startTime
            val sessionText = formatSessionDuration(sessionDuration)
            "Since firewall started • $sessionText ago"
        } ?: "Since firewall started"
    } else {
        "Firewall inactive"
    }
}

/**
 * Formats session duration into a human-readable string.
 * 
 * @param durationMs Duration in milliseconds
 * @return Formatted duration string (e.g., "2h 15m", "45m", "30s")
 */
private fun formatSessionDuration(durationMs: Long): String {
    val seconds = durationMs / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "${days}d ${hours % 24}h"
        hours > 0 -> "${hours}h ${minutes % 60}m"
        minutes > 0 -> "${minutes}m"
        else -> "${seconds}s"
    }
}