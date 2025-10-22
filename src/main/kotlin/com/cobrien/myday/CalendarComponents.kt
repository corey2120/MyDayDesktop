package com.cobrien.myday

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.compose.ui.text.style.TextOverflow

data class CalendarDay(val dayNumberText: String, val date: Date)

enum class CalendarViewMode {
    MONTH, WEEK, DAY
}

@Composable
fun SimpleCalendarView(
    tasks: List<Task>,
    selectedDate: Date?,
    onDateClick: (Date) -> Unit,
    currentMonth: Int,
    currentYear: Int,
    onMonthChange: (Int, Int) -> Unit,
    showHolidays: Boolean = true,
    calendarEvents: List<CalendarEvent> = emptyList()
) {
    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }

    when (viewMode) {
        CalendarViewMode.MONTH -> MonthCalendarView(
            tasks = tasks,
            selectedDate = selectedDate,
            onDateClick = onDateClick,
            currentMonth = currentMonth,
            currentYear = currentYear,
            onMonthChange = onMonthChange,
            viewMode = viewMode,
            onViewModeChange = { viewMode = it },
            showHolidays = showHolidays,
            calendarEvents = calendarEvents
        )
        CalendarViewMode.WEEK -> WeekCalendarView(
            tasks = tasks,
            selectedDate = selectedDate ?: Date(),
            onDateClick = onDateClick,
            viewMode = viewMode,
            onViewModeChange = { viewMode = it }
        )
        CalendarViewMode.DAY -> DayCalendarView(
            tasks = tasks,
            selectedDate = selectedDate ?: Date(),
            onDateClick = onDateClick,
            viewMode = viewMode,
            onViewModeChange = { viewMode = it }
        )
    }
}

