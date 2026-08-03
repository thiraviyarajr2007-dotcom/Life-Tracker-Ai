package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricBlue

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = DarkSurface.copy(alpha = 0.85f),
    borderColor: Color = Color.White.copy(alpha = 0.15f),
    glowColor: Color = ElectricBlue.copy(alpha = 0.35f),
    isActive: Boolean = false,
    cornerRadius: Dp = 28.dp,
    contentPadding: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cardGlowPulse")
    val glowPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val effectiveBorderColor = if (isActive) {
        glowColor.copy(alpha = glowPulseAlpha)
    } else {
        borderColor
    }

    val shape = RoundedCornerShape(cornerRadius)
    Card(
        onClick = onClick ?: {},
        enabled = onClick != null,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, effectiveBorderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 10.dp else 6.dp,
            pressedElevation = 2.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isActive) 18.dp else 12.dp,
                shape = shape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = if (isActive) glowColor else Color.Black.copy(alpha = 0.4f)
            )
    ) {
        Column(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

object GlassmorphicCardPresets {
    @Composable
    fun Standard(
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        isActive: Boolean = false,
        content: @Composable ColumnScope.() -> Unit
    ) {
        GlassmorphicCard(
            modifier = modifier,
            backgroundColor = DarkSurface.copy(alpha = 0.85f),
            borderColor = Color.White.copy(alpha = 0.15f),
            isActive = isActive,
            onClick = onClick,
            content = content
        )
    }

    @Composable
    fun Premium(
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        isActive: Boolean = true,
        content: @Composable ColumnScope.() -> Unit
    ) {
        GlassmorphicCard(
            modifier = modifier,
            backgroundColor = DarkSurface.copy(alpha = 0.92f),
            borderColor = ElectricBlue.copy(alpha = 0.4f),
            glowColor = ElectricBlue,
            isActive = isActive,
            onClick = onClick,
            content = content
        )
    }

    @Composable
    fun Dark(
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        content: @Composable ColumnScope.() -> Unit
    ) {
        GlassmorphicCard(
            modifier = modifier,
            backgroundColor = Color(0xFF0B1020).copy(alpha = 0.95f),
            borderColor = Color.White.copy(alpha = 0.1f),
            onClick = onClick,
            content = content
        )
    }

    @Composable
    fun Accent(
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        content: @Composable ColumnScope.() -> Unit
    ) {
        GlassmorphicCard(
            modifier = modifier,
            backgroundColor = Color(0xFF0F172A).copy(alpha = 0.90f),
            borderColor = AccentCyan.copy(alpha = 0.35f),
            glowColor = AccentCyan,
            isActive = true,
            onClick = onClick,
            content = content
        )
    }

    @Composable
    fun Success(
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        isActive: Boolean = true,
        content: @Composable ColumnScope.() -> Unit
    ) {
        GlassmorphicCard(
            modifier = modifier,
            backgroundColor = Color(0xFF062C22).copy(alpha = 0.85f),
            borderColor = AccentEmerald.copy(alpha = 0.4f),
            glowColor = AccentEmerald,
            isActive = isActive,
            onClick = onClick,
            content = content
        )
    }

    @Composable
    fun Minimal(
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        content: @Composable ColumnScope.() -> Unit
    ) {
        GlassmorphicCard(
            modifier = modifier,
            backgroundColor = Color.White.copy(alpha = 0.05f),
            borderColor = Color.White.copy(alpha = 0.12f),
            onClick = onClick,
            content = content
        )
    }
}


