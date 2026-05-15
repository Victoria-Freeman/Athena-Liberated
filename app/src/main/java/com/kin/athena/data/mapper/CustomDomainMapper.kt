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

package com.kin.athena.data.mapper

import com.kin.athena.data.database.entity.CustomDomainEntity
import com.kin.athena.domain.model.CustomDomain

fun CustomDomainEntity.toDomain(): CustomDomain {
    return CustomDomain(
        id = id,
        domain = domain,
        description = description,
        isRegex = isRegex,
        isAllowlist = isAllowlist,
        createdAt = createdAt,
        isEnabled = isEnabled
    )
}

fun CustomDomain.toEntity(): CustomDomainEntity {
    return CustomDomainEntity(
        id = id,
        domain = domain,
        description = description,
        isRegex = isRegex,
        isAllowlist = isAllowlist,
        createdAt = createdAt,
        isEnabled = isEnabled
    )
}

fun List<CustomDomainEntity>.toDomainList(): List<CustomDomain> {
    return map { it.toDomain() }
}

fun List<CustomDomain>.toEntityList(): List<CustomDomainEntity> {
    return map { it.toEntity() }
}