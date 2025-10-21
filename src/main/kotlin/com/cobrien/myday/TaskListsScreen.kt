package com.cobrien.myday

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TaskListsScreen(
    viewModel: AppViewModel,
    onTaskListClick: (String) -> Unit
) {
    val taskLists by viewModel.taskLists.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    var showAddListDialog by remember { mutableStateOf(false) }
    
    if (showAddListDialog) {
        AddTaskListDialog(
            viewModel = viewModel,
            onDismiss = { showAddListDialog = false }
        )
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (taskLists.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "No task lists yet",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Create your first task list",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(taskLists) { taskList ->
                    val taskCount = tasks.count { it.listId == taskList.id }
                    val completedCount = tasks.count { it.listId == taskList.id && it.isCompleted }
                    
                    TaskListCard(
                        taskList = taskList,
                        taskCount = taskCount,
                        completedCount = completedCount,
                        onClick = { onTaskListClick(taskList.id) }
                    )
                }
            }
        }
        
        FloatingActionButton(
            onClick = { showAddListDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task List")
        }
    }
}

@Composable
private fun TaskListCard(
    taskList: TaskList,
    taskCount: Int,
    completedCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(taskList.color)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = taskList.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = taskList.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (taskCount > 0) "$completedCount / $taskCount tasks completed" else "No tasks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskListDialog(
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    var listName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(0xFF6200EE) }
    
    val availableColors = listOf(
        0xFF6200EE to "Purple",
        0xFFE91E63 to "Pink",
        0xFFF44336 to "Red",
        0xFFFF9800 to "Orange",
        0xFF4CAF50 to "Green",
        0xFF2196F3 to "Blue",
        0xFF00BCD4 to "Cyan",
        0xFF9C27B0 to "Deep Purple"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Task List") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextField(
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("List Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Choose Color", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableColors.forEach { (color, _) ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                                .clickable { selectedColor = color }
                                .then(
                                    if (color == selectedColor) {
                                        Modifier.padding(4.dp)
                                    } else {
                                        Modifier
                                    }
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.addTaskList(listName, selectedColor)
                    onDismiss()
                },
                enabled = listName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
