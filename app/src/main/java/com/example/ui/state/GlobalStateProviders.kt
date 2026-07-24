package com.example.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.data.local.entities.ExpenseEntity
import com.example.data.local.entities.HabitEntity
import com.example.data.local.entities.TaskEntity

/**
 * Global state providers for managing Tasks, Expenses, and Habits
 * reactively across the dashboard and individual tracker screens in Android Compose.
 */
data class TasksStateProvider(
    val tasks: List<TaskEntity>,
    val onAddTask: (title: String, category: String, priority: String, dueDate: Long) -> Unit,
    val onToggleTask: (task: TaskEntity) -> Unit,
    val onDeleteTask: (task: TaskEntity) -> Unit
)

data class ExpensesStateProvider(
    val expenses: List<ExpenseEntity>,
    val monthlyBudget: Double,
    val onAddExpense: (title: String, amount: Double, type: String, category: String, notes: String) -> Unit,
    val onDeleteExpense: (expense: ExpenseEntity) -> Unit
)

data class HabitsStateProvider(
    val habits: List<HabitEntity>,
    val onAddHabit: (name: String, category: String, frequency: String) -> Unit,
    val onToggleHabit: (habit: HabitEntity) -> Unit,
    val onDeleteHabit: (habit: HabitEntity) -> Unit
)

val LocalTasksState = staticCompositionLocalOf<TasksStateProvider> {
    error("TasksStateProvider not provided")
}

val LocalExpensesState = staticCompositionLocalOf<ExpensesStateProvider> {
    error("ExpensesStateProvider not provided")
}

val LocalHabitsState = staticCompositionLocalOf<HabitsStateProvider> {
    error("HabitsStateProvider not provided")
}

@Composable
fun ProvideGlobalState(
    tasksProvider: TasksStateProvider,
    expensesProvider: ExpensesStateProvider,
    habitsProvider: HabitsStateProvider,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalTasksState provides tasksProvider,
        LocalExpensesState provides expensesProvider,
        LocalHabitsState provides habitsProvider,
        content = content
    )
}
