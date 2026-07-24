package com.example.ui.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CustomBarChart
import com.example.ui.components.GlassmorphicCard

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
                Text(
                    text = "Reports & Analytics",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

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

        // Key Performance Indicators Card
        item {
            GlassmorphicCard {
                Text("$selectedTimeframe Life Metrics", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("Tasks Done", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$taskCount Tasks", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Column {
                        Text("Total Expense", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$$expenseTotal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Column {
                        Text("Top Habit Streak", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$habitStreakMax Days", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }

        // Productivity Trend Bar Chart
        item {
            GlassmorphicCard {
                Text("Daily Activity Consistency", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))

                val sampleData = listOf(
                    "Mon" to 75f,
                    "Tue" to 88f,
                    "Wed" to 60f,
                    "Thu" to 95f,
                    "Fri" to 82f,
                    "Sat" to 70f,
                    "Sun" to 90f
                )

                CustomBarChart(dataPoints = sampleData)
            }
        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Summary Report") },
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
