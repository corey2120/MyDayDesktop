# MyDay Desktop

A comprehensive desktop application for Linux that combines Tasks, Calendar, and Notes to help you navigate your day. Built with Kotlin Compose Multiplatform.

## Features

### Tasks
- **Multiple Task Lists**: Organize tasks into different lists with custom colors
- **Task Management**: Create, complete, and delete tasks
- **Due Dates**: Set due dates for tasks with visual indicators
- **Color-Coded Lists**: Each list has its own color for easy identification

### Calendar
- **Monthly View**: Navigate through months to see your scheduled tasks
- **Task Visualization**: Days with tasks are highlighted
- **Today Indicator**: Current day is clearly marked
- **Task Details**: View all tasks scheduled for a selected date

### Notes (MyJournal-style)
- **Write First, Name Later**: Start writing immediately without thinking about titles
- **Beautiful Themed Colors**: 
  - 5 Background themes: Cream, Mint, Sky, Rose, Lavender
  - 5 Text colors: Brown, Gray, Blue, Green, Purple
  - Adjustable font sizes (12-24sp)
- **Distraction-Free Writing**: Full-screen editor with minimal UI
- **Date Display**: Each note shows when it was created
- **Persistent Storage**: All notes saved locally to `~/.myday/`

## Installation

### Running from Source

1. **Clone or navigate to the project**:
   ```bash
   cd /home/cobrien/Projects/MyDayDesktop
   ```

2. **Run the application**:
   ```bash
   ./gradlew run
   ```

### Building an AppImage

1. **Build the distribution**:
   ```bash
   ./build.sh
   ```

2. **The AppImage will be created in the project directory**

### Building DEB/RPM packages

```bash
./gradlew packageDeb    # For Debian/Ubuntu
./gradlew packageRpm    # For Fedora/RHEL
```

## Usage

### Tasks Tab
- Click the **+** button to add a new task
- Click the checkbox to mark tasks as complete/incomplete
- Use the **Add List** tab to create new task lists
- Click the delete button on any task to remove it

### Calendar Tab
- Use arrows to navigate between months
- Click any date to view tasks scheduled for that day
- Days with tasks are highlighted with a border
- Today's date is shown with a colored background

### Notes Tab
- Click **+** to create a new note
- Start writing immediately - the title comes later
- Use the palette icon to customize colors and font size
- Click the checkmark to save and name your note
- Click any note card to edit it
- Click the delete icon on a note card to remove it

## Data Storage

All your data is stored locally in:
```
~/.myday/data.json
```

This includes all tasks, task lists, and notes.

## Keyboard Tips

- The note editor provides a distraction-free writing experience
- All changes are saved when you click the save button
- You can customize each note's appearance individually

## System Requirements

- Linux (any modern distribution)
- Java 17 or higher
- X11 or Wayland display server

## Technology

Built with:
- **Kotlin** - Modern, expressive programming language
- **Compose Multiplatform** - Declarative UI framework
- **Material 3** - Modern design system
- **Kotlinx Serialization** - JSON persistence

## License

MIT License - Feel free to use and modify as you wish.

## Credits

Inspired by the Android MyDay app and MyJournal Desktop application.
