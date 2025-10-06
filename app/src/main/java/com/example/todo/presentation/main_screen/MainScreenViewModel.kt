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
import com.example.todo.domain.use_cases.GetProjectByIdUseCase
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
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val navigateToAuth: () -> Unit
) : ViewModel() {
    var state by mutableStateOf(MainScreenState())
        private set

    private var projectsJob: Job? = null
    private var currentProjectJob: Job? = null

    private val emptyCounts = mapOf(
        Status.TODO to 0,
        Status.IN_PROGRESS to 0,
        Status.COMPLETED to 0
    )

    init {
        getCurrentUser()
    }

    fun onEvent(event: MainScreenEvent) {
        when (event) {

            is MainScreenEvent.SelectProject -> {
                val project = event.project
                // оптимистично обновляем UI и пересчитываем счётчики
                state = state.copy(
                    currentProject = project,
                    countOfTasksByStatus = computeCounts(project)
                )
                // подписываемся на поток конкретного проекта, чтобы получать обновления задач
                if (project.id.isNotBlank()) {
                    subscribeToProject(project.id)
                }
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

                                    // если удалили текущий проект — отменяем подписку и сбрасываем счётчики
                                    if (state.currentProject?.id == projectId) {
                                        cancelProjectSubscription()
                                        state = state.copy(
                                            currentProject = null,
                                            countOfTasksByStatus = emptyCounts
                                        )
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
                    val projects = result.data ?: emptyList()
                    val currentId = state.currentProject?.id
                    val updatedCurrent = currentId?.let { id ->
                        projects.firstOrNull { it.id == id }
                    }
                    state = state.copy(
                        isLoadingProjects = false,
                        projects = projects,
                        currentProject = updatedCurrent,
                        countOfTasksByStatus = updatedCurrent?.let { computeCounts(it) } ?: emptyCounts,
                        error = ""
                    )
                    Log.d("MainScreenViewModel", "Projects loaded: ${state.projects.size}")

                    // если после загрузки проектов нашли текущий - убедимся, что подписка активна
                    updatedCurrent?.id?.let { pid ->
                        subscribeToProject(pid)
                    }
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

    private fun computeCounts(project: Project): Map<Status, Int> {
        val tasks = project.tasks
        return mapOf(
            Status.TODO to tasks.count { it.status == Status.TODO },
            Status.IN_PROGRESS to tasks.count { it.status == Status.IN_PROGRESS },
            Status.COMPLETED to tasks.count { it.status == Status.COMPLETED }
        )
    }

    private fun subscribeToProject(projectId: String) {
        // отменяем предыдущую подписку
        currentProjectJob?.cancel()
        currentProjectJob = getProjectByIdUseCase(projectId)
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        // можно выставлять флаг загрузки по проекту при необходимости
                    }
                    is Resource.Success -> {
                        val project = result.data
                        if (project != null) {
                            state = state.copy(
                                currentProject = project,
                                countOfTasksByStatus = computeCounts(project)
                            )
                        }
                    }
                    is Resource.Error -> {
                        // логируем ошибку, но не ломаем UI
                        Log.w("MainScreenViewModel", "Project subscription error: ${result.message}")
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private fun cancelProjectSubscription() {
        currentProjectJob?.cancel()
        currentProjectJob = null
    }

    override fun onCleared() {
        super.onCleared()
        projectsJob?.cancel()
        currentProjectJob?.cancel()
    }
}