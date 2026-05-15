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

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.kin.athena.R
import com.kin.athena.presentation.components.material.MaterialTextField
import com.kin.athena.presentation.screens.settings.components.SettingDialog
import com.kin.athena.service.vpn.network.util.NetworkConstants

@Composable
fun CustomBlocklistDialog(
    onExit: () -> Unit,
    onDone: (blocklist: String) -> Unit,
) {
    val context = LocalContext.current

    var dns by remember { mutableStateOf(TextFieldValue("")) }

    fun isValidUrl(url: String): Boolean {
        val urlRegex = Regex(
            "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$",
            RegexOption.IGNORE_CASE
        )
        return url.isNotBlank() && urlRegex.matches(url)
    }

    SettingDialog(
        text = stringResource(R.string.dns_custom),
        onExit = { onExit() }
    ) {
        Column {
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
                    value = dns,
                    onValueChange = { dns = it },
                    placeholder = "https://blocklist.example.com",
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isValidUrl(dns.text)) {
                        onDone(dns.text)
                        onExit()
                    } else {
                        Toast.makeText(context, context.getString(R.string.dns_invalid), Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(R.string.common_done))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}