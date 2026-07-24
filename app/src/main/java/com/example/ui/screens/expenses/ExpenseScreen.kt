package com.example.ui.screens.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ExpenseEntity
import com.example.ui.components.CustomBarChart
import com.example.ui.components.CustomPieChart
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.PieChartSlice
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    expenses: List<ExpenseEntity>,
    monthlyBudget: Double,
    onAddExpense: (String, Double, String, String, String) -> Unit,
    onDeleteExpense: (ExpenseEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val totalIncome = expenses.filter { it.type == "Income" }.sumOf { it.amount }
    val totalExpense = expenses.filter { it.type == "Expense" }.sumOf { it.amount }
    val netBalance = totalIncome - totalExpense

    // Prepare Pie Chart slices by category
    val categoryColors = mapOf(
        "Food" to Color(0xFFFF5722),
        "Shopping" to Color(0xFFE91E63),
        "Health" to Color(0xFF4CAF50),
        "Transport" to Color(0xFF2196F3),
        "Bills" to Color(0xFFFFC107),
        "General" to Color(0xFF9C27B0)
    )

    val expenseSlices = expenses.filter { it.type == "Expense" }
        .groupBy { it.category }
        .map { (cat, list) ->
            PieChartSlice(
                category = cat,
                value = list.sumOf { it.amount },
                color = categoryColors[cat] ?: Color(0xFF607D8B)
            )
        }

    Box(modifier = Modifier.fillMaxSize().testTag("expense_screen")) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Expense Tracker & Budget",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Summary Card
            item {
                GlassmorphicCard(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                ) {
                    Text("Total Net Balance", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        text = String.format(Locale.getDefault(), "$%.2f", netBalance),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("Income", fontSize = 11.sp, color = Color(0xFF2E7D32))
                            Text(
                                text = String.format(Locale.getDefault(), "$%.2f", totalIncome),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        Column {
                            Text("Expenses", fontSize = 11.sp, color = Color(0xFFC62828))
                            Text(
                                text = String.format(Locale.getDefault(), "$%.2f", totalExpense),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }
            }

            // Spending Visualizer (Pie Chart)
            if (expenseSlices.isNotEmpty()) {
                item {
                    GlassmorphicCard {
                        Text("Category Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CustomPieChart(slices = expenseSlices, modifier = Modifier.size(130.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                expenseSlices.take(4).forEach { slice ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = slice.color,
                                            shape = MaterialTheme.shapes.small,
                                            modifier = Modifier.size(12.dp)
                                        ) {}
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${slice.category}: $${slice.value.toInt()}", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Transactions History
            item {
                Text("Recent Transactions", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            items(expenses) { expense ->
                GlassmorphicCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(expense.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${expense.category} • ${expense.type}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = String.format(Locale.getDefault(), "%s$%.2f", if (expense.type == "Income") "+" else "-", expense.amount),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (expense.type == "Income") Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                        IconButton(onClick = { onDeleteExpense(expense) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 20.dp)
                .testTag("add_expense_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Transaction")
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, amount, type, category, notes ->
                onAddExpense(title, amount, type, category, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Expense") }
    var category by remember { mutableStateOf("Food") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Transaction") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_expense_title_input")
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_expense_amount_input")
                )

                Text("Type", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Expense", "Income").forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t) }
                        )
                    }
                }

                Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Food", "Shopping", "Health", "Transport", "Bills", "Salary").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amount > 0) {
                        onConfirm(title, amount, type, category, "")
                    }
                },
                modifier = Modifier.testTag("add_expense_confirm_btn")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
