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

package com.kin.athena.presentation.screens.settings.subSettings.dns

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.kin.athena.R
import com.kin.athena.domain.model.CustomDomain
import com.kin.athena.presentation.components.material.MaterialTextField
import com.kin.athena.presentation.screens.settings.components.SettingDialog
import com.kin.athena.presentation.screens.settings.components.SettingsBox
import com.kin.athena.presentation.screens.settings.components.SettingType
import com.kin.athena.presentation.screens.settings.components.IconType
import com.kin.athena.presentation.screens.settings.components.settingsContainer
import com.kin.athena.presentation.components.material.MaterialScaffold
import com.kin.athena.presentation.screens.home.components.SearchBar
import com.kin.athena.presentation.screens.settings.components.*
import com.kin.athena.presentation.screens.settings.viewModel.SettingsViewModel
import com.kin.athena.presentation.screens.settings.subSettings.dns.viewModel.DomainManagementViewModel
import com.kin.athena.presentation.screens.settings.subSettings.dns.viewModel.ValidationResult

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DomainManagementScreen(
    navController: NavController,
    settings: SettingsViewModel,
    viewModel: DomainManagementViewModel = hiltViewModel()
) {
    val allowlistDomains by viewModel.allowlistDomains.collectAsState()
    val blocklistDomains by viewModel.blocklistDomains.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    
    var showAddDomainDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    MaterialScaffold(
        topBar = {
            DomainManagementTopBar(
                pagerState = pagerState,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onClearSearch = { searchQuery = "" },
                onAddDomain = { showAddDomainDialog = true },
                onBackClicked = { navController.navigateUp() },
                coroutineScope = coroutineScope
            )
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> DomainSection(
                    domains = allowlistDomains,
                    searchQuery = searchQuery,
                    isAllowlist = true,
                    onDeleteDomain = { viewModel.removeDomain(it) },
                    onToggleEnabled = { viewModel.toggleDomainEnabled(it) }
                )
                1 -> DomainSection(
                    domains = blocklistDomains,
                    searchQuery = searchQuery,
                    isAllowlist = false,
                    onDeleteDomain = { viewModel.removeDomain(it) },
                    onToggleEnabled = { viewModel.toggleDomainEnabled(it) }
                )
            }
        }
    }
    
    // Add domain dialog
    if (showAddDomainDialog) {
        AddDomainDialog(
            isAllowlist = pagerState.currentPage == 0,
            onDismiss = { showAddDomainDialog = false },
            onDomainAdded = { domain, description, isRegex ->
                viewModel.addDomain(domain, description, isRegex, pagerState.currentPage == 0)
                showAddDomainDialog = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DomainManagementTopBar(
    pagerState: PagerState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onAddDomain: () -> Unit,
    onBackClicked: () -> Unit,
    coroutineScope: CoroutineScope
) {
    Column {
        SearchBar(
            text = "Search domains...",
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onClearClick = onClearSearch,
            button = {
                IconButton(onClick = onAddDomain) {
                    Icon(Icons.Rounded.Add, contentDescription = "")
                }
            },
            onBackClicked = onBackClicked
        )

        // Tab selection buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(35.dp, 0.dp, 35.dp, 30.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            DomainSectionButton(
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                pageIndex = 0,
                icon = Icons.Rounded.Shield,
                label = stringResource(R.string.dns_allowlist_title),
                shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp)
            )
            DomainSectionButton(
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                pageIndex = 1,
                icon = Icons.AutoMirrored.Rounded.List,
                label = stringResource(R.string.dns_blocklist_title),
                shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.DomainSectionButton(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    pageIndex: Int,
    icon: ImageVector,
    label: String,
    shape: RoundedCornerShape
) {
    val isSelected = pagerState.currentPage == pageIndex

    Box(
        modifier = Modifier
            .height(48.dp)
            .weight(1f)
            .clip(shape)
            .background(
                color = if (isSelected)
                    MaterialTheme.colorScheme.surfaceContainer
                else
                    MaterialTheme.colorScheme.surfaceContainerLow
            )
            .clickable {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pageIndex)
                }
            }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (isSelected)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                maxLines = 1,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun DomainSection(
    domains: List<CustomDomain>,
    searchQuery: String,
    isAllowlist: Boolean,
    onDeleteDomain: (CustomDomain) -> Unit,
    onToggleEnabled: (CustomDomain) -> Unit
) {
    val filteredDomains = remember(domains, searchQuery) {
        if (searchQuery.isBlank()) {
            domains
        } else {
            domains.filter { domain ->
                domain.domain.contains(searchQuery, ignoreCase = true) ||
                domain.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (filteredDomains.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isAllowlist) Icons.Rounded.Shield else Icons.AutoMirrored.Rounded.List,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) {
                            "No domains found matching \"$searchQuery\""
                        } else if (isAllowlist) {
                            stringResource(R.string.dns_no_domains_allowlist)
                        } else {
                            stringResource(R.string.dns_no_domains_blocklist)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                settingsContainer {
                    filteredDomains.forEach { domain ->
                        DomainCard(
                            domain = domain,
                            onDelete = { onDeleteDomain(domain) },
                            onToggleEnabled = { onToggleEnabled(domain) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DomainCard(
    domain: CustomDomain,
    onDelete: () -> Unit,
    onToggleEnabled: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    val domainTitle = buildString {
        append(domain.domain)
        if (domain.isRegex) append(" • REGEX")
    }
    
    val domainDescription = when {
        domain.description.isNotEmpty() -> domain.description
        domain.isAllowlist -> "Allowed domain"
        else -> "Blocked domain"
    }

    SettingsBox(
        title = domainTitle,
        description = domainDescription,
        icon = IconType.VectorIcon(
            if (domain.isAllowlist) Icons.Rounded.Shield else Icons.Rounded.Block
        ),
        isEnabled = true, // Always show the item, but use text color to indicate enabled state
        actionType = SettingType.CUSTOM,
        customButton = {
            IconButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    )
    
    // Delete confirmation dialog
    if (showDeleteConfirm) {
        SettingDialog(
            text = "Delete Domain",
            onExit = { showDeleteConfirm = false }
        ) {
            Column {
                Text(
                    text = "Are you sure you want to delete this domain?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = domain.domain,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showDeleteConfirm = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(stringResource(R.string.common_cancel))
                    }
                    Button(
                        onClick = {
                            onDelete()
                            showDeleteConfirm = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Delete")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Composable
private fun AddDomainDialog(
    isAllowlist: Boolean,
    onDismiss: () -> Unit,
    onDomainAdded: (String, String, Boolean) -> Unit
) {
    val context = LocalContext.current
    var domain by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }
    
    // Auto-detect regex patterns
    val isRegex = remember(domain.text) {
        val regexChars = listOf(".*", "?", "+", "^", "$", "{", "}", "(", ")", "|", "[", "]", "\\")
        regexChars.any { domain.text.contains(it) }
    }
    
    fun validateAndAdd() {
        when {
            domain.text.isBlank() -> {
                Toast.makeText(context, "Please enter a domain", Toast.LENGTH_SHORT).show()
            }
            isRegex -> {
                try {
                    Regex(domain.text)
                    onDomainAdded(domain.text, description.text, true)
                    onDismiss()
                } catch (_: Exception) {
                    Toast.makeText(context, "Invalid regex pattern", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                // Let ViewModel handle URL extraction and validation
                onDomainAdded(domain.text, description.text, false)
                onDismiss()
            }
        }
    }
    
    SettingDialog(
        text = stringResource(R.string.dns_add_domain),
        onExit = onDismiss
    ) {
        Column {
            // Type indicator card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAllowlist) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isAllowlist) Icons.Rounded.Shield else Icons.AutoMirrored.Rounded.List,
                        contentDescription = null,
                        tint = if (isAllowlist) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAllowlist) 
                            stringResource(R.string.dns_allowlist_title) 
                        else 
                            stringResource(R.string.dns_blocklist_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isAllowlist) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Auto-detect indicator
            if (isRegex) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Regex pattern detected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Domain input
            Text(
                text = if (isRegex) "Regex Pattern" else "Domain",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                MaterialTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = domain,
                    onValueChange = { domain = it },
                    placeholder = if (isRegex) ".*\\.example\\.com" else "example.com",
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Description input
            Text(
                text = "Description (Optional)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                MaterialTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Add a note about this domain...",
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bottom buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(stringResource(R.string.common_cancel))
                }
                Button(
                    onClick = { validateAndAdd() },
                    modifier = Modifier.weight(1f),
                    enabled = domain.text.isNotBlank()
                ) {
                    Text(stringResource(R.string.common_done))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}