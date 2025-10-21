package com.cobrien.myday

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Calendar Sync Module
 * Handles import/export of tasks in iCalendar (ICS) format
 * Compatible with Google Calendar, Outlook, Apple Calendar, etc.
 */

// iCalendar event data class
data class ICalEvent(
    val uid: String,
    val summary: String,
    val description: String = "",
    val startDateTime: Date,
    val endDateTime: Date? = null,
    val isCompleted: Boolean = false,
    val created: Date = Date(),
    val lastModified: Date = Date()
)

/**
 * Export tasks to iCalendar (ICS) format
 */
fun exportTasksToICS(tasks: List<Task>, filePath: String): Boolean {
    return try {
        val icsContent = buildString {
            // iCalendar header
            appendLine("BEGIN:VCALENDAR")
            appendLine("VERSION:2.0")
            appendLine("PRODID:-//MyDay Desktop//Calendar Export//EN")
            appendLine("CALSCALE:GREGORIAN")
            appendLine("METHOD:PUBLISH")
            appendLine("X-WR-CALNAME:MyDay Tasks")
            appendLine("X-WR-TIMEZONE:${TimeZone.getDefault().id}")

            // Convert tasks to VTODO components
            tasks.forEach { task ->
                appendLine("BEGIN:VTODO")
                appendLine("UID:${task.id}@myday.desktop")
                appendLine("DTSTAMP:${formatDateTimeICS(Date())}")
                appendLine("SUMMARY:${escapeICSText(task.description)}")

                // Parse task date/time
                val taskDate = parseTaskDateTime(task.dateTime)
                if (taskDate != null) {
                    appendLine("DUE:${formatDateTimeICS(taskDate)}")
                }

                // Status
                appendLine("STATUS:${if (task.isCompleted) "COMPLETED" else "NEEDS-ACTION"}")

                if (task.isCompleted) {
                    appendLine("PERCENT-COMPLETE:100")
                }

                appendLine("CATEGORIES:MyDay")
                appendLine("END:VTODO")
            }

            appendLine("END:VCALENDAR")
        }

        File(filePath).writeText(icsContent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * Import tasks from iCalendar (ICS) file
 */
fun importTasksFromICS(filePath: String): List<ICalEvent> {
    return try {
        val icsContent = File(filePath).readText()
        parseICSContent(icsContent)
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

/**
 * Parse ICS file content and extract events/todos
 */
private fun parseICSContent(icsContent: String): List<ICalEvent> {
    val events = mutableListOf<ICalEvent>()

    // Split into VEVENT and VTODO blocks
    val eventBlocks = extractBlocks(icsContent, "VEVENT") + extractBlocks(icsContent, "VTODO")

    eventBlocks.forEach { block ->
        try {
            val uid = extractField(block, "UID") ?: UUID.randomUUID().toString()
            val summary = extractField(block, "SUMMARY") ?: "Untitled"
            val description = extractField(block, "DESCRIPTION") ?: ""

            // Parse dates
            val dtStart = extractField(block, "DTSTART")?.let { parseDateTimeICS(it) }
            val dtEnd = extractField(block, "DTEND")?.let { parseDateTimeICS(it) }
            val due = extractField(block, "DUE")?.let { parseDateTimeICS(it) }

            // Determine start/end times
            val startDateTime = dtStart ?: due ?: Date()
            val endDateTime = dtEnd ?: dtStart

            // Check completion status
            val status = extractField(block, "STATUS")
            val isCompleted = status == "COMPLETED"

            events.add(
                ICalEvent(
                    uid = uid,
                    summary = unescapeICSText(summary),
                    description = unescapeICSText(description),
                    startDateTime = startDateTime,
                    endDateTime = endDateTime,
                    isCompleted = isCompleted
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return events
}

/**
 * Extract all blocks of a given type from ICS content
 */
private fun extractBlocks(icsContent: String, blockType: String): List<String> {
    val blocks = mutableListOf<String>()
    val beginPattern = "BEGIN:$blockType"
    val endPattern = "END:$blockType"

    var currentIndex = 0
    while (true) {
        val beginIndex = icsContent.indexOf(beginPattern, currentIndex)
        if (beginIndex == -1) break

        val endIndex = icsContent.indexOf(endPattern, beginIndex)
        if (endIndex == -1) break

        blocks.add(icsContent.substring(beginIndex, endIndex + endPattern.length))
        currentIndex = endIndex + endPattern.length
    }

    return blocks
}

/**
 * Extract a field value from an ICS block
 */
private fun extractField(block: String, fieldName: String): String? {
    val pattern = "$fieldName[;:](.*)".toRegex()
    val match = pattern.find(block) ?: return null
    return match.groupValues[1].trim()
}

/**
 * Format date/time to ICS format (YYYYMMDDTHHMMSSZ)
 */
private fun formatDateTimeICS(date: Date): String {
    val format = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US)
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(date)
}

/**
 * Parse ICS date/time format
 */
private fun parseDateTimeICS(dateTimeStr: String): Date? {
    return try {
        // Remove any parameters (e.g., TZID)
        val cleanDateTime = dateTimeStr.split(":").last().trim()

        // Try different formats
        val formats = listOf(
            SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US),
            SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.US),
            SimpleDateFormat("yyyyMMdd", Locale.US)
        )

        formats.forEach { format ->
            try {
                if (dateTimeStr.endsWith("Z")) {
                    format.timeZone = TimeZone.getTimeZone("UTC")
                }
                return format.parse(cleanDateTime)
            } catch (e: Exception) {
                // Try next format
            }
        }

        null
    } catch (e: Exception) {
        null
    }
}

/**
 * Escape special characters for ICS format
 */
private fun escapeICSText(text: String): String {
    return text
        .replace("\\", "\\\\")
        .replace(",", "\\,")
        .replace(";", "\\;")
        .replace("\n", "\\n")
}

/**
 * Unescape ICS text
 */
private fun unescapeICSText(text: String): String {
    return text
        .replace("\\n", "\n")
        .replace("\\;", ";")
        .replace("\\,", ",")
        .replace("\\\\", "\\")
}

/**
 * Convert ICalEvent to Task
 */
fun ICalEvent.toTask(listId: String): Task {
    return Task(
        id = uid.replace("@myday.desktop", ""),
        description = summary,
        dateTime = formatTaskDateTime(startDateTime),
        isCompleted = isCompleted,
        listId = listId
    )
}
