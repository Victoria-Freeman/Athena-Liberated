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

package com.kin.athena.service.vpn.network.transport.ipv4

import com.kin.athena.core.logging.Logger
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun IPv4.copy(): IPv4 {
    return IPv4(
        ipVersion,
        internetHeaderLength,
        dscpOrTypeOfService,
        ecn,
        totalLength,
        identification,
        flagsAndFragmentOffset,
        timeToLive,
        protocol,
        headerChecksum,
        sourceIP,
        destinationIP
    )
}

fun IPv4.toByteArray(): ByteArray {
    val buffer = ByteArray(iPHeaderLength)
    buffer.fillBasicHeaderFields(this)
    buffer.fillSourceAndDestinationIP(this)
    return buffer
}

fun ByteBuffer.toIPv4Header(): IPv4 {
    checkMinimumHeaderLength()
    val versionAndHeaderLengthByte = get()
    validateIpVersion(versionAndHeaderLengthByte)

    val headerLengthInWords = (versionAndHeaderLengthByte.toInt() and 0x0F).toByte()
    validateHeaderLength(headerLengthInWords)

    val dscpAndEcnByte = get()
    val differentiatedServicesCodePoint = (dscpAndEcnByte.toInt() shr 2).toByte()
    val explicitCongestionNotification = (dscpAndEcnByte.toInt() and 0x03).toByte()
    val totalLength = getShort().toInt()
    val identification = getShort().toInt()
    val flagsAndFragmentOffsetShort = getShort()

    val timeToLive = get()
    val protocol = get()
    val checksum = getShort().toInt()
    val sourceIpAddress = getInt()
    val destinationIpAddress = getInt()

    skipOptionsIfPresent(headerLengthInWords)

    return IPv4(
        (versionAndHeaderLengthByte.toInt() shr 4).toByte(),
        headerLengthInWords,
        differentiatedServicesCodePoint,
        explicitCongestionNotification,
        totalLength,
        identification,
        flagsAndFragmentOffsetShort,
        timeToLive,
        protocol,
        checksum,
        sourceIpAddress,
        destinationIpAddress
    )
}

private fun ByteArray.fillBasicHeaderFields(header: IPv4) {
    this[0] = ((header.internetHeaderLength.toInt() and 0x0F) or 0x40).toByte()
    this[1] = ((header.dscpOrTypeOfService.toInt() shl 2) or (header.ecn.toInt() and 0x03)).toByte()
    this[2] = (header.totalLength shr 8).toByte()
    this[3] = header.totalLength.toByte()
    this[4] = (header.identification shr 8).toByte()
    this[5] = header.identification.toByte()

    this[6] = (((header.fragmentOffset.toInt() shr 8) and 0x1F) or if (header.isFragmentationAllowed) 0x40 else 0).toByte()
    this[7] = header.fragmentOffset.toByte()
    this[8] = header.timeToLive
    this[9] = header.protocol
    this[10] = (header.headerChecksum shr 8).toByte()
    this[11] = header.headerChecksum.toByte()
}

private fun ByteArray.fillSourceAndDestinationIP(header: IPv4) {
    ByteBuffer.allocate(8).apply {
        order(ByteOrder.BIG_ENDIAN)
        putInt(0, header.sourceIP)
        putInt(4, header.destinationIP)
        System.arraycopy(array(), 0, this@fillSourceAndDestinationIP, 12, 8)
    }
}

private fun ByteBuffer.checkMinimumHeaderLength() {
    if (remaining() < 20) {
        Logger.error("Minimum IPv4 header length is 20 bytes. There are less than 20 bytes available from the current position.")

    }
}

private fun validateIpVersion(versionAndHeaderLengthByte: Byte) {
    val ipVersion = (versionAndHeaderLengthByte.toInt() shr 4).toByte()
    if (ipVersion.toInt() != 0x04) {

    }
}

private fun ByteBuffer.validateHeaderLength(headerLengthInWords: Byte) {
    val headerLengthInBytes = headerLengthInWords * 4
    if (capacity() < headerLengthInBytes) {
        Logger.error("Not enough space in buffer for IP header")
    }
}

private fun ByteBuffer.skipOptionsIfPresent(headerLengthInWords: Byte) {
    if (headerLengthInWords > 5) {
        for (i in 0 until headerLengthInWords - 5) {
            getInt()
        }
    }
}