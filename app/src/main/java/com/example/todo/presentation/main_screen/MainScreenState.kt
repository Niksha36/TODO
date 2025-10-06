package com.example.todo.presentation.main_screen

import com.example.core.domain.model.User
import com.example.todo.data.dto.ProjectDto
import com.example.todo.domain.model.Project
import com.example.todo.presentation.tasks_screen.components.Status

data class MainScreenState(
    val isLoading: Boolean = true,
    val currentUser: User? = null,
    val error: String = "",
    val projects: List<Project> = emptyList(),
    val countOfTasksByStatus: Map<Status, Int> = emptyMap(),
    val currentProject: Project? = null,
    val isLoadingProjects: Boolean = false,
    val isCreateProjectDialogVisible: Boolean = false,
    val  createProjectDialogState: CreateProjectDialogState = CreateProjectDialogState()
)

data class CreateProjectDialogState(
    val name: String = "",
    val members: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)