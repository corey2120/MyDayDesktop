package com.cobrien.myday

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: String?,
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val noteCategories by viewModel.noteCategories.collectAsState()

    val note = remember(noteId) {
        if (noteId.isNullOrBlank()) {
            Note(title = "", content = "")
        } else {
            viewModel.getNoteById(noteId) ?: Note(title = "", content = "")
        }
    }

    var content by remember { mutableStateOf(note.content) }
    var backgroundColor by remember { mutableStateOf(Color(note.backgroundColor)) }
    var textColor by remember { mutableStateOf(Color(note.textColor)) }
    var fontSize by remember { mutableStateOf(note.fontSize) }
    var selectedCategoryId by remember { mutableStateOf(note.categoryId) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }

    val timestamp = remember { System.currentTimeMillis() }
    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Write", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showColorPicker = true }) {
                        Icon(Icons.Default.Palette, "Customize Colors")
                    }
                    IconButton(onClick = { showSaveDialog = true }) {
                        Icon(Icons.Default.Done, "Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = dateFormat.format(Date(timestamp)),
                    style = MaterialTheme.typography.labelLarge,
                    color = textColor.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = {
                        Text(
                            "Start writing your thoughts...\n\nWhat's on your mind?\nWhat happened today?\nIdeas, plans, or reflections?\n\nWrite freely, you'll name this note when you're done.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor.copy(alpha = 0.4f),
                            lineHeight = (fontSize + 8).sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 400.dp),
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize + 8).sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = textColor,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showSaveDialog) {
        var dialogTitle by remember { mutableStateOf(note.title) }

        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(
                    if (note.title != "Untitled") "Rename Note" else "Name Your Note",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        if (note.title != "Untitled")
                            "Edit the title for this note:"
                        else
                            "Give this note a memorable title:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = dialogTitle,
                        onValueChange = { dialogTitle = it },
                        label = { Text("Title") },
                        placeholder = { Text("e.g., Project Ideas") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Leave blank for \"Untitled\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Category selector
                    Text(
                        "Category (optional):",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "None" option
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { selectedCategoryId = null },
                            label = { Text("None") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        noteCategories.forEach { category ->
                            FilterChip(
                                selected = selectedCategoryId == category.id,
                                onClick = { selectedCategoryId = category.id },
                                label = { Text("${category.icon} ${category.name}") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedNote = note.copy(
                            title = dialogTitle.ifBlank { "Untitled" },
                            content = content,
                            backgroundColor = backgroundColor.toArgb().toLong(),
                            textColor = textColor.toArgb().toLong(),
                            fontSize = fontSize,
                            categoryId = selectedCategoryId,
                            lastModified = System.currentTimeMillis()
                        )
                        if (noteId.isNullOrBlank()) {
                            viewModel.addNote(
                                title = updatedNote.title,
                                content = updatedNote.content,
                                backgroundColor = updatedNote.backgroundColor,
                                textColor = updatedNote.textColor,
                                fontSize = updatedNote.fontSize,
                                categoryId = updatedNote.categoryId
                            )
                        } else {
                            viewModel.updateNote(updatedNote)
                        }
                        showSaveDialog = false
                        onBack()
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showColorPicker) {
        CustomizationDialog(
            backgroundColor = backgroundColor,
            textColor = textColor,
            fontSize = fontSize,
            onBackgroundColorChange = { backgroundColor = it },
            onTextColorChange = { textColor = it },
            onFontSizeChange = { fontSize = it },
            onDismiss = { showColorPicker = false }
        )
    }
}

@Composable
fun CustomizationDialog(
    backgroundColor: Color,
    textColor: Color,
    fontSize: Int,
    onBackgroundColorChange: (Color) -> Unit,
    onTextColorChange: (Color) -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Customize Your Note",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Background Color
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Background Theme",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val bgColors = listOf(
                            Color(0xFFFFF3E0) to "Cream",
                            Color(0xFFE0F2F1) to "Mint",
                            Color(0xFFE1F5FE) to "Sky",
                            Color(0xFFFCE4EC) to "Rose",
                            Color(0xFFF3E5F5) to "Lavender"
                        )
                        bgColors.forEach { (color, name) ->
                            Surface(
                                modifier = Modifier
                                    .size(56.dp)
                                    .weight(1f),
                                color = color,
                                shape = MaterialTheme.shapes.medium,
                                border = if (backgroundColor == color)
                                    BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
                                else
                                    BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                                onClick = { onBackgroundColorChange(color) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (backgroundColor == color) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = name,
                                            tint = Color(0xFF5D4037),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Text Color
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Text Color",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val textColors = listOf(
                            Color(0xFF6B4423) to "Brown",
                            Color(0xFF424242) to "Gray",
                            Color(0xFF1565C0) to "Blue",
                            Color(0xFF2E7D32) to "Green",
                            Color(0xFF6A1B9A) to "Purple"
                        )
                        textColors.forEach { (color, name) ->
                            Surface(
                                modifier = Modifier
                                    .size(56.dp)
                                    .weight(1f),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.medium,
                                border = if (textColor == color)
                                    BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
                                else
                                    BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                                onClick = { onTextColorChange(color) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "Aa",
                                        color = color,
                                        fontWeight = if (textColor == color) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Font Size
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Font Size",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "${fontSize}sp",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = fontSize.toFloat(),
                        onValueChange = { onFontSizeChange(it.toInt()) },
                        valueRange = 12f..24f,
                        steps = 11,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Preview text
                    Text(
                        "The quick brown fox jumps over the lazy dog",
                        fontSize = fontSize.sp,
                        color = textColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor, MaterialTheme.shapes.small)
                            .padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
