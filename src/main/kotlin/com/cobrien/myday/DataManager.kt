package com.cobrien.myday

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Handles backup, restore, and export operations for app data
 */
object DataManager {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /**
     * Creates a backup of all app data to a JSON file
     * @param filePath The destination file path
     * @param appData The app data to backup
     * @return Result indicating success or failure
     */
    fun createBackup(filePath: String, appData: AppData): Result<String> {
        return try {
            val file = File(filePath)
            val jsonString = json.encodeToString(appData)
            file.writeText(jsonString)
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Restores app data from a backup JSON file
     * @param filePath The source backup file path
     * @return Result containing the restored AppData or error
     */
    fun restoreBackup(filePath: String): Result<AppData> {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                return Result.failure(Exception("Backup file not found"))
            }

            val jsonString = file.readText()
            val appData = json.decodeFromString<AppData>(jsonString)
            Result.success(appData)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Failed to restore backup: ${e.message}"))
        }
    }

    /**
     * Exports tasks to CSV format
     * @param filePath The destination file path
     * @param tasks The tasks to export
     * @param taskLists The task lists for name lookup
     * @return Result indicating success or failure
     */
    fun exportTasksToCSV(filePath: String, tasks: List<Task>, taskLists: List<TaskList>): Result<String> {
        return try {
            val file = File(filePath)
            val csvContent = buildString {
                // Header
                appendLine("ID,Description,Date/Time,Completed,List,Priority,Tags,Notes,Recurrence Type,Recurrence Interval")

                // Task rows
                tasks.forEach { task ->
                    val listName = taskLists.find { it.id == task.listId }?.name ?: "Unknown"
                    val tags = task.tags.joinToString(";")
                    val notes = task.notes.replace(",", "\\,").replace("\n", " ")

                    appendLine("${task.id},\"${task.description}\",${task.dateTime},${task.isCompleted},$listName,${task.priority.toDisplayName()},\"$tags\",\"$notes\",${task.recurrence.type},${task.recurrence.interval}")
                }
            }

            file.writeText(csvContent)
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Exports notes to CSV format
     * @param filePath The destination file path
     * @param notes The notes to export
     * @param noteCategories The note categories for name lookup
     * @return Result indicating success or failure
     */
    fun exportNotesToCSV(filePath: String, notes: List<Note>, noteCategories: List<NoteCategory>): Result<String> {
        return try {
            val file = File(filePath)
            val csvContent = buildString {
                // Header
                appendLine("ID,Title,Content,Category,Created At,Last Modified,Background Color,Text Color,Font Size")

                // Note rows
                notes.forEach { note ->
                    val categoryName = noteCategories.find { it.id == note.categoryId }?.name ?: "Uncategorized"
                    val content = note.content.replace(",", "\\,").replace("\n", " ")
                    val title = note.title.replace(",", "\\,")
                    val createdDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(note.createdAt))
                    val modifiedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(note.lastModified))

                    appendLine("${note.id},\"$title\",\"$content\",$categoryName,$createdDate,$modifiedDate,${note.backgroundColor},${note.textColor},${note.fontSize}")
                }
            }

            file.writeText(csvContent)
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Exports all app data to JSON format
     * @param filePath The destination file path
     * @param appData The app data to export
     * @return Result indicating success or failure
     */
    fun exportToJSON(filePath: String, appData: AppData): Result<String> {
        return createBackup(filePath, appData)
    }

    /**
     * Generates a timestamped backup filename
     * @param prefix Optional prefix for the filename
     * @return Formatted filename with timestamp
     */
    fun generateBackupFilename(prefix: String = "MyDay_Backup"): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
        return "${prefix}_$timestamp.json"
    }

    /**
     * Generates a timestamped export filename
     * @param type The type of export (e.g., "Tasks", "Notes")
     * @param format The file format (e.g., "csv", "json")
     * @return Formatted filename with timestamp
     */
    fun generateExportFilename(type: String, format: String): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
        return "MyDay_${type}_$timestamp.$format"
    }
}
