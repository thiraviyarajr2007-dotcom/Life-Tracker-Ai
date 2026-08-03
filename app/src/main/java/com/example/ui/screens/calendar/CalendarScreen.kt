package com.example.ui.screens.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CalendarEventEntity
import com.example.ui.components.GlassmorphicCard
import java.text.SimpleDateFormat
import java.util.*

enum class CalendarViewMode {
    MONTHLY, WEEKLY, AGENDA, TIMELINE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    events: List<CalendarEventEntity>,
    onAddEvent: (String, String, Long, String, String) -> Unit,
    onDeleteEvent: (CalendarEventEntity) -> Unit
) {
    var selectedDay by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }
    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTHLY) }
    var showAddDialog by remember { mutableStateOf(false) }

    val currentMonthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

    Box(modifier = Modifier.fillMaxSize().testTag("calendar_screen")) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Calendar & Planner",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Calendar View Mode Selector (Monthly, Weekly, Agenda, Timeline)
            ScrollableTabRow(
                selectedTabIndex = viewMode.ordinal,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                CalendarViewMode.values().forEach { mode ->
                    Tab(
                        selected = viewMode == mode,
                        onClick = { viewMode = mode },
                        text = {
                            Text(
                                text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            AnimatedContent(targetState = viewMode, label = "CalendarViewTransition") { mode ->
                when (mode) {
                    CalendarViewMode.MONTHLY -> {
                        Column {
                            GlassmorphicCard {
                                Text(
                                    text = currentMonthYear,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                                        Text(day, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                val daysList = (1..30).toList()
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    daysList.chunked(7).forEach { week ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {
                                            week.forEach { dayNumber ->
                                                val isSelected = dayNumber == selectedDay
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                                        .clickable { selectedDay = dayNumber },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "$dayNumber",
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            EventsListForDay(selectedDay = selectedDay, events = events, onDeleteEvent = onDeleteEvent)
                        }
                    }

                    CalendarViewMode.WEEKLY -> {
                        Column {
                            GlassmorphicCard {
                                Text("This Week Schedule", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    listOf("Mon 20", "Tue 21", "Wed 22", "Thu 23", "Fri 24", "Sat 25", "Sun 26").forEach { weekDay ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                            modifier = Modifier.padding(2.dp)
                                        ) {
                                            Text(
                                                text = weekDay,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            EventsListForDay(selectedDay = selectedDay, events = events, onDeleteEvent = onDeleteEvent)
                        }
                    }

                    CalendarViewMode.AGENDA -> {
                        Column {
                            Text("Agenda View", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                items(events) { ev ->
                                    GlassmorphicCard {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(ev.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                                Text("📍 ${ev.location.ifBlank { "Virtual Workspace" }} • ${ev.eventType}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                            }
                                            IconButton(onClick = { onDeleteEvent(ev) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    CalendarViewMode.TIMELINE -> {
                        Column {
                            Text("Timeline View", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                listOf(
                                    "09:00 AM" to "Morning Kickoff & AI Sync",
                                    "10:30 AM" to "Deep Work Focus Session",
                                    "01:00 PM" to "Hydration & Health Check",
                                    "03:00 PM" to "Sprint Planning & Code Review",
                                    "06:30 PM" to "HIIT Cardio Workout"
                                ).forEach { (time, title) ->
                                    item {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(10.dp)
                                            ) {}
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(time, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            GlassmorphicCard(modifier = Modifier.weight(1f)) {
                                                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 20.dp)
                .testTag("add_calendar_event_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Event")
        }
    }

    if (showAddDialog) {
        AddCalendarEventDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, eventType, location ->
                onAddEvent(title, desc, System.currentTimeMillis(), eventType, location)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun EventsListForDay(
    selectedDay: Int,
    events: List<CalendarEventEntity>,
    onDeleteEvent: (CalendarEventEntity) -> Unit
) {
    Text("Events & Tasks for Day $selectedDay", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(8.dp))

    if (events.isEmpty()) {
        Text("No events scheduled on this date.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(events, key = { it.id }) { event ->
                GlassmorphicCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(event.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${event.eventType} • ${event.location.ifBlank { "Virtual" }}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            if (event.description.isNotBlank()) {
                                Text(event.description, fontSize = 13.sp)
                            }
                        }
                        IconButton(onClick = { onDeleteEvent(event) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddCalendarEventDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var eventType by remember { mutableStateOf("Event") }
    var location by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_event_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Event", "Birthday", "Reminder", "Task").forEach { type ->
                        FilterChip(
                            selected = eventType == type,
                            onClick = { eventType = type },
                            label = { Text(type) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onConfirm(title, description, eventType, location) },
                modifier = Modifier.testTag("add_event_confirm_btn")
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
