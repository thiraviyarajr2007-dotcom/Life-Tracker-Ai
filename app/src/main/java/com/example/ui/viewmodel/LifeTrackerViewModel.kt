package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.data.remote.GeminiApiService
import com.example.data.repository.LifeTrackerRepository
import com.example.domain.model.AppModule
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AiChatMessage(
    val sender: String, // "User" or "Gemini"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class LifeTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = LifeTrackerRepository(
        db.taskDao(),
        db.habitDao(),
        db.expenseDao(),
        db.healthDao(),
        db.journalDao(),
        db.noteDao(),
        db.goalDao(),
        db.calendarDao(),
        db.userDao(),
        db.userStatsDao(),
        db.categoryDao(),
        GeminiApiService()
    )

    // Current Active Screen / Module
    private val _currentModule = MutableStateFlow(AppModule.DASHBOARD)
    val currentModule: StateFlow<AppModule> = _currentModule.asStateFlow()

    // Auth State
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow("user@gmail.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    init {
        viewModelScope.launch {
            repository.clearFakeSeedData()
        }
    }

    fun clearAllUserData() {
        viewModelScope.launch {
            repository.deleteAllUserData()
        }
    }

    // Cloud Sync State
    private val _syncStatus = MutableStateFlow("Synced") // "Idle", "Syncing", "Synced", "Error"
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    private val _lastSyncedTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastSyncedTimestamp: StateFlow<Long> = _lastSyncedTimestamp.asStateFlow()

    // Database Flows
    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<HabitEntity>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthLogs: StateFlow<List<HealthLogEntity>> = repository.allHealthLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestHealthLog: StateFlow<HealthLogEntity?> = repository.latestHealthLog
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val journals: StateFlow<List<JournalEntity>> = repository.allJournals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calendarEvents: StateFlow<List<CalendarEventEntity>> = repository.allCalendarEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userStats: StateFlow<UserStatsEntity?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // HUD Notification State
    private val _hudEvent = MutableStateFlow<com.example.ui.components.HudEvent?>(null)
    val hudEvent: StateFlow<com.example.ui.components.HudEvent?> = _hudEvent.asStateFlow()

    // Level Up Dialog State
    private val _levelUpEvent = MutableStateFlow<Int?>(null)
    val levelUpEvent: StateFlow<Int?> = _levelUpEvent.asStateFlow()

    // Hunter Stats State
    private val _hunterStats = MutableStateFlow(com.example.domain.model.HunterStats())
    val hunterStats: StateFlow<com.example.domain.model.HunterStats> = _hunterStats.asStateFlow()

    fun triggerHudAlert(title: String, subtitle: String, xpGained: Int = 0, isLevelUp: Boolean = false) {
        _hudEvent.value = com.example.ui.components.HudEvent(
            title = title,
            subtitle = subtitle,
            xpGained = xpGained,
            isLevelUp = isLevelUp
        )
    }

    fun dismissHudAlert() {
        _hudEvent.value = null
    }

    fun clearLevelUpEvent() {
        _levelUpEvent.value = null
    }

    fun allocateStatPoint(statName: String) {
        val current = _hunterStats.value
        if (current.unallocatedPoints <= 0) return
        val updated = when (statName.uppercase()) {
            "STR" -> current.copy(strength = current.strength + 1, unallocatedPoints = current.unallocatedPoints - 1)
            "AGI" -> current.copy(agility = current.agility + 1, unallocatedPoints = current.unallocatedPoints - 1)
            "INT" -> current.copy(intelligence = current.intelligence + 1, unallocatedPoints = current.unallocatedPoints - 1)
            "VIT" -> current.copy(vitality = current.vitality + 1, unallocatedPoints = current.unallocatedPoints - 1)
            "SNE" -> current.copy(sense = current.sense + 1, unallocatedPoints = current.unallocatedPoints - 1)
            else -> current
        }
        _hunterStats.value = updated
        triggerHudAlert("STAT POINT ALLOCATED", "$statName upgraded to ${when(statName.uppercase()) { "STR" -> updated.strength; "AGI" -> updated.agility; "INT" -> updated.intelligence; "VIT" -> updated.vitality; else -> updated.sense }}")
    }

    fun grantXp(xpAmount: Int, actionTitle: String = "Quest Cleared") {
        viewModelScope.launch {
            val current = userStats.value ?: UserStatsEntity()
            val previousLevel = com.example.domain.model.GamificationProfile.calculateLevel(current.totalXp)
            val newTotalXp = current.totalXp + xpAmount
            val newLevel = com.example.domain.model.GamificationProfile.calculateLevel(newTotalXp)
            
            val levelUpOccurred = newLevel > previousLevel

            val updatedStats = current.copy(
                totalXp = newTotalXp,
                level = newLevel,
                coins = current.coins + (xpAmount / 5),
                updatedAt = System.currentTimeMillis()
            )
            repository.updateUserStats(updatedStats)

            if (levelUpOccurred) {
                _hunterStats.value = _hunterStats.value.copy(
                    unallocatedPoints = _hunterStats.value.unallocatedPoints + 3
                )
                _levelUpEvent.value = newLevel
                triggerHudAlert(
                    title = "LEVEL UP! REACHED LEVEL $newLevel",
                    subtitle = com.example.domain.model.GamificationProfile.calculateLevelTitle(newLevel),
                    xpGained = xpAmount,
                    isLevelUp = true
                )
            } else {
                triggerHudAlert(
                    title = actionTitle,
                    subtitle = "+$xpAmount XP Gained for System Progress",
                    xpGained = xpAmount
                )
            }
        }
    }

    // AI Assistant State
    private val _aiChatMessages = MutableStateFlow<List<AiChatMessage>>(
        listOf(
            AiChatMessage(
                sender = "Gemini",
                text = "Hello! 👋 I'm your Life Tracker AI Assistant. I can analyze your habits, health logs, expenses, and journal entries to give you actionable life optimizations. How can I help you today?"
            )
        )
    )
    val aiChatMessages: StateFlow<List<AiChatMessage>> = _aiChatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Navigation Action
    fun navigateTo(module: AppModule) {
        _currentModule.value = module
    }

    // Auth Actions
    fun login(email: String) {
        _userEmail.value = email
        _isLoggedIn.value = true
        _currentModule.value = AppModule.DASHBOARD
        triggerCloudSync()
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            _syncStatus.value = "Syncing"
            val result = repository.syncUserCloudData(_userEmail.value)
            if (result.isSuccess) {
                _syncStatus.value = "Synced"
                _lastSyncedTimestamp.value = System.currentTimeMillis()
            } else {
                _syncStatus.value = "Error"
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentModule.value = AppModule.AUTH
    }

    // Category Actions
    fun addCategory(name: String, colorHex: String = "#3B82F6", iconName: String = "Folder") {
        viewModelScope.launch {
            repository.insertCategory(CategoryEntity(name = name, colorHex = colorHex, iconName = iconName))
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    // Task Actions
    fun addTask(title: String, category: String, priority: String, dueDate: Long, categoryId: Long = 1L) {
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(title = title, category = category, priority = priority, dueDate = dueDate, categoryId = categoryId)
            )
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            val newlyCompleted = !task.isCompleted
            repository.updateTask(task.copy(isCompleted = newlyCompleted))
            if (newlyCompleted) {
                val xpGained = when (task.priority) {
                    "High" -> 100
                    "Medium" -> 50
                    "Low" -> 25
                    else -> 50
                }
                grantXp(xpGained, "Quest Cleared: \"${task.title}\"")
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Habit Actions
    fun addHabit(name: String, category: String, frequency: String) {
        viewModelScope.launch {
            repository.insertHabit(HabitEntity(name = name, category = category, frequency = frequency))
        }
    }

    fun toggleHabitToday(habit: HabitEntity) {
        viewModelScope.launch {
            val newCompleted = !habit.isCompletedToday
            val newStreak = if (newCompleted) habit.streakCount + 1 else maxOf(0, habit.streakCount - 1)
            repository.updateHabit(
                habit.copy(
                    isCompletedToday = newCompleted,
                    streakCount = newStreak,
                    lastCompletedDate = if (newCompleted) System.currentTimeMillis() else habit.lastCompletedDate
                )
            )
            if (newCompleted) {
                val xpGained = 50 + (newStreak * 5)
                grantXp(xpGained, "Daily Duty Cleared: \"${habit.name}\"")
            }
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // Expense Actions
    fun addExpense(title: String, amount: Double, type: String, category: String, notes: String) {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(title = title, amount = amount, type = type, category = category, notes = notes)
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // Health Actions
    fun addWaterIntake(amountMl: Int) {
        viewModelScope.launch {
            val current = latestHealthLog.value ?: HealthLogEntity()
            val updated = current.copy(waterIntakeMl = current.waterIntakeMl + amountMl)
            repository.insertHealthLog(updated)
        }
    }

    fun logHealthMetrics(weightKg: Float, heightCm: Float, sleepHours: Float, steps: Int, mood: String, workoutMins: Int) {
        viewModelScope.launch {
            val current = latestHealthLog.value ?: HealthLogEntity()
            val updated = current.copy(
                weightKg = weightKg,
                heightCm = heightCm,
                sleepHours = sleepHours,
                stepsCount = steps,
                mood = mood,
                workoutDurationMins = workoutMins
            )
            repository.insertHealthLog(updated)
        }
    }

    // Journal Actions
    fun addJournalEntry(title: String, content: String, moodTag: String, tags: String) {
        viewModelScope.launch {
            repository.insertJournal(
                JournalEntity(title = title, content = content, moodTag = moodTag, tags = tags)
            )
        }
    }

    fun deleteJournal(journal: JournalEntity) {
        viewModelScope.launch {
            repository.deleteJournal(journal)
        }
    }

    // Note Actions
    fun addNote(title: String, content: String, isChecklist: Boolean, folder: String) {
        viewModelScope.launch {
            repository.insertNote(
                NoteEntity(title = title, content = content, isChecklist = isChecklist, folder = folder)
            )
        }
    }

    fun toggleNotePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Goal Actions
    fun addGoal(title: String, description: String, category: String, type: String, milestones: String) {
        viewModelScope.launch {
            repository.insertGoal(
                GoalEntity(title = title, description = description, category = category, type = type, milestones = milestones)
            )
        }
    }

    fun updateGoalProgress(goal: GoalEntity, progress: Int) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(progressPercent = progress.coerceIn(0, 100)))
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // Calendar Event Actions
    fun addCalendarEvent(title: String, description: String, startTime: Long, eventType: String, location: String) {
        viewModelScope.launch {
            repository.insertCalendarEvent(
                CalendarEventEntity(title = title, description = description, startTime = startTime, eventType = eventType, location = location)
            )
        }
    }

    fun deleteCalendarEvent(event: CalendarEventEntity) {
        viewModelScope.launch {
            repository.deleteCalendarEvent(event)
        }
    }

    // Settings Profile Actions
    fun updateProfileNameAndGoal(name: String, budget: Double, waterGoal: Int, darkMode: Boolean) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.updateProfile(
                current.copy(name = name, monthlyBudget = budget, dailyWaterGoalMl = waterGoal, isDarkMode = darkMode)
            )
        }
    }

    // Gemini AI Assistant Chat
    fun sendMessageToAi(promptText: String) {
        if (promptText.isBlank()) return

        val userMessage = AiChatMessage(sender = "User", text = promptText)
        _aiChatMessages.value = _aiChatMessages.value + userMessage
        _isAiLoading.value = true

        viewModelScope.launch {
            // Build rich contextual context from Room database snapshot for Gemini
            val currentTasks = tasks.value.take(5).joinToString { "${it.title} (${if (it.isCompleted) "Done" else "Pending"})" }
            val currentHabits = habits.value.joinToString { "${it.name}: ${it.streakCount} day streak" }
            val totalExpense = expenses.value.filter { it.type == "Expense" }.sumOf { it.amount }
            val health = latestHealthLog.value
            val healthContext = "Water: ${health?.waterIntakeMl ?: 0}ml, Sleep: ${health?.sleepHours ?: 0f}h, Mood: ${health?.mood ?: "Good"}"

            val contextualPrompt = """
                User Profile context:
                - Recent Tasks: $currentTasks
                - Active Habits: $currentHabits
                - Total Expenses Today: $$totalExpense
                - Health Metrics: $healthContext

                User Query: $promptText
            """.trimIndent()

            val response = repository.askGemini(contextualPrompt)
            val aiMessage = AiChatMessage(sender = "Gemini", text = response)
            _aiChatMessages.value = _aiChatMessages.value + aiMessage
            _isAiLoading.value = false
        }
    }
}
