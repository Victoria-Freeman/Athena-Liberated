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

import com.kin.athena.service.vpn.network.util.NetworkConstants
import com.kin.athena.service.firewall.model.FireWallModel
import com.kin.athena.service.vpn.network.transport.extenions.toIp
import com.kin.athena.service.vpn.network.transport.icmp.ICMPModel
import com.kin.athena.service.vpn.network.transport.ipv4.IPv4
import com.kin.athena.service.vpn.network.transport.tcp.TCPHeader
import com.kin.athena.service.vpn.network.transport.udp.UDPModel

interface ProtocolHandler {
    fun handle(protocol: Any?, ipHeader: IPv4?): FireWallModel?
}

class ICMPHandler : ProtocolHandler {
    override fun handle(protocol: Any?, ipHeader: IPv4?): FireWallModel? {
        return  if (protocol != null && ipHeader != null) {
           if (protocol is ICMPModel) {
                FireWallModel(
                    sourceIP = ipHeader.sourceIP.toIp(),
                    destinationIP = ipHeader.destinationIP.toIp(),
                    protocol = NetworkConstants.ICMP_PROTOCOL,
                )
            } else null
        } else null
    }
}

class TCPHandler : ProtocolHandler {
    override fun handle(protocol: Any?, ipHeader: IPv4?): FireWallModel? {
        return if (protocol != null && ipHeader != null) {
             if (protocol is TCPHeader) {
                FireWallModel(
                    destinationIP = ipHeader.destinationIP.toIp(),
                    sourceIP = ipHeader.sourceIP.toIp(),
                    destinationPort = protocol.destinationPort,
                    sourcePort = protocol.sourcePort,
                    protocol = NetworkConstants.TCP_PROTOCOL,
                )
            } else null
        } else null
    }
}

class UDPHandler : ProtocolHandler {
    override fun handle(protocol: Any?, ipHeader: IPv4?): FireWallModel? {
        return if (protocol != null && ipHeader != null) {
            if (protocol is UDPModel) {
                FireWallModel(
                    destinationIP = ipHeader.destinationIP.toIp(),
                    sourceIP = ipHeader.sourceIP.toIp(),
                    destinationPort = protocol.destinationPort,
                    sourcePort = protocol.sourcePort,
                    protocol = NetworkConstants.UDP_PROTOCOL,
                )
            } else null
        } else null
    }
}

object ProtocolHandlerFactory {
    fun getHandler(protocol: Any?): ProtocolHandler? {
        return when (protocol) {
            is ICMPModel -> ICMPHandler()
            is TCPHeader -> TCPHandler()
            is UDPModel -> UDPHandler()
            else -> null
        }
    }
}