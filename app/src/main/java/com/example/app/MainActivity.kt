package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.app.ui.theme.TODOTheme
import com.example.auth.presentation.login_screen.LoginScreen
import com.example.auth.presentation.login_screen.LoginViewModel
import com.example.auth.presentation.register_screen.ProfileSetupScreen
import com.example.auth.presentation.register_screen.RegisterScreen
import com.example.auth.presentation.register_screen.RegisterViewModel
import com.example.core.presentation.navigation.Destination
import com.example.core.presentation.splash_screen.SplashScreen
import com.example.todo.presentation.create_task_screen.CreateTaskScreen
import com.example.todo.presentation.create_task_screen.CreateTaskScreenViewModel
import com.example.todo.presentation.main_screen.MainScreen
import com.example.todo.presentation.main_screen.MainScreenViewModel
import com.example.todo.presentation.tasks_screen.TasksScreen
import com.example.todo.presentation.tasks_screen.TasksScreenViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.compose.navigation.koinNavViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TODOTheme {
                val navController = rememberNavController()
                AppNavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                )
            }
        }
    }
}


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        startDestination = Destination.SplashScreen,
        navController = navController,
        modifier = modifier
    ) {
        composable<Destination.SplashScreen> {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Destination.AuthScreens) {
                        popUpTo(Destination.SplashScreen) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Destination.TodoScreens) {
                        popUpTo(Destination.SplashScreen) { inclusive = true }
                    }
                },
            )
        }

        navigation<Destination.TodoScreens>(startDestination = Destination.MainScreen) {
            composable<Destination.MainScreen> {
                val viewModel = koinViewModel<MainScreenViewModel>(
                    parameters = {
                        parametersOf({
                            navController.navigate(Destination.AuthScreens) {
                                popUpTo(Destination.TodoScreens) { inclusive = true }
                            }
                        }
                        )
                    }
                )

                MainScreen(
                    onEvent = viewModel::onEvent,
                    state = viewModel.state,
                    navigateToTasks = { status, projectId ->
                        navController.navigate(
                            Destination.TasksScreen(
                                taskStatus = status,
                                projectId = projectId
                            )
                        )
                    }
                )
            }

            composable<Destination.TasksScreen> {
                val viewModel = koinViewModel<TasksScreenViewModel>()
                TasksScreen(
                    navigateBack = { navController.popBackStack() },
                    state = viewModel.state,
                    navigateToCreateTaskScreen = { projectId ->
                        navController.navigate(Destination.CreateTaskScreen(projectId))
                    },
                    onEvent = viewModel::onEvent,
                    navigateToEditTaskScreen = { projectId, task ->
                        navController.navigate(Destination.CreateTaskScreen(projectId, task))
                    }
                )
            }
            composable<Destination.CreateTaskScreen> {
                val viewModel = koinViewModel<CreateTaskScreenViewModel>()
                CreateTaskScreen(
                    state = viewModel.state,
                    onEvent = viewModel::onEvent,
                    navigateBack = { navController.popBackStack() }
                )
            }
        }

        composable<Destination.AuthScreens> {
            AuthNavHost(
                onNavigateToApp = {
                    navController.navigate(Destination.TodoScreens) {
                        popUpTo(Destination.AuthScreens) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun AuthNavHost(onNavigateToApp: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val authNavController = rememberNavController()
        NavHost(
            startDestination = Destination.LoginScreen,
            navController = authNavController,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable<Destination.LoginScreen> {
                val viewModel = koinViewModel<LoginViewModel>()
                LoginScreen(
                    state = viewModel.state,
                    onEvent = viewModel::onEvent,
                    navigateToApp = onNavigateToApp,
                    navigateToRegister = {
                        authNavController.navigate(Destination.RegisterScreen)
                    }
                )
            }

            composable<Destination.RegisterScreen> {
                val viewModel = koinViewModel<RegisterViewModel>()
                RegisterScreen(
                    onEvent = viewModel::onEvent,
                    state = viewModel.state,
                    navigateToSignIn = { authNavController.navigate(Destination.LoginScreen) },
                    navigateToProfileSetup = { authNavController.navigate(Destination.ProfileSetupScreen) },
                )
            }
            composable<Destination.ProfileSetupScreen> {
                val parentEntry = remember(authNavController.currentBackStackEntry) {
                    authNavController.getBackStackEntry(
                        Destination.RegisterScreen
                    )
                }
                val viewModel =
                    koinNavViewModel<RegisterViewModel>(viewModelStoreOwner = parentEntry)
                ProfileSetupScreen(
                    onEvent = viewModel::onEvent,
                    state = viewModel.state,
                    navigateBack = { authNavController.popBackStack() },
                    navigateToApp = onNavigateToApp
                )
            }
        }
    }
}
