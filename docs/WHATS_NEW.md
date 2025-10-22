# MyDay Desktop - What's New

## Overview
MyDay Desktop transforms the Android MyDay application into a native Linux desktop app with significant improvements to the Notes section, inspired by MyJournal Desktop.

## Major Changes from Android Version

### 🎨 Notes Section - Complete Redesign

The notes section has been completely reimagined following the MyJournal philosophy:

#### Before (Android MyDay):
- Small text field dialog for quick notes
- Title required upfront
- Limited customization
- Basic card view
- No theming options

#### After (Desktop MyDay):
- **Full-screen editor** for distraction-free writing
- **Write-first workflow** - no title field during writing
- **Save dialog** appears when you're done - name it then
- **Rich theming system**:
  - 5 background themes (Cream, Mint, Sky, Rose, Lavender)
  - 5 text colors (Brown, Gray, Blue, Green, Purple)
  - Font size slider (12-24sp)
- **Beautiful grid layout** with staggered cards
- **Persistent styling** - each note remembers its colors
- **Date display** showing when you wrote it
- **Smart date formatting** - "2h ago", "3d ago", or full date

### 📝 Writing Experience

**MyJournal-style Features:**
1. Click + to create note
2. Immediately start writing (no title prompt)
3. See today's date at the top
4. Customizable placeholder text encourages free writing
5. When done, click save
6. Dialog appears: "Name Your Note"
7. Give it a title (or leave as "Untitled")
8. Note is saved with all your customizations

**Placeholder Text Example:**
```
Start writing your thoughts...

What's on your mind?
What happened today?
Ideas, plans, or reflections?

Write freely, you'll name this note when you're done.
```

### 🖥️ Desktop-Specific Improvements

1. **Navigation**: Clean bottom navigation bar (Tasks | Calendar | Notes)
2. **Window Size**: Optimized 1200x800dp window
3. **Keyboard-Friendly**: Desktop interaction patterns
4. **File Storage**: Simple JSON file at `~/.myday/data.json`
5. **No Permissions**: No Android permissions needed

### 🎯 Task & Calendar Features (Preserved)

All the task management and calendar features from Android are preserved:
- Multiple task lists with colors
- Task completion tracking
- Due dates with calendar integration
- Monthly calendar view
- Visual task indicators

## Side-by-Side Comparison

### Note Creation Flow

**Android MyDay:**
```
1. Click +
2. Dialog appears with title and content fields
3. Fill both fields
4. Click Save
```

**Desktop MyDay (New):**
```
1. Click +
2. Full-screen editor opens
3. Start writing immediately
4. Click save when done
5. Dialog asks for title
6. Name and save
```

### Note Customization

**Android MyDay:**
- Background color only
- Fixed text color
- No font size options
- Set when creating

**Desktop MyDay (New):**
- 5 background themes
- 5 text colors
- Font size slider (12-24sp)
- Customize anytime via palette button
- Each note remembers its style

### Visual Layout

**Android MyDay:**
- 2-column grid (fixed)
- Simple cards
- Title + content preview
- No date display

**Desktop MyDay (New):**
- Adaptive staggered grid
- Themed cards (colored backgrounds)
- Title + content preview
- Smart date formatting
- Delete button on card

## Technical Changes

### Architecture
- **Before**: Android Room database with DAO
- **After**: Kotlin serialization with JSON files

### Dependencies
- **Before**: AndroidX, Room, Material Design 2
- **After**: Compose Multiplatform, Material 3, Kotlinx Serialization

### State Management
- **Before**: LiveData/Flow with ViewModels
- **After**: StateFlow with desktop ViewModel (no lifecycle)

## File Locations

### Android MyDay
- App data: `/data/data/com.example.myday/databases/`
- Shared preferences: `/data/data/com.example.myday/shared_prefs/`

### Desktop MyDay
- All data: `~/.myday/data.json`
- Single JSON file for everything

## Benefits of Desktop Version

1. **Better Writing Experience**: Full keyboard, large screen, no distractions
2. **Easier Backup**: Single JSON file
3. **Cross-Device**: Can edit the JSON on multiple Linux machines
4. **Privacy**: All local, no cloud, no permissions
5. **Performance**: Native desktop performance
6. **Multitasking**: Run alongside other desktop apps

## What's the Same

- All task management features
- Calendar view and functionality
- Task lists with colors
- Data model structure
- Material Design aesthetic

## What's Better

- ✨ Notes writing experience (MyJournal-style)
- 🎨 Rich theming and customization
- 🖥️ Desktop-native interactions
- 📁 Simple file-based storage
- ⚡ Fast startup and performance

## Migration Path (Future)

To move from Android MyDay to Desktop:
1. Export Android data (feature to be added)
2. Convert to JSON format
3. Place in `~/.myday/data.json`
4. Launch Desktop version

## Quick Start

```bash
cd /home/cobrien/Projects/MyDayDesktop
./run.sh
```

Start writing your first note and experience the difference!
