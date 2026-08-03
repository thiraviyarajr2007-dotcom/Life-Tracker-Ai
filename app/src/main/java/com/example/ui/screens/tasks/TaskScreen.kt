package com.example.ui.screens.tasks

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.TaskEntity
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentCyan
import kotlin.random.Random

data class CategoryOption(
    val name: String,
    val emoji: String,
    val color: Color
)

val PRESET_CATEGORIES = listOf(
    CategoryOption("Work", "💼", Color(0xFF1E88E5)),
    CategoryOption("Personal", "👤", Color(0xFF8E24AA)),
    CategoryOption("Health", "🏥", Color(0xFFE53935)),
    CategoryOption("Finance", "💳", Color(0xFF43A047)),
    CategoryOption("Study", "📚", Color(0xFFFB8C00)),
    CategoryOption("Shopping", "🛒", Color(0xFF00ACC1)),
    CategoryOption("Fitness", "🏃", Color(0xFFD81B60)),
    CategoryOption("Home", "🏠", Color(0xFF5E35B1)),
    CategoryOption("General", "🏷️", Color(0xFF757575))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    tasks: List<TaskEntity>,
    onAddTask: (String, String, String, Long) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPriorityFilter by remember { mutableStateOf("All") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredTasks by remember(tasks, searchQuery, selectedPriorityFilter, selectedCategoryFilter) {
        derivedStateOf {
            tasks.filter { task ->
                val matchesPriority = if (selectedPriorityFilter == "All") true else task.priority.equals(selectedPriorityFilter, ignoreCase = true)
                val matchesCategory = if (selectedCategoryFilter == "All") true else task.category.equals(selectedCategoryFilter, ignoreCase = true)
                val matchesQuery = searchQuery.isBlank() ||
                        task.title.contains(searchQuery, ignoreCase = true) ||
                        task.category.contains(searchQuery, ignoreCase = true)
                matchesPriority && matchesCategory && matchesQuery
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().testTag("tasks_screen")) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Task Manager",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Real-time Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_search_input"),
                placeholder = { Text("Search tasks by title or category...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search tasks",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Priority & Category Filter Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Priority Filter Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Priority:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf("All", "High", "Medium", "Low").forEach { priority ->
                        FilterChip(
                            selected = selectedPriorityFilter == priority,
                            onClick = { selectedPriorityFilter = priority },
                            label = { Text(priority) },
                            modifier = Modifier.testTag("filter_priority_$priority")
                        )
                    }
                }

                // Category Filter Row
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Category:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == "All",
                            onClick = { selectedCategoryFilter = "All" },
                            label = { Text("All Categories") },
                            modifier = Modifier.testTag("filter_category_all")
                        )
                    }
                    items(PRESET_CATEGORIES) { category ->
                        FilterChip(
                            selected = selectedCategoryFilter == category.name,
                            onClick = { selectedCategoryFilter = category.name },
                            label = { Text("${category.emoji} ${category.name}") },
                            modifier = Modifier.testTag("filter_category_${category.name.lowercase()}")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotBlank()) {
                            "No tasks matching '$searchQuery'"
                        } else {
                            "No tasks found for current filters.\nTap + to create a new task!"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        AnimatedTaskItemCard(
                            task = task,
                            onToggleTask = onToggleTask,
                            onDeleteTask = onDeleteTask
                        )
                    }
                }
            }
        }

        // Add Task FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 20.dp)
                .testTag("add_task_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, category, priority ->
                onAddTask(title, category, priority, System.currentTimeMillis())
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun PriorityBadge(priority: String) {
    val (badgeColor, textColor) = when (priority.lowercase()) {
        "high" -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        "medium" -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
        else -> Color(0xFFE8F5E9) to Color(0xFF388E3C)
    }
    Surface(
        color = badgeColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = priority,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun CategoryBadge(category: String) {
    val preset = PRESET_CATEGORIES.find { it.name.equals(category, ignoreCase = true) }
    val emoji = preset?.emoji ?: "🏷️"

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "$emoji $category",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategoryName by remember { mutableStateOf("Work") }
    var isCustomCategory by remember { mutableStateOf(false) }
    var customCategoryText by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("High") }

    val finalCategory = if (isCustomCategory && customCategoryText.isNotBlank()) {
        customCategoryText.trim()
    } else {
        selectedCategoryName
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_task_title_input")
                )

                // Category Selection Section
                Text(
                    text = "Select Category",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.testTag("add_task_category_row")
                ) {
                    items(PRESET_CATEGORIES) { cat ->
                        FilterChip(
                            selected = !isCustomCategory && selectedCategoryName == cat.name,
                            onClick = {
                                isCustomCategory = false
                                selectedCategoryName = cat.name
                            },
                            label = { Text("${cat.emoji} ${cat.name}") },
                            leadingIcon = if (!isCustomCategory && selectedCategoryName == cat.name) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            modifier = Modifier.testTag("add_task_cat_${cat.name.lowercase()}")
                        )
                    }
                    item {
                        FilterChip(
                            selected = isCustomCategory,
                            onClick = { isCustomCategory = true },
                            label = { Text("✏️ Custom...") },
                            modifier = Modifier.testTag("add_task_cat_custom")
                        )
                    }
                }

                if (isCustomCategory) {
                    OutlinedTextField(
                        value = customCategoryText,
                        onValueChange = { customCategoryText = it },
                        label = { Text("Custom Category Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_task_custom_category_input")
                    )
                }

                // Priority Selection
                Text(
                    text = "Priority Level",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("High", "Medium", "Low").forEach { pri ->
                        FilterChip(
                            selected = priority == pri,
                            onClick = { priority = pri },
                            label = { Text(pri) },
                            modifier = Modifier.testTag("add_task_priority_$pri")
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, finalCategory, priority)
                    }
                },
                modifier = Modifier.testTag("add_task_confirm_btn")
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AnimatedTaskItemCard(
    task: TaskEntity,
    onToggleTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit
) {
    var triggerConfetti by remember { mutableStateOf(false) }

    // Card scale animation (shrinks slightly then bounces back when clicked/completed)
    val cardScale by animateFloatAsState(
        targetValue = if (triggerConfetti) 0.96f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { triggerConfetti = false },
        label = "cardScale"
    )

    val confettiProgress by animateFloatAsState(
        targetValue = if (triggerConfetti) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutLinearInEasing),
        label = "confetti"
    )

    GlassmorphicCard(
        modifier = Modifier
            .scale(cardScale)
            .testTag("task_item_${task.id}")
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(48.dp)
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = {
                            if (!task.isCompleted) {
                                triggerConfetti = true
                            }
                            onToggleTask(task)
                        },
                        modifier = Modifier.size(40.dp)
                    )

                    // Confetti burst particles when checked!
                    if (confettiProgress > 0f && confettiProgress < 1f) {
                        Canvas(modifier = Modifier.size(60.dp)) {
                            val colors = listOf(AccentEmerald, AccentCoral, AccentCyan, AccentIndigo, Color(0xFFFFD54F))
                            val particles = 10
                            for (i in 0 until particles) {
                                val angle = (i * (360f / particles)) * (Math.PI / 180f)
                                val distance = 28.dp.toPx() * confettiProgress
                                val x = (center.x + Math.cos(angle) * distance).toFloat()
                                val y = (center.y + Math.sin(angle) * distance).toFloat()
                                val particleColor = colors[i % colors.size]
                                val alpha = (1f - confettiProgress).coerceIn(0f, 1f)
                                drawCircle(
                                    color = particleColor.copy(alpha = alpha),
                                    radius = 3.dp.toPx() * (1f - confettiProgress * 0.5f),
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PriorityBadge(priority = task.priority)
                        CategoryBadge(category = task.category)
                    }
                }

                IconButton(onClick = { onDeleteTask(task) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

