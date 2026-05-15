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

package com.kin.athena.presentation.screens.settings.subSettings.about

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kin.athena.R
import com.kin.athena.core.utils.constants.ProjectConstants
import com.kin.athena.core.utils.extensions.safeNavigate
import com.kin.athena.presentation.navigation.routes.HomeRoutes
import com.kin.athena.presentation.screens.settings.components.IconType
import com.kin.athena.presentation.screens.settings.components.SettingType
import com.kin.athena.presentation.screens.settings.components.SettingsBox
import com.kin.athena.presentation.screens.settings.components.SettingsScaffold
import com.kin.athena.presentation.screens.settings.components.settingsContainer
import com.kin.athena.presentation.screens.settings.subSettings.about.components.LogoWithBlob
import com.kin.athena.presentation.screens.settings.subSettings.about.components.SettingsVerification
import com.kin.athena.presentation.screens.settings.viewModel.SettingsViewModel
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.material.icons.rounded.Gavel

@Composable
fun AboutScreen(
    navController: NavController,
    settings: SettingsViewModel,
) {
    val uriHandler = LocalUriHandler.current

    SettingsScaffold(
        settings = settings,
        title = stringResource(id = R.string.about_title),
        onBackNavClicked = { navController.navigateUp() }
    ) {
        item {
            LogoWithBlob {
                navController.safeNavigate(HomeRoutes.Debug.route)
            }
        }
        item {
            SettingsVerification(
                isValid = settings.getAppSignature() in ProjectConstants.SHA_256_SIGNING,
                title = stringResource(id = R.string.about_verified_build),
                description = stringResource(id = R.string.about_maintained_by) + " " + ProjectConstants.DEVELOPER
            )
        }
        settingsContainer {
            SettingsBox(
                title = stringResource(id = R.string.about_liberated_by),
                description = "Victoria Freeman",
                actionType = SettingType.TEXT,
                icon = IconType.VectorIcon(Icons.Rounded.Verified),
            )
            SettingsBox(
                title = stringResource(id = R.string.details_version),
                description = settings.version,
                icon = IconType.VectorIcon(Icons.Rounded.Info),
                actionType = SettingType.TEXT,
            )
            SettingsBox(
                title = stringResource(id = R.string.about_build_type),
                description = "Liberated!",
                icon = IconType.VectorIcon(Icons.Rounded.Build),
                actionType = SettingType.TEXT,
            )
        }
        settingsContainer {
            SettingsBox(
                title = stringResource(id = R.string.about_latest_release),
                icon = IconType.VectorIcon(Icons.Rounded.Verified),
                actionType = SettingType.LINK,
                onLinkClicked = { uriHandler.openUri(ProjectConstants.PROJECT_DOWNLOADS) },
            )
            SettingsBox(
                title = stringResource(id = R.string.about_source_code),
                icon = IconType.VectorIcon(Icons.Rounded.Download),
                actionType = SettingType.LINK,
                onLinkClicked = { uriHandler.openUri(ProjectConstants.PROJECT_SOURCE_CODE) }
            )
            SettingsBox(
                title = stringResource(id = R.string.settings_license),
                description = stringResource(id = R.string.license_description),
                icon = IconType.VectorIcon(Icons.Rounded.Gavel),
                actionType = SettingType.LINK,
                onLinkClicked = { uriHandler.openUri(ProjectConstants.LICENSE_URL) }
            )
        }
    }
}