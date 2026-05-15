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

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kin.athena.R
import com.kin.athena.presentation.components.material.MaterialTextField
import com.kin.athena.presentation.screens.settings.components.SettingDialog
import com.kin.athena.presentation.screens.settings.subSettings.dns.viewModel.BlocklistEntry
import com.kin.athena.presentation.screens.settings.subSettings.dns.viewModel.BlocklistValidationState
import com.kin.athena.presentation.screens.settings.subSettings.dns.viewModel.CustomBlocklistViewModel
import com.kin.athena.presentation.screens.settings.subSettings.dns.viewModel.DialogState
import com.kin.athena.presentation.components.material.Chip

@Composable
fun AdvancedCustomBlocklistDialog(
    onExit: () -> Unit,
    onDone: (entry: BlocklistEntry) -> Unit,
    viewModel: CustomBlocklistViewModel = hiltViewModel()
) {
    val importedCategoryLabel = stringResource(R.string.blocklist_imported_category)
    val dialogState by viewModel.dialogState.collectAsState()
    val currentEntry by viewModel.currentEntry.collectAsState()
    val validationState by viewModel.validationState.collectAsState()
    val presetBlocklists by viewModel.presetBlocklists.collectAsState()
    val importResults by viewModel.importResults.collectAsState()
    val isImporting by viewModel.isImporting.collectAsState()

    LaunchedEffect(Unit) {
        if (dialogState is DialogState.Hidden) {
            viewModel.showAddDialog()
        }
    }

    if (dialogState !is DialogState.Hidden) {
        SettingDialog(
            text = when (dialogState) {
                is DialogState.AddingNew -> stringResource(R.string.blocklist_add)
                is DialogState.Editing -> stringResource(R.string.blocklist_edit)
                is DialogState.ImportingFromFile -> stringResource(R.string.blocklist_import_file)
                is DialogState.ImportingFromUrl -> stringResource(R.string.blocklist_import_multiple)
                else -> stringResource(R.string.blocklist_custom)
            },
            onExit = {
                viewModel.hideDialog()
                onExit()
            }
        ) {
            when (dialogState) {
                is DialogState.AddingNew, is DialogState.Editing -> {
                    AddEditBlocklistContent(
                        entry = currentEntry,
                        validationState = validationState,
                        viewModel = viewModel,
                        onSave = { entry ->
                            onDone(entry)
                            viewModel.hideDialog()
                            onExit()
                        }
                    )
                }
                is DialogState.ImportingFromFile, is DialogState.ImportingFromUrl -> {
                    ImportBlocklistContent(
                        importResults = importResults,
                        isImporting = isImporting,
                        presetBlocklists = presetBlocklists,
                        viewModel = viewModel,
                        onImportSelected = { urls ->
                            urls.forEach { url -> 
                                onDone(BlocklistEntry(
                                    url = url,
                                    name = url.substringAfterLast("/").substringBefore("?").take(30),
                                    description = url,
                                    category = importedCategoryLabel
                                ))
                            }
                            viewModel.hideDialog()
                            onExit()
                        }
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun AddEditBlocklistContent(
    entry: BlocklistEntry?,
    validationState: BlocklistValidationState,
    viewModel: CustomBlocklistViewModel,
    onSave: (BlocklistEntry) -> Unit
) {
    val customBlocklistDefault = stringResource(R.string.blocklist_custom)
    var url by remember { mutableStateOf(TextFieldValue(entry?.url ?: "")) }
    var name by remember { mutableStateOf(TextFieldValue(entry?.name ?: "")) }
    var selectedCategory by remember { mutableStateOf(entry?.category ?: "Custom") }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    val categories = listOf("Custom", "Ad Blocking", "Privacy", "Malware", "Social", "Adult Content", "Other")

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // URL Input with Validation
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
                        modifier = Modifier.weight(1f),
                        value = url,
                        onValueChange = { 
                            url = it
                            val normalizedUrl = viewModel.normalizeUrl(it.text)
                            val updatedEntry = (entry ?: BlocklistEntry("")).copy(url = normalizedUrl)
                            viewModel.updateCurrentEntry(updatedEntry)
                        },
                        placeholder = stringResource(R.string.blocklist_url_placeholder),
                        singleLine = true
                    )
                    
                    IconButton(
                        onClick = {
                            if (url.text.isNotBlank()) {
                                viewModel.validateBlocklist(viewModel.normalizeUrl(url.text))
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Validate URL"
                        )
                    }
                }

                // Validation Status
                Spacer(modifier = Modifier.height(8.dp))
                ValidationStatusCard(validationState, viewModel)
            }
        }

        item {
            // Name Input
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
                    value = name,
                    onValueChange = { name = it },
                    placeholder = stringResource(R.string.blocklist_name_placeholder),
                    singleLine = true
                )
            }
        }


        item {
            // Category Selection
            Column {
                Text(
                    text = stringResource(R.string.blocklist_category),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCategoryDropdown = true },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedCategory)
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = "Select Category"
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }


        item {
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.hideDialog()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.common_cancel))
                }
                
                Button(
                    onClick = {
                        val finalEntry = BlocklistEntry(
                            url = viewModel.normalizeUrl(url.text),
                            name = name.text.ifBlank { customBlocklistDefault },
                            description = "",
                            category = selectedCategory,
                            validationState = validationState
                        )
                        onSave(finalEntry)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = url.text.isNotBlank() && 
                             viewModel.isValidUrl(url.text) &&
                             (validationState.isValid == true || !validationState.isValidating)
                ) {
                    Text(stringResource(R.string.blocklist_custom))
                }
            }
        }
    }
}

