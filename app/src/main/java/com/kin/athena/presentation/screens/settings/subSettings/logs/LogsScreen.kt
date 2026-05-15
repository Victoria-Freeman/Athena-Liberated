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

package com.kin.athena.presentation.screens.settings.subSettings.logs

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Dns
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kin.athena.R
import com.kin.athena.core.utils.extensions.*
import com.kin.athena.domain.model.Log
import com.kin.athena.presentation.components.material.MaterialPlaceholder
import com.kin.athena.presentation.components.material.MaterialScaffold
import com.kin.athena.presentation.screens.home.components.SearchBar
import com.kin.athena.presentation.screens.settings.subSettings.logs.viewModel.LogsViewModel
import com.kin.athena.service.firewall.utils.Port
import java.util.Locale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.kin.athena.presentation.navigation.routes.LogRoutes
import com.kin.athena.presentation.screens.settings.subSettings.logs.components.NetworkStatsSection
import com.kin.athena.service.firewall.model.FirewallResult
import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogsScreen(
    onBackNavClicked: () -> Unit,
    navController: NavController,
    logsViewModel: LogsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val networkStats = logsViewModel.networkStats.collectAsState()
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    val filteredLogs = logsViewModel.filteredLogs.collectAsState()

    MaterialScaffold(
        topBar = {
            LogsTopBar(
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                query = logsViewModel.query.value,
                onQueryChange = logsViewModel::onQueryChanged,
                onClearClick = logsViewModel::clearQuery,
                onDeleteClick = logsViewModel::deleteLogs,
                onBackClicked = onBackNavClicked
            )
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> DnsRequestsSection(
                    logs = filteredLogs.value,
                    context = context,
                    navController = navController
                )
                1 -> PacketsSection(
                    logs = filteredLogs.value,
                    context = context,
                    navController = navController
                )
                2 -> StatsSection(networkStats = networkStats.value)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogsTopBar(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onBackClicked: () -> Unit
) {
    Column {
        SearchBar(
            text = stringResource(R.string.logs_search_packets),
            query = query,
            onQueryChange = onQueryChange,
            onClearClick = onClearClick,
            button = {
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Rounded.Delete, contentDescription = null)
                }
            },
            onBackClicked = onBackClicked
        )

        // Mode selection buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(35.dp, 0.dp, 35.dp, 30.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            SectionButton(
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                pageIndex = 0,
                icon = Icons.Rounded.Dns,
                label = stringResource(R.string.logs_dns),
                shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp)
            )
            SectionButton(
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                pageIndex = 1,
                icon = Icons.Rounded.Description,
                label = stringResource(R.string.logs_packets),
                shape = RoundedCornerShape(0.dp)
            )
            SectionButton(
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                pageIndex = 2,
                icon = Icons.Rounded.Analytics,
                label = stringResource(R.string.logs_stats),
                shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.SectionButton(
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
fun PacketsSection(
    logs: List<List<Log>>,
    context: Context,
    navController: NavController
) {
    // Show all packets regardless of whether destinationAddress is null
    val packetsWithDomains = remember(logs) {
        logs.filter { it.isNotEmpty() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (packetsWithDomains.isEmpty()) {
            MaterialPlaceholder(
                placeholderIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Description,
                        contentDescription = null,
                        modifier = Modifier.scale(2f),
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                placeholderText = stringResource(R.string.logs_no_logs)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
            ) {
                items(
                    items = packetsWithDomains,
                    key = { logGroup -> logGroup.firstOrNull()?.id ?: 0 }
                ) { logGroup ->
                    if (logGroup.firstOrNull()?.packageID != 0) {
                        LogGroupCard(
                            logGroup = logGroup,
                            context = context,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DnsRequestsSection(
    logs: List<List<Log>>,
    context: Context,
    navController: NavController
) {
    // Filter logs to show only DNS queries/responses (port 53)
    val dnsLogs = remember(logs) {
        logs.map { logGroup ->
            logGroup.filter { log ->
                log.destinationPort == "53" || log.sourcePort == "53"
            }
        }.filter { it.isNotEmpty() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (dnsLogs.isEmpty()) {
            MaterialPlaceholder(
                placeholderIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Dns,
                        contentDescription = null,
                        modifier = Modifier.scale(2f),
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                placeholderText = stringResource(R.string.logs_no_dns_requests)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
            ) {
                items(
                    items = dnsLogs,
                    key = { logGroup -> logGroup.firstOrNull()?.id ?: 0 }
                ) { logGroup ->
                    if (logGroup.firstOrNull()?.packageID != 0) {
                        LogGroupCard(
                            logGroup = logGroup,
                            context = context,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsSection(networkStats: com.kin.athena.domain.model.NetworkStatsState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        NetworkStatsSection(networkStats = networkStats)

        Spacer(modifier = Modifier.height(16.dp))

        // Additional stats can be added here in the future
        MaterialPlaceholder(
            placeholderIcon = {
                Icon(
                    imageVector = Icons.Rounded.Analytics,
                    contentDescription = null,
                    modifier = Modifier.scale(2f),
                    tint = MaterialTheme.colorScheme.outline
                )
            },
            placeholderText = stringResource(R.string.logs_stats_info)
        )
    }
}

@Composable
fun LogsContent(logs: List<List<Log>>, context: Context, navController: NavController) {
    if (logs.isEmpty()) {
        MaterialPlaceholder(
            placeholderIcon = {
                Icon(
                    imageVector = Icons.Rounded.FileCopy,
                    contentDescription = null,
                    modifier = Modifier.scale(2f),
                    tint = MaterialTheme.colorScheme.outline
                )
            },
            placeholderText = stringResource(R.string.logs_no_logs)
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
        ) {
            items(logs) { logGroup ->
                if (logGroup.firstOrNull()?.packageID != 0) {
                    LogGroupCard(
                        logGroup = logGroup,
                        context = context,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun LogGroupCard(logGroup: List<Log>, context: Context, includeHeader: Boolean = true, navController: NavController) {
    val application = context.uidToApplication(logGroup.first().packageID)
    val iconBitmap = application?.getApplicationIcon(
        context.packageManager,
        MaterialTheme.colorScheme.onSurface.toArgb(),
        context = context
    )?.toBitmap()?.asImageBitmap()
    application?.getApplicationName(context.packageManager)?.let {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .background(
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (includeHeader) {
                    ApplicationHeader(
                        appName = it,
                        appIcon = iconBitmap,
                        timestamp = logGroup.first().time.toFormattedDateTime()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                LogDetails(logGroup, navController)
            }
        }
    }
}

@Composable
fun ApplicationHeader(appName: String, appIcon: androidx.compose.ui.graphics.ImageBitmap?, timestamp: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        appIcon?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(text = appName)
            Text(text = timestamp, fontSize = 12.sp)
        }
    }
}

@Composable
fun LogDetails(logs: List<Log>, navController: NavController) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
    ) {
        logs.forEach { log ->
            val color = when (log.packetStatus) {
                FirewallResult.ACCEPT -> Color.Unspecified
                FirewallResult.DROP -> MaterialTheme.colorScheme.error
                FirewallResult.DNS_BLOCKED -> MaterialTheme.colorScheme.error
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                    .clickable {
                        navController.safeNavigate(LogRoutes.Packet.createRoute(log.id))
                    }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 18.dp, vertical = 4.dp)
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        color = color,
                        text = (Port.getServiceName(log.destinationPort.toInt()) ?: log.destinationPort).uppercase(Locale.ROOT),
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        color = color,
                        text = if (log.destinationAddress != null) extractBaseDomain(log.destinationAddress) else log.destinationIP,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .weight(2f)
                            .padding(horizontal = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        tint = if (color != MaterialTheme.colorScheme.error) LocalContentColor.current else color,
                        imageVector = Icons.Rounded.ArrowForwardIos,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .scale(0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

fun extractBaseDomain(fullDomain: String): String {
    return try {
        val url = URL("http://$fullDomain")
        val host = url.host
        val parts = host.split(".")
        if (parts.size >= 2) {
            "${parts[parts.size - 2]}.${parts[parts.size - 1]}"
        } else {
            host
        }
    } catch (e: Exception) {
        fullDomain
    }
}