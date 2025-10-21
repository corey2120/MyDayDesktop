# MyDay Desktop - Complete Redesign Plan

## Current Issues
1. ❌ Wrong navigation order (Tasks/Calendar/Notes instead of Calendar/Tasks/Notes)
2. ❌ No Home Screen widgets (Greeting, Quote, News)
3. ❌ No Settings screen with theme switcher
4. ❌ Calendar implementation doesn't match Android
5. ❌ No proper HorizontalPager navigation
6. ❌ Notes section is too complex (MyJournal style instead of simple dialog)

## Android Version Structure

### Main Navigation (HorizontalPager)
1. **Calendar (Home)** - Page 0
   - Optional: Greeting Widget
   - Optional: Quote Widget  
   - Optional: News Widget
   - Calendar Month View
   - Tasks for Selected Date

2. **Tasks** - Page 1
   - Task Lists Screen → Tasks for List

3. **Notes** - Page 2
   - Notes List → Note Detail

### Settings Screen
- Home Screen Widgets toggles
- Theme Switcher (8 themes)
- Security (PIN for secure notes)
- Calendar Sync
- App Info

## Required Files to Create/Update

### Core App Structure
- [x] Models.kt - Update to match Android models
- [x] DataRepository.kt - Keep current JSON approach
- [x] AppViewModel.kt - Update to match Android ViewModel
- [ ] Main.kt - Keep simple
- [ ] MyDayApp.kt - **COMPLETE REWRITE**

### Screens
- [ ] **HomeCalendarScreen.kt** - NEW (Calendar with widgets)
- [ ] **TaskListsScreen.kt** - NEW
- [ ] **TasksScreen.kt** - REWRITE (for specific list)
- [ ] **NotesScreen.kt** - SIMPLIFY (remove MyJournal complexity)
- [ ] **NoteDetailScreen.kt** - NEW (simple note dialog)
- [ ] **SettingsScreen.kt** - NEW

### Components
- [ ] **HomeWidgets.kt** - NEW (Greeting, Quote, News)
- [ ] **CalendarComponents.kt** - NEW (SimpleCalendarView, TaskViewer)
- [ ] **ThemeSystem.kt** - NEW (8 themes matching Android)

### Updated Build
- [ ] build-package.sh - Update after testing

## Implementation Order

### Phase 1: Core Structure (1-2 hours)
1. Update Models to match Android
2. Update AppViewModel  
3. Create theme system with 8 themes
4. Update DataRepository for new models

### Phase 2: Screens (2-3 hours)
5. Create HomeCalendarScreen with widgets
6. Create simplified NotesScreen
7. Create TaskListsScreen  
8. Update TasksScreen for single list
9. Create SettingsScreen

### Phase 3: Main App (1 hour)
10. Rewrite MyDayApp with HorizontalPager
11. Add proper navigation
12. Test all flows

### Phase 4: Polish & Test (1 hour)
13. Fix any bugs
14. Test theme switching
15. Test data persistence
16. Update documentation

## Key Design Decisions

### Keep from Current Version
- JSON data persistence (works well)
- Desktop window setup
- Build system

### Change to Match Android
- Navigation structure (HorizontalPager)
- Screen order (Calendar/Tasks/Notes)
- Theme system (8 themes)
- Settings screen
- Home widgets
- Simplified notes

### Desktop-Specific Adaptations
- HorizontalPager → TabRow with content pane
- News Widget → Simplified (no RSS parsing, use demo data)
- Calendar sync → Skip for now (desktop doesn't have device calendar)
- Secure notes PIN → Skip for now (optional feature)

## Estimated Time
- **Total**: 5-7 hours for complete redesign
- **First working version**: 3-4 hours
- **Fully polished**: 5-7 hours

## Testing Checklist
- [ ] Calendar shows correctly
- [ ] Widgets toggle on/off
- [ ] Theme switching works
- [ ] Tasks can be added/completed/deleted
- [ ] Notes can be created/edited/deleted
- [ ] Navigation flows properly
- [ ] Data persists across restarts
- [ ] AppImage builds and runs
- [ ] Settings save properly

## Notes
This will be a significant rewrite but will result in a desktop version that truly matches the Android experience.
