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

package com.kin.athena.presentation.screens.settings.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kin.athena.R
import com.kin.athena.core.utils.extensions.toBitmap
import com.kin.athena.presentation.components.CircleWrapper
import com.kin.athena.presentation.components.material.MaterialText

enum class SettingType {
    RADIOBUTTON,
    SWITCH,
    LINK,
    TEXT,
    CUSTOM,
    CLIPBOARD
}


fun LazyListScope.settingsContainer(content: @Composable () -> Unit) {
    item {
        Column(
            modifier = Modifier.clip(RoundedCornerShape(32.dp))
        ) {
            content()
        }
    }
}

sealed class IconType {
    data class VectorIcon(val imageVector: ImageVector) : IconType()
    data class DrawableIcon(val drawable: Drawable) : IconType()
}

@Composable
fun SettingBoxSmall(
    title: String,
    description: String,
    originalPrice: String? = null,
    salePrice: String? = null,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = dimensionResource(id = R.dimen.card_padding_bottom))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.tertiary
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(
                horizontal = dimensionResource(id = R.dimen.card_padding_horizontal),
                vertical = 12.dp
            )
            .clickable {
                onAction()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            text = title,
            color = MaterialTheme.colorScheme.background
        )
        Spacer(modifier = Modifier.weight(1f))
        if (originalPrice != null && salePrice != null) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    fontWeight = FontWeight.Light,
                    fontSize = 10.sp,
                    text = originalPrice,
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                    textDecoration = TextDecoration.LineThrough
                )
                Text(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    text = salePrice,
                    color = MaterialTheme.colorScheme.background
                )
            }
        } else {
            Text(
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                text = description,
                color = MaterialTheme.colorScheme.background
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        CircleWrapper(
            size = 3.dp,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Icon(
                modifier = Modifier.scale(0.6f),
                imageVector = Icons.Rounded.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SettingsBox(
    title: String,
    description: String? = null,
    icon: IconType? = null,
    size: Dp = 12.dp,
    isEnabled: Boolean = true,
    isUsable: Boolean = true,
    isCentered: Boolean = false,
    actionType: SettingType,
    variable: Boolean? = null,
    onSwitchEnabled: (Boolean) -> Unit = {},
    onLinkClicked: () -> Unit = {},
    customButton: @Composable () -> Unit = { RenderCustomIcon() },
    customAction: @Composable (() -> Unit) -> Unit = {},
    customText: String = "",
    clipboardText: String = "",
    circleWrapperColor: Color = MaterialTheme.colorScheme.background,
    circleWrapperSize: Dp = 6.dp,
    onNotUsableClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    var showCustomAction by remember { mutableStateOf(false) }
    if (showCustomAction) customAction { showCustomAction = !showCustomAction }

    AnimatedVisibility(visible = isEnabled) {
        Box(
            modifier = Modifier
                .padding(bottom = dimensionResource(id = R.dimen.card_padding_bottom))
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (isUsable) MaterialTheme.colorScheme.surfaceContainer 
                    else MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
                )
                .clickable {
                    if (isUsable) {
                        handleAction(
                            context,
                            actionType,
                            variable,
                            onSwitchEnabled,
                            { showCustomAction = !showCustomAction },
                            onLinkClicked,
                            clipboardText
                        )
                    } else {
                        onNotUsableClicked()
                    }
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.card_padding_horizontal),
                        vertical = size
                    )
                    .fillMaxWidth()
            ) {
                Row(
                    Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    icon?.let {
                        when (it) {
                            is IconType.VectorIcon -> {
                                CircleWrapper(
                                    size = 12.dp,
                                    color = circleWrapperColor
                                ) {
                                    Icon(
                                        imageVector = it.imageVector,
                                        contentDescription = null,
                                        tint = if (isUsable) MaterialTheme.colorScheme.primary 
                                               else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    )
                                }
                            }
                            is IconType.DrawableIcon -> {
                                CircleWrapper(
                                    size = circleWrapperSize,
                                    color = circleWrapperColor
                                ) {
                                    Image(
                                        bitmap = it.drawable.toBitmap().asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .height(38.dp - circleWrapperSize)
                                            .width(38.dp - circleWrapperSize)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    if (actionType != SettingType.LINK && !description.isNullOrBlank()) {
                        MaterialText(
                            title = title,
                            description = description.ifBlank { clipboardText },
                            titleColor = if (isUsable) MaterialTheme.colorScheme.onSurface 
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            descriptionColor = if (isUsable) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                              else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    } else {
                        Text(
                            title,
                            color = if (isUsable) MaterialTheme.colorScheme.onSurface 
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            textAlign = if (isCentered) TextAlign.Center else TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                RenderActionComponent(actionType, variable, onSwitchEnabled, onLinkClicked, customText, customButton)
            }
        }
    }
}

private fun handleAction(
    context: Context,
    actionType: SettingType,
    variable: Boolean?,
    onSwitchEnabled: (Boolean) -> Unit,
    customAction: () -> Unit,
    onLinkClicked: () -> Unit,
    clipboardText: String
) {
    when (actionType) {
        SettingType.RADIOBUTTON -> onSwitchEnabled(variable == false)
        SettingType.SWITCH -> onSwitchEnabled(variable == false)
        SettingType.LINK -> onLinkClicked()
        SettingType.CUSTOM -> customAction()
        SettingType.CLIPBOARD -> copyToClipboard(context, clipboardText)
        SettingType.TEXT -> { /* No action needed */ }
    }
}

@Composable
private fun RenderClipboardIcon() {
    Icon(
        imageVector = Icons.Default.ContentCopy,
        contentDescription = null,
        modifier = Modifier.padding(dimensionResource(id = R.dimen.icon_padding)),
        tint = MaterialTheme.colorScheme.primary
    )
}

fun copyToClipboard(context: Context, clipboardText: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(context.getString(com.kin.athena.R.string.dialog_copied), clipboardText)
    clipboard.setPrimaryClip(clip)
}

@Composable
private fun RenderActionComponent(
    actionType: SettingType,
    variable: Boolean?,
    onSwitchEnabled: (Boolean) -> Unit,
    onLinkClicked: () -> Unit,
    customText: String,
    customButton: @Composable () -> Unit,
) {
    when (actionType) {
        SettingType.RADIOBUTTON -> RenderRadioButton(variable, onSwitchEnabled)
        SettingType.SWITCH -> RenderSwitch(variable, onSwitchEnabled)
        SettingType.LINK -> RenderLinkIcon(onLinkClicked)
        SettingType.TEXT -> RenderText(customText)
        SettingType.CLIPBOARD -> RenderClipboardIcon()
        SettingType.CUSTOM -> customButton()
    }
}

@Composable
private fun RenderRadioButton(variable: Boolean?, onSwitchEnabled: (Boolean) -> Unit) {
    RadioButton(
        selected = variable == true,
        onClick = { onSwitchEnabled(true) }
    )
}

@Composable
private fun RenderSwitch(variable: Boolean?, onSwitchEnabled: (Boolean) -> Unit) {
    Switch(
        checked = variable == true,
        onCheckedChange = { onSwitchEnabled(it) },
        modifier = Modifier
            .scale(0.9f)
            .padding(0.dp)
    )
}

@Composable
private fun RenderLinkIcon(onLinkClicked: () -> Unit) {
    Icon(
        imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
        contentDescription = null,
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.icon_padding))
            .clickable { onLinkClicked() },
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun RenderCustomIcon() {
    Icon(
        imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
        contentDescription = null,
        modifier = Modifier
            .scale(0.6f)
            .padding(dimensionResource(id = R.dimen.icon_padding))
    )
}

@Composable
private fun RenderText(customText: String) {
    Text(
        text = customText,
        fontSize = 14.sp,
        modifier = Modifier.padding(dimensionResource(id = R.dimen.icon_padding))
    )
}
