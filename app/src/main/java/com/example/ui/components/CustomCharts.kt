package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PieChartSlice(
    val category: String,
    val value: Double,
    val color: Color
)

@Composable
fun CustomPieChart(
    slices: List<PieChartSlice>,
    modifier: Modifier = Modifier.size(160.dp),
    strokeWidth: Dp = 22.dp
) {
    val total = slices.sumOf { it.value }.coerceAtLeast(1.0)
    var startAngle = -90f

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            slices.forEach { slice ->
                val sweepAngle = ((slice.value / total) * 360f).toFloat()
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
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "gauge"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = activeColor.copy(alpha = 0.15f),
                style = Stroke(width = strokeWidth.toPx())
            )
            drawArc(
                color = activeColor,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "$progressPercent%",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
