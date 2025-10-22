# MyDay Desktop


    A comprehensive productivity desktop application that combines Tasks, Calendar, Notes, and News to help you organize your day.

Version Platform License Kotlin Compose
Features
📋 Task Management

    Multiple Task Lists - Organize tasks into customizable lists with unique colors
    Smart Task Handling - Create, complete, edit, and delete tasks effortlessly
    Due Date Tracking - Set and visualize task deadlines
    Progress Indicators - See task completion status at a glance
    Task Details - Add descriptions and manage task priorities

📅 Advanced Calendar

    Multiple Views - Switch between Month, Week, and Day views
    Task Integration - See all your tasks directly in the calendar
    Visual Indicators - Days with tasks are clearly highlighted
    Smooth Navigation - Intuitive controls for browsing dates
    Google Calendar Sync 🆕 - Live integration with Google Calendar
        OAuth 2.0 secure authentication
        Read calendar events in real-time
        Multi-calendar support
        Auto-sync when changing months
        See events alongside your tasks
    Calendar Import/Export - Import/Export tasks via iCalendar (.ics) format
        Compatible with Google Calendar, Outlook, Apple Calendar
        Duplicate prevention

📝 Notes (Journal-style)

    Quick Capture - Start writing immediately, name later
    Beautiful Themes - 5 background colors and 5 text colors
    Customizable - Adjustable font sizes (12-24sp)
    Distraction-Free - Full-screen editor with minimal UI
    Timestamped - Automatic creation date tracking
    Organized Grid - Visual card layout for easy browsing

📰 News Feed

    Live Headlines - Stay updated with the latest news
    Multiple Sources - BBC, CNN, NPR, The Verge, Ars Technica, and more
    Category Filters - Technology, Business, Science, Health, Sports, General
    Clickable Articles - Opens in your default browser
    Smart Caching - 30-minute cache for better performance
    Customizable - Choose your preferred news source and category

🏠 Home Dashboard

    Personalized Greeting - Welcome message based on time of day
    Daily Quote - Inspirational quotes to start your day
    Quick Overview - See your tasks, calendar, and news at a glance
    Customizable Widgets - Show/hide sections based on your preferences

🎨 Appearance & Customization

    Dark/Light Mode - Easy on the eyes, day or night
    Multiple Themes - Default, Purple, Ocean, Forest, Sunset, Rose, Sky, Lavender
    Material Design 3 - Modern, beautiful UI following Google's design standards
    Responsive Layout - Adapts to your window size

Installation
Quick Start (AppImage - Recommended)

Download the latest release:

wget https://github.com/yourusername/MyDayDesktop/releases/download/v1.0.1/MyDay-1.0.1-x86_64.AppImage

Make it executable:

chmod +x MyDay-1.0.1-x86_64.AppImage

Run it:

./MyDay-1.0.1-x86_64.AppImage

Tarball Installation

Download and extract:

wget https://github.com/yourusername/MyDayDesktop/releases/download/v1.0.1/MyDay-1.0.1-linux.tar.gz
tar xzf MyDay-1.0.1-linux.tar.gz
cd MyDay-1.0.1-linux

Run the launcher:

./myday.sh

System-wide Installation (Optional)

sudo cp -r MyDay-1.0.1-linux /opt/myday
sudo ln -s /opt/myday/myday.sh /usr/local/bin/myday

Now you can run myday from anywhere!
Build from Source

Clone the repository:

git clone https://github.com/yourusername/MyDayDesktop.git
cd MyDayDesktop

Run directly:

./gradlew run

Build distributions:

./build-package.sh all  # Creates both AppImage and tarball

Usage Guide
Tasks Tab

    Click + to add a new task
    Check the box to mark tasks complete
    Click Add List to create custom task lists
    Click the delete icon to remove tasks
    Set due dates using the date picker

Calendar Tab

    Switch between Month, Week, and Day views using the tabs
    Use ← and → arrows to navigate
    Click any date to see tasks for that day
    Days with tasks show a colored dot indicator
    Sync Your Calendar:
        Go to Settings → Calendar Sync
        Export tasks to .ics file for use in Google Calendar, Outlook, etc.
        Import .ics files to bring tasks from other calendars

Notes Tab

    Click + to create a new note
    Start writing immediately
    Click the palette icon to customize appearance
    Choose background color, text color, and font size
    Click ✓ to save
    Click any note card to edit
    Click the delete icon to remove

News Tab

    Browse latest headlines from multiple sources
    Click any headline to read the full article in your browser
    Change news source and category in Settings
    News refreshes automatically with smart caching

Settings

    Toggle widgets on/off (Greeting, Quote, News)
    Choose your preferred news source and category
    Switch between Dark/Light mode
    Select from 8 beautiful themes
    Manage calendar synchronization

System Requirements

    OS: Linux (any modern distribution)
    Java: JRE/JDK 17 or higher
    Display: X11 or Wayland
    Memory: 512 MB RAM (recommended: 1 GB)
    Storage: 100 MB free space

Data Storage

All your data is stored locally and securely in:

~/.myday/data.json

This includes:

    All tasks and task lists
    Notes with formatting
    Calendar events
    Application settings

Backup Your Data

Simply copy the ~/.myday directory:

cp -r ~/.myday ~/myday-backup

Technology Stack

Built with modern, robust technologies:

    Kotlin - Modern, concise programming language
    Compose Multiplatform - Declarative UI framework
    Material 3 - Google's latest design system
    Kotlinx Serialization - JSON persistence
    Kotlinx Coroutines - Asynchronous programming

Version History
v1.0.1 (Latest - October 2025)

    ✅ Calendar Sync - iCalendar import/export
    ✅ Calendar Redesign - Month/Week/Day views
    ✅ News Widget - Clickable headlines with caching
    ✅ Professional Icon - 512x512 calendar-themed
    ✅ Performance - Optimized rendering and caching
    ✅ Bug Fixes - Deprecated icons, navigation improvements

v1.0.0 (October 2025)

    Initial release with core features
    Tasks, Calendar, Notes, News, and Home dashboard
    Dark/Light mode and multiple themes
    Data persistence

See full changelog
Contributing

Contributions are welcome! Whether it's:

    🐛 Bug reports
    💡 Feature suggestions
    📝 Documentation improvements
    🔧 Code contributions

Please feel free to open an issue or submit a pull request.
Roadmap

See VERSION_1.0.2_TODO.md for planned features:

    UI/UX improvements
    Subtasks and priorities
    Rich text formatting in notes
    Recurring tasks
    Pomodoro timer
    Windows and macOS support

License

This project is licensed under the MIT License - see the LICENSE file for details.
Support

Having issues? Here are some resources:

    📖 Quick Start Guide
    🔧 Troubleshooting
    🐛 Report a Bug
    💬 Discussions

Screenshots

    Coming soon! Screenshots of the application in action.

Acknowledgments

    Inspired by the Android MyDay app
    Icons from Material Design Icons
    News feeds from various RSS sources

Made with ❤️ using Kotlin and Compose Multiplatform

Star ⭐ this repository if you find it helpful!
