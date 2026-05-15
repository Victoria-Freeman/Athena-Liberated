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

package com.kin.athena.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kin.athena.core.logging.Logger
import com.kin.athena.core.utils.extensions.popUpToTop
import com.kin.athena.presentation.theme.EasyWallTheme
import com.kin.athena.presentation.navigation.AppNavHost
import com.kin.athena.presentation.navigation.routes.HomeRoutes
import com.kin.athena.presentation.screens.home.viewModel.HomeViewModel
import com.kin.athena.presentation.screens.settings.subSettings.dns.hosts.FileHelper
import com.kin.athena.presentation.screens.settings.subSettings.dns.hosts.HostState
import com.kin.athena.presentation.screens.settings.subSettings.dns.hosts.RuleDatabaseUpdateWorker
import com.kin.athena.presentation.screens.settings.subSettings.dns.hosts.Configuration
import com.kin.athena.presentation.screens.settings.viewModel.SettingsViewModel
import com.kin.athena.service.utils.manager.NetworkSpeedManager
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException


var config = Configuration.load()

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private var settings: SettingsViewModel? = null
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { !viewModel.isReady.value }
        }
        enableEdgeToEdge()
        setupUI(savedInstanceState == null)
    }

    override fun onResume() {
        super.onResume()
        settings?.let {
            it.loadDefaultRoute()
            if (it.defaultRoute != HomeRoutes.Home.route) {
                if (it.settings.value.lockImmediately) {
                    navController.navigate(it.defaultRoute!!) { popUpToTop(navController) }
                }
            }
        }
    }

    private fun refresh() {
        val workRequest = OneTimeWorkRequestBuilder<RuleDatabaseUpdateWorker>()
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun setupUI(saved: Boolean) {
        setContent {
            if (!areHostsFilesExistent() && saved) {
                refresh()
            }

            refresh()
            navController = rememberNavController()

            settings = hiltViewModel<SettingsViewModel>().apply {
                val homeViewModel = hiltViewModel<HomeViewModel>()

                EasyWallTheme(this) {
                    val iconsColor = MaterialTheme.colorScheme.background

                    LaunchedEffect(homeViewModel) {
                        homeViewModel.initialize(this@apply)
                        
                        if (settings.value.networkSpeedMonitor) {
                            NetworkSpeedManager.start(this@MainActivity)
                            Logger.info("Network speed monitor started on app startup")
                        }
                    }
                    
                    LaunchedEffect(homeViewModel.applicationState.value) {
                        if (homeViewModel.applicationState.value is com.kin.athena.presentation.screens.home.viewModel.ApplicationListState.Success) {
                            viewModel.showSlashScreen(true)
                        }
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AppNavHost(
                            settings = this,
                            startDestination = this@apply.defaultRoute!!,
                            homeViewModel = homeViewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    private fun areHostsFilesExistent(): Boolean {
        if (!config.hosts.enabled) {
            return true
        }

        for (item in config.hosts.items) {
            if (item.state != HostState.IGNORE) {
                try {
                    val reader = FileHelper.openItemFile(item) ?: return false
                    reader.close()
                } catch (e: IOException) {
                    Logger.error("areHostFilesExistent: Failed to open file {$item}", e)
                    return false
                }
            }
        }
        return true
    }
}
