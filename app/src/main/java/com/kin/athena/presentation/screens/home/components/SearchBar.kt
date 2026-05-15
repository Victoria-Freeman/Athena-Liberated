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

package com.kin.athena.presentation.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kin.athena.R
import com.kin.athena.presentation.components.material.MaterialButton
import com.kin.athena.presentation.components.onIconPositioned

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    showStartInfo: Boolean = false,
    text: String = stringResource(R.string.common_search),
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    button: @Composable () -> Unit,
    onFirewallClicked: (() -> Unit)? = null,
    firewallColor: Color? = null,
    onBackClicked: (() -> Unit)? = null,
    onFirewallIconPositioned: ((Offset) -> Unit)? = null
) {
    val isTyping = query.isNotBlank()
    val searchBarScale by animateFloatAsState(
        targetValue = if (isTyping) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 300f
        ),
        label = "searchBarScale"
    )
    
    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp, 0.dp, 24.dp, 18.dp)
            .scale(searchBarScale),
        query = query,
        placeholder = { Text(text = if (showStartInfo) LocalContext.current.getString(R.string.search_bar_placeholder) else text) },
        leadingIcon = {
            if (onFirewallClicked != null && firewallColor != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        modifier = onFirewallIconPositioned?.let { 
                            Modifier.onIconPositioned(it) 
                        } ?: Modifier,
                        onClick = { onFirewallClicked() }
                    ) {
                        Icon(Icons.Rounded.Security, tint = firewallColor, contentDescription = "")
                    }
                }
            } else {
                if (onBackClicked != null) {
                    IconButton(onClick = {onBackClicked() }) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "", modifier = Modifier.scale(0.8f))
                    }
                }
            }
        },
        trailingIcon = {
            Row(
                modifier = Modifier.padding(end = 6.dp)
            ) {
                AnimatedVisibility(
                    visible = query.isNotBlank(),
                    enter = slideInHorizontally(
                        animationSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = 300f
                        ),
                        initialOffsetX = { it / 2 }
                    ) + fadeIn(
                        animationSpec = tween(250)
                    ) + scaleIn(
                        animationSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = 400f
                        ),
                        initialScale = 0.7f
                    ),
                    exit = slideOutHorizontally(
                        animationSpec = tween(200),
                        targetOffsetX = { it / 2 }
                    ) + fadeOut(
                        animationSpec = tween(200)
                    ) + scaleOut(
                        animationSpec = tween(200),
                        targetScale = 0.7f
                    )
                ) {
                    MaterialButton(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = ""
                    ) {
                        onClearClick()
                    }
                }
                
                AnimatedVisibility(
                    visible = query.isBlank(),
                    enter = slideInHorizontally(
                        animationSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = 300f
                        ),
                        initialOffsetX = { -it / 2 }
                    ) + fadeIn(
                        animationSpec = tween(250)
                    ) + scaleIn(
                        animationSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = 400f
                        ),
                        initialScale = 0.7f
                    ),
                    exit = slideOutHorizontally(
                        animationSpec = tween(200),
                        targetOffsetX = { -it / 2 }
                    ) + fadeOut(
                        animationSpec = tween(200)
                    ) + scaleOut(
                        animationSpec = tween(200),
                        targetScale = 0.7f
                    )
                ) {
                    button()
                }
            }
        },
        onQueryChange = onQueryChange,
        onSearch = onQueryChange,
        onActiveChange = {},
        active = false,
        colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {}
}
