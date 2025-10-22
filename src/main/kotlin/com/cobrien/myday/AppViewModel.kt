package com.cobrien.myday

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppViewModel {
    private val repository = DataRepository()
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _appData = MutableStateFlow(AppData())
    val appData: StateFlow<AppData> = _appData.asStateFlow()
    
    val tasks: StateFlow<List<Task>> = MutableStateFlow(emptyList())
    val taskLists: StateFlow<List<TaskList>> = MutableStateFlow(listOf(
        TaskList(id = "general", name = "General", color = 0xFF6200EE)
    ))
    val notes: StateFlow<List<Note>> = MutableStateFlow(emptyList())
    val noteCategories: StateFlow<List<NoteCategory>> = MutableStateFlow(listOf(
        NoteCategory(id = "personal", name = "Personal", color = 0xFF2196F3, icon = "👤"),
        NoteCategory(id = "work", name = "Work", color = 0xFFFF9800, icon = "💼"),
        NoteCategory(id = "ideas", name = "Ideas", color = 0xFF9C27B0, icon = "💡")
    ))
    val calendarEvents: StateFlow<List<CalendarEvent>> = MutableStateFlow(emptyList())
    val availableTags: StateFlow<List<String>> = MutableStateFlow(emptyList())
    val settings: StateFlow<AppSettings> = MutableStateFlow(AppSettings())

    private val googleCalendarService = GoogleCalendarService()
    
    var isLoading by mutableStateOf(true)
        private set
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            isLoading = true
            val data = repository.loadData()
            _appData.value = data
            (tasks as MutableStateFlow).value = data.tasks
            (taskLists as MutableStateFlow).value = data.taskLists
            (notes as MutableStateFlow).value = data.notes
            (calendarEvents as MutableStateFlow).value = data.calendarEvents
            (availableTags as MutableStateFlow).value = data.availableTags
            (settings as MutableStateFlow).value = data.settings
            isLoading = false
        }
    }
    
    private fun saveData() {
        viewModelScope.launch {
            val data = AppData(
                tasks = tasks.value,
                taskLists = taskLists.value,
                notes = notes.value,
                calendarEvents = calendarEvents.value,
                availableTags = availableTags.value,
                settings = settings.value
            )
            _appData.value = data
            repository.saveData(data)
        }
    }

    private fun updateAvailableTags() {
        val allTags = tasks.value.flatMap { it.tags }.distinct().sorted()
        (availableTags as MutableStateFlow).value = allTags
    }
    
    // Task operations
    fun addTask(
        description: String,
        listId: String,
        date: Date?,
        priority: TaskPriority = TaskPriority.NONE,
        tags: List<String> = emptyList(),
        subtasks: List<Subtask> = emptyList(),
        recurrence: RecurrenceConfig = RecurrenceConfig(),
        notes: String = ""
    ) {
        val dateTime = formatTaskDateTime(date)
        val newTask = Task(
            description = description,
            dateTime = dateTime,
            listId = listId,
            priority = priority,
            tags = tags,
            subtasks = subtasks,
            recurrence = recurrence,
            notes = notes
        )
        (tasks as MutableStateFlow).value = tasks.value + newTask
        updateAvailableTags()
        saveData()
    }

    fun updateTask(task: Task) {
        val updatedTasks = tasks.value.map {
            if (it.id == task.id) task else it
        }
        (tasks as MutableStateFlow).value = updatedTasks
        updateAvailableTags()
        saveData()
    }
    
    fun toggleTaskCompleted(taskId: String) {
        val updatedTasks = tasks.value.map { task ->
            if (task.id == taskId) {
                val updated = task.copy(isCompleted = !task.isCompleted)
                // Handle recurring tasks
                if (updated.isCompleted && updated.recurrence.type != RecurrenceType.NONE) {
                    val nextDate = updated.recurrence.getNextDate(parseTaskDateTime(updated.dateTime) ?: Date())
                    if (nextDate != null) {
                        // Create new instance for next recurrence
                        val newTask = updated.copy(
                            id = UUID.randomUUID().toString(),
                            dateTime = formatTaskDateTime(nextDate),
                            isCompleted = false,
                            subtasks = updated.subtasks.map { it.copy(id = UUID.randomUUID().toString(), isCompleted = false) }
                        )
                        viewModelScope.launch {
                            (tasks as MutableStateFlow).value = tasks.value + newTask
                            saveData()
                        }
                    }
                }
                updated
            } else {
                task
            }
        }
        (tasks as MutableStateFlow).value = updatedTasks
        saveData()
    }

    fun toggleSubtaskCompleted(taskId: String, subtaskId: String) {
        val updatedTasks = tasks.value.map { task ->
            if (task.id == taskId) {
                val updatedSubtasks = task.subtasks.map { subtask ->
                    if (subtask.id == subtaskId) {
                        subtask.copy(isCompleted = !subtask.isCompleted)
                    } else {
                        subtask
                    }
                }
                task.copy(subtasks = updatedSubtasks)
            } else {
                task
            }
        }
        (tasks as MutableStateFlow).value = updatedTasks
        saveData()
    }

    fun addSubtask(taskId: String, description: String) {
        val updatedTasks = tasks.value.map { task ->
            if (task.id == taskId) {
                val newSubtask = Subtask(description = description)
                task.copy(subtasks = task.subtasks + newSubtask)
            } else {
                task
            }
        }
        (tasks as MutableStateFlow).value = updatedTasks
        saveData()
    }

    fun deleteSubtask(taskId: String, subtaskId: String) {
        val updatedTasks = tasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(subtasks = task.subtasks.filter { it.id != subtaskId })
            } else {
                task
            }
        }
        (tasks as MutableStateFlow).value = updatedTasks
        saveData()
    }
    
    fun deleteTask(task: Task) {
        (tasks as MutableStateFlow).value = tasks.value.filter { it.id != task.id }
        updateAvailableTags()
        saveData()
    }

    // Search and filter operations
    fun searchTasks(query: String): List<Task> {
        if (query.isBlank()) return tasks.value

        val lowerQuery = query.lowercase()
        return tasks.value.filter { task ->
            task.description.lowercase().contains(lowerQuery) ||
            task.notes.lowercase().contains(lowerQuery) ||
            task.tags.any { it.lowercase().contains(lowerQuery) } ||
            task.subtasks.any { it.description.lowercase().contains(lowerQuery) }
        }
    }

    fun filterTasksByPriority(priority: TaskPriority): List<Task> {
        return tasks.value.filter { it.priority == priority }
    }

    fun filterTasksByTags(tags: List<String>): List<Task> {
        if (tags.isEmpty()) return tasks.value
        return tasks.value.filter { task ->
            tags.any { tag -> task.tags.contains(tag) }
        }
    }

    fun filterTasksByList(listId: String): List<Task> {
        return tasks.value.filter { it.listId == listId }
    }

    fun getTaskById(taskId: String): Task? {
        return tasks.value.find { it.id == taskId }
    }
    
    // Task List operations
    fun addTaskList(name: String, color: Long = 0xFF6200EE): TaskList {
        val newList = TaskList(name = name, color = color)
        (taskLists as MutableStateFlow).value = taskLists.value + newList
        saveData()
        return newList
    }
    
    fun deleteTaskList(listId: String) {
        if (listId == "general") return // Don't delete default list
        
        (tasks as MutableStateFlow).value = tasks.value.filter { it.listId != listId }
        (taskLists as MutableStateFlow).value = taskLists.value.filter { it.id != listId }
        saveData()
    }
    
    // Note operations
    fun addNote(
        title: String = "",
        content: String,
        backgroundColor: Long = 0xFFFFF3E0,
        textColor: Long = 0xFF6B4423,
        fontSize: Int = 16,
        categoryId: String? = null
    ) {
        val newNote = Note(
            title = title,
            content = content,
            backgroundColor = backgroundColor,
            categoryId = categoryId,
            textColor = textColor,
            fontSize = fontSize
        )
        (notes as MutableStateFlow).value = notes.value + newNote
        saveData()
    }
    
    fun updateNote(note: Note) {
        val updatedNotes = notes.value.map {
            if (it.id == note.id) note else it
        }
        (notes as MutableStateFlow).value = updatedNotes
        saveData()
    }
    
    fun deleteNote(note: Note) {
        (notes as MutableStateFlow).value = notes.value.filter { it.id != note.id }
        saveData()
    }
    
    fun getNoteById(id: String): Note? {
        return notes.value.find { it.id == id }
    }
    
    // Settings operations
    fun setShowGreeting(show: Boolean) {
        (settings as MutableStateFlow).value = settings.value.copy(showGreeting = show)
        saveData()
    }
    
    fun setShowQuote(show: Boolean) {
        (settings as MutableStateFlow).value = settings.value.copy(showQuote = show)
        saveData()
    }
    
    fun setShowNews(show: Boolean) {
        (settings as MutableStateFlow).value = settings.value.copy(showNews = show)
        saveData()
    }
    
    fun setNewsCategory(category: String) {
        (settings as MutableStateFlow).value = settings.value.copy(newsCategory = category)
        saveData()
    }

    fun setNewsSource(source: String) {
        (settings as MutableStateFlow).value = settings.value.copy(newsSource = source)
        saveData()
    }

    fun setTheme(themeName: String) {
        (settings as MutableStateFlow).value = settings.value.copy(themeName = themeName)
        saveData()
    }

    fun setDarkMode(isDarkMode: Boolean) {
        (settings as MutableStateFlow).value = settings.value.copy(isDarkMode = isDarkMode)
        saveData()
    }

    // Calendar sync operations
    fun exportTasksToCalendar(filePath: String): Boolean {
        return try {
            exportTasksToICS(tasks.value, filePath)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importTasksFromCalendar(filePath: String): Int {
        return try {
            val events = importTasksFromICS(filePath)
            val generalList = taskLists.value.find { it.name == "General" }
                ?: taskLists.value.firstOrNull()
                ?: TaskList(id = "general", name = "General", color = 0xFF6200EE)

            // Convert events to tasks
            val newTasks = events.map { event ->
                event.toTask(generalList.id)
            }

            // Add only tasks that don't already exist (by ID or description+date)
            val existingTaskIds = tasks.value.map { it.id }.toSet()
            val existingTaskKeys = tasks.value.map { "${it.description}_${it.dateTime}" }.toSet()

            val tasksToAdd = newTasks.filter { newTask ->
                !existingTaskIds.contains(newTask.id) &&
                        !existingTaskKeys.contains("${newTask.description}_${newTask.dateTime}")
            }

            if (tasksToAdd.isNotEmpty()) {
                (tasks as MutableStateFlow).value = tasks.value + tasksToAdd
                saveData()
            }

            tasksToAdd.size
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    // Google Calendar sync operations
    fun initializeGoogleCalendar(): Result<Unit> {
        return try {
            googleCalendarService.initialize()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun isGoogleCalendarAuthenticated(): Boolean {
        return googleCalendarService.isAuthenticated()
    }

    fun getGoogleCalendarList(): List<Pair<String, String>> {
        return try {
            googleCalendarService.getCalendarList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun syncGoogleCalendar(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            try {
                val config = settings.value.googleCalendar
                if (!config.syncEnabled || config.selectedCalendars.isEmpty()) {
                    return@launch
                }

                val events = googleCalendarService.getEventsFromCalendars(
                    config.selectedCalendars,
                    startDate,
                    endDate
                )

                (calendarEvents as MutableStateFlow).value = events

                // Update last sync time
                val updatedConfig = config.copy(lastSyncTime = System.currentTimeMillis())
                (settings as MutableStateFlow).value = settings.value.copy(googleCalendar = updatedConfig)

                saveData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setGoogleCalendarSyncEnabled(enabled: Boolean) {
        val updatedConfig = settings.value.googleCalendar.copy(syncEnabled = enabled)
        (settings as MutableStateFlow).value = settings.value.copy(googleCalendar = updatedConfig)
        saveData()
    }

    fun setSelectedGoogleCalendars(calendarIds: List<String>) {
        val updatedConfig = settings.value.googleCalendar.copy(selectedCalendars = calendarIds)
        (settings as MutableStateFlow).value = settings.value.copy(googleCalendar = updatedConfig)
        saveData()
    }

    fun signOutGoogleCalendar() {
        googleCalendarService.signOut()
        val updatedConfig = GoogleCalendarConfig()
        (settings as MutableStateFlow).value = settings.value.copy(googleCalendar = updatedConfig)
        (calendarEvents as MutableStateFlow).value = emptyList()
        saveData()
    }

    // Backup and Export operations
    fun createBackup(filePath: String): Result<String> {
        val data = AppData(
            tasks = tasks.value,
            taskLists = taskLists.value,
            notes = notes.value,
            noteCategories = noteCategories.value,
            calendarEvents = calendarEvents.value,
            availableTags = availableTags.value,
            settings = settings.value
        )
        return DataManager.createBackup(filePath, data)
    }

    fun restoreBackup(filePath: String): Result<Unit> {
        return try {
            val result = DataManager.restoreBackup(filePath)
            result.onSuccess { data ->
                _appData.value = data
                (tasks as MutableStateFlow).value = data.tasks
                (taskLists as MutableStateFlow).value = data.taskLists
                (notes as MutableStateFlow).value = data.notes
                (calendarEvents as MutableStateFlow).value = data.calendarEvents
                (availableTags as MutableStateFlow).value = data.availableTags
                (settings as MutableStateFlow).value = data.settings

                // Save the restored data
                saveData()
            }
            result.map { }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun exportTasksToCSV(filePath: String): Result<String> {
        return DataManager.exportTasksToCSV(filePath, tasks.value, taskLists.value)
    }

    fun exportNotesToCSV(filePath: String): Result<String> {
        return DataManager.exportNotesToCSV(filePath, notes.value, noteCategories.value)
    }

    fun exportAllDataToJSON(filePath: String): Result<String> {
        val data = AppData(
            tasks = tasks.value,
            taskLists = taskLists.value,
            notes = notes.value,
            noteCategories = noteCategories.value,
            calendarEvents = calendarEvents.value,
            availableTags = availableTags.value,
            settings = settings.value
        )
        return DataManager.exportToJSON(filePath, data)
    }
}
