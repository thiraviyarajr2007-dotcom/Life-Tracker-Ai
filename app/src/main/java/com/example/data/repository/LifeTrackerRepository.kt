package com.example.data.repository

import com.example.data.local.dao.*
import com.example.data.local.entities.*
import com.example.data.remote.FirestoreManager
import com.example.data.remote.GeminiApiService
import kotlinx.coroutines.flow.Flow

class LifeTrackerRepository(
    private val taskDao: TaskDao,
    private val habitDao: HabitDao,
    private val expenseDao: ExpenseDao,
    private val healthDao: HealthDao,
    private val journalDao: JournalDao,
    private val noteDao: NoteDao,
    private val goalDao: GoalDao,
    private val calendarDao: CalendarDao,
    private val userDao: UserDao,
    private val userStatsDao: UserStatsDao,
    private val categoryDao: CategoryDao? = null,
    private val geminiApiService: GeminiApiService,
    private val firestoreManager: FirestoreManager = FirestoreManager()
) {
    // Categories
    val allCategories: Flow<List<CategoryEntity>> = categoryDao?.getAllCategories() ?: kotlinx.coroutines.flow.flowOf(emptyList())
    suspend fun insertCategory(category: CategoryEntity): Long = categoryDao?.insertCategory(category) ?: 0L
    suspend fun deleteCategory(category: CategoryEntity) { categoryDao?.deleteCategory(category) }

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    suspend fun insertTask(task: TaskEntity): Long {
        val id = taskDao.insertTask(task)
        val entityWithId = if (task.id == 0L) task.copy(id = id) else task
        firestoreManager.saveDocument("tasks", entityWithId.id.toString(), mapOf(
            "title" to entityWithId.title,
            "category" to entityWithId.category,
            "categoryId" to entityWithId.categoryId,
            "priority" to entityWithId.priority,
            "isCompleted" to entityWithId.isCompleted,
            "dueDate" to entityWithId.dueDate
        ))
        return id
    }
    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
        firestoreManager.saveDocument("tasks", task.id.toString(), mapOf(
            "title" to task.title,
            "category" to task.category,
            "priority" to task.priority,
            "isCompleted" to task.isCompleted,
            "dueDate" to task.dueDate
        ))
    }
    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
        firestoreManager.deleteDocument("tasks", task.id.toString())
    }

    // Habits
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()
    suspend fun insertHabit(habit: HabitEntity): Long {
        val id = habitDao.insertHabit(habit)
        val entityWithId = if (habit.id == 0L) habit.copy(id = id) else habit
        firestoreManager.saveDocument("habits", entityWithId.id.toString(), mapOf(
            "name" to entityWithId.name,
            "category" to entityWithId.category,
            "frequency" to entityWithId.frequency,
            "streakCount" to entityWithId.streakCount,
            "isCompletedToday" to entityWithId.isCompletedToday
        ))
        return id
    }
    suspend fun updateHabit(habit: HabitEntity) {
        habitDao.updateHabit(habit)
        firestoreManager.saveDocument("habits", habit.id.toString(), mapOf(
            "name" to habit.name,
            "category" to habit.category,
            "frequency" to habit.frequency,
            "streakCount" to habit.streakCount,
            "isCompletedToday" to habit.isCompletedToday
        ))
    }
    suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.deleteHabit(habit)
        firestoreManager.deleteDocument("habits", habit.id.toString())
    }

    // Expenses
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    suspend fun insertExpense(expense: ExpenseEntity): Long {
        val id = expenseDao.insertExpense(expense)
        val entityWithId = if (expense.id == 0L) expense.copy(id = id) else expense
        firestoreManager.saveDocument("expenses", entityWithId.id.toString(), mapOf(
            "title" to entityWithId.title,
            "amount" to entityWithId.amount,
            "type" to entityWithId.type,
            "category" to entityWithId.category,
            "notes" to entityWithId.notes,
            "date" to entityWithId.date
        ))
        return id
    }
    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
        firestoreManager.saveDocument("expenses", expense.id.toString(), mapOf(
            "title" to expense.title,
            "amount" to expense.amount,
            "type" to expense.type,
            "category" to expense.category,
            "notes" to expense.notes,
            "date" to expense.date
        ))
    }
    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
        firestoreManager.deleteDocument("expenses", expense.id.toString())
    }

    // Health
    val allHealthLogs: Flow<List<HealthLogEntity>> = healthDao.getAllHealthLogs()
    val latestHealthLog: Flow<HealthLogEntity?> = healthDao.getLatestHealthLog()
    suspend fun insertHealthLog(healthLog: HealthLogEntity): Long {
        val id = healthDao.insertHealthLog(healthLog)
        val entityWithId = if (healthLog.id == 0L) healthLog.copy(id = id) else healthLog
        firestoreManager.saveDocument("health_logs", entityWithId.id.toString(), mapOf(
            "weightKg" to entityWithId.weightKg,
            "heightCm" to entityWithId.heightCm,
            "waterIntakeMl" to entityWithId.waterIntakeMl,
            "sleepHours" to entityWithId.sleepHours,
            "stepsCount" to entityWithId.stepsCount,
            "mood" to entityWithId.mood,
            "workoutDurationMins" to entityWithId.workoutDurationMins,
            "date" to entityWithId.date
        ))
        return id
    }
    suspend fun updateHealthLog(healthLog: HealthLogEntity) {
        healthDao.updateHealthLog(healthLog)
        firestoreManager.saveDocument("health_logs", healthLog.id.toString(), mapOf(
            "weightKg" to healthLog.weightKg,
            "heightCm" to healthLog.heightCm,
            "waterIntakeMl" to healthLog.waterIntakeMl,
            "sleepHours" to healthLog.sleepHours,
            "stepsCount" to healthLog.stepsCount,
            "mood" to healthLog.mood,
            "workoutDurationMins" to healthLog.workoutDurationMins,
            "date" to healthLog.date
        ))
    }

    // Journal
    val allJournals: Flow<List<JournalEntity>> = journalDao.getAllJournalEntries()
    suspend fun insertJournal(journal: JournalEntity): Long {
        val id = journalDao.insertJournalEntry(journal)
        val entityWithId = if (journal.id == 0L) journal.copy(id = id) else journal
        firestoreManager.saveDocument("journals", entityWithId.id.toString(), mapOf(
            "title" to entityWithId.title,
            "content" to entityWithId.content,
            "moodTag" to entityWithId.moodTag,
            "tags" to entityWithId.tags,
            "date" to entityWithId.date
        ))
        return id
    }
    suspend fun deleteJournal(journal: JournalEntity) {
        journalDao.deleteJournalEntry(journal)
        firestoreManager.deleteDocument("journals", journal.id.toString())
    }

    // Notes
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    suspend fun insertNote(note: NoteEntity): Long {
        val id = noteDao.insertNote(note)
        val entityWithId = if (note.id == 0L) note.copy(id = id) else note
        firestoreManager.saveDocument("notes", entityWithId.id.toString(), mapOf(
            "title" to entityWithId.title,
            "content" to entityWithId.content,
            "isChecklist" to entityWithId.isChecklist,
            "isPinned" to entityWithId.isPinned,
            "folder" to entityWithId.folder,
            "updatedAt" to entityWithId.updatedAt
        ))
        return id
    }
    suspend fun updateNote(note: NoteEntity) {
        noteDao.updateNote(note)
        firestoreManager.saveDocument("notes", note.id.toString(), mapOf(
            "title" to note.title,
            "content" to note.content,
            "isChecklist" to note.isChecklist,
            "isPinned" to note.isPinned,
            "folder" to note.folder,
            "updatedAt" to note.updatedAt
        ))
    }
    suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
        firestoreManager.deleteDocument("notes", note.id.toString())
    }

    // Goals
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()
    suspend fun insertGoal(goal: GoalEntity): Long {
        val id = goalDao.insertGoal(goal)
        val entityWithId = if (goal.id == 0L) goal.copy(id = id) else goal
        firestoreManager.saveDocument("goals", entityWithId.id.toString(), mapOf(
            "title" to entityWithId.title,
            "description" to entityWithId.description,
            "category" to entityWithId.category,
            "type" to entityWithId.type,
            "progressPercent" to entityWithId.progressPercent,
            "milestones" to entityWithId.milestones,
            "deadline" to entityWithId.deadline
        ))
        return id
    }
    suspend fun updateGoal(goal: GoalEntity) {
        goalDao.updateGoal(goal)
        firestoreManager.saveDocument("goals", goal.id.toString(), mapOf(
            "title" to goal.title,
            "description" to goal.description,
            "category" to goal.category,
            "type" to goal.type,
            "progressPercent" to goal.progressPercent,
            "milestones" to goal.milestones,
            "deadline" to goal.deadline
        ))
    }
    suspend fun deleteGoal(goal: GoalEntity) {
        goalDao.deleteGoal(goal)
        firestoreManager.deleteDocument("goals", goal.id.toString())
    }

    // Calendar
    val allCalendarEvents: Flow<List<CalendarEventEntity>> = calendarDao.getAllEvents()
    suspend fun insertCalendarEvent(event: CalendarEventEntity): Long {
        val id = calendarDao.insertEvent(event)
        val entityWithId = if (event.id == 0L) event.copy(id = id) else event
        firestoreManager.saveDocument("calendar_events", entityWithId.id.toString(), mapOf(
            "title" to entityWithId.title,
            "description" to entityWithId.description,
            "startTime" to entityWithId.startTime,
            "endTime" to entityWithId.endTime,
            "eventType" to entityWithId.eventType,
            "location" to entityWithId.location
        ))
        return id
    }
    suspend fun deleteCalendarEvent(event: CalendarEventEntity) {
        calendarDao.deleteEvent(event)
        firestoreManager.deleteDocument("calendar_events", event.id.toString())
    }

    // User Profile
    val userProfile: Flow<UserProfileEntity?> = userDao.getUserProfile()
    suspend fun updateProfile(profile: UserProfileEntity) {
        userDao.insertOrUpdateUserProfile(profile)
        firestoreManager.saveDocument("user_profile", profile.id.toString(), mapOf(
            "name" to profile.name,
            "email" to profile.email,
            "monthlyBudget" to profile.monthlyBudget,
            "dailyWaterGoalMl" to profile.dailyWaterGoalMl,
            "isDarkMode" to profile.isDarkMode
        ))
    }

    // User Gamification Stats
    val userStats: Flow<UserStatsEntity?> = userStatsDao.getUserStats()
    suspend fun updateUserStats(stats: UserStatsEntity) {
        userStatsDao.insertOrUpdateUserStats(stats)
        firestoreManager.saveDocument("user_stats", stats.id.toString(), mapOf(
            "totalXp" to stats.totalXp,
            "level" to stats.level,
            "coins" to stats.coins,
            "streakDays" to stats.streakDays,
            "updatedAt" to stats.updatedAt
        ))
    }

    /**
     * Performs a full secure sync of all local user data with Firebase Firestore for the authenticated Google user.
     */
    suspend fun syncUserCloudData(userEmail: String): Result<Unit> {
        return try {
            val tasksList = taskDao.getAllTasksList()
            tasksList.forEach { task ->
                firestoreManager.saveUserDocument(userEmail, "tasks", task.id.toString(), mapOf(
                    "title" to task.title,
                    "category" to task.category,
                    "priority" to task.priority,
                    "isCompleted" to task.isCompleted,
                    "dueDate" to task.dueDate
                ))
            }

            val habitsList = habitDao.getAllHabitsList()
            habitsList.forEach { habit ->
                firestoreManager.saveUserDocument(userEmail, "habits", habit.id.toString(), mapOf(
                    "name" to habit.name,
                    "category" to habit.category,
                    "frequency" to habit.frequency,
                    "streakCount" to habit.streakCount,
                    "isCompletedToday" to habit.isCompletedToday
                ))
            }

            val expensesList = expenseDao.getAllExpensesList()
            expensesList.forEach { exp ->
                firestoreManager.saveUserDocument(userEmail, "expenses", exp.id.toString(), mapOf(
                    "title" to exp.title,
                    "amount" to exp.amount,
                    "type" to exp.type,
                    "category" to exp.category,
                    "notes" to exp.notes,
                    "date" to exp.date
                ))
            }

            val healthLogs = healthDao.getAllHealthLogsList()
            healthLogs.forEach { hl ->
                firestoreManager.saveUserDocument(userEmail, "health_logs", hl.id.toString(), mapOf(
                    "waterIntakeMl" to hl.waterIntakeMl,
                    "sleepHours" to hl.sleepHours,
                    "stepsCount" to hl.stepsCount,
                    "mood" to hl.mood,
                    "workoutDurationMins" to hl.workoutDurationMins,
                    "date" to hl.date
                ))
            }

            val profile = userDao.getUserProfileSingle() ?: UserProfileEntity(email = userEmail)
            userDao.insertOrUpdateUserProfile(profile.copy(email = userEmail))
            firestoreManager.saveUserDocument(userEmail, "profile", "user_profile", mapOf(
                "name" to profile.name,
                "email" to userEmail,
                "monthlyBudget" to profile.monthlyBudget,
                "dailyWaterGoalMl" to profile.dailyWaterGoalMl,
                "isDarkMode" to profile.isDarkMode,
                "syncedAt" to System.currentTimeMillis()
            ))

            val stats = userStatsDao.getUserStatsSingle() ?: UserStatsEntity()
            firestoreManager.saveUserDocument(userEmail, "stats", "user_stats", mapOf(
                "totalXp" to stats.totalXp,
                "level" to stats.level,
                "coins" to stats.coins,
                "streakDays" to stats.streakDays,
                "updatedAt" to stats.updatedAt
            ))

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("LifeTrackerRepository", "Error syncing user cloud data: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteAllUserData() {
        taskDao.deleteAllTasks()
        habitDao.deleteAllHabits()
        expenseDao.deleteAllExpenses()
        healthDao.deleteAllHealthLogs()
        journalDao.deleteAllJournalEntries()
        noteDao.deleteAllNotes()
        goalDao.deleteAllGoals()
        calendarDao.deleteAllEvents()

        userStatsDao.insertOrUpdateUserStats(
            UserStatsEntity(id = 1, totalXp = 0, level = 1, coins = 0, streakDays = 0)
        )
    }

    suspend fun clearFakeSeedData() {
        taskDao.getAllTasksList().filter {
            it.title.contains("Review Weekly Life Goals") ||
            it.title.contains("Upper Body HIIT") ||
            it.title.contains("Atomic Habits") ||
            it.title.contains("Hydrate 2.5 Liters") ||
            it.title.contains("AI Spending Analysis")
        }.forEach { taskDao.deleteTask(it) }

        habitDao.getAllHabitsList().filter {
            it.name.contains("Mindfulness Meditation") ||
            it.name.contains("Drink 250ml Water") ||
            it.name.contains("30 Mins Daily Reading") ||
            it.name.contains("No Sugar After") ||
            it.name.contains("Weekly Budget Review")
        }.forEach { habitDao.deleteHabit(it) }

        expenseDao.getAllExpensesList().filter {
            it.title.contains("Organic Grocery") ||
            it.title.contains("Gym Membership") ||
            it.title.contains("Freelance UX") ||
            it.title.contains("Coffee & Bakery") ||
            it.title.contains("Metro Transit")
        }.forEach { expenseDao.deleteExpense(it) }

        journalDao.getAllJournalEntriesList().filter {
            it.title.contains("Reflections on Productivity") ||
            it.title.contains("Evening Gratitude")
        }.forEach { journalDao.deleteJournalEntry(it) }

        noteDao.getAllNotesList().filter {
            it.title.contains("High Performance Morning Routine") ||
            it.title.contains("Healthy Grocery Checklist")
        }.forEach { noteDao.deleteNote(it) }

        goalDao.getAllGoalsList().filter {
            it.title.contains("Complete AI Life Tracker") ||
            it.title.contains("Run a Full Marathon")
        }.forEach { goalDao.deleteGoal(it) }

        calendarDao.getAllEventsList().filter {
            it.title.contains("Design System Review") ||
            it.title.contains("Mom's Birthday")
        }.forEach { calendarDao.deleteEvent(it) }

        val p = userDao.getUserProfileSingle()
        if (p?.name == "Alex Rivera") {
            userDao.insertOrUpdateUserProfile(p.copy(name = "User", email = ""))
        }
    }

    // Gemini AI helper
    suspend fun askGemini(prompt: String): String {
        return geminiApiService.generateAiResponse(prompt)
    }
}
