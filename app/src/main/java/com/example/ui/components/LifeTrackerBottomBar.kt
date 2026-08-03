package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppModule
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo

@Composable
fun LifeTrackerBottomBar(
    currentModule: AppModule,
    onModuleSelected: (AppModule) -> Unit,
    onOpenMoreMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("life_tracker_bottom_bar"),
        contentAlignment = Alignment.BottomCenter
    ) {
        val dockShape = RoundedCornerShape(28.dp)

        Surface(
            shape = dockShape,
            color = Color(0xFF0F1526).copy(alpha = 0.92f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = dockShape,
                    clip = false,
                    ambientColor = Color.Black,
                    spotColor = AccentIndigo.copy(alpha = 0.4f)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🏠 Home
                BottomNavItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isSelected = currentModule == AppModule.DASHBOARD,
                    onClick = { onModuleSelected(AppModule.DASHBOARD) },
                    modifier = Modifier.weight(1f),
                    testTag = "nav_home"
                )

                // 📅 Planner
                BottomNavItem(
                    icon = Icons.Default.DateRange,
                    label = "Planner",
                    isSelected = currentModule == AppModule.CALENDAR,
                    onClick = { onModuleSelected(AppModule.CALENDAR) },
                    modifier = Modifier.weight(1f),
                    testTag = "nav_planner"
                )

                // 📊 Reports
                BottomNavItem(
                    icon = Icons.Default.BarChart,
                    label = "Reports",
                    isSelected = currentModule == AppModule.REPORTS,
                    onClick = { onModuleSelected(AppModule.REPORTS) },
                    modifier = Modifier.weight(1f),
                    testTag = "nav_reports"
                )

                // 👤 Profile
                BottomNavItem(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    isSelected = currentModule == AppModule.PROFILE,
                    onClick = { onModuleSelected(AppModule.PROFILE) },
                    modifier = Modifier.weight(1f),
                    testTag = "nav_profile"
                )

                // ☰ More
                BottomNavItem(
                    icon = Icons.Default.MoreHoriz,
                    label = "More",
                    isSelected = false,
                    onClick = onOpenMoreMenu,
                    modifier = Modifier.weight(1f),
                    testTag = "nav_more"
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "NavScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) AccentCyan else Color.White.copy(alpha = 0.6f),
        label = "NavIconColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.55f),
        label = "NavTextColor"
    )

    val itemShape = RoundedCornerShape(20.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clip(itemShape)
            .background(
                if (isSelected) {
                    Brush.verticalGradient(
                        colors = listOf(
                            AccentIndigo.copy(alpha = 0.45f),
                            Color(0xFF1E293B).copy(alpha = 0.3f)
                        )
                    )
                } else {
                    Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier
                    .scale(scale)
                    .size(20.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(width = 12.dp, height = 2.dp)
                        .clip(CircleShape)
                        .background(AccentCyan)
                )
            }
        }
    }
}



