        package com.cobrien.myday

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showNewsCategoryDialog by remember { mutableStateOf(false) }
    var showNewsSourceDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
            // Home Screen Widgets Section
            Text(
                "Home Screen Widgets",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggle(
                        title = "Greeting",
                        description = "Show personalized greeting",
                        checked = settings.showGreeting,
                        onCheckedChange = { viewModel.setShowGreeting(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingToggle(
                        title = "Daily Quote",
                        description = "Show inspirational quote",
                        checked = settings.showQuote,
                        onCheckedChange = { viewModel.setShowQuote(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingToggle(
                        title = "News Feed",
                        description = "Show latest news headlines",
                        checked = settings.showNews,
                        onCheckedChange = { viewModel.setShowNews(it) }
                    )
                    if (settings.showNews) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("News Source", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    settings.newsSource,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TextButton(onClick = { showNewsSourceDialog = true }) {
                                Text("Change")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("News Category", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    settings.newsCategory.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TextButton(onClick = { showNewsCategoryDialog = true }) {
                                Text("Change")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Appearance Section
            Text(
                "Appearance",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggle(
                        title = "Dark Mode",
                        description = "Use dark color scheme",
                        checked = settings.isDarkMode,
                        onCheckedChange = { viewModel.setDarkMode(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Theme", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                settings.themeName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(onClick = { showThemeDialog = true }) {
                            Text("Change")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Calendar Sync Section
            Text(
                "Calendar Sync",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CalendarSyncSection(viewModel)

            Spacer(modifier = Modifier.height(24.dp))

            // About Section
            Text(
                "About",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("MyDay Desktop", style = MaterialTheme.typography.titleMedium)
                    Text("Version 1.0.1", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Task management with calendar and notes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
    }

    if (showThemeDialog) {
        ThemeSwitcherDialog(
            currentTheme = settings.themeName,
            onThemeSelected = { theme ->
                viewModel.setTheme(theme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
    
    if (showNewsCategoryDialog) {
        NewsCategoryDialog(
            currentCategory = settings.newsCategory,
            onCategorySelected = { category ->
                viewModel.setNewsCategory(category)
                showNewsCategoryDialog = false
            },
            onDismiss = { showNewsCategoryDialog = false }
        )
    }

    if (showNewsSourceDialog) {
        NewsSourceDialog(
            currentSource = settings.newsSource,
            onSourceSelected = { source ->
                viewModel.setNewsSource(source)
                showNewsSourceDialog = false
            },
            onDismiss = { showNewsSourceDialog = false }
        )
    }
}

@Composable
private fun SettingToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun ThemeSwitcherDialog(
    currentTheme: String,
    onThemeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose a Theme") },
        text = {
            Column {
                availableThemes.forEach { themeName ->
                    Text(
                        text = themeName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onThemeSelected(themeName)
                            }
                            .padding(vertical = 12.dp),
                        color = if (themeName == currentTheme)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun NewsCategoryDialog(
    currentCategory: String,
    onCategorySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val categories = listOf(
        "general" to "General",
        "technology" to "Technology",
        "business" to "Business",
        "science" to "Science",
        "health" to "Health",
        "sports" to "Sports"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("News Category") },
        text = {
            Column {
                categories.forEach { (id, name) ->
                    Text(
                        text = name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCategorySelected(id)
                            }
                            .padding(vertical = 12.dp),
                        color = if (id == currentCategory)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}


@Composable
private fun NewsSourceDialog(
    currentSource: String,
    onSourceSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sources = listOf(
        "BBC" to "BBC News - Global coverage",
        "CNN" to "CNN - Top stories",
        "NPR" to "NPR - National Public Radio",
        "THE VERGE" to "The Verge - Tech & culture",
        "ARS TECHNICA" to "Ars Technica - Tech analysis",
        "ENGADGET" to "Engadget - Tech news",
        "TECHCRUNCH" to "TechCrunch - Startups & tech",
        "WIRED" to "Wired - Technology & science"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select News Source") },
        text = {
            Column {
                sources.forEach { (id, name) ->
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSourceSelected(id)
                            }
                            .padding(vertical = 12.dp),
                        color = if (id == currentSource)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun CalendarSyncSection(viewModel: AppViewModel) {
    var syncMessage by remember { mutableStateOf<String?>(null) }
    var showMessage by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Calendar Sync",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Export and import tasks in iCalendar format (compatible with Google Calendar, Outlook, Apple Calendar, etc.)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Export button
            Button(
                onClick = {
                    val fileChooser = javax.swing.JFileChooser()
                    fileChooser.dialogTitle = "Export Tasks to Calendar"
                    fileChooser.selectedFile = java.io.File("MyDay_Tasks.ics")
                    fileChooser.fileFilter = javax.swing.filechooser.FileNameExtensionFilter("iCalendar files (*.ics)", "ics")

                    if (fileChooser.showSaveDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
                        val file = fileChooser.selectedFile
                        val filePath = if (file.extension != "ics") "${file.absolutePath}.ics" else file.absolutePath

                        val success = viewModel.exportTasksToCalendar(filePath)
                        syncMessage = if (success) {
                            "✓ Successfully exported tasks to $filePath"
                        } else {
                            "✗ Failed to export tasks"
                        }
                        showMessage = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export Tasks to Calendar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Import button
            OutlinedButton(
                onClick = {
                    val fileChooser = javax.swing.JFileChooser()
                    fileChooser.dialogTitle = "Import Tasks from Calendar"
                    fileChooser.fileFilter = javax.swing.filechooser.FileNameExtensionFilter("iCalendar files (*.ics)", "ics")

                    if (fileChooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
                        val file = fileChooser.selectedFile
                        val importedCount = viewModel.importTasksFromCalendar(file.absolutePath)

                        syncMessage = if (importedCount > 0) {
                            "✓ Successfully imported $importedCount task(s)"
                        } else {
                            "No new tasks to import"
                        }
                        showMessage = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Import Tasks from Calendar")
            }

            // Show sync message
            if (showMessage && syncMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = if (syncMessage!!.startsWith("✓"))
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = syncMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (syncMessage!!.startsWith("✓"))
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showMessage = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("×", fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}
