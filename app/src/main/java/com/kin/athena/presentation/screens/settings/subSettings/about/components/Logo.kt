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

package com.kin.athena.presentation.screens.settings.subSettings.about.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kin.athena.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LogoWithBlob(onTripleClick: () -> Unit) {
    val blobDrawable: Painter = painterResource(id = R.drawable.blob)
    val logoPainter: Painter = painterResource(id = R.drawable.logo)

    var clickCount by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    fun handleClick() {
        clickCount++
        if (clickCount == 3) {
            onTripleClick()
            clickCount = 0
        } else {
            coroutineScope.launch {
                delay(500)
                clickCount = 0
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(300.dp)
        ) {
            Image(
                painter = blobDrawable,
                contentDescription = "Blob Shape",
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { handleClick() })
                    },
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceContainer)
            )
            Image(
                painter = logoPainter,
                contentDescription = "Logo Image",
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { handleClick() })
                    }
                    .size(120.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
    }
}