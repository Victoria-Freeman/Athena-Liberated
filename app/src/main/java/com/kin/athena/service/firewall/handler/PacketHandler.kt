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
package com.kin.athena.service.firewall.handler

import com.kin.athena.core.logging.Logger
import com.kin.athena.service.firewall.model.FireWallModel
import com.kin.athena.service.firewall.model.FirewallResult
import com.kin.athena.service.vpn.network.transport.dns.DNSModel
import com.kin.athena.service.vpn.network.transport.ipv4.IPv4

fun filterPacket(protocol: Any?, ipHeader: IPv4?, ruleManager: RuleHandler, dnsModel: DNSModel? = null, uid: Int? = null, bypassCheck: Boolean = false): Triple<Boolean, Int, FirewallResult> {
    val handler = ProtocolHandlerFactory.getHandler(protocol)
    val fireWallModel = handler?.handle(protocol, ipHeader) ?: uid?.let { FireWallModel(uid = uid) }
    return fireWallModel?.let { ruleManager.handle(it, dnsModel, bypassCheck) } ?: Triple(true, 0, FirewallResult.ACCEPT)
}