package com.cobrien.myday

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

enum class Screen {
    HOME,
    TASK_LISTS,
    TASKS_FOR_LIST,
    NOTES,
    NOTE_EDITOR,
    SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDayApp() {
    val viewModel = remember { AppViewModel() }
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedTaskListId by remember { mutableStateOf<String?>(null) }
    var selectedNoteId by remember { mutableStateOf<String?>(null) }

    // Get selected theme from settings
    val settings by viewModel.settings.collectAsState()
    val colorScheme = getThemeByName(settings.themeName, settings.isDarkMode)

    MaterialTheme(colorScheme = colorScheme) {
        Scaffold(
            topBar = {
                when (currentScreen) {
                    Screen.HOME -> {
                        TopAppBar(
                            title = { Text("MyDay") },
                            actions = {
                                IconButton(onClick = { currentScreen = Screen.SETTINGS }) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                                }
                            }
                        )
                    }
                    Screen.TASK_LISTS -> {
                        TopAppBar(
                            title = { Text("Task Lists") },
                            navigationIcon = {
                                IconButton(onClick = { currentScreen = Screen.HOME }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            },
                            actions = {
                                IconButton(onClick = { currentScreen = Screen.SETTINGS }) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                                }
                            }
                        )
                    }
                    Screen.TASKS_FOR_LIST -> {
                        val taskLists by viewModel.taskLists.collectAsState()
                        val taskList = taskLists.find { it.id == selectedTaskListId }
                        TopAppBar(
                            title = { Text(taskList?.name ?: "Tasks") },
                            navigationIcon = {
                                IconButton(onClick = { currentScreen = Screen.TASK_LISTS }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        )
                    }
                    Screen.NOTES -> {
                        TopAppBar(
                            title = { Text("Notes") },
                            navigationIcon = {
                                IconButton(onClick = { currentScreen = Screen.HOME }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            },
                            actions = {
                                IconButton(onClick = { currentScreen = Screen.SETTINGS }) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                                }
                            }
                        )
                    }
                    Screen.NOTE_EDITOR -> {
                        // NoteEditorScreen has its own TopAppBar
                    }
                    Screen.SETTINGS -> {
                        TopAppBar(
                            title = { Text("Settings") },
                            navigationIcon = {
                                IconButton(onClick = { currentScreen = Screen.HOME }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        )
                    }
                }
            },
            bottomBar = {
                if (currentScreen != Screen.NOTE_EDITOR &&
                    currentScreen != Screen.TASKS_FOR_LIST &&
                    currentScreen != Screen.SETTINGS) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            label = { Text("Home") },
                            selected = currentScreen == Screen.HOME,
                            onClick = { currentScreen = Screen.HOME }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                            label = { Text("Tasks") },
                            selected = currentScreen == Screen.TASK_LISTS,
                            onClick = { currentScreen = Screen.TASK_LISTS }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Description, contentDescription = null) },
                            label = { Text("Notes") },
                            selected = currentScreen == Screen.NOTES,
                            onClick = { currentScreen = Screen.NOTES }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentScreen) {
                    Screen.HOME -> {
                        HomeCalendarScreen(viewModel = viewModel)
                    }
                    Screen.TASK_LISTS -> {
                        TaskListsScreen(
                            viewModel = viewModel,
                            onTaskListClick = { listId ->
                                selectedTaskListId = listId
                                currentScreen = Screen.TASKS_FOR_LIST
                            }
                        )
                    }
                    Screen.TASKS_FOR_LIST -> {
                        selectedTaskListId?.let { listId ->
                            TasksForListScreen(
                                viewModel = viewModel,
                                listId = listId
                            )
                        }
                    }
                    Screen.NOTES -> {
                        NotesScreen(
                            viewModel = viewModel,
                            onNoteClick = { noteId ->
                                selectedNoteId = noteId
                                currentScreen = Screen.NOTE_EDITOR
                            }
                        )
                    }
                    Screen.NOTE_EDITOR -> {
                        NoteEditorScreen(
                            noteId = selectedNoteId,
                            viewModel = viewModel,
                            onBack = {
                                selectedNoteId = null
                                currentScreen = Screen.NOTES
                            }
                        )
                    }
                    Screen.SETTINGS -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.HOME }
                        )
                    }
                }
            }
        }
    }
}
