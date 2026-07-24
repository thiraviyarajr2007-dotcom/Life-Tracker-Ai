package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.UserProfileEntity
import com.example.ui.components.GlassmorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfileEntity?,
    userEmail: String,
    onSaveProfile: (String, Double, Int) -> Unit,
    onLogout: () -> Unit
) {
    val profile = userProfile ?: UserProfileEntity()

    var nameText by remember(profile) { mutableStateOf(profile.name) }
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
                text = "Profile Management",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Profile Avatar Card
        item {
            GlassmorphicCard {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = nameText.take(1).uppercase(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(nameText, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(userEmail, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Profile Goals Editor
        item {
            GlassmorphicCard {
                Text("Personal Targets", fontWeight = FontWeight.Bold, fontSize = 16.sp)

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
                    modifier = Modifier.fillMaxWidth().testTag("save_profile_btn")
                ) {
                    Text("Save Profile Changes", fontWeight = FontWeight.Bold)
                }
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
                Text("Sign Out", fontWeight = FontWeight.Bold)
            }
        }
    }
}
