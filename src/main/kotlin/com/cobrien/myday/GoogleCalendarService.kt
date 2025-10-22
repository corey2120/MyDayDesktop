package com.cobrien.myday

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.Date

/**
 * Google Calendar Service
 * Handles authentication and interaction with Google Calendar API
 */
class GoogleCalendarService {

    companion object {
        private const val APPLICATION_NAME = "MyDay Desktop"
        private val JSON_FACTORY = GsonFactory.getDefaultInstance()
        private val SCOPES = listOf(CalendarScopes.CALENDAR_READONLY)
        private const val MYDAY_DIRECTORY = ".myday"
        private const val CREDENTIALS_FILE_NAME = "credentials.json"
        private const val TOKENS_DIRECTORY_PATH = ".myday/tokens"
    }

    private var calendarService: Calendar? = null

    /**
     * Creates an authorized Credential object.
     */
    @Throws(IOException::class)
    private fun getCredentials(httpTransport: NetHttpTransport): Credential {
        // Load client secrets from credentials.json in user's home directory
        val myDayDir = File(System.getProperty("user.home"), MYDAY_DIRECTORY)
        val credentialsFile = File(myDayDir, CREDENTIALS_FILE_NAME)

        if (!credentialsFile.exists()) {
            throw IOException(
                "Credentials file not found: ${credentialsFile.absolutePath}\n" +
                "Please download credentials.json from Google Cloud Console:\n" +
                "1. Go to https://console.cloud.google.com/\n" +
                "2. Create a new project or select existing one\n" +
                "3. Enable Google Calendar API\n" +
                "4. Create OAuth 2.0 credentials (Desktop application)\n" +
                "5. Download credentials.json and place it in ~/.myday/"
            )
        }

        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, FileReader(credentialsFile))

        // Build flow and trigger user authorization request
        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(System.getProperty("user.home"), TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()

        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    /**
     * Initialize the Calendar service
     */
    @Throws(Exception::class)
    fun initialize() {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val credential = getCredentials(httpTransport)

        calendarService = Calendar.Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    /**
     * Check if the service is authenticated and ready
     */
    fun isAuthenticated(): Boolean {
        return calendarService != null
    }

    /**
     * Get list of user's calendars
     */
    fun getCalendarList(): List<Pair<String, String>> {
        val service = calendarService ?: throw IllegalStateException("Service not initialized")

        return try {
            val calendarList = service.calendarList().list().execute()
            calendarList.items.map { calendar ->
                Pair(calendar.id, calendar.summary ?: "Unnamed Calendar")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetch events from a specific calendar
     */
    fun getEventsFromCalendar(
        calendarId: String,
        startDate: Date,
        endDate: Date
    ): List<CalendarEvent> {
        val service = calendarService ?: throw IllegalStateException("Service not initialized")

        return try {
            val timeMin = DateTime(startDate)
            val timeMax = DateTime(endDate)

            val events = service.events().list(calendarId)
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()

            // Get calendar name
            val calendar = service.calendars().get(calendarId).execute()
            val calendarName = calendar.summary ?: "Calendar"

            events.items.map { event ->
                val start = event.start?.dateTime ?: event.start?.date
                val end = event.end?.dateTime ?: event.end?.date

                CalendarEvent(
                    id = event.id ?: "",
                    summary = event.summary ?: "No Title",
                    description = event.description ?: "",
                    startDateTime = start?.value ?: System.currentTimeMillis(),
                    endDateTime = end?.value,
                    calendarId = calendarId,
                    calendarName = calendarName,
                    colorId = event.colorId,
                    isAllDay = event.start?.dateTime == null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetch events from multiple calendars
     */
    fun getEventsFromCalendars(
        calendarIds: List<String>,
        startDate: Date,
        endDate: Date
    ): List<CalendarEvent> {
        return calendarIds.flatMap { calendarId ->
            getEventsFromCalendar(calendarId, startDate, endDate)
        }
    }

    /**
     * Sign out and remove stored credentials
     */
    fun signOut() {
        try {
            val tokensDir = File(System.getProperty("user.home"), TOKENS_DIRECTORY_PATH)
            if (tokensDir.exists()) {
                tokensDir.deleteRecursively()
            }
            calendarService = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
