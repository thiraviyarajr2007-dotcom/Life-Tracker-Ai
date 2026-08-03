package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class PieChartSlice(
    val category: String,
    val value: Double,
    val color: Color
)

@Composable
fun CustomPieChart(
    slices: List<PieChartSlice>,
    modifier: Modifier = Modifier.size(160.dp),
    strokeWidth: Dp = 20.dp,
    centerContent: @Composable ColumnScope.() -> Unit = {}
) {
    val total = slices.sumOf { it.value }.coerceAtLeast(1.0)
    var startAngle = -90f

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            slices.forEach { slice ->
                val sweepAngle = ((slice.value / total) * 360f).toFloat()
                if (sweepAngle > 0f) {
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            centerContent()
        }
    }
}

@Composable
fun CustomBarChart(
    dataPoints: List<Pair<String, Float>>,
    maxVal: Float = (dataPoints.maxOfOrNull { it.second } ?: 100f).coerceAtLeast(1f),
    barColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier.fillMaxWidth().height(140.dp)
) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        dataPoints.forEach { (label, value) ->
            val animatedHeightFraction by animateFloatAsState(
                targetValue = (value / maxVal).coerceIn(0.05f, 1f),
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                label = "barHeight"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Canvas(
                    modifier = Modifier
                        .width(18.dp)
                        .fillMaxHeight(0.8f)
                ) {
                    val barHeight = size.height * animatedHeightFraction
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(0f, size.height - barHeight),
                        size = Size(size.width, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CircularProgressGauge(
    progressPercent: Int,
    modifier: Modifier = Modifier.size(100.dp),
    strokeWidth: Dp = 10.dp,
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercent.coerceIn(0, 100) / 100f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "gauge"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Track background ring
            drawCircle(
                color = activeColor.copy(alpha = 0.15f),
                style = Stroke(width = strokeWidth.toPx())
            )
            // Animated progress ring
            drawArc(
                color = activeColor,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AppleActivityRings(
    taskProgress: Float = 0.85f,    // Coral ring
    habitProgress: Float = 0.90f,   // Emerald ring
    healthProgress: Float = 0.75f,  // Cyan ring
    modifier: Modifier = Modifier.size(120.dp)
) {
    val animTask by animateFloatAsState(
        targetValue = taskProgress.coerceIn(0f, 1f),
        animationSpec = tween(1100, easing = FastOutSlowInEasing),
        label = "ringTask"
    )
    val animHabit by animateFloatAsState(
        targetValue = habitProgress.coerceIn(0f, 1f),
        animationSpec = tween(1300, easing = FastOutSlowInEasing),
        label = "ringHabit"
    )
    val animHealth by animateFloatAsState(
        targetValue = healthProgress.coerceIn(0f, 1f),
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "ringHealth"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = 9.dp.toPx()
            val paddingPx = 3.dp.toPx()

            // Outer Ring (Coral / Move)
            val outerRadius = (size.minDimension / 2) - strokePx / 2
            drawCircle(
                color = AccentCoral.copy(alpha = 0.2f),
                radius = outerRadius,
                style = Stroke(width = strokePx)
            )
            drawArc(
                color = AccentCoral,
                startAngle = -90f,
                sweepAngle = animTask * 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(size.width / 2 - outerRadius, size.height / 2 - outerRadius),
                size = Size(outerRadius * 2, outerRadius * 2)
            )

            // Middle Ring (Emerald / Habits)
            val middleRadius = outerRadius - strokePx - paddingPx
            drawCircle(
                color = AccentEmerald.copy(alpha = 0.2f),
                radius = middleRadius,
                style = Stroke(width = strokePx)
            )
            drawArc(
                color = AccentEmerald,
                startAngle = -90f,
                sweepAngle = animHabit * 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(size.width / 2 - middleRadius, size.height / 2 - middleRadius),
                size = Size(middleRadius * 2, middleRadius * 2)
            )

            // Inner Ring (Cyan / Health)
            val innerRadius = middleRadius - strokePx - paddingPx
            drawCircle(
                color = AccentCyan.copy(alpha = 0.2f),
                radius = innerRadius,
                style = Stroke(width = strokePx)
            )
            drawArc(
                color = AccentCyan,
                startAngle = -90f,
                sweepAngle = animHealth * 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(size.width / 2 - innerRadius, size.height / 2 - innerRadius),
                size = Size(innerRadius * 2, innerRadius * 2)
            )
        }
    }
}

@Composable
fun WeeklyLineChart(
    dataPoints: List<Pair<String, Float>>,
    lineColor: Color = Color(0xFF1E88E5),
    modifier: Modifier = Modifier.fillMaxWidth().height(120.dp)
) {
    val maxVal = (dataPoints.maxOfOrNull { it.second } ?: 100f).coerceAtLeast(1f)

    Column(modifier = modifier) {
        Canvas(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (dataPoints.isEmpty()) return@Canvas

            val stepX = size.width / (dataPoints.size - 1).coerceAtLeast(1)
            val path = Path()

            dataPoints.forEachIndexed { index, pair ->
                val x = index * stepX
                val y = size.height - ((pair.second / maxVal) * size.height)

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }

                drawCircle(
                    color = lineColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dataPoints.forEach { (day, _) ->
                Text(day, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun HabitContributionGraph(
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val levelColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        Color(0xFFC8E6C9),
        Color(0xFF81C784),
        Color(0xFF4CAF50),
        Color(0xFF2E7D32)
    )

    Column(modifier = modifier) {
        Text("Habit & Activity Heatmap (GitHub Style)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(14) { col ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    repeat(5) { row ->
                        val intensity = (col + row * 2) % 5
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(levelColors[intensity])
                        )
                    }
                }
            }
        }
    }
}
