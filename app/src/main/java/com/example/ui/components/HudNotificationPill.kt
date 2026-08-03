package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.ElectricBlue
import kotlinx.coroutines.delay

data class HudEvent(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val subtitle: String,
    val xpGained: Int = 0,
    val isLevelUp: Boolean = false
)

@Composable
fun HudNotificationPill(
    hudEvent: HudEvent?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(hudEvent?.id) {
        if (hudEvent != null) {
            visible = true
            delay(3500)
            visible = false
            delay(300)
            onDismiss()
        } else {
            visible = false
        }
    }

    AnimatedVisibility(
        visible = visible && hudEvent != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
        ) + fadeIn(animationSpec = tween(200)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(250)
        ) + fadeOut(animationSpec = tween(200)),
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 8.dp)
    ) {
        hudEvent?.let { event ->
            val pillShape = RoundedCornerShape(32.dp)
            val borderColor = if (event.isLevelUp) AccentPurple else ElectricBlue

            Surface(
                shape = pillShape,
                color = Color(0xFF0A1025).copy(alpha = 0.95f),
                border = BorderStroke(1.5.dp, borderColor.copy(alpha = 0.8f)),
                modifier = Modifier
                    .widthIn(max = 380.dp)
                    .clip(pillShape)
                    .clickable { onDismiss() }
                    .shadow(
                        elevation = 16.dp,
                        shape = pillShape,
                        spotColor = borderColor
                    )
                    .testTag("dynamic_island_hud_pill")
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        if (event.isLevelUp) AccentPurple else ElectricBlue,
                                        Color(0xFF00E5FF)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (event.isLevelUp) Icons.Default.AutoAwesome else Icons.Default.Bolt,
                            contentDescription = "System Alert",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SYSTEM ARCHITECT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                letterSpacing = 1.sp
                            )
                            if (event.xpGained > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+${event.xpGained} XP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }

                        Text(
                            text = event.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = event.subtitle,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
