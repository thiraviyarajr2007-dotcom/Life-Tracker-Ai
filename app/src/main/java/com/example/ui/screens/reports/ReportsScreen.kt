package com.example.ui.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    taskCount: Int,
    expenseTotal: Double,
    habitStreakMax: Int,
    waterIntakeAverage: Int
) {
    var selectedTimeframe by remember { mutableStateOf("Weekly") }
    var showExportDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("reports_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reports & Analytics 📊",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Comprehensive performance & behavioral insights",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { showExportDialog = true },
                    modifier = Modifier.testTag("export_report_btn")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = "Export Report", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Weekly", "Monthly", "Yearly").forEach { timeframe ->
                    FilterChip(
                        selected = selectedTimeframe == timeframe,
                        onClick = { selectedTimeframe = timeframe },
                        label = { Text(timeframe) }
                    )
                }
            }
        }

        // Circular Gauge KPI Card
        item {
            GlassmorphicCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("$selectedTimeframe Efficiency Score", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• $taskCount Tasks completed", fontSize = 12.sp, color = Color(0xFF1E88E5))
                        Text("• $$expenseTotal total logged expenses", fontSize = 12.sp, color = Color(0xFFFB8C00))
                        Text("• $habitStreakMax days top habit streak", fontSize = 12.sp, color = Color(0xFF43A047))
                    }

                    CircularProgressGauge(
                        progressPercent = 84,
                        activeColor = Color(0xFF1E88E5),
                        modifier = Modifier.size(90.dp)
                    )
                }
            }
        }

        // Weekly Line Chart
        item {
            GlassmorphicCard {
                Text("Weekly Focus & Energy Trend Line", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                val sampleLineData = listOf(
                    "Mon" to 65f,
                    "Tue" to 82f,
                    "Wed" to 78f,
                    "Thu" to 92f,
                    "Fri" to 88f,
                    "Sat" to 70f,
                    "Sun" to 95f
                )

                WeeklyLineChart(dataPoints = sampleLineData, lineColor = Color(0xFF1E88E5))
            }
        }

        // Expense Pie Chart & Breakdown
        item {
            GlassmorphicCard {
                Text("Expense Distribution Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomPieChart(
                        slices = listOf(
                            PieChartSlice("Food", 450.0, Color(0xFFFB8C00)),
                            PieChartSlice("Bills", 1200.0, Color(0xFFE53935)),
                            PieChartSlice("Shopping", 350.0, Color(0xFF8E24AA)),
                            PieChartSlice("Health", 250.0, Color(0xFF43A047))
                        ),
                        modifier = Modifier.size(130.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        LegendItem("Bills & Utilities", "$1,200", Color(0xFFE53935))
                        LegendItem("Food & Dining", "$450", Color(0xFFFB8C00))
                        LegendItem("Shopping", "$350", Color(0xFF8E24AA))
                        LegendItem("Health & Wellness", "$250", Color(0xFF43A047))
                    }
                }
            }
        }

        // Habit Heatmap (GitHub Style)
        item {
            GlassmorphicCard {
                HabitContributionGraph()
            }
        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Analytics Summary") },
            text = {
                Text("Your $selectedTimeframe Life Report has been generated cleanly as a text summary. You can copy or save it to your records.")
            },
            confirmButton = {
                Button(onClick = { showExportDialog = false }) {
                    Text("Download Summary")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun LegendItem(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = MaterialTheme.shapes.extraSmall, color = color, modifier = Modifier.size(10.dp)) {}
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "$label: ", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Text(text = value, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
