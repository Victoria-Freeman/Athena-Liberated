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

package com.kin.athena.service.vpn.network.transport.tcp

import com.kin.athena.core.logging.Logger
import com.kin.athena.service.vpn.network.transport.ipv4.IPv4
import java.nio.ByteBuffer
import java.nio.ByteOrder

object TcpConstants {
    const val MIN_HEADER_LENGTH_BYTES = 20
    const val OFFSET_SOURCE_PORT = 0
    const val OFFSET_DESTINATION_PORT = 2
    const val OFFSET_SEQUENCE_NUMBER = 4
    const val OFFSET_ACKNOWLEDGMENT_NUMBER = 8
    const val OFFSET_DATA_OFFSET_RESERVED_NS = 12
    const val OFFSET_FLAGS = 13
    const val OFFSET_WINDOW_SIZE = 14
    const val OFFSET_CHECKSUM = 16
    const val OFFSET_URGENT_POINTER = 18
    const val OFFSET_OPTIONS = 20

    const val DATA_OFFSET_MASK = 0xF0
    const val DATA_OFFSET_SHIFT = 4
    const val RESERVED_MASK = 0x0E
    const val RESERVED_SHIFT = 1
    const val NS_FLAG_MASK = 0x01

    const val OPT_KIND_EOL: UByte = 0u
    const val OPT_KIND_NOP: UByte = 1u
    const val OPT_KIND_MSS: UByte = 2u
    const val OPT_KIND_WINDOW_SCALE: UByte = 3u
    const val OPT_KIND_SACK_PERMITTED: UByte = 4u
    const val OPT_KIND_TIMESTAMP: UByte = 8u

    const val OPT_LEN_MSS = 4
    const val OPT_LEN_WINDOW_SCALE = 3
    const val OPT_LEN_SACK_PERMITTED = 2
    const val OPT_LEN_TIMESTAMP = 10
}

enum class TCPFlag(val bitPosition: Int) {
    FIN(0),
    SYN(1),
    RST(2),
    PSH(3),
    ACK(4),
    URG(5),
    ECE(6),
    CWR(7);

    val flagValue: Int = 1 shl bitPosition
}

sealed class TCPOption {
    abstract val kind: UByte
    abstract val length: UByte
    abstract fun serialize(buffer: ByteBuffer)

    object EndOfOptionList : TCPOption() {
        override val kind: UByte = TcpConstants.OPT_KIND_EOL
        override val length: UByte = 1u
        override fun serialize(buffer: ByteBuffer) {
            buffer.put(kind.toByte())
        }
    }

    object NoOperation : TCPOption() {
        override val kind: UByte = TcpConstants.OPT_KIND_NOP
        override val length: UByte = 1u
        override fun serialize(buffer: ByteBuffer) {
            buffer.put(kind.toByte())
        }

        override fun toString(): String = "NoOperation"
    }

    data class MaximumSegmentSize(val mss: UShort) : TCPOption() {
        override val kind: UByte = TcpConstants.OPT_KIND_MSS
        override val length: UByte = TcpConstants.OPT_LEN_MSS.toUByte()
        override fun serialize(buffer: ByteBuffer) {
            buffer.put(kind.toByte())
            buffer.put(length.toByte())
            buffer.putShort(mss.toShort())
        }
    }

    data class WindowScale(val scaleFactor: UByte) : TCPOption() {
        override val kind: UByte = TcpConstants.OPT_KIND_WINDOW_SCALE
        override val length: UByte = TcpConstants.OPT_LEN_WINDOW_SCALE.toUByte()
        override fun serialize(buffer: ByteBuffer) {
            buffer.put(kind.toByte())
            buffer.put(length.toByte())
            buffer.put(scaleFactor.toByte())
        }
    }

    object SACKPermitted : TCPOption() {
        override val kind: UByte = TcpConstants.OPT_KIND_SACK_PERMITTED
        override val length: UByte = TcpConstants.OPT_LEN_SACK_PERMITTED.toUByte()
        override fun serialize(buffer: ByteBuffer) {
            buffer.put(kind.toByte())
            buffer.put(length.toByte())
        }

