package com.cobrien.myday

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
enum class TaskPriority {
    NONE, LOW, MEDIUM, HIGH
}

@Serializable
enum class RecurrenceType {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY
}

@Serializable
data class Subtask(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val isCompleted: Boolean = false
)

@Serializable
data class RecurrenceConfig(
    val type: RecurrenceType = RecurrenceType.NONE,
    val interval: Int = 1, // e.g., every 2 days, every 3 weeks
    val endDate: String? = null // Format: "yyyy-MM-dd"
)

@Serializable
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val dateTime: String, // Format: "yyyy-MM-dd HH:mm:ss"
    val isCompleted: Boolean = false,
    val listId: String,
    val priority: TaskPriority = TaskPriority.NONE,
    val tags: List<String> = emptyList(),
    val subtasks: List<Subtask> = emptyList(),
    val recurrence: RecurrenceConfig = RecurrenceConfig(),
    val notes: String = "" // Additional notes/description
)

@Serializable
data class TaskList(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Long = 0xFF6200EE
)

@Serializable
data class NoteCategory(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Long = 0xFF6200EE,
    val icon: String = "📁" // Emoji icon for the category
)

@Serializable
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String,
    val backgroundColor: Long = 0xFFFFF3E0,  // Cream
    val textColor: Long = 0xFF6B4423,  // Brown
    val fontSize: Int = 16,
    val createdAt: Long = System.currentTimeMillis(),
    val categoryId: String? = null, // Category this note belongs to
    val lastModified: Long = System.currentTimeMillis()
)

@Serializable
data class CalendarEvent(
    val id: String,
    val summary: String,
    val description: String = "",
    val startDateTime: Long, // Unix timestamp in milliseconds
    val endDateTime: Long? = null,
    val calendarId: String, // Google Calendar ID
    val calendarName: String = "",
    val colorId: String? = null,
    val isAllDay: Boolean = false
)

@Serializable
data class GoogleCalendarConfig(
    val syncEnabled: Boolean = false,
    val selectedCalendars: List<String> = emptyList(), // List of calendar IDs to sync
    val lastSyncTime: Long = 0L,
    val autoSyncInterval: Int = 15 // minutes
)

@Serializable
data class NotificationSettings(
    val enabled: Boolean = true,
    val eventReminders: Boolean = true,
    val taskReminders: Boolean = true,
    val reminderMinutesBefore: Int = 15,
    val dailySummary: Boolean = true,
    val dailySummaryHour: Int = 8,
    val dailySummaryMinute: Int = 0
)

@Serializable
data class AppSettings(
    val showGreeting: Boolean = true,
    val showQuote: Boolean = true,
    val showNews: Boolean = false,
    val newsCategory: String = "general",
    val newsSource: String = "BBC",
    val themeName: String = "Default Blue",
    val isDarkMode: Boolean = false,
    val googleCalendar: GoogleCalendarConfig = GoogleCalendarConfig(),
    val showHolidays: Boolean = true,
    val notifications: NotificationSettings = NotificationSettings()
)

@Serializable
data class AppData(
    val tasks: List<Task> = emptyList(),
    val taskLists: List<TaskList> = listOf(
        TaskList(id = "general", name = "General", color = 0xFF6200EE)
    ),
    val notes: List<Note> = emptyList(),
    val noteCategories: List<NoteCategory> = listOf(
        NoteCategory(id = "personal", name = "Personal", color = 0xFF2196F3, icon = "👤"),
        NoteCategory(id = "work", name = "Work", color = 0xFFFF9800, icon = "💼"),
        NoteCategory(id = "ideas", name = "Ideas", color = 0xFF9C27B0, icon = "💡")
    ),
    val calendarEvents: List<CalendarEvent> = emptyList(),
    val availableTags: List<String> = emptyList(), // All tags used across tasks
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

// Task priority helpers
fun TaskPriority.toColor(): Long {
    return when (this) {
        TaskPriority.HIGH -> 0xFFEF5350 // Red
        TaskPriority.MEDIUM -> 0xFFFFA726 // Orange
        TaskPriority.LOW -> 0xFF66BB6A // Green
        TaskPriority.NONE -> 0xFF9E9E9E // Gray
    }
}

fun TaskPriority.toDisplayName(): String {
    return when (this) {
        TaskPriority.HIGH -> "High"
        TaskPriority.MEDIUM -> "Medium"
        TaskPriority.LOW -> "Low"
        TaskPriority.NONE -> "None"
    }
}

// Calculate next recurrence date
fun RecurrenceConfig.getNextDate(currentDate: Date): Date? {
    if (type == RecurrenceType.NONE) return null

    val calendar = Calendar.getInstance()
    calendar.time = currentDate

    when (type) {
        RecurrenceType.DAILY -> calendar.add(Calendar.DAY_OF_MONTH, interval)
        RecurrenceType.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, interval)
        RecurrenceType.MONTHLY -> calendar.add(Calendar.MONTH, interval)
        RecurrenceType.YEARLY -> calendar.add(Calendar.YEAR, interval)
        RecurrenceType.NONE -> return null
    }

    // Check if we've passed the end date
    if (endDate != null) {
        val endCal = Calendar.getInstance()
        val endDateParsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(endDate)
        if (endDateParsed != null) {
            endCal.time = endDateParsed
            if (calendar.after(endCal)) {
                return null
            }
        }
    }

    return calendar.time
}

// Google Calendar color mapping
// Based on Google Calendar API event colors
fun getEventColor(colorId: String?): Long {
    return when (colorId) {
        "1" -> 0xFF7986CB // Lavender
        "2" -> 0xFF33B679 // Sage
        "3" -> 0xFF8E24AA // Grape
        "4" -> 0xFFE67C73 // Flamingo
        "5" -> 0xFFF6BF26 // Banana
        "6" -> 0xFFFD7E14 // Tangerine
        "7" -> 0xFF039BE5 // Peacock
        "8" -> 0xFF616161 // Graphite
        "9" -> 0xFF3F51B5 // Blueberry
        "10" -> 0xFF0B8043 // Basil
        "11" -> 0xFFD50000 // Tomato
        else -> 0xFF039BE5 // Default blue (Peacock)
    }
}
