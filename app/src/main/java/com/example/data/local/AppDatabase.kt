package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.*
import com.example.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        HabitEntity::class,
        ExpenseEntity::class,
        HealthLogEntity::class,
        JournalEntity::class,
        NoteEntity::class,
        GoalEntity::class,
        CalendarEventEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun healthDao(): HealthDao
    abstract fun journalDao(): JournalDao
    abstract fun noteDao(): NoteDao
    abstract fun goalDao(): GoalDao
    abstract fun calendarDao(): CalendarDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "life_tracker_ai_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial data asynchronously on thread
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { seedDatabase(it) }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDatabase(db: AppDatabase) {
            val now = System.currentTimeMillis()
            val dayMs = 86400000L

            // Initial User Profile
            db.userDao().insertOrUpdateUserProfile(
                UserProfileEntity(
                    id = 1,
                    name = "Alex Rivera",
                    email = "alex.rivera@lifetracker.ai",
                    isDarkMode = true,
                    dailyWaterGoalMl = 2500,
                    dailyCalorieGoal = 2200,
                    dailyStepGoal = 10000,
                    monthlyBudget = 3500.0,
                    avatarId = 1
                )
            )

            // Seed Tasks
            val tasks = listOf(
                TaskEntity(title = "Review Weekly Life Goals & Key Milestones", priority = "High", category = "Work", isCompleted = false, dueDate = now + dayMs),
                TaskEntity(title = "30-Minute Upper Body HIIT Workout", priority = "High", category = "Health", isCompleted = true, dueDate = now),
                TaskEntity(title = "Complete AI Spending Analysis with Gemini", priority = "Medium", category = "Finance", isCompleted = false, dueDate = now + dayMs * 2),
                TaskEntity(title = "Read 20 pages of 'Atomic Habits'", priority = "Low", category = "Personal", isCompleted = true, dueDate = now),
                TaskEntity(title = "Hydrate 2.5 Liters of Water Today", priority = "High", category = "Health", isCompleted = false, dueDate = now)
            )
            tasks.forEach { db.taskDao().insertTask(it) }

            // Seed Habits
            val habits = listOf(
                HabitEntity(name = "Morning Mindfulness Meditation", category = "Mental Health", frequency = "Daily", streakCount = 14, isCompletedToday = true),
                HabitEntity(name = "Drink 250ml Water Every 2 Hours", category = "Health", frequency = "Daily", streakCount = 9, isCompletedToday = false),
                HabitEntity(name = "30 Mins Daily Reading", category = "Learning", frequency = "Daily", streakCount = 21, isCompletedToday = true),
                HabitEntity(name = "No Sugar After 8 PM", category = "Health", frequency = "Daily", streakCount = 5, isCompletedToday = false),
                HabitEntity(name = "Weekly Budget Review", category = "Finance", frequency = "Weekly", streakCount = 4, isCompletedToday = true)
            )
            habits.forEach { db.habitDao().insertHabit(it) }

            // Seed Expenses
            val expenses = listOf(
                ExpenseEntity(title = "Organic Grocery Shopping", amount = 84.50, type = "Expense", category = "Food", date = now - dayMs),
                ExpenseEntity(title = "Monthly Gym Membership", amount = 49.99, type = "Expense", category = "Health", date = now - dayMs * 2),
                ExpenseEntity(title = "Freelance UX Design Income", amount = 1250.00, type = "Income", category = "Salary", date = now - dayMs * 3),
                ExpenseEntity(title = "Coffee & Bakery Snack", amount = 8.75, type = "Expense", category = "Food", date = now),
                ExpenseEntity(title = "Metro Transit Pass", amount = 35.00, type = "Expense", category = "Transport", date = now - dayMs * 4)
            )
            expenses.forEach { db.expenseDao().insertExpense(it) }

            // Seed Health Log
            db.healthDao().insertHealthLog(
                HealthLogEntity(
                    date = now,
                    waterIntakeMl = 1750,
                    sleepHours = 7.5f,
                    weightKg = 72.4f,
                    heightCm = 178f,
                    stepsCount = 8420,
                    caloriesBurned = 540,
                    mood = "⚡ Energetic",
                    workoutDurationMins = 45,
                    workoutType = "HIIT & Core Cardio"
                )
            )

            // Seed Journal
            val journals = listOf(
                JournalEntity(
                    title = "Reflections on Productivity & Focus",
                    content = "Today was a fantastic day for deep work. I finished all critical high-priority tasks by noon. Using the Gemini AI Assistant for meal planning saved almost an hour of decision fatigue!",
                    date = now,
                    moodTag = "⚡ Energetic",
                    tags = "Mindset, AI, Growth"
                ),
                JournalEntity(
                    title = "Evening Gratitude & Walk",
                    content = "Took a 45-minute sunset walk around the park. Watched the horizon glow in orange and violet. Grateful for sound health, good coffee, and productive momentum.",
                    date = now - dayMs,
                    moodTag = "😊 Happy",
                    tags = "Gratitude, Nature"
                )
            )
            journals.forEach { db.journalDao().insertJournalEntry(it) }

            // Seed Notes
            val notes = listOf(
                NoteEntity(
                    title = "🚀 High Performance Morning Routine",
                    content = "1. Hydrate 500ml water immediately upon waking\n2. 10 Mins light morning stretch & sunlight exposure\n3. Zero phone notifications for first 60 minutes\n4. Review daily top 3 priorities on Life Tracker AI",
                    isPinned = true,
                    folder = "Routine"
                ),
                NoteEntity(
                    title = "🛒 Healthy Grocery Checklist",
                    content = "Spinach|Avocados|Greek Yogurt|Blueberries|Almonds|Oatmeal|Wild Salmon|Chicken Breast",
                    isChecklist = true,
                    checklistItems = "Spinach, Avocados, Greek Yogurt, Blueberries, Almonds, Oatmeal",
                    isPinned = false,
                    folder = "Shopping"
                )
            )
            notes.forEach { db.noteDao().insertNote(it) }

            // Seed Goals
            val goals = listOf(
                GoalEntity(
                    title = "Complete AI Life Tracker Project",
                    description = "Deploy robust Android life management app with full offline capabilities and Gemini AI intelligence.",
                    category = "Career",
                    type = "Short-term",
                    progressPercent = 85,
                    deadline = now + dayMs * 7,
                    milestones = "UI Mockups|Room DB Schema|Gemini AI REST Service|Testing & Build"
                ),
                GoalEntity(
                    title = "Run a Full Marathon in 4 Hours",
                    description = "Consistently train 4 days per week building stamina and aerobic endurance.",
                    category = "Health",
                    type = "Long-term",
                    progressPercent = 40,
                    deadline = now + dayMs * 90,
                    milestones = "5K Run under 25 mins|10K Continuous Run|Half Marathon Finish"
                )
            )
            goals.forEach { db.goalDao().insertGoal(it) }

            // Seed Calendar Events
            val calendarEvents = listOf(
                CalendarEventEntity(
                    title = "Design System Review Meeting",
                    description = "Walkthrough Jetpack Compose UI components and color palette with the team.",
                    startTime = now + 3600000L * 2,
                    endTime = now + 3600000L * 3,
                    eventType = "Event",
                    location = "Virtual Meeting Room 4"
                ),
                CalendarEventEntity(
                    title = "Mom's Birthday Celebration 🎉",
                    description = "Family dinner at Sunset Grill.",
                    startTime = now + dayMs * 3,
                    endTime = now + dayMs * 3 + 7200000L,
                    eventType = "Birthday",
                    location = "Sunset Grill Bistro"
                )
            )
            calendarEvents.forEach { db.calendarDao().insertEvent(it) }
        }
    }
}
