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

package com.kin.athena.presentation.screens.settings.subSettings.dns.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.kin.athena.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadDialog(
    title: String,
    isVisible: Boolean,
    downloadState: DownloadState,
    progress: Int = 0,
    stage: String = "",
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null
) {
    if (!isVisible) return
    
    // Animation values
    val infiniteTransition = rememberInfiniteTransition(label = "download_animation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )
    
    val scaleAnimation by animateFloatAsState(
        targetValue = when (downloadState) {
            DownloadState.Success -> 1.2f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "scale"
    )
    
    val containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    
    BasicAlertDialog(
        onDismissRequest = { 
            if (downloadState != DownloadState.Downloading) {
                onDismiss()
            }
        },
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(containerColor)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Icon and content based on state
            when (downloadState) {
                DownloadState.Downloading -> {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.CloudDownload,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(40.dp)
                                .rotate(rotationAngle)
                        )
                    }
                    
                    // Show stage text
                    Text(
                        text = when (stage) {
                            "downloading" -> stringResource(R.string.download_processing)
                            "parsing" -> "Parsing domains..."
                            else -> stringResource(R.string.download_processing)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                    )

                    // Modern progress indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(0.1f))
                    ) {
                        if (progress > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress / 100f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        } else {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.download_please_wait),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        textAlign = TextAlign.Center
                    )
                }
                
                DownloadState.Success -> {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(scaleAnimation)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    
                    Text(
                        text = stringResource(R.string.download_success),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = stringResource(R.string.download_rules_downloaded),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                }
                
                DownloadState.NetworkError -> {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    
                    Text(
                        text = stringResource(R.string.download_network_error),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = stringResource(R.string.download_check_internet),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                }
                
                DownloadState.Error -> {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    
                    Text(
                        text = stringResource(R.string.download_failed_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = stringResource(R.string.download_error_message),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                }
            }
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (downloadState) {
                    DownloadState.Downloading -> {
                        // No buttons during download
                    }
                    
                    DownloadState.Success -> {
                        Spacer(modifier = Modifier.weight(1f))
                        FilledTonalButton(
                            onClick = onDismiss,
                            modifier = Modifier.height(48.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                stringResource(R.string.common_done),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    DownloadState.NetworkError, DownloadState.Error -> {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                            )
                        ) {
                            Text(stringResource(R.string.common_cancel))
                        }
                        
                        if (onRetry != null) {
                            FilledTonalButton(
                                onClick = onRetry,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    stringResource(R.string.common_retry),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Auto-dismiss success dialog after 2.5 seconds
    LaunchedEffect(downloadState) {
        if (downloadState == DownloadState.Success) {
            delay(2500)
            onDismiss()
        }
    }
}

enum class DownloadState {
    Downloading,
    Success,
    NetworkError,
    Error
}