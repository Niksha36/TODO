package com.example.todo.presentation.main_screen

import com.example.core.domain.model.User

sealed interface CreateProjectDialogEvent {
    data class AddUserToProject(val email: String) : CreateProjectDialogEvent
    data object CreateProject : CreateProjectDialogEvent
    data class OnProjectNameChange(val name: String) : CreateProjectDialogEvent
    data class OnRemoveMember(val member: User) : CreateProjectDialogEvent
}