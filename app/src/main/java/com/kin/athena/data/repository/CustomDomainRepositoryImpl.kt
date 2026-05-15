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

package com.kin.athena.data.repository

import com.kin.athena.data.database.dao.CustomDomainDao
import com.kin.athena.data.mapper.toDomain
import com.kin.athena.data.mapper.toDomainList
import com.kin.athena.data.mapper.toEntity
import com.kin.athena.data.mapper.toEntityList
import com.kin.athena.domain.model.CustomDomain
import com.kin.athena.domain.repository.CustomDomainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomDomainRepositoryImpl @Inject constructor(
    private val customDomainDao: CustomDomainDao
) : CustomDomainRepository {

    override fun getAllowlistDomains(): Flow<List<CustomDomain>> {
        return customDomainDao.getDomainsByType(isAllowlist = true)
            .map { it.toDomainList() }
    }

    override fun getBlocklistDomains(): Flow<List<CustomDomain>> {
        return customDomainDao.getDomainsByType(isAllowlist = false)
            .map { it.toDomainList() }
    }

    override fun getEnabledAllowlistDomains(): Flow<List<CustomDomain>> {
        return customDomainDao.getEnabledDomainsByType(isAllowlist = true)
            .map { it.toDomainList() }
    }

    override fun getEnabledBlocklistDomains(): Flow<List<CustomDomain>> {
        return customDomainDao.getEnabledDomainsByType(isAllowlist = false)
            .map { it.toDomainList() }
    }

    override fun getAllDomains(): Flow<List<CustomDomain>> {
        return customDomainDao.getAllDomains()
            .map { it.toDomainList() }
    }

    override suspend fun getDomainById(id: Long): CustomDomain? {
        return customDomainDao.getDomainById(id)?.toDomain()
    }

    override suspend fun isDomainExists(domain: String, isAllowlist: Boolean): Boolean {
        return customDomainDao.isDomainExists(domain, isAllowlist)
    }

    override suspend fun insertDomain(domain: CustomDomain): Long {
        return customDomainDao.insertDomain(domain.toEntity())
    }

    override suspend fun insertDomains(domains: List<CustomDomain>) {
        customDomainDao.insertDomains(domains.toEntityList())
    }

    override suspend fun updateDomain(domain: CustomDomain) {
        customDomainDao.updateDomain(domain.toEntity())
    }

    override suspend fun deleteDomain(domain: CustomDomain) {
        customDomainDao.deleteDomain(domain.toEntity())
    }

    override suspend fun deleteDomainById(id: Long) {
        customDomainDao.deleteDomainById(id)
    }

    override suspend fun deleteAllDomainsByType(isAllowlist: Boolean) {
        customDomainDao.deleteAllDomainsByType(isAllowlist)
    }

    override suspend fun updateDomainEnabled(id: Long, isEnabled: Boolean) {
        customDomainDao.updateDomainEnabled(id, isEnabled)
    }

    override suspend fun getAllowlistCount(): Int {
        return customDomainDao.getDomainCountByType(isAllowlist = true)
    }

    override suspend fun getBlocklistCount(): Int {
        return customDomainDao.getDomainCountByType(isAllowlist = false)
    }

    override suspend fun getEnabledAllowlistCount(): Int {
        return customDomainDao.getEnabledDomainCountByType(isAllowlist = true)
    }

    override suspend fun getEnabledBlocklistCount(): Int {
        return customDomainDao.getEnabledDomainCountByType(isAllowlist = false)
    }
}