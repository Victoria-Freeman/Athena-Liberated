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

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.kin.athena.R
import com.kin.athena.presentation.components.material.MaterialTextField
import com.kin.athena.presentation.screens.settings.components.SettingDialog
import com.kin.athena.service.vpn.network.util.NetworkConstants

@Composable
fun CustomDnsDialog(
    onExit: () -> Unit,
    onDone: (dns1v4: String, dns2v4: String, dns1v6: String, dns2v6: String) -> Unit,
    dns1v4Key: String?,
    dns2v4Key: String?,
    dns1v6Key: String?,
    dns2v6Key: String?
) {
    val context = LocalContext.current

    var dns1v4 by remember { mutableStateOf(TextFieldValue(dns1v4Key ?: "")) }
    var dns2v4 by remember { mutableStateOf(TextFieldValue(dns2v4Key ?: "")) }
    var dns1v6 by remember { mutableStateOf(TextFieldValue(dns1v6Key ?: "")) }
    var dns2v6 by remember { mutableStateOf(TextFieldValue(dns2v6Key ?: "")) }

    fun isValidIpv4(ip: String): Boolean {
        val ipv4Regex = Regex(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
        )
        return ipv4Regex.matches(ip)
    }

    fun isValidIpv6(ip: String): Boolean {
        val ipv6Regex = Regex(
            "^(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$"
        )
        return ipv6Regex.matches(ip)
    }

    SettingDialog(
        text = stringResource(R.string.dns_custom),
        onExit = { onExit() }
    ) {
        Column {
            // IPv4 Primary DNS
            Text(
                text = "IPv4 Primary DNS",
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
                    value = dns1v4,
                    onValueChange = { dns1v4 = it },
                    placeholder = NetworkConstants.DNS_SERVERS[0].ipv4Primary,
                    singleLine = true,
                    keyboardType = KeyboardType.Number
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // IPv4 Secondary DNS
            Text(
                text = "IPv4 Secondary DNS",
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
                    value = dns2v4,
                    onValueChange = { dns2v4 = it },
                    placeholder = NetworkConstants.DNS_SERVERS[0].ipv4Secondary,
                    singleLine = true,
                    keyboardType = KeyboardType.Number
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // IPv6 Primary DNS
            Text(
                text = "IPv6 Primary DNS",
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
                    value = dns1v6,
                    onValueChange = { dns1v6 = it },
                    placeholder = NetworkConstants.DNS_SERVERS[0].ipv6Primary,
                    singleLine = true,
                    keyboardType = KeyboardType.Ascii
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // IPv6 Secondary DNS
            Text(
                text = "IPv6 Secondary DNS",
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
                    value = dns2v6,
                    onValueChange = { dns2v6 = it },
                    placeholder = NetworkConstants.DNS_SERVERS[0].ipv6Secondary,
                    singleLine = true,
                    keyboardType = KeyboardType.Ascii
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
                        val isValidDns1v4 = dns1v4.text.isNotBlank() && isValidIpv4(dns1v4.text)
                        val isValidDns2v4 = dns2v4.text.isBlank() || isValidIpv4(dns2v4.text)
                        val isValidDns1v6 = dns1v6.text.isBlank() || isValidIpv6(dns1v6.text)
                        val isValidDns2v6 = dns2v6.text.isBlank() || isValidIpv6(dns2v6.text)
                        
                        if (isValidDns1v4 && isValidDns2v4 && isValidDns1v6 && isValidDns2v6) {
                            onDone(
                                dns1v4.text,
                                dns2v4.text.ifBlank { "" },
                                dns1v6.text.ifBlank { "" },
                                dns2v6.text.ifBlank { "" }
                            )
                            onExit()
                        } else {
                            Toast.makeText(context, context.getString(R.string.dns_invalid), Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = dns1v4.text.isNotBlank()
                ) {
                    Text(stringResource(R.string.common_done))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}