@Composable
private fun CalendarViewModeSelector(
    currentMode: CalendarViewMode,
    onModeChange: (CalendarViewMode) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            CalendarViewMode.values().forEach { mode ->
                val isSelected = mode == currentMode
                Surface(
                    onClick = { onModeChange(mode) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                           else Color.Transparent,
                    modifier = Modifier.padding(0.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthCalendarView(
    tasks: List<Task>,
    selectedDate: Date?,
    onDateClick: (Date) -> Unit,
    currentMonth: Int,
    currentYear: Int,
    onMonthChange: (Int, Int) -> Unit,
    viewMode: CalendarViewMode,
    onViewModeChange: (CalendarViewMode) -> Unit,
    showHolidays: Boolean = true,
    calendarEvents: List<CalendarEvent> = emptyList()
) {
    // Use rememberUpdatedState to ensure we always get the latest values
    val currentMonthState by rememberUpdatedState(currentMonth)
    val currentYearState by rememberUpdatedState(currentYear)

    // Cache date formatter to avoid recreation on each recomposition
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val goToNextMonth = {
        val newCalendar = Calendar.getInstance().apply {
            clear()
            set(Calendar.YEAR, currentYearState)
            set(Calendar.MONTH, currentMonthState)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, 1)
        }
        val newMonth = newCalendar.get(Calendar.MONTH)
        val newYear = newCalendar.get(Calendar.YEAR)
        onMonthChange(newMonth, newYear)
    }

    val goToPreviousMonth = {
        val newCalendar = Calendar.getInstance().apply {
            clear()
            set(Calendar.YEAR, currentYearState)
            set(Calendar.MONTH, currentMonthState)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, -1)
        }
        val newMonth = newCalendar.get(Calendar.MONTH)
        val newYear = newCalendar.get(Calendar.YEAR)
        onMonthChange(newMonth, newYear)
    }

    val monthStartDate = Calendar.getInstance().apply {
        clear()
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val daysInMonth = monthStartDate.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeekSystem = Calendar.getInstance().firstDayOfWeek
    val firstDayOfWeekOfMonth = monthStartDate.get(Calendar.DAY_OF_WEEK)
    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(monthStartDate.time)
    val emptyCells = (firstDayOfWeekOfMonth - firstDayOfWeekSystem + 7) % 7

    val dayCellsData = remember(currentYear, currentMonth) {
        (1..emptyCells).map { null } + (1..daysInMonth).map { day ->
            val dayCalendar = Calendar.getInstance().apply {
                time = monthStartDate.time
                set(Calendar.DAY_OF_MONTH, day)
            }
            CalendarDay(day.toString(), dayCalendar.time)
        }
    }

    // Calculate number of rows needed (always need full weeks)
    val totalCells = dayCellsData.size
    val numberOfRows = kotlin.math.ceil(totalCells / 7.0).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalIconButton(onClick = goToPreviousMonth) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                    }
                    Text(
                        text = monthName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FilledTonalIconButton(onClick = goToNextMonth) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                    }
                }
                CalendarViewModeSelector(
                    currentMode = viewMode,
                    onModeChange = onViewModeChange
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Week number header
                Box(
                    modifier = Modifier.width(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Wk",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }

                val dayFormatter = SimpleDateFormat("E", Locale.getDefault())
                val tempCal = Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, firstDayOfWeekSystem) }
                repeat(7) {
                    Text(
                        text = dayFormatter.format(tempCal.time).take(3),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    tempCal.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Use a Column with Rows instead of LazyVerticalGrid to avoid height issues
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Split cells into rows of 7
                for (rowIndex in 0 until numberOfRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Week number column
                        val firstDayOfWeek = dayCellsData.getOrNull(rowIndex * 7)
                        if (firstDayOfWeek != null) {
                            val weekCalendar = Calendar.getInstance().apply {
                                time = firstDayOfWeek.date
                            }
                            val weekNumber = weekCalendar.get(Calendar.WEEK_OF_YEAR)
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = weekNumber.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(32.dp))
                        }

                        for (colIndex in 0 until 7) {
                            val cellIndex = rowIndex * 7 + colIndex
                            val calendarDayData = if (cellIndex < dayCellsData.size) dayCellsData[cellIndex] else null

                            Box(modifier = Modifier.weight(1f)) {
                                if (calendarDayData != null) {
                                    val localDate = calendarDayData.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                    val isSelected = selectedDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() == localDate
                                    val isCurrentDay = LocalDate.now() == localDate
                                    val holiday = if (showHolidays) HolidayProvider.getHoliday(localDate) else null

                                    // Optimize task filtering with remember to avoid recalculation on every recomposition
                                    val tasksForDay = remember(tasks, calendarDayData.date) {
                                        val dateString = dateFormatter.format(calendarDayData.date)
                                        tasks.filter { task ->
                                            try {
                                                task.dateTime.startsWith(dateString)
                                            } catch (e: Exception) {
                                                false
                                            }
                                        }
                                    }

                                    // Filter calendar events for this day
                                    val eventsForDay = remember(calendarEvents, calendarDayData.date) {
                                        calendarEvents.filter { event ->
                                            try {
                                                val eventDate = Date(event.startDateTime).toInstant()
                                                    .atZone(ZoneId.systemDefault()).toLocalDate()
                                                eventDate == localDate
                                            } catch (e: Exception) {
                                                false
                                            }
                                        }
                                    }

                                    val tasksForDayCount = tasksForDay.size
                                    val completedTasksCount = tasksForDay.count { it.isCompleted }
                                    val hasUncompletedTasks = tasksForDayCount > completedTasksCount

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                when {
                                                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                                                    isCurrentDay -> MaterialTheme.colorScheme.secondaryContainer
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable { onDateClick(calendarDayData.date) }
                                            .padding(4.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = calendarDayData.dayNumberText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isSelected || isCurrentDay) FontWeight.Bold else FontWeight.Normal,
                                                color = when {
                                                    isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                                    isCurrentDay -> MaterialTheme.colorScheme.onSecondaryContainer
                                                    holiday != null && holiday.type == HolidayType.FEDERAL -> MaterialTheme.colorScheme.error
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                }
                                            )

                                            // Show event dots indicator
                                            if (eventsForDay.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                    modifier = Modifier.height(6.dp)
                                                ) {
                                                    // Show up to 3 event dots with their colors
                                                    eventsForDay.take(3).forEach { event ->
                                                        Box(
                                                            modifier = Modifier
                                                                .size(5.dp)
                                                                .clip(CircleShape)
                                                                .background(Color(getEventColor(event.colorId)))
                                                        )
                                                    }
                                                    // If more than 3 events, show a +N indicator
                                                    if (eventsForDay.size > 3) {
                                                        Text(
                                                            text = "+${eventsForDay.size - 3}",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.7f
                                                            ),
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }

                                            // Show holiday indicator
                                            if (holiday != null) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            when (holiday.type) {
                                                                HolidayType.FEDERAL -> MaterialTheme.colorScheme.error
                                                                HolidayType.OBSERVANCE -> MaterialTheme.colorScheme.primary
                                                                HolidayType.RELIGIOUS -> MaterialTheme.colorScheme.tertiary
                                                                HolidayType.INTERNATIONAL -> MaterialTheme.colorScheme.secondary
                                                            }
                                                        )
                                                )
                                            }
                                            if (tasksForDayCount > 0) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                // Show task count badge
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(
                                                            if (hasUncompletedTasks)
                                                                MaterialTheme.colorScheme.primary
                                                            else
                                                                MaterialTheme.colorScheme.tertiary
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = if (completedTasksCount > 0) "$completedTasksCount/$tasksForDayCount"
                                                               else "$tasksForDayCount",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (hasUncompletedTasks)
                                                            MaterialTheme.colorScheme.onPrimary
                                                        else
                                                            MaterialTheme.colorScheme.onTertiary,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.aspectRatio(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekCalendarView(
    tasks: List<Task>,
    selectedDate: Date,
    onDateClick: (Date) -> Unit,
    viewMode: CalendarViewMode,
    onViewModeChange: (CalendarViewMode) -> Unit
) {
    // Cache date formatter
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val calendar = Calendar.getInstance().apply {
        time = selectedDate
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
    }

    val weekDays = remember(selectedDate) {
        (0..6).map { dayOffset ->
            val dayCalendar = Calendar.getInstance().apply {
                time = calendar.time
                add(Calendar.DAY_OF_MONTH, dayOffset)
            }
            CalendarDay(dayCalendar.get(Calendar.DAY_OF_MONTH).toString(), dayCalendar.time)
        }
    }

    fun goToPreviousWeek() {
        val newDate = Calendar.getInstance().apply {
            time = selectedDate
            add(Calendar.WEEK_OF_YEAR, -1)
        }.time
        onDateClick(newDate)
    }

    fun goToNextWeek() {
        val newDate = Calendar.getInstance().apply {
            time = selectedDate
            add(Calendar.WEEK_OF_YEAR, 1)
        }.time
        onDateClick(newDate)
    }

    val weekRange = SimpleDateFormat("MMM d", Locale.getDefault()).format(weekDays.first().date) +
                    " - " + SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(weekDays.last().date)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalIconButton(onClick = ::goToPreviousWeek) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Week")
                    }
                    Text(
                        text = weekRange,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FilledTonalIconButton(onClick = ::goToNextWeek) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Week")
                    }
                }
                CalendarViewModeSelector(
                    currentMode = viewMode,
                    onModeChange = onViewModeChange
                )
            }
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weekDays.forEach { dayData ->
                    val isSelected = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() ==
                        dayData.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    val isCurrentDay = LocalDate.now() ==
                        dayData.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

                    // Optimize task filtering with remember
                    val tasksForDay = remember(tasks, dayData.date) {
                        val dateString = dateFormatter.format(dayData.date)
                        tasks.filter { task ->
                            try {
                                task.dateTime.startsWith(dateString)
                            } catch (e: Exception) {
                                false
                            }
                        }
                    }
                    val tasksForDayCount = tasksForDay.size
                    val completedTasksCount = tasksForDay.count { it.isCompleted }
                    val hasUncompletedTasks = tasksForDayCount > completedTasksCount

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                                    isCurrentDay -> MaterialTheme.colorScheme.secondaryContainer
                                    else -> Color.Transparent
                                }
                            )
                            .clickable { onDateClick(dayData.date) }
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    ) {
                        val dayFormatter = SimpleDateFormat("EEE", Locale.getDefault())
                        Text(
                            text = dayFormatter.format(dayData.date),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                isCurrentDay -> MaterialTheme.colorScheme.onSecondaryContainer
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dayData.dayNumberText,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                isCurrentDay -> MaterialTheme.colorScheme.onSecondaryContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                        if (tasksForDayCount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (hasUncompletedTasks)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.tertiary
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (completedTasksCount > 0) "$completedTasksCount/$tasksForDayCount"
                                           else "$tasksForDayCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (hasUncompletedTasks)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onTertiary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCalendarView(
    tasks: List<Task>,
    selectedDate: Date,
    onDateClick: (Date) -> Unit,
    viewMode: CalendarViewMode,
    onViewModeChange: (CalendarViewMode) -> Unit
) {
    // Cache date formatters
    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()) }
    val taskDateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    fun goToPreviousDay() {
        val newDate = Calendar.getInstance().apply {
            time = selectedDate
            add(Calendar.DAY_OF_MONTH, -1)
        }.time
        onDateClick(newDate)
    }

    fun goToNextDay() {
        val newDate = Calendar.getInstance().apply {
            time = selectedDate
            add(Calendar.DAY_OF_MONTH, 1)
        }.time
        onDateClick(newDate)
    }

    val isToday = LocalDate.now() == selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

    // Optimize task filtering with remember
    val tasksForDay = remember(tasks, selectedDate) {
        val dateString = taskDateFormatter.format(selectedDate)
        tasks.filter { task ->
            try {
                task.dateTime.startsWith(dateString)
            } catch (e: Exception) {
                false
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalIconButton(onClick = ::goToPreviousDay) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Day")
                    }
                    Column {
                        Text(
                            text = dateFormatter.format(selectedDate),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (isToday) {
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    FilledTonalIconButton(onClick = ::goToNextDay) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Day")
                    }
                }
                CalendarViewModeSelector(
                    currentMode = viewMode,
                    onModeChange = onViewModeChange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task summary for the day
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${tasksForDay.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Total Tasks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${tasksForDay.count { it.isCompleted }}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${tasksForDay.count { !it.isCompleted }}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskViewer(
    selectedDate: LocalDate,
    tasks: List<Task>,
    taskLists: List<TaskList>,
    calendarEvents: List<CalendarEvent> = emptyList(),
    onAddTaskClicked: () -> Unit,
    onToggleTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    showHolidays: Boolean = true
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Schedule for", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        selectedDate.format(dateFormatter),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                FilledTonalIconButton(onClick = onAddTaskClicked) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }

            HorizontalDivider()

            val holiday = if (showHolidays) HolidayProvider.getHoliday(selectedDate) else null

            if (tasks.isEmpty() && calendarEvents.isEmpty() && holiday == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tasks or events for this day",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    // Holiday Section
                    if (holiday != null) {
                        item {
                            HolidayItem(holiday = holiday)
                        }
                    }

                    // Calendar Events Section
                    if (calendarEvents.isNotEmpty()) {
                        item {
                            Text(
                                "Calendar Events",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 8.dp)
                            )
                        }
                        items(calendarEvents) { event ->
                            CalendarEventItem(event = event)
                        }
                    }

                    // Tasks Section
                    if (tasks.isNotEmpty()) {
                        item {
                            Text(
                                "Tasks",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 8.dp)
                            )
                        }
                        items(tasks) { task ->
                            val taskList = taskLists.find { it.id == task.listId }
                            TaskItem(
                                task = task,
                                taskListColor = taskList?.color ?: 0xFF6200EE,
                                onToggle = { onToggleTask(task) },
                                onDelete = { onDeleteTask(task) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HolidayItem(holiday: Holiday) {
    val holidayColor = when (holiday.type) {
        HolidayType.FEDERAL -> Color(0xFFD32F2F) // Red
        HolidayType.OBSERVANCE -> Color(0xFF1976D2) // Blue
        HolidayType.RELIGIOUS -> Color(0xFF7B1FA2) // Purple
        HolidayType.INTERNATIONAL -> Color(0xFF388E3C) // Green
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(holidayColor.copy(alpha = 0.1f))
            .border(1.dp, holidayColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Holiday icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(holidayColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🎉",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = holiday.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = when (holiday.type) {
                    HolidayType.FEDERAL -> "Federal Holiday"
                    HolidayType.OBSERVANCE -> "Observance"
                    HolidayType.RELIGIOUS -> "Religious Holiday"
                    HolidayType.INTERNATIONAL -> "International Holiday"
                },
                style = MaterialTheme.typography.bodySmall,
                color = holidayColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CalendarEventItem(event: CalendarEvent) {
    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    val eventColor = Color(getEventColor(event.colorId))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Color accent bar on the left
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(eventColor)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(eventColor.copy(alpha = 0.1f))
                .padding(12.dp)
        ) {
            Text(
                text = event.summary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calendar name with colored dot
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(eventColor)
                    )
                    Text(
                        text = event.calendarName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (!event.isAllDay) {
                    Text("•", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = timeFormatter.format(Date(event.startDateTime)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text("•", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "All day",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    taskListColor: Long,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completion checkbox
            IconButton(onClick = onToggle) {
                Icon(
                    if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = if (task.isCompleted) "Mark incomplete" else "Mark complete",
                    tint = Color(taskListColor)
                )
            }

            // Task content
            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Priority indicator
                    if (task.priority != TaskPriority.NONE) {
                        Surface(
                            shape = CircleShape,
                            color = Color(task.priority.toColor()),
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }

                // Tags
                if (task.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        task.tags.take(3).forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    tag,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        if (task.tags.size > 3) {
                            Text(
                                "+${task.tags.size - 3}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Subtask progress
                if (task.subtasks.isNotEmpty()) {
                    val completedSubtasks = task.subtasks.count { it.isCompleted }
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = "Subtasks",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "$completedSubtasks/${task.subtasks.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Recurrence indicator
                if (task.recurrence.type != RecurrenceType.NONE) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Recurring",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            when (task.recurrence.type) {
                                RecurrenceType.DAILY -> "Daily"
                                RecurrenceType.WEEKLY -> "Weekly"
                                RecurrenceType.MONTHLY -> "Monthly"
                                RecurrenceType.YEARLY -> "Yearly"
                                RecurrenceType.NONE -> ""
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
