package com.cobrien.myday

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedTaskDialog(
    viewModel: AppViewModel,
    listId: String,
    taskId: String? = null,
    onDismiss: () -> Unit
) {
    val existingTask = taskId?.let { viewModel.getTaskById(it) }
    val availableTags by viewModel.availableTags.collectAsState()

    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var notes by remember { mutableStateOf(existingTask?.notes ?: "") }
    var selectedDate by remember { mutableStateOf<Date?>(
        existingTask?.dateTime?.let { parseTaskDateTime(it) } ?: Date()
    ) }
    var priority by remember { mutableStateOf(existingTask?.priority ?: TaskPriority.NONE) }
    var selectedTags by remember { mutableStateOf(existingTask?.tags ?: emptyList()) }
    var subtasks by remember { mutableStateOf(existingTask?.subtasks ?: emptyList()) }
    var recurrenceType by remember { mutableStateOf(existingTask?.recurrence?.type ?: RecurrenceType.NONE) }
    var recurrenceInterval by remember { mutableStateOf(existingTask?.recurrence?.interval ?: 1) }

    var showPriorityPicker by remember { mutableStateOf(false) }
    var showTagPicker by remember { mutableStateOf(false) }
    var showRecurrencePicker by remember { mutableStateOf(false) }
    var newSubtaskText by remember { mutableStateOf("") }
    var newTagText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (existingTask != null) "Edit Task" else "New Task",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (description.isNotBlank()) {
                                    val recurrence = RecurrenceConfig(
                                        type = recurrenceType,
                                        interval = recurrenceInterval
                                    )

                                    if (existingTask != null) {
                                        viewModel.updateTask(
                                            existingTask.copy(
                                                description = description,
                                                notes = notes,
                                                dateTime = formatTaskDateTime(selectedDate),
                                                priority = priority,
                                                tags = selectedTags,
                                                subtasks = subtasks,
                                                recurrence = recurrence
                                            )
                                        )
                                    } else {
                                        viewModel.addTask(
                                            description = description,
                                            listId = listId,
                                            date = selectedDate,
                                            priority = priority,
                                            tags = selectedTags,
                                            subtasks = subtasks,
                                            recurrence = recurrence,
                                            notes = notes
                                        )
                                    }
                                    onDismiss()
                                }
                            },
                            enabled = description.isNotBlank()
                        ) {
                            Text("Save")
                        }
                    }
                }

                // Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Task Description
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Task Description *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // Priority Selector
                    item {
                        PrioritySelector(
                            priority = priority,
                            onPriorityChange = { priority = it }
                        )
                    }

                    // Tags Section
                    item {
                        TagsSection(
                            selectedTags = selectedTags,
                            availableTags = availableTags,
                            onTagAdd = { tag ->
                                if (tag.isNotBlank() && !selectedTags.contains(tag)) {
                                    selectedTags = selectedTags + tag
                                }
                            },
                            onTagRemove = { tag ->
                                selectedTags = selectedTags - tag
                            }
                        )
                    }

                    // Subtasks Section
                    item {
                        SubtasksSection(
                            subtasks = subtasks,
                            onSubtaskAdd = { description ->
                                if (description.isNotBlank()) {
                                    subtasks = subtasks + Subtask(description = description)
                                }
                            },
                            onSubtaskToggle = { subtaskId ->
                                subtasks = subtasks.map {
                                    if (it.id == subtaskId) it.copy(isCompleted = !it.isCompleted) else it
                                }
                            },
                            onSubtaskDelete = { subtaskId ->
                                subtasks = subtasks.filter { it.id != subtaskId }
                            }
                        )
                    }

                    // Recurrence Section
                    item {
                        RecurrenceSelector(
                            recurrenceType = recurrenceType,
                            interval = recurrenceInterval,
                            onRecurrenceTypeChange = { recurrenceType = it },
                            onIntervalChange = { recurrenceInterval = it }
                        )
                    }

                    // Notes Section
                    item {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Additional Notes") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp),
                            maxLines = 5
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrioritySelector(
    priority: TaskPriority,
    onPriorityChange: (TaskPriority) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Priority",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskPriority.values().forEach { p ->
                FilterChip(
                    selected = priority == p,
                    onClick = { onPriorityChange(p) },
                    label = { Text(p.toDisplayName()) },
                    leadingIcon = if (priority == p) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(p.toColor()).copy(alpha = 0.3f),
                        selectedLabelColor = Color(p.toColor())
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagsSection(
    selectedTags: List<String>,
    availableTags: List<String>,
    onTagAdd: (String) -> Unit,
    onTagRemove: (String) -> Unit
) {
    var newTagText by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Tags",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        // Selected Tags
        if (selectedTags.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedTags.forEach { tag ->
                    AssistChip(
                        onClick = { onTagRemove(tag) },
                        label = { Text(tag) },
                        trailingIcon = {
                            Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                        }
                    )
                }
            }
        }

        // Tag Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTagText,
                onValueChange = {
                    newTagText = it
                    showSuggestions = it.isNotBlank()
                },
                label = { Text("Add tag") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            IconButton(
                onClick = {
                    if (newTagText.isNotBlank()) {
                        onTagAdd(newTagText.trim())
                        newTagText = ""
                        showSuggestions = false
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add tag")
            }
        }

        // Tag Suggestions
        if (showSuggestions && newTagText.isNotBlank()) {
            val suggestions = availableTags.filter {
                it.contains(newTagText, ignoreCase = true) && !selectedTags.contains(it)
            }.take(5)

            if (suggestions.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        suggestions.forEach { suggestion ->
                            Text(
                                suggestion,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onTagAdd(suggestion)
                                        newTagText = ""
                                        showSuggestions = false
                                    }
                                    .padding(8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubtasksSection(
    subtasks: List<Subtask>,
    onSubtaskAdd: (String) -> Unit,
    onSubtaskToggle: (String) -> Unit,
    onSubtaskDelete: (String) -> Unit
) {
    var newSubtaskText by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Subtasks (${subtasks.count { it.isCompleted }}/${subtasks.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        // Existing Subtasks
        subtasks.forEach { subtask ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onSubtaskToggle(subtask.id) }) {
                    Icon(
                        if (subtask.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = if (subtask.isCompleted) "Completed" else "Not completed",
                        tint = if (subtask.isCompleted)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Text(
                    text = subtask.description,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else null,
                    color = if (subtask.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { onSubtaskDelete(subtask.id) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Add New Subtask
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newSubtaskText,
                onValueChange = { newSubtaskText = it },
                label = { Text("Add subtask") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            IconButton(
                onClick = {
                    if (newSubtaskText.isNotBlank()) {
                        onSubtaskAdd(newSubtaskText.trim())
                        newSubtaskText = ""
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add subtask")
            }
        }
    }
}

@Composable
private fun RecurrenceSelector(
    recurrenceType: RecurrenceType,
    interval: Int,
    onRecurrenceTypeChange: (RecurrenceType) -> Unit,
    onIntervalChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Recurrence",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        // Recurrence Type Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                RecurrenceType.NONE to "None",
                RecurrenceType.DAILY to "Daily",
                RecurrenceType.WEEKLY to "Weekly",
                RecurrenceType.MONTHLY to "Monthly"
            ).forEach { (type, label) ->
                FilterChip(
                    selected = recurrenceType == type,
                    onClick = { onRecurrenceTypeChange(type) },
                    label = { Text(label) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Interval Selector (if not NONE)
        if (recurrenceType != RecurrenceType.NONE) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Every")
                OutlinedTextField(
                    value = interval.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { value ->
                            if (value > 0) onIntervalChange(value)
                        }
                    },
                    modifier = Modifier.width(80.dp),
                    singleLine = true
                )
                Text(
                    when (recurrenceType) {
                        RecurrenceType.DAILY -> if (interval == 1) "day" else "days"
                        RecurrenceType.WEEKLY -> if (interval == 1) "week" else "weeks"
                        RecurrenceType.MONTHLY -> if (interval == 1) "month" else "months"
                        RecurrenceType.YEARLY -> if (interval == 1) "year" else "years"
                        RecurrenceType.NONE -> ""
                    }
                )
            }
        }
    }
}
