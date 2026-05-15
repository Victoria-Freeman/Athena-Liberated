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

package com.kin.athena.presentation.screens.settings.subSettings.network.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kin.athena.core.logging.Logger
import com.kin.athena.domain.model.Ip
import com.kin.athena.domain.usecase.networkFilter.NetworkFilterUseCases
import com.kin.athena.service.utils.manager.FirewallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class IpDialogViewModel @Inject constructor(
    val firewallManager: FirewallManager,
) : ViewModel() {

    private val _dialogIpv4TextField: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
    val dialogIpv4TextField: State<TextFieldValue> = _dialogIpv4TextField


    fun updateIpv4DialogText(text: TextFieldValue) {
        Logger.error(text.text)
        _dialogIpv4TextField.value = text
    }

    private val _dialogIpv6TextField: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
    val dialogIpv6TextField: State<TextFieldValue> = _dialogIpv6TextField


    fun updateIpv6DialogText(text: TextFieldValue) {
        _dialogIpv6TextField.value = text
    }
}
