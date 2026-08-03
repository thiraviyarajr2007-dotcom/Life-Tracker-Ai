package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class QuickAddType {
    TASK, EXPENSE, HABIT, NOTE, GOAL
}

@Composable
fun QuickAddFab(
    onAddTask: (title: String, category: String, priority: String, dueDate: Long) -> Unit,
    onAddExpense: (title: String, amount: Double, type: String, category: String, notes: String) -> Unit,
    onAddHabit: (name: String, category: String, frequency: String) -> Unit,
    onAddNote: (title: String, content: String, isChecklist: Boolean, folder: String) -> Unit,
    onAddGoal: (title: String, description: String, category: String, type: String, milestones: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var activeDialog by remember { mutableStateOf<QuickAddType?>(null) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        label = "FabRotation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    SpeedDialItem("📋 Task", Color(0xFF1E88E5)) {
                        activeDialog = QuickAddType.TASK
                        isExpanded = false
                    }
                    SpeedDialItem("💰 Expense", Color(0xFFFB8C00)) {
                        activeDialog = QuickAddType.EXPENSE
                        isExpanded = false
                    }
                    SpeedDialItem("🔥 Habit", Color(0xFF43A047)) {
                        activeDialog = QuickAddType.HABIT
                        isExpanded = false
                    }
                    SpeedDialItem("📝 Note", Color(0xFF8E24AA)) {
                        activeDialog = QuickAddType.NOTE
                        isExpanded = false
                    }
                    SpeedDialItem("🎯 Goal", Color(0xFFE53935)) {
                        activeDialog = QuickAddType.GOAL
                        isExpanded = false
                    }
                }
            }

            ExtendedFloatingActionButton(
                onClick = { isExpanded = !isExpanded },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Expand Quick Add",
                        modifier = Modifier.rotate(rotationAngle)
                    )
                },
                text = {
                    Text(
                        text = if (isExpanded) "Close" else "＋ Quick Add",
                        fontWeight = FontWeight.Bold
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.testTag("quick_add_fab")
            )
        }
    }

    when (activeDialog) {
        QuickAddType.TASK -> {
            QuickAddTaskDialog(
                onDismiss = { activeDialog = null },
                onConfirm = { t, c, p ->
                    onAddTask(t, c, p, System.currentTimeMillis())
                    activeDialog = null
                }
            )
        }
        QuickAddType.EXPENSE -> {
            QuickAddExpenseDialog(
                onDismiss = { activeDialog = null },
                onConfirm = { t, a, c ->
                    onAddExpense(t, a, "Expense", c, "Quick logged")
                    activeDialog = null
                }
            )
        }
        QuickAddType.HABIT -> {
            QuickAddHabitDialog(
                onDismiss = { activeDialog = null },
                onConfirm = { name, category ->
                    onAddHabit(name, category, "Daily")
                    activeDialog = null
                }
            )
        }
        QuickAddType.NOTE -> {
            QuickAddNoteDialog(
                onDismiss = { activeDialog = null },
                onConfirm = { title, content ->
                    onAddNote(title, content, false, "General")
                    activeDialog = null
                }
            )
        }
        QuickAddType.GOAL -> {
            QuickAddGoalDialog(
                onDismiss = { activeDialog = null },
                onConfirm = { title, desc ->
                    onAddGoal(title, desc, "Personal", "Short-Term", "Step 1 Completed")
                    activeDialog = null
                }
            )
        }
        null -> {}
    }
}

@Composable
private fun SpeedDialItem(
    label: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = accentColor,
        contentColor = Color.White,
        shadowElevation = 6.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun QuickAddTaskDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Work") }
    var priority by remember { mutableStateOf("High") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("＋ Quick Add Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("quick_task_title")
                )
                Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Work", "Personal", "Health", "Finance").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 12.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, category, priority) }) {
                Text("Save Task")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun QuickAddExpenseDialog(onDismiss: () -> Unit, onConfirm: (String, Double, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food & Dining") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("＋ Quick Add Expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($ or ₹)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val amt = amountText.toDoubleOrNull() ?: 0.0
                if (title.isNotBlank() && amt > 0) onConfirm(title, amt, category)
            }) {
                Text("Save Expense")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun QuickAddHabitDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Health") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("＋ Quick Add Habit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onConfirm(name, category) }) {
                Text("Save Habit")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun QuickAddNoteDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("＋ Quick Add Note") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true)
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Note content") }, minLines = 2)
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, content) }) {
                Text("Save Note")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun QuickAddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("＋ Quick Add Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Goal Title") }, singleLine = true)
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, minLines = 2)
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, desc) }) {
                Text("Save Goal")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
