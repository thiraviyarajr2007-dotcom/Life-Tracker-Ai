package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.domain.model.AchievementBadge
import com.example.domain.model.BadgeCategory
import com.example.domain.model.GamificationProfile
import com.example.ui.theme.ElectricBlue

@Composable
fun GamificationCard(
    profile: GamificationProfile = GamificationProfile(),
    modifier: Modifier = Modifier,
    onViewAllBadges: () -> Unit = {}
) {
    var selectedBadgeForDetails by remember { mutableStateOf<AchievementBadge?>(null) }

    val xpProgressAnimated by animateFloatAsState(
        targetValue = profile.levelProgressPercent,
        label = "XpProgressAnimation"
    )

    GlassmorphicCard(
        cornerRadius = 28.dp,
        backgroundColor = Color(0xFF0A1025).copy(alpha = 0.92f),
        borderColor = Color(profile.rank.colorHex).copy(alpha = 0.4f),
        glowColor = Color(profile.rank.colorHex),
        isActive = true,
        modifier = modifier
            .fillMaxWidth()
            .testTag("gamification_card")
    ) {
        Column {
            // Header with Rank Badge & Hunter Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                androidx.compose.ui.graphics.Brush.radialGradient(
                                    colors = listOf(
                                        Color(profile.rank.colorHex),
                                        Color(0xFF0A1025)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 26.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(profile.rank.colorHex).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(profile.rank.colorHex).copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = profile.rank.displayName.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(profile.rank.colorHex),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LVL ${profile.level}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = profile.levelTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Monarch Coins & Streak Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E1B4B).copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("🪙", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile.coins}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFFFD54F)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF311018).copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("🔥", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile.streakDays}d",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFFF6E40)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hunter Stats Mini Radar Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF050816))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "STR", value = profile.stats.strength, color = Color(0xFFEF4444))
                StatItem(label = "AGI", value = profile.stats.agility, color = Color(0xFF00E5FF))
                StatItem(label = "INT", value = profile.stats.intelligence, color = Color(0xFF7C3AED))
                StatItem(label = "VIT", value = profile.stats.vitality, color = Color(0xFF10B981))
                StatItem(label = "SNE", value = profile.stats.sense, color = Color(0xFFF59E0B))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated XP Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SYSTEM LEVEL PROGRESS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${profile.xpInCurrentLevel} / ${profile.xpNeededForNextLevel} XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { xpProgressAnimated },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = ElectricBlue,
                    trackColor = Color(0xFF111827)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Unlocked Badges Horizontal Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monarch Seals (${profile.badges.count { it.isUnlocked }}/${profile.badges.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                TextButton(onClick = onViewAllBadges) {
                    Text("View All", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(profile.badges) { badge ->
                    BadgeItemCard(
                        badge = badge,
                        onClick = { selectedBadgeForDetails = badge }
                    )
                }
            }
        }
    }

    // Badge Details Modal Dialog
    selectedBadgeForDetails?.let { badge ->
        BadgeDetailsDialog(
            badge = badge,
            onDismiss = { selectedBadgeForDetails = null }
        )
    }
}

@Composable
private fun StatItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = "$value", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
    }
}

@Composable
fun BadgeItemCard(
    badge: AchievementBadge,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (badge.isUnlocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        border = if (badge.isUnlocked) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.width(110.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (badge.isUnlocked) Color(0xFFFFD54F).copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.2f))
            ) {
                Text(
                    text = badge.icon,
                    fontSize = 20.sp,
                    color = if (badge.isUnlocked) Color.Unspecified else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = badge.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = if (badge.isUnlocked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(2.dp))

            if (badge.isUnlocked) {
                Text(
                    text = "Unlocked ✨",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32)
                )
            } else {
                Text(
                    text = "${badge.progressCurrent}/${badge.progressTarget}",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BadgeDetailsDialog(
    badge: AchievementBadge,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(badge.icon, fontSize = 40.sp)
        },
        title = {
            Text(badge.title, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = badge.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Category: ${badge.category.displayName}") }
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Reward: +${badge.xpReward} XP") }
                    )
                }

                if (badge.isUnlocked) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = "🎉 Unlocked on ${badge.unlockedDateFormatted ?: "Recently"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Progress: ${badge.progressCurrent} / ${badge.progressTarget}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { badge.progressCurrent.toFloat() / badge.progressTarget.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Awesome!")
            }
        }
    )
}

@Composable
fun FullGamificationBadgesGrid(
    profile: GamificationProfile = GamificationProfile(),
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(BadgeCategory.ALL) }
    var selectedBadgeForDetails by remember { mutableStateOf<AchievementBadge?>(null) }

    val filteredBadges = remember(selectedCategory, profile.badges) {
        if (selectedCategory == BadgeCategory.ALL) {
            profile.badges
        } else {
            profile.badges.filter { it.category == selectedCategory }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Achievement Badges & Trophies 🏆",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(BadgeCategory.values()) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category.displayName, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            filteredBadges.chunked(3).forEach { rowBadges ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowBadges.forEach { badge ->
                        Box(modifier = Modifier.weight(1f)) {
                            BadgeItemCard(
                                badge = badge,
                                onClick = { selectedBadgeForDetails = badge }
                            )
                        }
                    }
                    if (rowBadges.size < 3) {
                        repeat(3 - rowBadges.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    selectedBadgeForDetails?.let { badge ->
        BadgeDetailsDialog(
            badge = badge,
            onDismiss = { selectedBadgeForDetails = null }
        )
    }
}
