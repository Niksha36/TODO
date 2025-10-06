package com.example.todo.presentation.main_screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.utils.Resource
import com.example.todo.domain.model.Project
import com.example.todo.domain.use_cases.CreateProjectUseCase
import com.example.todo.domain.use_cases.GetCurrentUserFlowUseCase
import com.example.todo.domain.use_cases.GetUserByEmailUseCase
import com.example.todo.domain.use_cases.GetUserProjectsUseCase
import com.example.todo.domain.use_cases.RemoveProjectUseCase
import com.example.todo.domain.use_cases.SignOutUseCase
import com.example.todo.presentation.tasks_screen.components.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class MainScreenViewModel(
    private val getCurrentUserFlowUseCase: GetCurrentUserFlowUseCase,
    private val getUserProjectsUseCase: GetUserProjectsUseCase,
    private val createProjectUseCase: CreateProjectUseCase,
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val removeProjectUseCase: RemoveProjectUseCase,
    private val SignOutUseCase: SignOutUseCase,
    private val navigateToAuth: () -> Unit
) : ViewModel() {
    var state by mutableStateOf(MainScreenState())
        private set

    private var projectsJob: Job? = null

    init {
        getCurrentUser()
    }

    fun onEvent(event: MainScreenEvent) {
        when (event) {

            is MainScreenEvent.SelectProject -> {
                val tasks = event.project.tasks
                val countByStatus = mapOf(
                    Status.TODO to tasks.count { it.status == Status.TODO },
                    Status.IN_PROGRESS to tasks.count { it.status == Status.IN_PROGRESS },
                    Status.COMPLETED to tasks.count { it.status == Status.COMPLETED }
                )
                state = state.copy(
                    currentProject = event.project,
                    countOfTasksByStatus = countByStatus
                )
            }

            is MainScreenEvent.RefreshProjects -> {
                val userId = state.currentUser?.id
                if (!userId.isNullOrBlank()) {
                    getUserProjects(userId)
                }
            }
            is MainScreenEvent.OnCreateProjectDialogEvent -> {
                onCreateProjectDialogEvent(event.event)
            }

            MainScreenEvent.ToggleCreateProjectDialog -> {
                state = state.copy(
                    isCreateProjectDialogVisible = !state.isCreateProjectDialogVisible,
                    createProjectDialogState = if (state.isCreateProjectDialogVisible) state.createProjectDialogState else CreateProjectDialogState()
                )
            }

            MainScreenEvent.Logout -> {
                SignOutUseCase()
                navigateToAuth()
            }
            is MainScreenEvent.DeleteProject -> {
                val projectId = event.project.id
                if (projectId.isNotBlank()) {
                    viewModelScope.launch {
                        removeProjectUseCase(projectId).collect { result ->
                            when (result) {
                                is Resource.Loading -> {
                                    state = state.copy(isLoading = true, error = "")
                                }

                                is Resource.Success -> {
                                    state = state.copy(
                                        isLoading = false,
                                        error = ""
                                    )

                                    if (state.currentProject?.id == projectId) {
                                        state = state.copy(currentProject = null)
                                    }
                                    onEvent(MainScreenEvent.RefreshProjects)
                                }

                                is Resource.Error -> {
                                    state = state.copy(
                                        isLoading = false,
                                        error = result.message ?: "Unknown error"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onCreateProjectDialogEvent(event: CreateProjectDialogEvent) {
        when (event) {
            is CreateProjectDialogEvent.AddUserToProject -> getUserByEmail(event.email)
            CreateProjectDialogEvent.CreateProject -> {
                val projectDialogState = state.createProjectDialogState
                val owner = state.currentUser
                if (owner == null || owner.id.isBlank()) {
                    // Без владельца проект создавать нельзя
                    state = state.copy(
                        createProjectDialogState = state.createProjectDialogState.copy(
                            isLoading = false,
                            error = "Текущий пользователь не загружен"
                        )
                    )
                    return
                }
                val project = Project(
                    name = projectDialogState.name,
                    id = "",
                    owner = owner,
                    users = projectDialogState.members,
                    tasks = emptyList()
                )
                createProject(project)
            }

            is CreateProjectDialogEvent.OnProjectNameChange -> {
                state = state.copy(
                    createProjectDialogState = state.createProjectDialogState.copy(
                        name = event.name
                    )
                )
            }

            is CreateProjectDialogEvent.OnRemoveMember -> {
                val updatedMembers = state.createProjectDialogState.members.toMutableList().apply {
                    remove(event.member)
                }
                state = state.copy(
                    createProjectDialogState = state.createProjectDialogState.copy(
                        members = updatedMembers
                    )
                )
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserFlowUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        state = state.copy(isLoading = true, error = "")
                    }

                    is Resource.Success -> {
                        state = state.copy(
                            isLoading = false,
                            currentUser = result.data,
                            error = ""
                        )
                        Log.d("MainScreenViewModel", "Current user loaded: ${state.currentUser}")

                        result.data?.id
                            ?.let { id ->
                                Log.d("MainScreenViewModel", "Fetching projects for userId: $id")
                                getUserProjects(id)
                            }
                    }

                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            currentUser = null,
                            error = result.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }

    private fun createProject(project: Project) {
        viewModelScope.launch {
            createProjectUseCase(
                project = project
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        state = state.copy(isLoading = true, error = "")
                    }

                    is Resource.Success -> {
                        state = state.copy(
                            isLoading = false,
                            error = ""
                        )
                        onEvent(MainScreenEvent.ToggleCreateProjectDialog)
                        onEvent(MainScreenEvent.RefreshProjects)
                    }

                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            error = result.message ?: "Unknown error"
                        )
                    }
                }
            }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
        }
    }

    private fun getUserProjects(userId: String) {
        projectsJob?.cancel()
        projectsJob = getUserProjectsUseCase(
            userId = userId
        ).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    state = state.copy(isLoadingProjects = true, error = "")
                }

                is Resource.Success -> {
                    state = state.copy(
                        isLoadingProjects = false,
                        projects = result.data ?: emptyList(),
                        error = ""
                    )
                    Log.d("MainScreenViewModel", "Projects loaded: ${state.projects.size}")
                }

                is Resource.Error -> {
                    state = state.copy(
                        isLoadingProjects = false,
                        projects = emptyList(),
                        error = result.message ?: "Unknown error"
                    )
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun getUserByEmail(email: String) {
        getUserByEmailUseCase(email).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    state = state.copy(
                        createProjectDialogState = state.createProjectDialogState.copy(
                            isLoading = true,
                            error = ""
                        )
                    )
                }

                is Resource.Success -> {
                    val user = result.data
                    if (user != null) {
                        val updatedMembers = state.createProjectDialogState.members + user
                        state = state.copy(
                            createProjectDialogState = state.createProjectDialogState.copy(
                                members = updatedMembers,
                                isLoading = false,
                                error = ""
                            )
                        )
                    } else {
                        state = state.copy(
                            createProjectDialogState = state.createProjectDialogState.copy(
                                isLoading = false,
                                error = "User not found"
                            )
                        )
                    }
                }

                is Resource.Error -> {
                    state = state.copy(
                        createProjectDialogState = state.createProjectDialogState.copy(
                            isLoading = false,
                            error = result.message ?: "Unknown error"
                        )
                    )
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}