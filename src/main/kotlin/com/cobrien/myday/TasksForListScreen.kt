package com.cobrien.myday

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksForListScreen(
    viewModel: AppViewModel,
    listId: String
) {
    val tasks by viewModel.tasks.collectAsState()
    val taskLists by viewModel.taskLists.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    val taskList = taskLists.find { it.id == listId }
    val tasksForList = tasks.filter { it.listId == listId }
    val incompleteTasks = tasksForList.filter { !it.isCompleted }
    val completedTasks = tasksForList.filter { it.isCompleted }

    if (showAddTaskDialog) {
        AddTaskToListDialog(
            viewModel = viewModel,
            listId = listId,
            onDismiss = { showAddTaskDialog = false }
        )
    }

    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                Button(
                    onClick = {
                        taskToDelete?.let { viewModel.deleteTask(it) }
                        taskToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTaskDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        if (tasksForList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No tasks in this list",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Add a task to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (incompleteTasks.isNotEmpty()) {
                    item {
                        Text(
                            "TO DO",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(incompleteTasks) { task ->
                        TaskListItem(
                            task = task,
                            taskList = taskList,
                            onToggle = { viewModel.toggleTaskCompleted(task.id) },
                            onDelete = { taskToDelete = task }
                        )
                    }
                }

                if (completedTasks.isNotEmpty()) {
                    item {
                        Text(
                            "COMPLETED",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(completedTasks) { task ->
                        TaskListItem(
                            task = task,
                            taskList = taskList,
                            onToggle = { viewModel.toggleTaskCompleted(task.id) },
                            onDelete = { taskToDelete = task }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskListItem(
    task: Task,
    taskList: TaskList?,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onToggle) {
            Icon(
                if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                contentDescription = if (task.isCompleted) "Mark incomplete" else "Mark complete",
                tint = taskList?.let { Color(it.color) } ?: MaterialTheme.colorScheme.primary
            )
        }
        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        ) {
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                color = if (task.isCompleted)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else
                    MaterialTheme.colorScheme.onSurface
            )
            val taskDate = parseDateTime(task.dateTime)
            val today = Calendar.getInstance()
            val taskCal = Calendar.getInstance().apply { if (taskDate != null) time = taskDate as Date }
            if (taskDate != null &&
                !(taskCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        taskCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))) {
                Text(
                    text = java.text.SimpleDateFormat("MMM d", Locale.getDefault()).format(taskDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete task",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskToListDialog(
    viewModel: AppViewModel,
    listId: String,
    onDismiss: () -> Unit
) {
    var taskDescription by remember { mutableStateOf("") }
    var hasDate by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Date()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Task Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = hasDate,
                        onCheckedChange = { hasDate = it }
                    )
                    Text("Set due date", modifier = Modifier.padding(start = 8.dp))
                }
                if (hasDate) {
                    Text(
                        "Due: ${java.text.SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(selectedDate)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.addTask(
                        description = taskDescription,
                        listId = listId,
                        date = if (hasDate) selectedDate else null
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

private fun parseDateTime(dateTime: String): Date? {
    return try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(dateTime)
    } catch (e: Exception) {
        null
    }
}
