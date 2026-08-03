package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.ElectricBlue
import kotlin.random.Random

private data class StarPoint(
    val xRatio: Float,
    val yRatio: Float,
    val size: Float,
    val baseAlpha: Float,
    val pulseSpeed: Int
)

@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    enableStarfield: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambientOrb")

    val orbOffset1 by infiniteTransition.animateFloat(
        initialValue = -40f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1"
    )

    val orbOffset2 by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2"
    )

    val starPulseTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "starPulse"
    )

    val stars = remember {
        val rand = Random(1234)
        val starColors = listOf(Color.White, Color(0xFFD0F0FF), Color(0xFFE0C0FF), Color(0xFFC0FFFF))
        List(160) {
            StarPoint(
                xRatio = rand.nextFloat(),
                yRatio = rand.nextFloat(),
                size = rand.nextFloat() * 2.2f + 0.8f,
                baseAlpha = rand.nextFloat() * 0.5f + 0.35f,
                pulseSpeed = rand.nextInt(3) + 1
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Translucent floating ambient volumetric lighting orbs & starfield behind screens
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Starfield
            if (enableStarfield) {
                stars.forEachIndexed { idx, star ->
                    val alphaMod = kotlin.math.sin((starPulseTime * star.pulseSpeed * 2 * Math.PI)).toFloat() * 0.30f
                    val currentAlpha = (star.baseAlpha + alphaMod).coerceIn(0.2f, 0.95f)
                    val starColor = when (idx % 4) {
                        0 -> Color.White.copy(alpha = currentAlpha)
                        1 -> ElectricBlue.copy(alpha = currentAlpha)
                        2 -> AccentCyan.copy(alpha = currentAlpha)
                        else -> AccentPurple.copy(alpha = currentAlpha * 0.8f)
                    }
                    drawCircle(
                        color = starColor,
                        radius = star.size,
                        center = Offset(star.xRatio * width, star.yRatio * height)
                    )
                }
            }

            // Top-Right Glowing Electric Blue Volumetric Orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ElectricBlue.copy(alpha = 0.28f),
                        AccentPurple.copy(alpha = 0.16f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.85f + orbOffset1, height * 0.15f + orbOffset2),
                    radius = width * 0.75f
                ),
                radius = width * 0.75f,
                center = Offset(width * 0.85f + orbOffset1, height * 0.15f + orbOffset2)
            )

            // Bottom-Left Glowing Cyan Orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AccentCyan.copy(alpha = 0.24f),
                        AccentIndigo.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.15f + orbOffset2, height * 0.75f + orbOffset1),
                    radius = width * 0.80f
                ),
                radius = width * 0.80f,
                center = Offset(width * 0.15f + orbOffset2, height * 0.75f + orbOffset1)
            )
        }

        // Floating particles
        FloatingParticles(config = FloatingParticlesPresets.standard)

        content()
    }
}

