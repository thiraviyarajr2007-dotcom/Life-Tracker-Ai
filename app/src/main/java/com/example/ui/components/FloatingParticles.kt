package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.ElectricBlue
import kotlin.random.Random

data class ParticleConfig(
    val count: Int = 40,
    val maxOpacity: Float = 0.25f,
    val driftSpeed: Int = 30000
)

object FloatingParticlesPresets {
    val subtle = ParticleConfig(count = 20, maxOpacity = 0.15f, driftSpeed = 35000)
    val standard = ParticleConfig(count = 40, maxOpacity = 0.25f, driftSpeed = 28000)
    val rich = ParticleConfig(count = 60, maxOpacity = 0.35f, driftSpeed = 22000)
    val dense = ParticleConfig(count = 80, maxOpacity = 0.40f, driftSpeed = 18000)
}

private data class ParticleState(
    val initialXRatio: Float,
    val initialYRatio: Float,
    val radius: Float,
    val color: Color,
    val speedXMultiplier: Float,
    val speedYMultiplier: Float,
    val alphaOffsetRatio: Float
)

@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    config: ParticleConfig = FloatingParticlesPresets.standard
) {
    val particles = remember(config.count) {
        val rand = Random(42)
        val colors = listOf(ElectricBlue, AccentCyan, AccentPurple, Color.White)
        List(config.count) {
            ParticleState(
                initialXRatio = rand.nextFloat(),
                initialYRatio = rand.nextFloat(),
                radius = rand.nextFloat() * 2.5f + 1f,
                color = colors[rand.nextInt(colors.size)],
                speedXMultiplier = (rand.nextFloat() - 0.5f) * 0.4f,
                speedYMultiplier = -0.3f - rand.nextFloat() * 0.5f,
                alphaOffsetRatio = rand.nextFloat()
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particlesDrift")
    val timeFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(config.driftSpeed, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "timeFraction"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { particle ->
            val currYRatio = (particle.initialYRatio + timeFraction * particle.speedYMultiplier) % 1.0f
            val yPos = if (currYRatio < 0) height * (1.0f + currYRatio) else height * currYRatio
            val xPos = ((particle.initialXRatio + timeFraction * particle.speedXMultiplier) % 1.0f + 1.0f) % 1.0f * width

            val alphaPulse = (kotlin.math.sin((timeFraction + particle.alphaOffsetRatio) * 2 * Math.PI).toFloat() + 1f) / 2f
            val opacity = (alphaPulse * config.maxOpacity).coerceIn(0.05f, config.maxOpacity)

            drawCircle(
                color = particle.color.copy(alpha = opacity),
                radius = particle.radius,
                center = Offset(xPos, yPos)
            )
        }
    }
}
