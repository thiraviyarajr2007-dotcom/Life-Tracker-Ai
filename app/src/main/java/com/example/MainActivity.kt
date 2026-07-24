package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ui.components.LifeTrackerBottomBar
import com.example.ui.components.LifeTrackerDrawerContent
import com.example.ui.navigation.AppNavHost
import com.example.ui.navigation.AppRouter
import com.example.ui.navigation.rememberAppRouter
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.state.ExpensesStateProvider
import com.example.ui.state.HabitsStateProvider
import com.example.ui.state.ProvideGlobalState
import com.example.ui.state.TasksStateProvider
import com.example.ui.theme.LifeTrackerTheme
import com.example.ui.viewmodel.LifeTrackerViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: LifeTrackerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val appRouter = rememberAppRouter()
            val navBackStackEntry by appRouter.navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val currentModule = AppRouter.moduleForRoute(currentRoute)

            val isLoggedIn by viewModel.isLoggedIn.collectAsState()
            val userEmail by viewModel.userEmail.collectAsState()

            val tasks by viewModel.tasks.collectAsState()
            val habits by viewModel.habits.collectAsState()
            val expenses by viewModel.expenses.collectAsState()
            val latestHealth by viewModel.latestHealthLog.collectAsState()
            val journals by viewModel.journals.collectAsState()
            val notes by viewModel.notes.collectAsState()
            val goals by viewModel.goals.collectAsState()
            val calendarEvents by viewModel.calendarEvents.collectAsState()
            val userProfile by viewModel.userProfile.collectAsState()
            val aiMessages by viewModel.aiChatMessages.collectAsState()
            val isAiLoading by viewModel.isAiLoading.collectAsState()

            val isDarkMode = userProfile?.isDarkMode ?: true

            LifeTrackerTheme(darkTheme = isDarkMode) {
                if (!isLoggedIn) {
                    AuthScreen(
                        onLoginSuccess = { email ->
                            viewModel.login(email)
                            Toast.makeText(context, "Welcome to Life Tracker AI!", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            LifeTrackerDrawerContent(
                                currentModule = currentModule,
                                userName = userProfile?.name ?: "Alex Rivera",
                                userEmail = userEmail,
                                onModuleSelected = { module ->
                                    viewModel.navigateTo(module)
                                    appRouter.navigateTo(module)
                                    scope.launch { drawerState.close() }
                                },
                                onLogout = {
                                    viewModel.logout()
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    ) {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                LifeTrackerBottomBar(
                                    currentModule = currentModule,
                                    onModuleSelected = { module ->
                                        viewModel.navigateTo(module)
                                        appRouter.navigateTo(module)
                                    },
                                    onOpenMoreMenu = { scope.launch { drawerState.open() } }
                                )
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                val tasksStateProvider = remember(tasks) {
                                    TasksStateProvider(
                                        tasks = tasks,
                                        onAddTask = { title, cat, pri, due -> viewModel.addTask(title, cat, pri, due) },
                                        onToggleTask = { task -> viewModel.toggleTaskCompletion(task) },
                                        onDeleteTask = { task -> viewModel.deleteTask(task) }
                                    )
                                }

                                val expensesStateProvider = remember(expenses, userProfile) {
                                    ExpensesStateProvider(
                                        expenses = expenses,
                                        monthlyBudget = userProfile?.monthlyBudget ?: 3500.0,
                                        onAddExpense = { title, amount, type, cat, notesStr ->
                                            viewModel.addExpense(title, amount, type, cat, notesStr)
                                        },
                                        onDeleteExpense = { expense -> viewModel.deleteExpense(expense) }
                                    )
                                }

                                val habitsStateProvider = remember(habits) {
                                    HabitsStateProvider(
                                        habits = habits,
                                        onAddHabit = { name, cat, freq -> viewModel.addHabit(name, cat, freq) },
                                        onToggleHabit = { habit -> viewModel.toggleHabitToday(habit) },
                                        onDeleteHabit = { habit -> viewModel.deleteHabit(habit) }
                                    )
                                }

                                ProvideGlobalState(
                                    tasksProvider = tasksStateProvider,
                                    expensesProvider = expensesStateProvider,
                                    habitsProvider = habitsStateProvider
                                ) {
                                    AppNavHost(
                                        appRouter = appRouter,
                                        viewModel = viewModel,
                                        userProfile = userProfile,
                                        userEmail = userEmail,
                                        tasks = tasks,
                                        habits = habits,
                                        expenses = expenses,
                                        latestHealth = latestHealth,
                                        journals = journals,
                                        notes = notes,
                                        goals = goals,
                                        calendarEvents = calendarEvents,
                                        aiMessages = aiMessages,
                                        isAiLoading = isAiLoading,
                                        isDarkMode = isDarkMode,
                                        context = context
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
