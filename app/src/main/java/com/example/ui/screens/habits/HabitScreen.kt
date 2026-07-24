package com.example.ui.screens.habits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.HabitEntity
import com.example.ui.components.CircularProgressGauge
import com.example.ui.components.GlassmorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitScreen(
    habits: List<HabitEntity>,
    onAddHabit: (String, String, String) -> Unit,
    onToggleHabit: (HabitEntity) -> Unit,
    onDeleteHabit: (HabitEntity) -> Unit
) {
    var selectedFrequency by remember { mutableStateOf("Daily") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredHabits = habits.filter { it.frequency.equals(selectedFrequency, ignoreCase = true) }
    val completedCount = filteredHabits.count { it.isCompletedToday }
    val completionPercent = if (filteredHabits.isNotEmpty()) (completedCount * 100) / filteredHabits.size else 0

    Box(modifier = Modifier.fillMaxSize().testTag("habits_screen")) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Habit Tracker",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Completion Progress Gauge Banner
            GlassmorphicCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("Today's Habit Streak", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("$completedCount of ${filteredHabits.size} Habits Completed", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    CircularProgressGauge(
                        progressPercent = completionPercent,
                        modifier = Modifier.size(60.dp),
                        strokeWidth = 6.dp,
                        activeColor = Color(0xFFFF9800)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Frequency Filter Tabs
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Daily", "Weekly", "Monthly").forEach { freq ->
                    FilterChip(
                        selected = selectedFrequency == freq,
                        onClick = { selectedFrequency = freq },
                        label = { Text(freq) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredHabits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No $selectedFrequency habits configured yet. Tap + to create one!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(filteredHabits, key = { it.id }) { habit ->
                        GlassmorphicCard {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.LocalFireDepartment,
                                        contentDescription = "Flame Streak",
                                        tint = if (habit.streakCount > 0) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(habit.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("${habit.category} • ${habit.streakCount} Day Streak 🔥", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onToggleHabit(habit) },
                                        modifier = Modifier.testTag("check_habit_${habit.id}")
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Check Habit",
                                            tint = if (habit.isCompletedToday) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(onClick = { onDeleteHabit(habit) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Habit", tint = MaterialTheme.colorScheme.error)
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
                .testTag("add_habit_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Habit")
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, category, freq ->
                onAddHabit(name, category, freq)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Health") }
    var frequency by remember { mutableStateOf("Daily") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Habit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_habit_name_input")
                )

                Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Health", "Mental", "Learning", "Finance").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                Text("Frequency", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Daily", "Weekly", "Monthly").forEach { freq ->
                        FilterChip(
                            selected = frequency == freq,
                            onClick = { frequency = freq },
                            label = { Text(freq) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, category, frequency) },
                modifier = Modifier.testTag("add_habit_confirm_btn")
            ) {
                Text("Create Habit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
