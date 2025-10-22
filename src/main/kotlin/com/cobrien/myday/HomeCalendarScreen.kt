package com.cobrien.myday

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Composable
fun HomeCalendarScreen(viewModel: AppViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val taskLists by viewModel.taskLists.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val calendarEvents by viewModel.calendarEvents.collectAsState()

    var selectedDate by remember { mutableStateOf(Date()) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    // Auto-sync Google Calendar when enabled
    LaunchedEffect(settings.googleCalendar.syncEnabled, currentMonth, currentYear) {
        if (settings.googleCalendar.syncEnabled && settings.googleCalendar.selectedCalendars.isNotEmpty()) {
            val calendar = Calendar.getInstance()
            calendar.set(currentYear, currentMonth, 1)
            val startDate = calendar.time

            calendar.add(Calendar.MONTH, 1)
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val endDate = calendar.time

            viewModel.syncGoogleCalendar(startDate, endDate)
        }
    }
    
    val tasksForSelectedDate by remember {
        derivedStateOf {
            tasks.filter { task ->
                try {
                    val taskDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(task.dateTime.substring(0, 10))
                    val selectedLocalDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    val taskLocalDate = taskDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                    taskLocalDate == selectedLocalDate
                } catch (e: Exception) {
                    false
                }
            }
        }
    }

    val eventsForSelectedDate by remember {
        derivedStateOf {
            calendarEvents.filter { event ->
                try {
                    val eventDate = Date(event.startDateTime)
                    val selectedLocalDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    val eventLocalDate = eventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    eventLocalDate == selectedLocalDate
                } catch (e: Exception) {
                    false
                }
            }
        }
    }
    
    if (showAddTaskDialog) {
        val generalList = taskLists.find { it.name == "General" } ?: taskLists.firstOrNull()
        if (generalList != null) {
            EnhancedTaskDialog(
                viewModel = viewModel,
                listId = generalList.id,
                onDismiss = { showAddTaskDialog = false }
            )
        }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Home Widgets with improved spacing
        if (settings.showGreeting) {
            item {
                GreetingWidget()
            }
        }

        if (settings.showQuote) {
            item {
                QuoteWidget()
            }
        }

        if (settings.showNews) {
            item {
                NewsWidget(category = settings.newsCategory, source = settings.newsSource)
            }
        }

        // Calendar Section with enhanced visual hierarchy
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "My Calendar",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    SimpleCalendarView(
                        tasks = tasks,
                        selectedDate = selectedDate,
                        onDateClick = { date ->
                            selectedDate = date
                        },
                        currentMonth = currentMonth,
                        currentYear = currentYear,
                        onMonthChange = { month, year ->
                            currentMonth = month
                            currentYear = year
                        },
                        showHolidays = settings.showHolidays,
                        calendarEvents = calendarEvents
                    )
                }
            }
        }

        // Task Viewer for Selected Date with improved layout
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TaskViewer(
                        selectedDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        tasks = tasksForSelectedDate,
                        taskLists = taskLists,
                        calendarEvents = eventsForSelectedDate,
                        onAddTaskClicked = { showAddTaskDialog = true },
                        onToggleTask = { task ->
                            viewModel.toggleTaskCompleted(task.id)
                        },
                        onDeleteTask = { task ->
                            viewModel.deleteTask(task)
                        },
                        showHolidays = settings.showHolidays
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    viewModel: AppViewModel,
    selectedDate: Date,
    onDismiss: () -> Unit
) {
    var taskDescription by remember { mutableStateOf("") }
    val taskLists by viewModel.taskLists.collectAsState()
    
    val generalList = taskLists.find { it.name == "General" }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Task Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val list = generalList ?: viewModel.addTaskList("General")
                    viewModel.addTask(
                        description = taskDescription,
                        listId = list.id,
                        date = selectedDate
                    )
                    onDismiss()
                },
                enabled = taskDescription.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
