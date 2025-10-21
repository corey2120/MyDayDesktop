package com.cobrien.myday

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val dateTime: String, // Format: "yyyy-MM-dd HH:mm:ss"
    val isCompleted: Boolean = false,
    val listId: String
)

@Serializable
data class TaskList(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Long = 0xFF6200EE
)

@Serializable
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String,
    val backgroundColor: Long = 0xFFFFF3E0,  // Cream
    val textColor: Long = 0xFF6B4423,  // Brown
    val fontSize: Int = 16,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class AppSettings(
    val showGreeting: Boolean = true,
    val showQuote: Boolean = true,
    val showNews: Boolean = false,
    val newsCategory: String = "general",
    val newsSource: String = "BBC",
    val themeName: String = "Default Blue",
    val isDarkMode: Boolean = false
)

@Serializable
data class AppData(
    val tasks: List<Task> = emptyList(),
    val taskLists: List<TaskList> = listOf(
        TaskList(id = "general", name = "General", color = 0xFF6200EE)
    ),
    val notes: List<Note> = emptyList(),
    val settings: AppSettings = AppSettings()
)

// Helper functions
fun formatTaskDateTime(date: Date?): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(date ?: Date())
}

fun parseTaskDateTime(dateTime: String): Date? {
    return try {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateTime)
    } catch (e: Exception) {
        null
    }
}
