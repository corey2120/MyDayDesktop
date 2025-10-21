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
    
    var selectedDate by remember { mutableStateOf(Date()) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    
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
    
    if (showAddTaskDialog) {
        AddTaskDialog(
            viewModel = viewModel,
            selectedDate = selectedDate,
            onDismiss = { showAddTaskDialog = false }
        )
    }
    
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Home Widgets
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
        
        // Calendar Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "My Calendar",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
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
                    }
                )
            }
        }
        
        // Task Viewer for Selected Date
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                TaskViewer(
                    selectedDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    tasks = tasksForSelectedDate,
                    taskLists = taskLists,
                    onAddTaskClicked = { showAddTaskDialog = true },
                    onToggleTask = { task ->
                        viewModel.toggleTaskCompleted(task.id)
                    },
                    onDeleteTask = { task ->
                        viewModel.deleteTask(task)
                    }
                )
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
