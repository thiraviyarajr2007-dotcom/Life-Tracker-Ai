package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entities.UserProfileEntity
import com.example.data.local.entities.UserStatsEntity
import com.example.data.remote.AuthManager
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfileEntity?,
    userEmail: String,
    userStats: UserStatsEntity? = null,
    hunterStats: com.example.domain.model.HunterStats = com.example.domain.model.HunterStats(),
    onAllocateStatPoint: (String) -> Unit = {},
    onSaveProfile: (String, Double, Int) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager() }
    val firebaseUser = remember { authManager.getCurrentFirebaseUser() }

    val profile = userProfile ?: UserProfileEntity()

    // Use Firebase displayName if present, else userProfile name, else fallback
    val initialName = firebaseUser?.displayName?.takeIf { it.isNotBlank() }
        ?: profile.name.takeIf { it.isNotBlank() }
        ?: if (userEmail.contains("@")) userEmail.substringBefore("@").replaceFirstChar { it.uppercase() } else "User"

    var nameText by remember(profile, firebaseUser) { mutableStateOf(initialName) }
    var budgetText by remember(profile) { mutableStateOf(profile.monthlyBudget.toString()) }
    var waterGoalText by remember(profile) { mutableStateOf(profile.dailyWaterGoalMl.toString()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "HUNTER PROFILE & SYSTEM STATS",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = com.example.ui.theme.ElectricBlue,
                letterSpacing = 1.sp
            )
        }

        // Firebase User Profile Avatar & Header Card
        item {
            GlassmorphicCard(
                cornerRadius = 28.dp,
                backgroundColor = androidx.compose.ui.graphics.Color(0xFF0A1025).copy(alpha = 0.95f),
                borderColor = com.example.ui.theme.ElectricBlue.copy(alpha = 0.5f),
                glowColor = com.example.ui.theme.ElectricBlue,
                isActive = true,
                modifier = Modifier.testTag("user_profile_card")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Photo URL from Google Auth or Fallback Avatar Circle
                    if (!firebaseUser?.photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(firebaseUser?.photoUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Google Account Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(AccentIndigo.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = nameText.take(1).uppercase(),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentIndigo
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = nameText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = androidx.compose.ui.graphics.Color.White
                    )

                    val displayEmail = firebaseUser?.email ?: userEmail
                    Text(
                        text = displayEmail,
                        fontSize = 13.sp,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = AccentEmerald.copy(alpha = 0.15f),
                            shape = CircleShape
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (firebaseUser != null) "Google Authenticated" else "System Reawakened",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentEmerald
                                )
                            }
                        }

                        val stats = userStats ?: UserStatsEntity()
                        Surface(
                            color = com.example.ui.theme.ElectricBlue.copy(alpha = 0.15f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "Lvl ${stats.level} • ${stats.totalXp} XP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = com.example.ui.theme.ElectricBlue,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Hunter System Stat Allocation Section
        item {
            GlassmorphicCard(
                cornerRadius = 24.dp,
                backgroundColor = androidx.compose.ui.graphics.Color(0xFF0A1025).copy(alpha = 0.95f),
                borderColor = com.example.ui.theme.AccentPurple.copy(alpha = 0.5f),
                glowColor = com.example.ui.theme.AccentPurple,
                isActive = hunterStats.unallocatedPoints > 0
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HUNTER STAT ATTRIBUTES",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = androidx.compose.ui.graphics.Color.White,
                            letterSpacing = 1.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (hunterStats.unallocatedPoints > 0) com.example.ui.theme.ElectricBlue.copy(alpha = 0.2f) else androidx.compose.ui.graphics.Color(0xFF111827),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (hunterStats.unallocatedPoints > 0) com.example.ui.theme.ElectricBlue else androidx.compose.ui.graphics.Color.DarkGray)
                        ) {
                            Text(
                                text = "Unallocated: ${hunterStats.unallocatedPoints} PTS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hunterStats.unallocatedPoints > 0) com.example.ui.theme.ElectricBlue else androidx.compose.ui.graphics.Color.Gray,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    StatAllocationRow("STR", "Strength (Workout & Physical Quests)", hunterStats.strength, hunterStats.unallocatedPoints > 0, androidx.compose.ui.graphics.Color(0xFFEF4444)) { onAllocateStatPoint("STR") }
                    StatAllocationRow("AGI", "Agility (Speed & Duty Streaks)", hunterStats.agility, hunterStats.unallocatedPoints > 0, com.example.ui.theme.ElectricBlue) { onAllocateStatPoint("AGI") }
                    StatAllocationRow("INT", "Intelligence (Study & Knowledge)", hunterStats.intelligence, hunterStats.unallocatedPoints > 0, com.example.ui.theme.AccentPurple) { onAllocateStatPoint("INT") }
                    StatAllocationRow("VIT", "Vitality (Health & Hydration)", hunterStats.vitality, hunterStats.unallocatedPoints > 0, androidx.compose.ui.graphics.Color(0xFF10B981)) { onAllocateStatPoint("VIT") }
                    StatAllocationRow("SNE", "Sense (Mindfulness & Focus)", hunterStats.sense, hunterStats.unallocatedPoints > 0, androidx.compose.ui.graphics.Color(0xFFF59E0B)) { onAllocateStatPoint("SNE") }
                }
            }
        }

        // Profile Goals Editor
        item {
            GlassmorphicCard {
                Text("Personal System Targets", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = androidx.compose.ui.graphics.Color.White)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { budgetText = it },
                    label = { Text("Monthly Budget ($)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("profile_budget_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = waterGoalText,
                    onValueChange = { waterGoalText = it },
                    label = { Text("Daily Water Goal (ml)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("profile_water_goal_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val b = budgetText.toDoubleOrNull() ?: profile.monthlyBudget
                        val w = waterGoalText.toIntOrNull() ?: profile.dailyWaterGoalMl
                        onSaveProfile(nameText, b, w)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.ElectricBlue),
                    modifier = Modifier.fillMaxWidth().testTag("save_profile_btn")
                ) {
                    Text("Save System Configuration", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.Black)
                }
            }
        }

        // Gamification Achievement Badges Grid
        item {
            GlassmorphicCard {
                com.example.ui.components.FullGamificationBadgesGrid()
            }
        }

        // Logout Card
        item {
            OutlinedButton(
                onClick = onLogout,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_btn")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out System", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatAllocationRow(
    code: String,
    description: String,
    value: Int,
    canUpgrade: Boolean,
    color: androidx.compose.ui.graphics.Color,
    onUpgrade: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f))
            ) {
                Text(
                    text = code,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = color,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "Current Value: $value",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
        }

        if (canUpgrade) {
            IconButton(
                onClick = onUpgrade,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
            ) {
                Text("+", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = androidx.compose.ui.graphics.Color.Black)
            }
        }
    }
}