@Composable
private fun ValidationStatusCard(
    validationState: BlocklistValidationState,
    viewModel: CustomBlocklistViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                validationState.isValidating -> MaterialTheme.colorScheme.surfaceVariant
                validationState.isValid == true -> MaterialTheme.colorScheme.primaryContainer
                validationState.isValid == false -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                validationState.isValidating -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.blocklist_validating),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                validationState.isValid == true -> {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Valid",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.blocklist_valid),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        
                        val details = buildList {
                            validationState.entryCount?.let { 
                                add(viewModel.getFormattedEntryCount(it))
                            }
                            validationState.contentSize?.let { 
                                add(viewModel.getFormattedFileSize(it))
                            }
                        }.joinToString(" • ")
                        
                        if (details.isNotEmpty()) {
                            Text(
                                text = details,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                validationState.isValid == false -> {
                    Icon(
                        imageVector = Icons.Rounded.Error,
                        contentDescription = "Invalid",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.blocklist_validation_failed),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        validationState.errorMessage?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                else -> {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.blocklist_enter_url),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportBlocklistContent(
    importResults: List<String>,
    isImporting: Boolean,
    presetBlocklists: List<BlocklistEntry>,
    viewModel: CustomBlocklistViewModel,
    onImportSelected: (List<String>) -> Unit
) {
    var selectedUrls by remember { mutableStateOf(setOf<String>()) }
    var importText by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.blocklist_import_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            // Import from text/clipboard
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.blocklist_paste_urls),
                            style = MaterialTheme.typography.titleSmall
                        )
                        
                        IconButton(
                            onClick = {
                                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clipData = clipboardManager.primaryClip
                                val clipText = clipData?.getItemAt(0)?.text?.toString() ?: ""
                                if (clipText.isNotEmpty()) {
                                    importText = TextFieldValue(clipText)
                                    viewModel.importFromText(clipText)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentPaste,
                                contentDescription = "Paste from clipboard"
                            )
                        }
                    }

                    MaterialTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = importText,
                        onValueChange = { 
                            importText = it
                            if (it.text.isNotEmpty()) {
                                viewModel.importFromText(it.text)
                            }
                        },
                        placeholder = stringResource(R.string.blocklist_paste_placeholder),
                        singleLine = false
                    )
                }
            }
        }

        if (isImporting) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.blocklist_parsing))
                }
            }
        }

        if (importResults.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.blocklist_found_urls, importResults.size),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }

            items(importResults) { url ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedUrls = if (selectedUrls.contains(url)) {
                                selectedUrls - url
                            } else {
                                selectedUrls + url
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedUrls.contains(url)) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedUrls.contains(url),
                            onCheckedChange = { checked ->
                                selectedUrls = if (checked) {
                                    selectedUrls + url
                                } else {
                                    selectedUrls - url
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SelectionContainer {
                            Text(
                                text = url,
                                style = MaterialTheme.typography.bodySmall,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { 
                            selectedUrls = if (selectedUrls.size == importResults.size) {
                                emptySet()
                            } else {
                                importResults.toSet()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            if (selectedUrls.size == importResults.size) stringResource(R.string.blocklist_deselect_all) else stringResource(R.string.blocklist_select_all)
                        )
                    }
                    
                    Button(
                        onClick = { onImportSelected(selectedUrls.toList()) },
                        enabled = selectedUrls.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.blocklist_import_selected, selectedUrls.size))
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.blocklist_popular_presets),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
        }

        items(presetBlocklists) { preset ->
            PresetBlocklistCard(
                preset = preset,
                isSelected = selectedUrls.contains(preset.url),
                onSelectionChanged = { selected ->
                    selectedUrls = if (selected) {
                        selectedUrls + preset.url
                    } else {
                        selectedUrls - preset.url
                    }
                }
            )
        }

        if (selectedUrls.isNotEmpty() && importResults.isEmpty()) {
            item {
                Button(
                    onClick = { onImportSelected(selectedUrls.toList()) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.blocklist_import_presets, selectedUrls.size))
                }
            }
        }
    }
}

@Composable
private fun PresetBlocklistCard(
    preset: BlocklistEntry,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectionChanged(!isSelected) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChanged
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = preset.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = preset.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row {
                    Chip(
                        onClick = { },
                        label = { 
                            Text(
                                text = preset.category,
                                fontSize = 10.sp
                            ) 
                        },
                        modifier = Modifier.height(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    preset.entryCount?.let { count ->
                        Chip(
                            onClick = { },
                            label = { 
                                Text(
                                    text = stringResource(R.string.blocklist_entries, count / 1000),
                                    fontSize = 10.sp
                                ) 
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }
    }
}