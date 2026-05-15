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

package com.kin.athena.service.vpn.network.util

object NetworkConstants {
    // DNS server structure: Name -> (IPv4 Primary, IPv4 Secondary, IPv6 Primary, IPv6 Secondary)
    data class DnsServer(
        val name: String,
        val ipv4Primary: String,
        val ipv4Secondary: String,
        val ipv6Primary: String,
        val ipv6Secondary: String
    )

    val DNS_SERVERS: List<DnsServer> = listOf(
        DnsServer("Google", "8.8.8.8", "8.8.4.4", "2001:4860:4860::8888", "2001:4860:4860::8844"),
        DnsServer("Cloudflare", "1.1.1.1", "1.0.0.1", "2606:4700:4700::1111", "2606:4700:4700::1001"),
        DnsServer("OpenDNS", "208.67.222.222", "208.67.220.220", "2620:119:35::35", "2620:119:53::53"),
        DnsServer("Quad9", "9.9.9.9", "149.112.112.112", "2620:fe::fe", "2620:fe::9"),
        DnsServer("Comodo Secure DNS", "8.26.56.26", "8.20.247.20", "2001:4860:4860::8888", "2001:4860:4860::8844"),
        DnsServer("Level3 DNS", "4.2.2.1", "4.2.2.2", "2001:4860:4860::8888", "2001:4860:4860::8844"),
        DnsServer("Verisign Public DNS", "64.6.64.6", "64.6.65.6", "2620:74:1b::1:1", "2620:74:1c::2:2"),
        DnsServer("Yandex Basic", "77.88.8.8", "77.88.8.1", "2a02:6b8::feed:0ff", "2a02:6b8:0:1::feed:0ff"),
        DnsServer("Yandex Safe", "77.88.8.88", "77.88.8.2", "2a02:6b8::feed:bad", "2a02:6b8:0:1::feed:bad"),
        DnsServer("Yandex Family", "77.88.8.7", "77.88.8.3", "2a02:6b8::feed:a11", "2a02:6b8:0:1::feed:a11"),
        DnsServer("CleanBrowsing Family Filter", "185.228.168.168", "185.228.169.168", "2a0d:2a00:1::1", "2a0d:2a00:2::1"),
        DnsServer("CleanBrowsing Adult Filter", "185.228.168.10", "185.228.169.11", "2a0d:2a00:1::2", "2a0d:2a00:2::2"),
        DnsServer("CleanBrowsing Security Filter", "185.228.168.9", "185.228.169.9", "2a0d:2a00:1::", "2a0d:2a00:2::"),
        DnsServer("Neustar Recursive", "156.154.70.1", "156.154.71.1", "2610:a1:1018::1", "2610:a1:1019::1"),
        DnsServer("Neustar Threat Protection", "156.154.70.2", "156.154.71.2", "2610:a1:1018::2", "2610:a1:1019::2"),
        DnsServer("Neustar Family Protection", "156.154.70.3", "156.154.71.3", "2610:a1:1018::3", "2610:a1:1019::3")
    )

    // Legacy compatibility - convert to old format
    @Deprecated("Use DNS_SERVERS directly")
    val DNS_SERVERS_LEGACY: List<Pair<String, Pair<String, String>>> = DNS_SERVERS.map {
        Pair(it.name, Pair(it.ipv4Primary, it.ipv4Secondary))
    }
    const val VPN_ADDRESS: String = "10.0.10.1"
    const val VPN_DNS_ADDRESS: String = "198.18.0.1"  // Local DNS for IPv4
    const val VPN_DNS6_ADDRESS: String = "fd00::53"    // Local DNS for IPv6
    const val ALL_TRAFFIC: String = "0.0.0.0"
    const val VPN6_ADDRESS: String = "fd00:1:fd00:1:fd00:1:fd00:1"

    const val ACTION_START_VPN = "com.kin.athena.START_VPN"
    const val ACTION_STOP_VPN = "com.kin.athena.vpn.controller.ACTION_STOP_VPN"

    const val ACTION_TOGGLE_WIFI = "com.kin.athena.vpn.controller.ACTION_TOGGLE_WIFI"
    const val ACTION_TOGGLE_CELLURAL = "com.kin.athena.vpn.controller.ACTION_TOGGLE_CELLULAR"

    const val ACTION_CLEAR_SESSIONS =  "com.kin.athena.vpn.controller.ACTION_CLEAR_SESSIONS"

    const val ACTION_START_ROOT = "com.kin.athena.START_ROOT"
    const val ACTION_STOP_ROOT = "com.kin.athena.vpn.controller.ACTION_STOP_ROOT"

    const val TCP_PROTOCOL: Byte = 6
    const val UDP_PROTOCOL: Byte = 17
    const val ICMP_PROTOCOL: Byte = 1
    const val MAX_PACKET_LEN = 8 * 1024
}
