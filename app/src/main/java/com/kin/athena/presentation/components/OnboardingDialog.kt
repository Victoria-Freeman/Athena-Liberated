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

package com.kin.athena.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kin.athena.R
import kotlin.math.roundToInt

@Composable
fun OnboardingOverlay(
    onDismiss: () -> Unit,
    onFirewallClick: () -> Unit = {},
    targetIconPosition: Offset = Offset.Zero // Pass the actual icon position from parent
) {
    val color = MaterialTheme.colorScheme.background
    val density = LocalDensity.current
    var showTutorialDialog by remember { mutableStateOf(true) }
    
    val cutoutRadius = 24.dp
    val cutoutRadiusPx = with(density) { cutoutRadius.toPx() }
    
    // Use the passed target position or fallback to estimated position
    val cutoutCenter = if (targetIconPosition != Offset.Zero) {
        targetIconPosition
    } else {
        // Fallback estimation for the security icon position
        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        Offset(
            x = with(density) { 52.dp.toPx() },
            y = with(density) { (statusBarHeight + 38.dp).toPx() }
        )
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Background overlay with cutout
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    // Absorb clicks - do nothing
                }
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            // Draw dark overlay
            drawRect(
                color = Color.Black.copy(alpha = 0.7f),
                size = size
            )
            
            // Clear circular area for the icon
            drawCircle(
                color = Color.Transparent,
                radius = cutoutRadiusPx,
                center = cutoutCenter,
                blendMode = BlendMode.Clear
            )
            
            // Add highlight ring around the cutout
            drawCircle(
                color = color,
                radius = cutoutRadiusPx + with(density) { 4.dp.toPx() },
                center = cutoutCenter,
                style = Stroke(width = with(density) { 2.dp.toPx() })
            )
        }
        
        // Clickable area for the security icon
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (cutoutCenter.x - cutoutRadiusPx).roundToInt(),
                        y = (cutoutCenter.y - cutoutRadiusPx).roundToInt()
                    )
                }
                .size(cutoutRadius * 2)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onFirewallClick()
                    onDismiss()
                }
        )
        
        // Tutorial dialog positioned relative to the cutout
        if (showTutorialDialog) {
            TutorialDialog(
                cutoutCenter = cutoutCenter,
                cutoutRadius = cutoutRadiusPx,
                onDismiss = { showTutorialDialog = false }
            )
        }
    }
}

@Composable
private fun TutorialDialog(
    cutoutCenter: Offset,
    cutoutRadius: Float,
    onDismiss: () -> Unit
) {
    val density = LocalDensity.current
    
    // Calculate exact positioning
    val spacingFromIconPx = with(density) { 12.dp.toPx() }
    
    // Position dialog to the right of the icon, perfectly centered vertically
    val dialogX = cutoutCenter.x + cutoutRadius + spacingFromIconPx
    val estimatedDialogHeight = with(density) { 55.dp.toPx() } // Approximate height
    val dialogY = cutoutCenter.y - (estimatedDialogHeight / 2f)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = dialogX.roundToInt(),
                        y = dialogY.roundToInt()
                    )
                }
                .wrapContentSize()
        ) {
            // Center the card content around the target Y position
            Box(
                modifier = Modifier.wrapContentSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Card(
                    modifier = Modifier.widthIn(max = 250.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_tap_security),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}

// Extension function to get icon position from a composable
@Composable
fun Modifier.onIconPositioned(onPosition: (Offset) -> Unit): Modifier {
    return this.onGloballyPositioned { coordinates ->
        val center = Offset(
            x = coordinates.size.width / 2f,
            y = coordinates.size.height / 2f
        )
        val globalCenter = coordinates.localToWindow(center)
        onPosition(globalCenter)
    }
}