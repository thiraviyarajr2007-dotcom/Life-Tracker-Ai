package com.example.ui.screens.ai

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ExpenseEntity
import com.example.data.local.entities.HabitEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.UserProfileEntity
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AiChatMessage
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    chatMessages: List<AiChatMessage>,
    isLoading: Boolean,
    tasks: List<TaskEntity> = emptyList(),
    habits: List<HabitEntity> = emptyList(),
    expenses: List<ExpenseEntity> = emptyList(),
    userProfile: UserProfileEntity? = null,
    onSendMessage: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: AI Coach Dashboard, 1: AI Chat
    var promptInput by remember { mutableStateOf("") }

    val pendingTasksCount = tasks.count { !it.isCompleted }
    val pendingHabitsCount = habits.count { !it.isCompletedToday }
    val totalExpenseThisMonth = expenses.filter { it.type == "Expense" }.sumOf { it.amount }
    val monthlyBudget = userProfile?.monthlyBudget ?: 3500.0
    val budgetDifference = totalExpenseThisMonth - monthlyBudget

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("ai_assistant_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI Spark", tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Life Tracker ✨ AI Coach",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Personalized Insights • Adaptive Life Optimization",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Selector (AI Coach Dashboard vs AI Chat)
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("✨ AI Dashboard")
            }
            SegmentedButton(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("💬 AI Chat")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedContent(
            targetState = selectedTab,
            label = "AiTabTransition"
        ) { tabIndex ->
            if (tabIndex == 0) {
                // ✨ AI COACH DASHBOARD VIEW
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // AI Coach Floating Hero Card
                    item {
                        GlassmorphicCard(
                            cornerRadius = 28.dp,
                            modifier = Modifier.fillMaxWidth().testTag("ai_coach_hero_card")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(AccentIndigo.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "✨ AI Coach",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = AccentIndigo
                                    )
                                }
                                Surface(
                                    color = AccentEmerald.copy(alpha = 0.15f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "Active Insight",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentEmerald,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Good Morning, ${userProfile?.name?.takeIf { it.isNotBlank() } ?: "User"} 👋",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎯 ", fontSize = 14.sp)
                                    Text("Tasks remaining today: ", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$pendingTasksCount pending", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentEmerald)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💰 ", fontSize = 14.sp)
                                    Text("Spent this month: ", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$${String.format(Locale.getDefault(), "%.2f", totalExpenseThisMonth)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentCyan)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🏃 ", fontSize = 14.sp)
                                    Text("Habits pending today: ", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$pendingHabitsCount habits", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentCoral)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Suggested Focus:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                FilterChip(
                                    selected = true,
                                    onClick = { onSendMessage("Help me finish my remaining 2 tasks") },
                                    label = { Text("⚡ Finish 2 remaining tasks") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentIndigo.copy(alpha = 0.18f),
                                        selectedLabelColor = AccentIndigo
                                    )
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { onSendMessage("Give me a 5 min evening wind-down routine") },
                                    label = { Text("🧘 5-min wind-down") }
                                )
                            }
                        }
                    }

                    // Today's System Digest Grid
                    item {
                        Text("Today's Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Tasks Status
                            GlassmorphicCard(modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.TaskAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$pendingTasksCount Tasks", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("Pending action items", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            // Habits Status
                            GlassmorphicCard(modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF9800))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$pendingHabitsCount Habits", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("Remaining today", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Budget Status Banner
                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = if (budgetDifference > 0) MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Budget Status", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    if (budgetDifference > 0) {
                                        Text(
                                            text = "⚠️ Budget exceeded by $${String.format("%.2f", budgetDifference)} this month.",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    } else {
                                        Text(
                                            text = "✅ Within monthly budget ($${String.format("%.2f", totalExpenseThisMonth)} / $${String.format("%.2f", monthlyBudget)})",
                                            color = Color(0xFF4CAF50),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // AI Suggested Workout Routine
                    item {
                        OutlinedCard(
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FitnessCenter, contentDescription = "Workout", tint = Color(0xFFE53935))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Suggested Workout", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "🏋️ 25-min High-Energy HIIT Cardio & Core",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "• 5 min Dynamic Warm-up\n• 15 min Bodyweight Circuits (Jumping Jacks, Squats, Planks)\n• 5 min Cool-down & Stretching",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // AI Suggested Study & Focus Schedule
                    item {
                        OutlinedCard(
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MenuBook, contentDescription = "Study Schedule", tint = Color(0xFF1E88E5))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Suggested Focus Schedule", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "📚 2x 45-min Deep Focus Pomodoro Sessions",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "• Session 1 (10:00 AM): Priority task execution\n• 15-min Rest & Hydration\n• Session 2 (02:30 PM): Code review & goal planning",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // AI Weekly Summary & Recommendations Card
                    item {
                        ElevatedCard(
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.BarChart, contentDescription = "Summary", tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Weekly AI Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "📈 Productivity index up by +14% compared to last week.\n💧 Hydration target reached 5/7 days.\n💡 Tip: Consolidate minor tasks into a single 30-min block at 4 PM to free up deep work time.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        selectedTab = 1
                                        onSendMessage("Please generate a detailed breakdown of my weekly goals and habits.")
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ask Coach in Chat")
                                }
                            }
                        }
                    }
                }
            } else {
                // 💬 AI CHAT ASSISTANT VIEW
                Column(modifier = Modifier.fillMaxSize()) {
                    // Quick Prompt Suggestions
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "Summarize Journal",
                            "Productivity Tips",
                            "Analyze Spending",
                            "Weekly Report",
                            "Suggest Habits"
                        ).forEach { promptTag ->
                            SuggestionChip(
                                onClick = { onSendMessage("Please give me personalized $promptTag based on my current data.") },
                                label = { Text(promptTag, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Chat Conversation List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(chatMessages) { msg ->
                            val isUser = msg.sender == "User"

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                            ) {
                                GlassmorphicCard(
                                    backgroundColor = if (isUser) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                                    modifier = Modifier.widthIn(max = 290.dp)
                                ) {
                                    Text(
                                        text = if (isUser) "You" else "Life Tracker AI Coach",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = msg.text,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        if (isLoading) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                    Text("AI Coach is analyzing your metrics...", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }

                    // Chat Input Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 80.dp)
                    ) {
                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            placeholder = { Text("Ask your AI Coach anything...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_prompt_input"),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (promptInput.isNotBlank()) {
                                    onSendMessage(promptInput)
                                    promptInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .testTag("ai_send_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}
