package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppModule

@Composable
fun LifeTrackerDrawerContent(
    currentModule: AppModule,
    userName: String,
    userEmail: String,
    onModuleSelected: (AppModule) -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .width(300.dp)
            .testTag("life_tracker_drawer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Profile Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = userName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = userEmail,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "LIFE MODULES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
            )

            DrawerItem(
                module = AppModule.DASHBOARD,
                icon = Icons.Default.Dashboard,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )
            DrawerItem(
                module = AppModule.TASKS,
                icon = Icons.Default.CheckCircle,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )
            DrawerItem(
                module = AppModule.HABITS,
                icon = Icons.Default.LocalFireDepartment,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )
            DrawerItem(
                module = AppModule.EXPENSES,
                icon = Icons.Default.AccountBalanceWallet,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )
            DrawerItem(
                module = AppModule.HEALTH,
                icon = Icons.Default.Favorite,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )
            DrawerItem(
                module = AppModule.JOURNAL,
                icon = Icons.Default.Book,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )
            DrawerItem(
                module = AppModule.NOTES,
                icon = Icons.Default.StickyNote2,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )
            DrawerItem(
                module = AppModule.GOALS,
                icon = Icons.Default.TrackChanges,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )
            DrawerItem(
                module = AppModule.CALENDAR,
                icon = Icons.Default.CalendarMonth,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "AI & ANALYTICS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
            )

            DrawerItem(
                module = AppModule.AI_ASSISTANT,
                icon = Icons.Default.AutoAwesome,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )
            DrawerItem(
                module = AppModule.REPORTS,
                icon = Icons.Default.BarChart,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )
            DrawerItem(
                module = AppModule.SETTINGS,
                icon = Icons.Default.Settings,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )
            DrawerItem(
                module = AppModule.PROFILE,
                icon = Icons.Default.Person,
                currentModule = currentModule,
                onSelect = onModuleSelected
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))

            NavigationDrawerItem(
                label = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
                selected = false,
                onClick = onLogout,
                icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = MaterialTheme.colorScheme.error) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun DrawerItem(
    module: AppModule,
    icon: ImageVector,
    currentModule: AppModule,
    onSelect: (AppModule) -> Unit
) {
    NavigationDrawerItem(
        label = { Text(module.title) },
        selected = currentModule == module,
        onClick = { onSelect(module) },
        icon = { Icon(icon, contentDescription = module.title) },
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
