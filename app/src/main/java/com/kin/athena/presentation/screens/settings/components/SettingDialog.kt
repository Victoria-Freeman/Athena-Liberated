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

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kin.athena.R
import com.kin.athena.presentation.components.material.MaterialTextField

@Composable
fun SettingDialog(
    text: String,
    onExit: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = { onExit() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(12.dp, 10.dp, 12.dp, 0.dp)
                .fillMaxWidth(0.85f)
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                fontSize = 20.sp,
            )
            content()
        }
    }
}


@Composable
fun IpDialog(
    onExit: () -> Unit,
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onFinished: () -> Unit,
    allowOnlyIPv4: Boolean = false,
    allowOnlyIPv6: Boolean = false,
) {
    val context = LocalContext.current

    fun isValidIp(ip: String): Boolean {
        val ipv4Regex = Regex(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
        )
        val ipv6Regex = Regex(
            "^(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|(([0-9a-fA-F]{1,4}:){1,7}:)|(([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4})|" +
                    "(([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2})|(([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3})|" +
                    "(([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4})|(([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5})|" +
                    "([0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6}))|(:((:[0-9a-fA-F]{1,4}){1,7}|:))|" +
                    "(fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,})|" +
                    "::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]|[1-9]|)[0-9])\\.){3,3}" +
                    "(25[0-5]|(2[0-4]|1{0,1}[0-9]|[1-9]|)[0-9])|" +
                    "([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]|[1-9]|)[0-9])\\.){3,3}" +
                    "(25[0-5]|(2[0-4]|1{0,1}[0-9]|[1-9]|)[0-9]))$"
        )

        return when {
            allowOnlyIPv4 -> ipv4Regex.matches(ip)
            allowOnlyIPv6 -> ipv6Regex.matches(ip)
            else -> ipv4Regex.matches(ip) || ipv6Regex.matches(ip)
        }
    }

    SettingDialog(
        text = stringResource(R.string.dialog_add_ip),
        onExit = onExit
    ) {
        Column {
            // IP type indicator
            Text(
                text = when {
                    allowOnlyIPv4 -> "IPv4 Address"
                    allowOnlyIPv6 -> "IPv6 Address"
                    else -> "IP Address"
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(16.dp)
                )
            ) {
                MaterialTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = textFieldValue,
                    onValueChange = onValueChange,
                    placeholder = when {
                        allowOnlyIPv4 -> "192.168.1.1"
                        allowOnlyIPv6 -> "2001:db8::1"
                        else -> "Enter IP address"
                    },
                    singleLine = true,
                    keyboardType = if (allowOnlyIPv4) KeyboardType.Number else KeyboardType.Text
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Bottom buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onExit,
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
                        if (isValidIp(textFieldValue.text)) {
                            onFinished()
                            onExit()
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.dialog_invalid_ip),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = textFieldValue.text.isNotBlank()
                ) {
                    Text(stringResource(R.string.common_done))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
