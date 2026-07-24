package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.model.AppModule

@Composable
fun LifeTrackerBottomBar(
    currentModule: AppModule,
    onModuleSelected: (AppModule) -> Unit,
    onOpenMoreMenu: () -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("life_tracker_bottom_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentModule == AppModule.DASHBOARD,
            onClick = { onModuleSelected(AppModule.DASHBOARD) },
            icon = {
                Icon(
                    imageVector = if (currentModule == AppModule.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                    contentDescription = "Dashboard"
                )
            },
            label = { Text("Home") },
            modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
            selected = currentModule == AppModule.TASKS,
            onClick = { onModuleSelected(AppModule.TASKS) },
            icon = {
                Icon(
                    imageVector = if (currentModule == AppModule.TASKS) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = "Tasks"
                )
            },
            label = { Text("Tasks") },
            modifier = Modifier.testTag("nav_tasks")
        )

        NavigationBarItem(
            selected = currentModule == AppModule.HABITS,
            onClick = { onModuleSelected(AppModule.HABITS) },
            icon = {
                Icon(
                    imageVector = if (currentModule == AppModule.HABITS) Icons.Filled.LocalFireDepartment else Icons.Outlined.LocalFireDepartment,
                    contentDescription = "Habits"
                )
            },
            label = { Text("Habits") },
            modifier = Modifier.testTag("nav_habits")
        )

        NavigationBarItem(
            selected = currentModule == AppModule.HEALTH,
            onClick = { onModuleSelected(AppModule.HEALTH) },
            icon = {
                Icon(
                    imageVector = if (currentModule == AppModule.HEALTH) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Health"
                )
            },
            label = { Text("Health") },
            modifier = Modifier.testTag("nav_health")
        )

        NavigationBarItem(
            selected = currentModule == AppModule.AI_ASSISTANT,
            onClick = { onModuleSelected(AppModule.AI_ASSISTANT) },
            icon = {
                Icon(
                    imageVector = if (currentModule == AppModule.AI_ASSISTANT) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                    contentDescription = "AI Assistant"
                )
            },
            label = { Text("AI") },
            modifier = Modifier.testTag("nav_ai")
        )

        NavigationBarItem(
            selected = false,
            onClick = onOpenMoreMenu,
            icon = {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "More Modules"
                )
            },
            label = { Text("More") },
            modifier = Modifier.testTag("nav_more")
        )
    }
}
