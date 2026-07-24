package com.example.ui.screens.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.GoalEntity
import com.example.ui.components.GlassmorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    goals: List<GoalEntity>,
    onAddGoal: (String, String, String, String, String) -> Unit,
    onUpdateProgress: (GoalEntity, Int) -> Unit,
    onDeleteGoal: (GoalEntity) -> Unit
) {
    var selectedType by remember { mutableStateOf("Short-term") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredGoals = goals.filter { it.type.equals(selectedType, ignoreCase = true) }

    Box(modifier = Modifier.fillMaxSize().testTag("goals_screen")) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Goal Tracker",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Short-term", "Long-term").forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredGoals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No $selectedType goals configured. Tap + to set a target!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(filteredGoals, key = { it.id }) { goal ->
                        GlassmorphicCard {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(goal.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    IconButton(onClick = { onDeleteGoal(goal) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }

                                Text("${goal.category} • Progress: ${goal.progressPercent}%", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { goal.progressPercent / 100f },
                                    modifier = Modifier.fillMaxWidth().height(8.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(goal.description, fontSize = 13.sp)

                                Spacer(modifier = Modifier.height(8.dp))
                                Slider(
                                    value = goal.progressPercent.toFloat(),
                                    onValueChange = { onUpdateProgress(goal, it.toInt()) },
                                    valueRange = 0f..100f,
                                    steps = 20,
                                    modifier = Modifier.fillMaxWidth().testTag("goal_slider_${goal.id}")
                                )
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
                .testTag("add_goal_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Goal")
        }
    }

    if (showAddDialog) {
        AddGoalDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, category, type, milestones ->
                onAddGoal(title, desc, category, type, milestones)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Career") }
    var type by remember { mutableStateOf("Short-term") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_goal_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Milestones") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Short-term", "Long-term").forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onConfirm(title, description, category, type, "") },
                modifier = Modifier.testTag("add_goal_confirm_btn")
            ) {
                Text("Create Goal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
