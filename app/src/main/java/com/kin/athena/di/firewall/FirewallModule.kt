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
import com.kin.athena.data.service.ConnectionStateManager
import com.kin.athena.data.service.NetworkManager
import com.kin.athena.domain.usecase.application.ApplicationUseCases
import com.kin.athena.domain.usecase.preferences.PreferencesUseCases
import com.kin.athena.service.firewall.handler.RuleHandler
import com.kin.athena.service.root.nflog.NflogManager
import com.kin.athena.service.root.service.RootConnectionService
import com.kin.athena.service.shizuku.ShizukuConnectionService
import com.kin.athena.service.utils.manager.FirewallManager
import com.kin.athena.service.utils.manager.FirewallStateManager
import com.kin.athena.service.vpn.service.VpnConnectionClient
import com.kin.athena.service.vpn.service.VpnConnectionServer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FirewallModule {
    @Provides
    @Singleton
    fun provideFirewallState(): FirewallStateManager {
        return FirewallStateManager()
    }

    @Provides
    @Singleton
    fun provideRootConnectionService(
        packageUseCases: ApplicationUseCases,
        networkLogger: NflogManager,
        preferencesUseCases: PreferencesUseCases,
        @ApplicationContext context: Context,
        ): RootConnectionService {
        return RootConnectionService().apply {
            this.packageManager = packageUseCases
            this.networkLogger = networkLogger
            this.preferencesUseCases = preferencesUseCases
            appContext = context
        }
    }

    @Provides
    @Singleton
    fun provideVpnConnection(
        ruleHandler: RuleHandler,
        applicationUseCases: ApplicationUseCases,
        preferencesUseCases: PreferencesUseCases,
        @ApplicationContext context: Context,
        ): VpnConnectionServer {
        return VpnConnectionServer().apply {
            this.ruleManager = ruleHandler
            this.applicationUseCases = applicationUseCases
            this.preferencesUseCases = preferencesUseCases
            appContext = context
        }
    }

    @Provides
    @Singleton
    fun provideShizukuService(
        applicationUseCases: ApplicationUseCases,
        preferencesUseCases: PreferencesUseCases,
        @ApplicationContext context: Context,
        networkFilterUseCases: com.kin.athena.domain.usecase.networkFilter.NetworkFilterUseCases,
        logUseCases: com.kin.athena.domain.usecase.log.LogUseCases
    ): ShizukuConnectionService {
        return ShizukuConnectionService().apply {
            this.applicationUseCases = applicationUseCases
            this.preferencesUseCases = preferencesUseCases
            this.appContext = context
            this.networkFilterUseCases = networkFilterUseCases
            this.logUseCases = logUseCases
        }
    }


    @Provides
    @Singleton
    fun provideFirewallManager(
        firewallStateManager: FirewallStateManager,
        @ApplicationContext context: Context,
        rootService: RootConnectionService,
        vpnService: VpnConnectionServer,
        shizukuService: ShizukuConnectionService
    ): FirewallManager {
        val firewallManager = FirewallManager(firewallStateManager, context, rootService, vpnService, shizukuService)
        shizukuService.firewallManager = firewallManager
        return firewallManager
    }
}