        override fun toString(): String = "SACKPermitted"
    }

    data class Timestamp(val tsValue: UInt, val tsEchoReply: UInt) : TCPOption() {
        override val kind: UByte = TcpConstants.OPT_KIND_TIMESTAMP
        override val length: UByte = TcpConstants.OPT_LEN_TIMESTAMP.toUByte()
        override fun serialize(buffer: ByteBuffer) {
            buffer.put(kind.toByte())
            buffer.put(length.toByte())
            buffer.putInt(tsValue.toInt())
            buffer.putInt(tsEchoReply.toInt())
        }
    }

    data class Unknown(
        override val kind: UByte,
        private val rawData: ByteArray
    ) : TCPOption() {
        override val length: UByte = (2 + rawData.size).toUByte()

        override fun serialize(buffer: ByteBuffer) {
            buffer.put(kind.toByte())
            buffer.put(length.toByte())
            buffer.put(rawData)
        }

        fun getData(): ByteArray = rawData.copyOf()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Unknown
            if (kind != other.kind) return false
            if (!rawData.contentEquals(other.rawData)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = kind.toInt()
            result = 31 * result + rawData.contentHashCode()
            return result
        }
    }

    companion object {
        fun parse(buffer: ByteBuffer): TCPOption {
            if (!buffer.hasRemaining()) {
                throw IllegalArgumentException("Buffer underflow: cannot read option kind.")
            }

            val kind = buffer.get().toUByte()

            return when (kind) {
                TcpConstants.OPT_KIND_EOL -> EndOfOptionList
                TcpConstants.OPT_KIND_NOP -> NoOperation
                else -> {
                    if (!buffer.hasRemaining()) {
                        throw IllegalArgumentException("Buffer underflow: cannot read option length for kind $kind.")
                    }

                    val length = buffer.get().toUByte()
                    if (length < 2u) {
                        throw IllegalArgumentException("Invalid option length $length for kind $kind (must be >= 2).")
                    }

                    val dataBytesLength = (length - 2u).toInt()
                    if (buffer.remaining() < dataBytesLength) {
                        throw IllegalArgumentException("Buffer underflow: option kind $kind, length $length, needs $dataBytesLength data bytes, found ${buffer.remaining()}.")
                    }

                    when (kind) {
                        TcpConstants.OPT_KIND_MSS -> {
                            if (length != TcpConstants.OPT_LEN_MSS.toUByte()) {
                                throw IllegalArgumentException("MSS option length mismatch.")
                            }
                            MaximumSegmentSize(buffer.getShort().toUShort())
                        }
                        TcpConstants.OPT_KIND_WINDOW_SCALE -> {
                            if (length != TcpConstants.OPT_LEN_WINDOW_SCALE.toUByte()) {
                                throw IllegalArgumentException("WindowScale option length mismatch.")
                            }
                            WindowScale(buffer.get().toUByte())
                        }
                        TcpConstants.OPT_KIND_SACK_PERMITTED -> {
                            if (length != TcpConstants.OPT_LEN_SACK_PERMITTED.toUByte()) {
                                throw IllegalArgumentException("SACKPermitted option length mismatch.")
                            }
                            SACKPermitted
                        }
                        TcpConstants.OPT_KIND_TIMESTAMP -> {
                            if (length != TcpConstants.OPT_LEN_TIMESTAMP.toUByte()) {
                                throw IllegalArgumentException("Timestamp option length mismatch.")
                            }
                            Timestamp(buffer.getInt().toUInt(), buffer.getInt().toUInt())
                        }
                        else -> {
                            val data = ByteArray(dataBytesLength)
                            buffer.get(data)
                            Unknown(kind, data)
                        }
                    }
                }
            }
        }
    }
}

