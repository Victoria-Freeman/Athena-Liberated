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

package com.kin.athena.service.vpn.network.transport.udp

import com.kin.athena.core.logging.Logger
import com.kin.athena.service.vpn.network.transport.ipv4.IPv4
import com.kin.athena.service.vpn.network.transport.ipv4.toByteArray
import com.kin.athena.service.vpn.network.util.PacketUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder


fun ByteBuffer.toUDPHeader(): UDPModel {
    if ((remaining()) < 8) {
        Logger.error("Minimum UDP header is 8 bytes.")
    }

    val srcPort = getShort().toInt() and 0xffff
    val destPort = getShort().toInt() and 0xffff
    val length = getShort().toInt() and 0xffff
    val checksum = getShort().toInt()

    return UDPModel(
        srcPort,
        destPort,
        length,
        checksum
    )
}

fun UDPModel.extractUDPData(buffer: ByteBuffer): ByteArray {
    val dataLength = this.length - 8
    val data = ByteArray(dataLength)
    buffer.get(data, 0, dataLength)

    return data
}

fun createUDPHeaderData(header: UDPModel): ByteArray {
    val buffer = ByteArray(8)
    val bb = ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN)

    bb.putShort(header.sourcePort.toShort())
    bb.putShort(header.destinationPort.toShort())
    bb.putShort(header.length.toShort())
    bb.putShort(header.checksum.toShort())

    return buffer
}

fun createResponsePacket(ipHeader: IPv4, requestPacket: UDPModel, responsePayload: ByteArray): ByteArray {
    val responseIP = ipHeader.copy().apply {
        val temp = sourceIP
        sourceIP = destinationIP
        destinationIP = temp
        identification = PacketUtil.packetId
        totalLength = iPHeaderLength + 8 + responsePayload.size
    }
    
    val responseUDP = UDPModel(
        sourcePort = requestPacket.destinationPort,
        destinationPort = requestPacket.sourcePort,
        length = 8 + responsePayload.size,
        checksum = 0
    )
    
    return createUDPPacketData(responseIP, responseUDP, responsePayload)
}

fun createUDPPacketData(ipHeader: IPv4, udpHeader: UDPModel, data: ByteArray?): ByteArray {
    val dataLength = data?.size ?: 0
    val totalLength = ipHeader.iPHeaderLength + 8 + dataLength
    val buffer = ByteArray(totalLength)

    val ipBuffer = ipHeader.toByteArray()
    val udpBuffer = createUDPHeaderData(udpHeader)

    System.arraycopy(ipBuffer, 0, buffer, 0, ipBuffer.size)
    
    System.arraycopy(udpBuffer, 0, buffer, ipBuffer.size, udpBuffer.size)
    
    data?.let { System.arraycopy(it, 0, buffer, ipBuffer.size + udpBuffer.size, dataLength) }

    val zero = byteArrayOf(0, 0)
    System.arraycopy(zero, 0, buffer, 10, 2) // Clear IP checksum field
    val ipChecksum = PacketUtil.calculateChecksum(buffer, 0, ipBuffer.size)
    System.arraycopy(ipChecksum, 0, buffer, 10, 2)

    val udpStart = ipBuffer.size
    System.arraycopy(zero, 0, buffer, udpStart + 6, 2) // Clear UDP checksum field
    val udpChecksum = PacketUtil.calculateUDPHeaderChecksum(
        buffer, udpStart, 8 + dataLength,
        ipHeader.destinationIP, ipHeader.sourceIP
    )
    System.arraycopy(udpChecksum, 0, buffer, udpStart + 6, 2)

    return buffer
}
