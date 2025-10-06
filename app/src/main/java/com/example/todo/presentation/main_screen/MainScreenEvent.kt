package com.example.todo.presentation.main_screen

import com.example.todo.domain.model.Project

sealed interface MainScreenEvent {
    data class SelectProject(val project: Project) : MainScreenEvent
    data class DeleteProject(val project: Project) : MainScreenEvent
    object RefreshProjects : MainScreenEvent
    object ToggleCreateProjectDialog : MainScreenEvent
    data class OnCreateProjectDialogEvent(val event: CreateProjectDialogEvent) : MainScreenEvent
    data object Logout : MainScreenEvent
}