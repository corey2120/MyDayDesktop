package com.cobrien.myday

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class DataRepository {
    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    private val dataDir = File(System.getProperty("user.home"), ".myday").apply {
        if (!exists()) mkdirs()
    }
    
    private val dataFile = File(dataDir, "data.json")
    
    suspend fun loadData(): AppData = withContext(Dispatchers.IO) {
        if (!dataFile.exists()) {
            return@withContext AppData()
        }
        
        try {
            val jsonString = dataFile.readText()
            json.decodeFromString<AppData>(jsonString)
        } catch (e: Exception) {
            println("Error loading data: ${e.message}")
            AppData()
        }
    }
    
    suspend fun saveData(data: AppData) = withContext(Dispatchers.IO) {
        try {
            val jsonString = json.encodeToString(data)
            dataFile.writeText(jsonString)
        } catch (e: Exception) {
            println("Error saving data: ${e.message}")
        }
    }
}
