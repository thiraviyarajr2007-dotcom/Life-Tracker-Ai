package com.example.domain.model

enum class HunterRank(val displayName: String, val colorHex: Long) {
    E_RANK("E-Rank Hunter", 0xFF9E9E9E),
    D_RANK("D-Rank Hunter", 0xFF4CAF50),
    C_RANK("C-Rank Hunter", 0xFF2196F3),
    B_RANK("B-Rank Hunter", 0xFF9C27B0),
    A_RANK("A-Rank Hunter", 0xFFFF9800),
    S_RANK("S-Rank Hunter", 0xFF00E5FF),
    NATIONAL_LEVEL("National Level Hunter", 0xFFFFD700),
    SHADOW_MONARCH("Shadow Monarch", 0xFF7C3AED)
}

enum class BadgeCategory(val displayName: String) {
    ALL("All Monarch Seals"),
    TASKS("Quests & Missions"),
    HABITS("Daily Duty Streaks"),
    EXPENSES("Treasury & Finance"),
    HEALTH("Vitality & Workout"),
    SPECIAL("Monarch Milestones")
}

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val category: BadgeCategory,
    val xpReward: Int = 100,
    val isUnlocked: Boolean = false,
    val progressCurrent: Int = 0,
    val progressTarget: Int = 100,
    val unlockedDateFormatted: String? = null
)

data class HunterStats(
    val strength: Int = 18,     // STR - workout, physical tasks
    val agility: Int = 22,      // AGI - habit streaks, quick tasks
    val intelligence: Int = 25, // INT - study, notes, AI chat
    val vitality: Int = 20,     // VIT - sleep, water intake
    val sense: Int = 19,        // SNE - journal, mindfulness
    val unallocatedPoints: Int = 5
)

data class GamificationProfile(
    val totalXp: Int = 2850,
    val coins: Int = 620,
    val streakDays: Int = 21,
    val stats: HunterStats = HunterStats(),
    val badges: List<AchievementBadge> = defaultAchievementBadges()
) {
    val level: Int get() = calculateLevel(totalXp)
    val rank: HunterRank get() = calculateRank(level)
    val levelTitle: String get() = calculateLevelTitle(level)
    val xpInCurrentLevel: Int get() = totalXp % XP_PER_LEVEL
    val xpNeededForNextLevel: Int get() = XP_PER_LEVEL
    val levelProgressPercent: Float get() = xpInCurrentLevel.toFloat() / XP_PER_LEVEL.toFloat()

    companion object {
        const val XP_PER_LEVEL = 500

        fun calculateLevel(totalXp: Int): Int {
            return (totalXp / XP_PER_LEVEL) + 1
        }

        fun calculateRank(level: Int): HunterRank {
            return when {
                level < 3 -> HunterRank.E_RANK
                level < 6 -> HunterRank.D_RANK
                level < 10 -> HunterRank.C_RANK
                level < 15 -> HunterRank.B_RANK
                level < 20 -> HunterRank.A_RANK
                level < 30 -> HunterRank.S_RANK
                level < 45 -> HunterRank.NATIONAL_LEVEL
                else -> HunterRank.SHADOW_MONARCH
            }
        }

        fun calculateLevelTitle(level: Int): String {
            return when {
                level < 3 -> "E-Rank Reawakened"
                level < 6 -> "System Player"
                level < 10 -> "Dungeon Specialist"
                level < 15 -> "Shadow Striker"
                level < 20 -> "Monarch Architect"
                level < 30 -> "S-Rank Sovereign"
                level < 45 -> "National Level Monarch"
                else -> "Monarch of Shadows"
            }
        }
    }
}

fun defaultAchievementBadges(): List<AchievementBadge> {
    return listOf(
        AchievementBadge(
            id = "task_first",
            title = "Awakening",
            description = "Complete your first System Quest",
            icon = "⚔️",
            category = BadgeCategory.TASKS,
            xpReward = 50,
            isUnlocked = true,
            progressCurrent = 1,
            progressTarget = 1,
            unlockedDateFormatted = "Jul 10, 2026"
        ),
        AchievementBadge(
            id = "task_master",
            title = "Quest Master",
            description = "Complete 25 priority quests",
            icon = "👑",
            category = BadgeCategory.TASKS,
            xpReward = 200,
            isUnlocked = true,
            progressCurrent = 25,
            progressTarget = 25,
            unlockedDateFormatted = "Jul 18, 2026"
        ),
        AchievementBadge(
            id = "task_centurion",
            title = "Centurion Slayer",
            description = "Clear 100 System Quests",
            icon = "🌌",
            category = BadgeCategory.TASKS,
            xpReward = 500,
            isUnlocked = false,
            progressCurrent = 68,
            progressTarget = 100
        ),
        AchievementBadge(
            id = "habit_streak_7",
            title = "7-Day Ignition",
            description = "Maintain a 7-day unbroken daily duty streak",
            icon = "🔥",
            category = BadgeCategory.HABITS,
            xpReward = 150,
            isUnlocked = true,
            progressCurrent = 7,
            progressTarget = 7,
            unlockedDateFormatted = "Jul 14, 2026"
        ),
        AchievementBadge(
            id = "habit_streak_21",
            title = "Shadow Persistence",
            description = "Reach a 21-day continuous daily duty streak",
            icon = "⚡",
            category = BadgeCategory.HABITS,
            xpReward = 350,
            isUnlocked = true,
            progressCurrent = 21,
            progressTarget = 21,
            unlockedDateFormatted = "Jul 24, 2026"
        ),
        AchievementBadge(
            id = "habit_streak_30",
            title = "Monarch Discipline",
            description = "Maintain a 30-day continuous duty streak",
            icon = "🚀",
            category = BadgeCategory.HABITS,
            xpReward = 600,
            isUnlocked = false,
            progressCurrent = 21,
            progressTarget = 30
        ),
        AchievementBadge(
            id = "budget_ninja",
            title = "Treasury Guard",
            description = "Stay within monthly budget for 30 consecutive days",
            icon = "💎",
            category = BadgeCategory.EXPENSES,
            xpReward = 250,
            isUnlocked = true,
            progressCurrent = 30,
            progressTarget = 30,
            unlockedDateFormatted = "Jul 20, 2026"
        ),
        AchievementBadge(
            id = "hydration_hero",
            title = "Elixir of Life",
            description = "Reach 2500ml daily water intake 10 times",
            icon = "💧",
            category = BadgeCategory.HEALTH,
            xpReward = 150,
            isUnlocked = true,
            progressCurrent = 10,
            progressTarget = 10,
            unlockedDateFormatted = "Jul 22, 2026"
        ),
        AchievementBadge(
            id = "zen_architect",
            title = "System Architect Sync",
            description = "Engage in 10 AI System Architect strategic advice sessions",
            icon = "✨",
            category = BadgeCategory.SPECIAL,
            xpReward = 250,
            isUnlocked = true,
            progressCurrent = 10,
            progressTarget = 10,
            unlockedDateFormatted = "Jul 23, 2026"
        ),
        AchievementBadge(
            id = "all_rounder",
            title = "Shadow Sovereign",
            description = "Engage all System Modules in a single week",
            icon = "🔮",
            category = BadgeCategory.SPECIAL,
            xpReward = 500,
            isUnlocked = false,
            progressCurrent = 8,
            progressTarget = 10
        )
    )
}

