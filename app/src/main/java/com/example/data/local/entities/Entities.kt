package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: String = "Medium", // High, Medium, Low
    val category: String = "Personal", // Work, Personal, Health, Finance, Study, etc.
    val dueDate: Long = System.currentTimeMillis(),
    val reminderTime: Long = 0,
    val isRecurring: Boolean = false,
    val repeatFrequency: String = "None" // Daily, Weekly, Monthly, None
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String = "Health",
    val frequency: String = "Daily", // Daily, Weekly, Monthly
    val streakCount: Int = 0,
    val isCompletedToday: Boolean = false,
    val lastCompletedDate: Long = 0,
    val targetDaysPerWeek: Int = 7
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String = "Expense", // Income, Expense
    val category: String = "General", // Food, Shopping, Transport, Salary, Bills, Entertainment
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "health_logs")
data class HealthLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val waterIntakeMl: Int = 0, // e.g. 2000 ml goal
    val sleepHours: Float = 0f,
    val weightKg: Float = 0f,
    val heightCm: Float = 175f,
    val stepsCount: Int = 0,
    val caloriesBurned: Int = 0,
    val mood: String = "😊 Happy", // Happy, Neutral, Calm, Energetic, Anxious, Sad
    val workoutDurationMins: Int = 0,
    val workoutType: String = "General Workout"
)

@Entity(tableName = "journal_entries")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val date: Long = System.currentTimeMillis(),
    val moodTag: String = "Calm",
    val tags: String = "Personal", // Comma separated tags
    val photoUri: String? = null,
    val voiceNoteSnippet: String? = null
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val isChecklist: Boolean = false,
    val checklistItems: String = "", // JSON array or pipe separated
    val isPinned: Boolean = false,
    val folder: String = "General",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Career",
    val type: String = "Short-term", // Long-term, Short-term
    val progressPercent: Int = 0,
    val deadline: Long = System.currentTimeMillis() + 86400000L * 30,
    val milestones: String = "" // pipe-separated list of milestone tasks
)

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = System.currentTimeMillis() + 3600000L,
    val eventType: String = "Event", // Event, Birthday, Reminder, Task
    val location: String = ""
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Alex Rivera",
    val email: String = "alex.rivera@lifetracker.ai",
    val isDarkMode: Boolean = true,
    val dailyWaterGoalMl: Int = 2500,
    val dailyCalorieGoal: Int = 2200,
    val dailyStepGoal: Int = 10000,
    val monthlyBudget: Double = 3000.0,
    val avatarId: Int = 1
)
