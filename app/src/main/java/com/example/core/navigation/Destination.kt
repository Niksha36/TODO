package com.example.core.presentation.navigation

import com.example.todo.presentation.tasks_screen.components.Status
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination {
    @Serializable
    data object SplashScreen: Destination
    @Serializable
    data object RegisterScreen: Destination
    @Serializable
    data object ProfileSetupScreen: Destination
    @Serializable
    data object LoginScreen: Destination
    @Serializable
    data object MainScreen: Destination
    @Serializable
    data object TaskDetailsScreen: Destination
    @Serializable
    data class CreateTaskScreen(val projectId: String, val taskId: String? = null): Destination
    @Serializable
    data class TasksScreen(val taskStatus: Status, val projectId: String): Destination


    @Serializable
    data object AuthScreens: Destination

    @Serializable
    data object TodoScreens: Destination

}