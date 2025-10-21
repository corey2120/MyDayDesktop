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
    val settings: StateFlow<AppSettings> = MutableStateFlow(AppSettings())
    
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
                settings = settings.value
            )
            _appData.value = data
            repository.saveData(data)
        }
    }
    
    // Task operations
    fun addTask(description: String, listId: String, date: Date?) {
        val dateTime = formatTaskDateTime(date)
        val newTask = Task(
            description = description,
            dateTime = dateTime,
            listId = listId
        )
        (tasks as MutableStateFlow).value = tasks.value + newTask
        saveData()
    }
    
    fun toggleTaskCompleted(taskId: String) {
        val updatedTasks = tasks.value.map {
            if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
        }
        (tasks as MutableStateFlow).value = updatedTasks
        saveData()
    }
    
    fun deleteTask(task: Task) {
        (tasks as MutableStateFlow).value = tasks.value.filter { it.id != task.id }
        saveData()
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
        fontSize: Int = 16
    ) {
        val newNote = Note(
            title = title,
            content = content,
            backgroundColor = backgroundColor,
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
}
