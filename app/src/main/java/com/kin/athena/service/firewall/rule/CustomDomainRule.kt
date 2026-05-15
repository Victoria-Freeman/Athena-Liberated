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

package com.kin.athena.service.firewall.rule

import com.kin.athena.core.logging.Logger
import com.kin.athena.domain.model.CustomDomain
import com.kin.athena.domain.repository.CustomDomainRepository
import com.kin.athena.domain.usecase.log.LogUseCases
import com.kin.athena.service.firewall.model.FireWallModel
import com.kin.athena.service.firewall.model.FirewallResult
import com.kin.athena.service.vpn.network.transport.dns.DNSModel
import com.kin.athena.service.firewall.handler.RuleHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class CustomDomainRule @Inject constructor(
    private val customDomainRepository: CustomDomainRepository
) : FirewallRule {
    
    private var allowlistDomains: List<CustomDomain> = emptyList()
    private var blocklistDomains: List<CustomDomain> = emptyList()
    private var isCustomDomainRulesEnabled = true
    private var ruleHandler: RuleHandler? = null

    init {
        observeCustomDomains()
    }

    private fun observeCustomDomains() {
        CoroutineScope(Dispatchers.IO).launch {
            // Observe allowlist domains
            customDomainRepository.getEnabledAllowlistDomains().collect { domains ->
                allowlistDomains = domains
                Logger.info("Updated allowlist domains: ${domains.size} domains")
            }
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            // Observe blocklist domains
            customDomainRepository.getEnabledBlocklistDomains().collect { domains ->
                blocklistDomains = domains
                Logger.info("Updated blocklist domains: ${domains.size} domains")
            }
        }
    }

    fun enableCustomDomainRules() {
        isCustomDomainRulesEnabled = true
        Logger.info("Custom domain rules enabled")
    }
    
    fun disableCustomDomainRules() {
        isCustomDomainRulesEnabled = false
        Logger.info("Custom domain rules disabled")
    }
    
    fun isCustomDomainRulesEnabled(): Boolean {
        return isCustomDomainRulesEnabled
    }
    
    fun setRuleHandler(handler: RuleHandler) {
        ruleHandler = handler
    }

    override fun check(
        packet: FireWallModel,
        dnsModel: DNSModel?,
        logUseCases: LogUseCases,
        result: FirewallResult
    ): FirewallResult {
        // If custom domain rules are disabled, don't affect the result
        if (!isCustomDomainRulesEnabled) {
            return result
        }
        
        dnsModel?.let { dns ->
            val domainName = dns.domainName
            
            // Check blocklist first - if domain matches blocklist, block it
            for (blockDomain in blocklistDomains) {
                if (isDomainMatch(domainName, blockDomain)) {
                    Logger.info("Blocked custom domain: $domainName (matched: ${blockDomain.domain})")
                    return FirewallResult.DNS_BLOCKED
                }
            }
            
            // Check allowlist - if domain matches allowlist, allow it (override other blocks)
            for (allowDomain in allowlistDomains) {
                if (isDomainMatch(domainName, allowDomain)) {
                    Logger.info("Allowed custom domain: $domainName (matched: ${allowDomain.domain})")
                    return FirewallResult.ACCEPT
                }
            }
        }
        
        // No custom domain rules matched, return the current result unchanged
        return result
    }
    
    private fun isDomainMatch(domainName: String, customDomain: CustomDomain): Boolean {
        return if (customDomain.isRegex) {
            try {
                val regex = Regex(customDomain.domain, RegexOption.IGNORE_CASE)
                regex.matches(domainName)
            } catch (e: Exception) {
                Logger.error("Invalid regex pattern in custom domain: ${customDomain.domain}", e)
                false
            }
        } else {
            // Exact match or subdomain match
            domainName.equals(customDomain.domain, ignoreCase = true) ||
            domainName.endsWith(".${customDomain.domain}", ignoreCase = true)
        }
    }
}