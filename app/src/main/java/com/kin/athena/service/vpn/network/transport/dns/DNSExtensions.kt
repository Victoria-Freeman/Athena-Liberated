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

package com.kin.athena.service.vpn.network.transport.dns

import java.nio.ByteBuffer

fun ByteBuffer.readDomainName(): String {
    val domainParts = mutableListOf<String>()
    var length = get().toInt() and 0xFF
    while (length > 0) {
        val part = ByteArray(length)
        get(part)
        domainParts.add(part.toString(Charsets.UTF_8))
        length = get().toInt() and 0xFF
    }
    return domainParts.joinToString(".")
}

fun ByteBuffer.toDNSModel(): DNSModel {
    val transactionId = this.short.toInt()
    val flags = this.short.toInt()
    val questionCount = this.short.toInt()
    val answerCount = this.short.toInt()
    val authorityCount = this.short.toInt()
    val additionalCount = this.short.toInt()
    val domainName = this.readDomainName()
    val queryType = this.short.toInt()
    val queryClass = this.short.toInt()

    return DNSModel(
        transactionId = transactionId,
        flags = flags,
        questionCount = questionCount,
        answerCount = answerCount,
        authorityCount = authorityCount,
        additionalCount = additionalCount,
        domainName = domainName,
        queryType = queryType,
        queryClass = queryClass
    )
}