data class TCPHeader(
    @Volatile var sourcePort: Int,
    @Volatile var destinationPort: Int,
    @Volatile var sequenceNumber: Long,
    @Volatile var acknowledgmentNumber: Long,
    @Volatile var isNS: Boolean = false,
    @Volatile var flags: Set<TCPFlag> = emptySet(),
    @Volatile var windowSize: Int,
    @Volatile var checksum: Int,
    @Volatile var urgentPointer: Int,
    @Volatile var options: List<TCPOption> = emptyList()
) {
    @get:Synchronized
    var reserved: Int = 0
        set(value) {
            if (value < 0 || value > 7) throw IllegalArgumentException("Reserved field must be between 0 and 7 (3 bits).")
            field = value
        }

    @get:Synchronized
    val dataOffset: Int
        get() {
            var optionsLengthBytes = 0
            for (option in options) {
                optionsLengthBytes += when (option) {
                    is TCPOption.EndOfOptionList, is TCPOption.NoOperation -> 1
                    else -> option.length.toInt()
                }
            }
            val paddedOptionsLength = (optionsLengthBytes + 3) / 4 * 4
            val totalHeaderLengthBytes = TcpConstants.MIN_HEADER_LENGTH_BYTES + paddedOptionsLength
            return totalHeaderLengthBytes / 4
        }

    @get:Synchronized
    val headerLength: Int
        get() = dataOffset * 4

    @Synchronized
    fun getMSS(): UShort? = options.filterIsInstance<TCPOption.MaximumSegmentSize>().firstOrNull()?.mss

    @Synchronized
    fun getWindowScaleFactor(): UByte? = options.filterIsInstance<TCPOption.WindowScale>().firstOrNull()?.scaleFactor

    @Synchronized
    fun calculateChecksum(ipHeader: IPv4, payload: ByteArray? = null): Int {
        this.checksum = 0

        val headerBytes = this.toByteArray()

        val payloadBytes = payload ?: ByteArray(0)
        val totalLength = headerBytes.size + payloadBytes.size

        val pseudoHeader = ByteBuffer.allocate(12)
        pseudoHeader.order(ByteOrder.BIG_ENDIAN)
        pseudoHeader.put(ipHeader.sourceIP.toIpByteArray()) // 4 bytes
        pseudoHeader.put(ipHeader.destinationIP.toIpByteArray()) // 4 bytes
        pseudoHeader.put(0.toByte()) // Zero
        pseudoHeader.put(ipHeader.protocol.toByte()) // Protocol (TCP = 6)
        pseudoHeader.putShort(totalLength.toShort()) // TCP length

        // Combine pseudo-header, header, and payload
        val totalSize = pseudoHeader.capacity() + headerBytes.size + payloadBytes.size
        val fullBuffer = ByteBuffer.allocate(totalSize + (if (totalSize % 2 != 0) 1 else 0)) // Pad to even bytes
        fullBuffer.order(ByteOrder.BIG_ENDIAN)
        fullBuffer.put(pseudoHeader.array())
        fullBuffer.put(headerBytes)
        fullBuffer.put(payloadBytes)
        if (totalSize % 2 != 0) {
            fullBuffer.put(0.toByte()) // Padding byte
        }

        // Calculate checksum
        fullBuffer.flip()
        var sum = 0L
        while (fullBuffer.hasRemaining()) {
            sum += (fullBuffer.getShort().toInt() and 0xFFFF).toLong()
        }
        sum = (sum and 0xFFFF) + (sum shr 16) // Add carry
        sum = (sum and 0xFFFF) + (sum shr 16) // Add carry again
        val checksum = (sum xor 0xFFFF).toInt() and 0xFFFF // One's complement

        // Update checksum field
        this.checksum = checksum
        return checksum
    }

    companion object {
        // Rate limiting for malformed packet logging
        private var lastMalformedLogTime = 0L
        private var malformedPacketCount = 0L
        private const val LOG_INTERVAL_MS = 5000L // Log every 5 seconds
        
        private fun logMalformedPacket(reason: String, buffer: ByteBuffer? = null) {
            malformedPacketCount++
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastMalformedLogTime > LOG_INTERVAL_MS) {
                val packetHex = buffer?.let { buf ->
                    val originalPos = buf.position()
                    buf.position(0)
                    val bytes = ByteArray(minOf(buf.remaining(), 60)) // Log first 60 bytes
                    buf.get(bytes)
                    buf.position(originalPos)
                    bytes.joinToString(" ") { "%02x".format(it) }
                } ?: "N/A"
                Logger.warn("TCP malformed packets detected ($malformedPacketCount total): $reason | Packet hex: $packetHex")
                lastMalformedLogTime = currentTime
                malformedPacketCount = 0
            }
        }
        
        @Synchronized
        fun fromByteBuffer(buffer: ByteBuffer): TCPHeader {
            val originalPosition = buffer.position()
            if (buffer.remaining() < TcpConstants.MIN_HEADER_LENGTH_BYTES) {
                throw IllegalArgumentException("Buffer too small for TCP header. Min ${TcpConstants.MIN_HEADER_LENGTH_BYTES} bytes required, got ${buffer.remaining()}.")
            }
            buffer.order(ByteOrder.BIG_ENDIAN)

            val sourcePort = buffer.getShort(originalPosition + TcpConstants.OFFSET_SOURCE_PORT).toInt() and 0xFFFF
            val destinationPort = buffer.getShort(originalPosition + TcpConstants.OFFSET_DESTINATION_PORT).toInt() and 0xFFFF
            val sequenceNumber = buffer.getInt(originalPosition + TcpConstants.OFFSET_SEQUENCE_NUMBER).toLong() and 0xFFFFFFFFL
            val acknowledgmentNumber = buffer.getInt(originalPosition + TcpConstants.OFFSET_ACKNOWLEDGMENT_NUMBER).toLong() and 0xFFFFFFFFL

            val byte12 = buffer.get(originalPosition + TcpConstants.OFFSET_DATA_OFFSET_RESERVED_NS).toInt() and 0xFF
            val parsedDataOffset = (byte12 and TcpConstants.DATA_OFFSET_MASK) ushr TcpConstants.DATA_OFFSET_SHIFT
            val parsedReserved = (byte12 and TcpConstants.RESERVED_MASK) ushr TcpConstants.RESERVED_SHIFT
            val isNS = (byte12 and TcpConstants.NS_FLAG_MASK) != 0

            val flagsByte = buffer.get(originalPosition + TcpConstants.OFFSET_FLAGS).toInt() and 0xFF
            val parsedFlags = mutableSetOf<TCPFlag>()
            TCPFlag.entries.forEach { flag ->
                if ((flagsByte and flag.flagValue) != 0) {
                    parsedFlags.add(flag)
                }
            }

            val windowSize = buffer.getShort(originalPosition + TcpConstants.OFFSET_WINDOW_SIZE).toInt() and 0xFFFF
            val checksum = buffer.getShort(originalPosition + TcpConstants.OFFSET_CHECKSUM).toInt() and 0xFFFF
            val urgentPointer = buffer.getShort(originalPosition + TcpConstants.OFFSET_URGENT_POINTER).toInt() and 0xFFFF

            // Reject malformed packets with invalid Data Offset
            if (parsedDataOffset == 0) {
                // Rate-limited logging for malformed packets with raw data
                val bufferCopy = buffer.duplicate()
                bufferCopy.position(originalPosition)
                logMalformedPacket("Data Offset is 0", bufferCopy)
                throw IllegalArgumentException("Malformed TCP packet: Data Offset cannot be 0")
            }
            
            val actualHeaderLengthBytes = parsedDataOffset * 4
            if (actualHeaderLengthBytes < TcpConstants.MIN_HEADER_LENGTH_BYTES) {
                val bufferCopy = buffer.duplicate()
                bufferCopy.position(originalPosition)
                logMalformedPacket("Data Offset $parsedDataOffset: calculated header length $actualHeaderLengthBytes bytes is less than minimum ${TcpConstants.MIN_HEADER_LENGTH_BYTES} bytes", bufferCopy)
                throw IllegalArgumentException("Malformed TCP packet: Data Offset $parsedDataOffset produces invalid header length")
            }
            if (buffer.remaining() < actualHeaderLengthBytes - (buffer.position() - originalPosition)) {
                throw IllegalArgumentException("Buffer remaining bytes (${buffer.remaining()}) insufficient for declared header length ($actualHeaderLengthBytes bytes) from current position.")
            }

            val optionsList = mutableListOf<TCPOption>()
            val optionsLengthBytes = actualHeaderLengthBytes - TcpConstants.MIN_HEADER_LENGTH_BYTES
            if (optionsLengthBytes > 0) {
                buffer.position(originalPosition + TcpConstants.OFFSET_OPTIONS)
                val optionsBuffer = buffer.slice()
                optionsBuffer.limit(optionsLengthBytes)
                optionsBuffer.order(ByteOrder.BIG_ENDIAN)

                parseOptions(optionsBuffer, optionsList)
            }

            buffer.position(originalPosition + actualHeaderLengthBytes)

            return TCPHeader(
                sourcePort = sourcePort,
                destinationPort = destinationPort,
                sequenceNumber = sequenceNumber,
                acknowledgmentNumber = acknowledgmentNumber,
                isNS = isNS,
                flags = parsedFlags,
                windowSize = windowSize,
                checksum = checksum,
                urgentPointer = urgentPointer,
                options = optionsList.toList()
            ).apply {
                this.reserved = parsedReserved
            }
        }

        @Synchronized
        private fun parseOptions(optionsBuffer: ByteBuffer, optionsList: MutableList<TCPOption>) {
            while (optionsBuffer.hasRemaining()) {
                val option = TCPOption.parse(optionsBuffer)
                optionsList.add(option)
                if (option is TCPOption.EndOfOptionList) {
                    break
                }
            }
        }
    }

    @Synchronized
    fun toByteArray(): ByteArray {
        val calculatedDataOffset = this.dataOffset
        if (calculatedDataOffset < 5 || calculatedDataOffset > 15) {
            throw IllegalStateException("Calculated Data Offset $calculatedDataOffset is out of TCP valid range (5-15).")
        }
        val totalHeaderLengthBytes = calculatedDataOffset * 4
        val buffer = ByteBuffer.allocate(totalHeaderLengthBytes)
        buffer.order(ByteOrder.BIG_ENDIAN)

        buffer.putShort(sourcePort.toShort())
        buffer.putShort(destinationPort.toShort())
        buffer.putInt(sequenceNumber.toInt())
        buffer.putInt(acknowledgmentNumber.toInt())

        val byte12 = ((calculatedDataOffset and 0xF) shl TcpConstants.DATA_OFFSET_SHIFT) or
                ((reserved and 0x7) shl TcpConstants.RESERVED_SHIFT) or
                (if (isNS) TcpConstants.NS_FLAG_MASK else 0)
        buffer.put(byte12.toByte())

        var flagsByte = 0
        flags.forEach { flag -> flagsByte = flagsByte or flag.flagValue }
        buffer.put(flagsByte.toByte())

        buffer.putShort(windowSize.toShort())
        buffer.putShort(checksum.toShort())
        buffer.putShort(urgentPointer.toShort())

        var currentOptionsBytesCount = 0
        options.forEach { opt ->
            opt.serialize(buffer)
            currentOptionsBytesCount += when (opt) {
                is TCPOption.EndOfOptionList, is TCPOption.NoOperation -> 1
                else -> opt.length.toInt()
            }
        }

        val optionsAreaLength = totalHeaderLengthBytes - TcpConstants.MIN_HEADER_LENGTH_BYTES
        var paddingBytesNeeded = optionsAreaLength - currentOptionsBytesCount
        while (paddingBytesNeeded > 0) {
            buffer.put(TcpConstants.OPT_KIND_NOP.toByte())
            paddingBytesNeeded--
        }

        if (buffer.position() != totalHeaderLengthBytes) {
            throw IllegalStateException("Serialization error: Buffer position ${buffer.position()} does not match calculated total header length $totalHeaderLengthBytes. Options written: $currentOptionsBytesCount, Padding: ${optionsAreaLength - currentOptionsBytesCount}.")
        }

        return buffer.array()
    }
}

@Synchronized
fun Int.toIpByteArray(): ByteArray {
    return byteArrayOf(
        (this shr 24 and 0xFF).toByte(),
        (this shr 16 and 0xFF).toByte(),
        (this shr 8 and 0xFF).toByte(),
        (this and 0xFF).toByte()
    )
}
