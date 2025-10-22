# Google Calendar Integration Setup Guide

## Overview

MyDay Desktop now supports syncing events from your Google Calendar! This feature allows you to view your calendar events alongside your tasks in the app.

## Features

- **Read-only sync**: View Google Calendar events in MyDay Desktop
- **Multiple calendar support**: Sync events from multiple Google Calendars
- **Automatic syncing**: Events automatically sync when you change months
- **Event details**: See event names, times, and which calendar they belong to

## Prerequisites

Before you can use Google Calendar sync, you need to:

1. Have a Google account with Google Calendar
2. Create a Google Cloud project and enable the Calendar API
3. Download OAuth 2.0 credentials

## Step-by-Step Setup

### 1. Create a Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click "Select a project" → "New Project"
3. Enter a project name (e.g., "MyDay Desktop")
4. Click "Create"

### 2. Enable Google Calendar API

1. In your project, go to **APIs & Services** → **Library**
2. Search for "Google Calendar API"
3. Click on "Google Calendar API"
4. Click **Enable**

### 3. Configure OAuth Consent Screen

1. Go to **APIs & Services** → **OAuth consent screen**
2. Select **External** user type
3. Click **Create**
4. Fill in the required fields:
   - **App name**: MyDay Desktop
   - **User support email**: Your email
   - **Developer contact information**: Your email
5. Click **Save and Continue**
6. On the "Scopes" screen, click **Save and Continue** (no need to add scopes manually)
7. On the "Test users" screen, add your email address as a test user
8. Click **Save and Continue**

### 4. Create OAuth 2.0 Credentials

1. Go to **APIs & Services** → **Credentials**
2. Click **+ CREATE CREDENTIALS** → **OAuth client ID**
3. Select **Desktop application** as the application type
4. Enter a name (e.g., "MyDay Desktop Client")
5. Click **Create**
6. Click **Download JSON** to download the credentials file
7. **Important**: Rename the downloaded file to `credentials.json`

### 5. Install credentials.json

Place the `credentials.json` file in the MyDay data directory:

```bash
# Copy credentials.json to the MyDay directory
cp credentials.json ~/.myday/

# Verify it's in the right place
ls ~/.myday/credentials.json
```

The file must be located at `~/.myday/credentials.json` (in your home directory). This ensures the app can find it regardless of where you launch it from.

## Using Google Calendar Sync

### First-Time Setup

1. Launch MyDay Desktop
2. Go to **Settings** (gear icon in navigation)
3. Scroll to **Calendar Sync** section
4. Click **Configure Google Calendar**
5. Click **Sign in with Google**
6. A browser window will open asking you to sign in to Google
7. Grant permission to MyDay Desktop to read your calendar
8. Return to the app

### Selecting Calendars

1. In the Google Calendar Settings dialog:
   - Toggle **Enable Sync** to ON
   - Check the calendars you want to sync
   - Click **Save**

### Viewing Events

- Navigate to the **Home** screen
- Your Google Calendar events will appear in the calendar view
- Select a date to see both tasks and events for that day
- Events are displayed with their calendar name and time

### Managing Sync

- **Re-sync**: Events automatically sync when you change months
- **Change calendars**: Go to Settings → Configure Google Calendar
- **Sign out**: Click "Sign Out" in Google Calendar Settings to revoke access

## Troubleshooting

### "Credentials file not found" Error

**Solution**: Make sure `credentials.json` is located at `~/.myday/credentials.json`. If you're launching from Gear Lever or another launcher, the file must be in the `.myday` directory in your home folder, not in the project directory.

### "Authentication failed" Error

**Solution**:
1. Check that the Calendar API is enabled in Google Cloud Console
2. Verify your email is added as a test user in OAuth consent screen
3. Try signing out and signing in again

### No Events Showing

**Solution**:
1. Make sure sync is enabled in Settings
2. Check that you've selected at least one calendar
3. Verify the selected calendars have events in the current month
4. Try navigating to a different month and back

### Browser Doesn't Open for Authentication

**Solution**:
1. Check that port 8888 is not blocked by firewall
2. Try manually opening the URL shown in the error message
3. Restart the application

## Privacy & Security

- **Read-only access**: MyDay Desktop can only *read* your calendar events, not modify them
- **Local storage**: Your credentials are stored locally in `~/.myday/tokens/`
- **No data collection**: Calendar data stays on your device and is not sent anywhere else
- **Revoke access**: You can revoke access anytime by signing out in the app or from [Google Account Settings](https://myaccount.google.com/permissions)

## API Limits

Google Calendar API has the following limits:
- **Queries per day**: 1,000,000 (you won't reach this in normal use)
- **Queries per 100 seconds**: 10,000

MyDay Desktop syncs events only when you change months, so these limits are more than sufficient for personal use.

## Future Enhancements

Planned features for future versions:
- Two-way sync (create/edit events from MyDay Desktop)
- Sync tasks to Google Calendar
- Offline caching of events
- Custom sync intervals
- Calendar color coding

## Support

If you encounter issues:
1. Check this guide for common solutions
2. Verify your `credentials.json` file is correct
3. Check the application logs for error messages
4. Create an issue on the GitHub repository

---

**Last Updated**: October 22, 2025 (Version 1.0.2)
