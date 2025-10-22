package com.cobrien.myday

import kotlinx.coroutines.*
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.Toolkit
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.swing.ImageIcon

/**
 * Desktop notification service for event reminders
 */
class NotificationService {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val scheduledReminders = mutableMapOf<String, Job>()

    companion object {
        private var trayIcon: TrayIcon? = null

        /**
         * Initialize system tray icon for notifications
         */
        fun initializeSystemTray() {
            if (!SystemTray.isSupported()) {
                println("System tray not supported on this platform")
                return
            }

            try {
                val tray = SystemTray.getSystemTray()

                // Create a simple icon (you can replace this with an actual icon file)
                val iconSize = tray.trayIconSize
                val image = Toolkit.getDefaultToolkit().createImage(
                    ByteArray(iconSize.width * iconSize.height)
                )

                trayIcon = TrayIcon(image, "MyDay Desktop").apply {
                    isImageAutoSize = true
                    toolTip = "MyDay Desktop - Event Reminders"
                }

                tray.add(trayIcon)
            } catch (e: Exception) {
                println("Failed to initialize system tray: ${e.message}")
            }
        }
    }

    /**
     * Show a desktop notification
     */
    fun showNotification(title: String, message: String, type: TrayIcon.MessageType = TrayIcon.MessageType.INFO) {
        trayIcon?.displayMessage(title, message, type)
    }

    /**
     * Schedule reminder for a calendar event
     */
    fun scheduleEventReminder(event: CalendarEvent, minutesBefore: Int = 15) {
        // Cancel existing reminder for this event
        scheduledReminders[event.id]?.cancel()

        val eventTime = Date(event.startDateTime).toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        val reminderTime = eventTime.minusMinutes(minutesBefore.toLong())
        val now = LocalDateTime.now()

        if (reminderTime.isAfter(now)) {
            val delayMillis = java.time.Duration.between(now, reminderTime).toMillis()

            val job = scope.launch {
                delay(delayMillis)
                showNotification(
                    "Upcoming Event",
                    "${event.summary} starts in $minutesBefore minutes",
                    TrayIcon.MessageType.INFO
                )
            }

            scheduledReminders[event.id] = job
        }
    }

    /**
     * Schedule reminder for a task
     */
    fun scheduleTaskReminder(task: Task, minutesBefore: Int = 15) {
        val taskDateTime = parseTaskDateTime(task.dateTime)
        if (taskDateTime == null || task.isCompleted) return

        scheduledReminders[task.id]?.cancel()

        val taskTime = taskDateTime.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        val reminderTime = taskTime.minusMinutes(minutesBefore.toLong())
        val now = LocalDateTime.now()

        if (reminderTime.isAfter(now)) {
            val delayMillis = java.time.Duration.between(now, reminderTime).toMillis()

            val job = scope.launch {
                delay(delayMillis)
                if (!task.isCompleted) {
                    showNotification(
                        "Task Reminder",
                        "${task.description} is due in $minutesBefore minutes",
                        TrayIcon.MessageType.WARNING
                    )
                }
            }

            scheduledReminders[task.id] = job
        }
    }

    /**
     * Schedule daily reminder for upcoming events and tasks
     */
    fun scheduleDailySummary(hour: Int = 8, minute: Int = 0, events: List<CalendarEvent>, tasks: List<Task>) {
        scope.launch {
            while (isActive) {
                val now = LocalDateTime.now()
                var nextRun = now.withHour(hour).withMinute(minute).withSecond(0)

                if (now.isAfter(nextRun)) {
                    nextRun = nextRun.plusDays(1)
                }

                val delayMillis = java.time.Duration.between(now, nextRun).toMillis()
                delay(delayMillis)

                // Show summary of today's events and tasks
                val today = LocalDateTime.now()
                val todayEvents = events.filter { event ->
                    val eventDate = Date(event.startDateTime).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    eventDate == today.toLocalDate()
                }

                val todayTasks = tasks.filter { task ->
                    !task.isCompleted && task.dateTime.startsWith(
                        today.toLocalDate().toString()
                    )
                }

                if (todayEvents.isNotEmpty() || todayTasks.isNotEmpty()) {
                    val message = buildString {
                        if (todayEvents.isNotEmpty()) {
                            append("${todayEvents.size} event(s)")
                        }
                        if (todayTasks.isNotEmpty()) {
                            if (isNotEmpty()) append(" and ")
                            append("${todayTasks.size} task(s)")
                        }
                        append(" scheduled for today")
                    }

                    showNotification(
                        "Daily Summary",
                        message,
                        TrayIcon.MessageType.INFO
                    )
                }
            }
        }
    }

    /**
     * Cancel all scheduled reminders
     */
    fun cancelAllReminders() {
        scheduledReminders.values.forEach { it.cancel() }
        scheduledReminders.clear()
    }

    /**
     * Cancel reminder for specific event/task
     */
    fun cancelReminder(id: String) {
        scheduledReminders[id]?.cancel()
        scheduledReminders.remove(id)
    }

    /**
     * Clean up resources
     */
    fun shutdown() {
        cancelAllReminders()
        scope.cancel()
    }
}
