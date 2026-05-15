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

package com.kin.athena.presentation.screens.settings.subSettings.lock.components

import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kin.athena.presentation.screens.settings.subSettings.lock.viewModel.LockScreenViewModel
import com.kin.athena.presentation.screens.settings.viewModel.SettingsViewModel
import kotlin.math.sqrt
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.kin.athena.R
import com.kin.athena.core.utils.extensions.popUpToTop
import com.kin.athena.presentation.navigation.routes.HomeRoutes
import com.kin.athena.presentation.navigation.routes.SettingRoutes
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PatternLock(
    settingsViewModel: SettingsViewModel,
    lockScreenViewModel: LockScreenViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current

    BackHandler {
        if (settingsViewModel.settings.value.pattern != null) {
            (context as? ComponentActivity)?.finish()
        } else {
            navController.navigateUp()
        }
    }

    val rowCount = 3
    val columnCount = 3

    val dotColor = MaterialTheme.colorScheme.primary
    val pathColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val successColor = Color.Green
    
    // Pattern validation state
    var patternStatus by remember { mutableStateOf("") }
    
    // Clear status after delay
    LaunchedEffect(patternStatus) {
        if (patternStatus == "Wrong Pattern") {
            delay(2000)
            patternStatus = ""
            lockScreenViewModel.clearPattern()
        } else if (patternStatus.isNotEmpty()) {
            delay(1500)
            patternStatus = ""
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 64.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val text = when {
            patternStatus.isNotEmpty() -> patternStatus
            settingsViewModel.settings.value.pattern == null -> stringResource(id = R.string.lock_setup_pattern)
            settingsViewModel.settings.value.pattern != null -> stringResource(id = R.string.lock_pattern)
            else -> "Unknown action"
        }

        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = when (patternStatus) {
                "Correct!" -> Color.Green
                "Wrong Pattern" -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onBackground
            }
        )

        Canvas(
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
                .background(MaterialTheme.colorScheme.background)
                .onSizeChanged { size ->
                    lockScreenViewModel.canvasSize = Size(size.width.toFloat(), size.height.toFloat())
                }
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            lockScreenViewModel.canvasSize
                                .takeIf { it != Size.Zero }
                                ?.let { size ->
                                    val cell = getNearestCell(Offset(it.x, it.y), size.width, size.height)
                                    if (cell != null) {
                                        lockScreenViewModel.clearPattern()
                                        lockScreenViewModel.selectedCellsIndexList.add(cell.index)
                                        lockScreenViewModel.selectedCellCenterList.add(cell.center)
                                        lockScreenViewModel.lastCellCenter = cell.center
                                        lockScreenViewModel.path = Path().apply { moveTo(cell.center.x, cell.center.y) }
                                        lockScreenViewModel.currentTouchOffset = Offset(it.x, it.y)
                                    } else {
                                        // Don't start pattern if not touching a dot
                                        lockScreenViewModel.clearPattern()
                                    }
                                }
                        }

                        MotionEvent.ACTION_MOVE -> {
                            // Only process move events if we have a valid pattern started
                            if (lockScreenViewModel.selectedCellsIndexList.isNotEmpty()) {
                                val touchOffset = Offset(it.x, it.y)
                                lockScreenViewModel.currentTouchOffset = touchOffset

                                lockScreenViewModel.canvasSize
                                    .takeIf { it != Size.Zero }
                                    ?.let { size ->
                                        val cell = getNearestCell(touchOffset, size.width, size.height)

                                        if (cell != null && cell.index !in lockScreenViewModel.selectedCellsIndexList) {
                                            lockScreenViewModel.selectedCellsIndexList.add(cell.index)
                                            lockScreenViewModel.selectedCellCenterList.add(cell.center)
                                            lockScreenViewModel.updatePath(cell.center)
                                            lockScreenViewModel.lastCellCenter = cell.center
                                        }
                                    }
                            }
                        }


                        MotionEvent.ACTION_UP -> {
                            // Clear finger following line when lifting thumb
                            lockScreenViewModel.currentTouchOffset = null
                            
                            if (lockScreenViewModel.selectedCellsIndexList.isNotEmpty()) {
                                if (settingsViewModel.settings.value.pattern == null) {
                                    // Setting new pattern
                                    settingsViewModel.update(
                                        settingsViewModel.settings.value.copy(
                                            passcode = null,
                                            pattern = lockScreenViewModel.selectedCellsIndexList.joinToString(""),
                                            fingerprint = false
                                        )
                                    )
                                    settingsViewModel.updateDefaultRoute(SettingRoutes.LockScreen.createRoute(null),)
                                    patternStatus = "Pattern Set!"
                                    lockScreenViewModel.clearPattern()
                                } else {
                                    // Verifying existing pattern
                                    if (settingsViewModel.settings.value.pattern == lockScreenViewModel.selectedCellsIndexList.joinToString("")) {
                                        patternStatus = "Correct!"
                                        // Success - navigate to home after delay
                                        navController.navigate(HomeRoutes.Home.route) { popUpToTop(navController) }
                                    } else {
                                        patternStatus = "Wrong Pattern"
                                        // Don't clear immediately - let user see the wrong pattern
                                    }
                                }
                            } else {
                                lockScreenViewModel.clearPattern()
                            }
                        }
                    }
                    true
                }
        ) {
            val width = size.width
            val height = size.height

            val boxSizeInX = width / columnCount
            val boxCenterInX = boxSizeInX / 2
            val boxSizeInY = height / rowCount
            val boxCenterInY = boxSizeInY / 2

            // Draw dots
            for (row in 0 until rowCount) {
                for (column in 0 until columnCount) {
                    val center = Offset(
                        (boxCenterInX + boxSizeInX * column),
                        (boxCenterInY + boxSizeInY * row)
                    )
                    val dotIndex = column + 1 + row * columnCount
                    val isSelected = dotIndex in lockScreenViewModel.selectedCellsIndexList
                    
                    // Draw main dot
                    val currentDotColor = when {
                        patternStatus == "Wrong Pattern" -> errorColor  // All dots red on error
                        patternStatus == "Correct!" && isSelected -> successColor
                        else -> dotColor  // All dots primary color, no transparency
                    }
                    
                    drawCircle(
                        color = currentDotColor,
                        radius = if (isSelected) 30f else 25f,
                        center = center
                    )
                }
            }

            // Draw path
            if (lockScreenViewModel.selectedCellCenterList.size > 1) {
                val currentPathColor = when (patternStatus) {
                    "Wrong Pattern" -> errorColor
                    "Correct!" -> successColor
                    else -> pathColor
                }
                
                drawPath(
                    path = lockScreenViewModel.path,
                    color = currentPathColor,
                    style = Stroke(
                        width = 20f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // Draw live finger-following line
            lockScreenViewModel.currentTouchOffset?.let { offset ->
                lockScreenViewModel.lastCellCenter?.let { lastCenter ->
                    // Always draw line to current finger position
                    val fingerFollowPath = Path().apply {
                        moveTo(lastCenter.x, lastCenter.y)
                        lineTo(offset.x, offset.y)
                    }
                    
                    // Draw main finger-following line
                    drawPath(
                        path = fingerFollowPath,
                        brush = Brush.linearGradient(
                            listOf(
                                pathColor.copy(alpha = 0.7f),
                                pathColor.copy(alpha = 0.3f)
                            ),
                            start = Offset(lastCenter.x, lastCenter.y),
                            end = Offset(offset.x, offset.y)
                        ),
                        style = Stroke(
                            width = 18f,
                            cap = StrokeCap.Round
                        )
                    )
                    
                    // Add a subtle glow effect
                    drawPath(
                        path = fingerFollowPath,
                        color = pathColor.copy(alpha = 0.2f),
                        style = Stroke(
                            width = 25f,
                            cap = StrokeCap.Round
                        )
                    )
                    
                    // Draw finger position indicator
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                pathColor.copy(alpha = 0.6f),
                                pathColor.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            radius = 20f
                        ),
                        radius = 20f,
                        center = offset
                    )
                    
                    drawCircle(
                        color = pathColor.copy(alpha = 0.8f),
                        radius = 8f,
                        center = offset
                    )
                }
            }
        }
    }
}


data class CellModel(
    val index: Int,
    val center: Offset
)

private fun getNearestCell(offset: Offset, width: Float, height: Float): CellModel? {
    val rowCount = 3
    val columnCount = 3

    val boxSizeInX = width / columnCount
    val boxCenterInX = boxSizeInX / 2
    val boxSizeInY = height / rowCount
    val boxCenterInY = boxSizeInY / 2

    val circleRadius = width / 8

    for (row in 0 until rowCount) {
        for (column in 0 until columnCount) {
            val cellCenter = Offset(
                (boxCenterInX + boxSizeInX * column),
                (boxCenterInY + boxSizeInY * row)
            )
            val distanceFromCenter = sqrt(
                (offset.x - cellCenter.x) * (offset.x - cellCenter.x) +
                        (offset.y - cellCenter.y) * (offset.y - cellCenter.y)
            )

            if (distanceFromCenter < circleRadius) {
                val index = column + 1 + row * columnCount
                return CellModel(index, cellCenter)
            }
        }
    }
    return null
}
