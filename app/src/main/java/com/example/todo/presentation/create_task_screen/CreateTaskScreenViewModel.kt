package com.example.todo.presentation.create_task_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.core.presentation.navigation.Destination
import com.example.core.utils.Resource
import com.example.todo.domain.model.Task
import com.example.todo.domain.use_cases.AddTaskToProjectUseCase
import com.example.todo.domain.use_cases.GetCurrentUserUseCase
import com.example.todo.domain.use_cases.GetTaskByIdUseCase
import com.example.todo.domain.use_cases.GetUserByEmailUseCase
import com.example.todo.domain.use_cases.UpdateTaskUseCase
import com.example.todo.presentation.tasks_screen.components.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CreateTaskScreenViewModel(
    private val addTaskToProjectUseCase: AddTaskToProjectUseCase,
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {
    private val projectId: String =
        savedStateHandle.toRoute<Destination.CreateTaskScreen>().projectId

    var state by mutableStateOf(CreateTaskScreenState())
        private set
    val taskId = savedStateHandle.toRoute<Destination.CreateTaskScreen>().taskId

    init {
        if (!taskId.isNullOrBlank()) {
            getTaskById(taskId)
        }
        val currentUser = getCurrentUserUseCase()
        state = state.copy(currentUser = currentUser)
    }

    fun onEvent(event: CreateTaskScreenEvent) {
        when (event) {
            is CreateTaskScreenEvent.OnDescriptionChange -> {
                state = state.copy(description = event.description, isModified = true)
            }

            is CreateTaskScreenEvent.OnPriorityChange -> {
                state = state.copy(priority = event.priority, isModified = true)
            }

            is CreateTaskScreenEvent.OnResponsiblePersonAdd -> {
                getUserByEmail(event.email)
            }

            is CreateTaskScreenEvent.OnResponsiblePersonRemove -> {
                val updatedMembers =
                    state.responsiblePersons.filter { it.email != event.user.email }
                state = state.copy(responsiblePersons = updatedMembers, isModified = true)
            }

            is CreateTaskScreenEvent.OnTagAdd -> {
                if (state.tag.isNotBlank() && !state.tags.contains(state.tag)) {
                    val updatedTags = state.tags + state.tag
                    state = state.copy(tags = updatedTags, tag = "", isModified = true)
                }
            }

            is CreateTaskScreenEvent.OnTagRemove -> {
                val updatedTags = state.tags.filter { it != event.tag }
                state = state.copy(tags = updatedTags, isModified = true)
            }

            is CreateTaskScreenEvent.OnTaskNameChange -> {
                state = state.copy(taskName = event.name, isModified = true)
            }

            is CreateTaskScreenEvent.OnDeadlineChange -> {
                state = state.copy(deadline = event.deadline, isModified = true)
            }

            is CreateTaskScreenEvent.OnTagChange -> {
                state = state.copy(tag = event.tag)
            }

            CreateTaskScreenEvent.SubmitTask -> {
                if (state.taskName.isBlank()) {
                    state = state.copy(error = "Task name cannot be empty")
                    return
                }
                if (state.currentUser == null) {
                    state = state.copy(error = "Current user is not available")
                    return
                }
                val task = Task(
                    projectId = projectId,
                    owner = state.currentUser!!,
                    title = state.taskName,
                    status = Status.TODO,
                    assignedTo = state.responsiblePersons,
                    id = "",
                    description = state.description,
                    deadline = state.deadline,
                    priority = state.priority,
                    tags = state.tags,
                )

                addTaskToProject(projectId, task)
            }

            CreateTaskScreenEvent.UpdateTask -> {
                if (state.taskName.isBlank()) {
                    state = state.copy(error = "Task name cannot be empty")
                    return
                }
                val ownerToKeep = state.editingTask?.owner ?: state.currentUser
                if (ownerToKeep == null) {
                    state = state.copy(error = "Current user is not available")
                    return
                }
                val task = Task(
                    id = taskId!!,
                    projectId = projectId,
                    owner = ownerToKeep,
                    title = state.taskName,
                    status = state.editingTask?.status ?: Status.TODO,
                    assignedTo = state.responsiblePersons,
                    description = state.description,
                    deadline = state.deadline,
                    priority = state.priority,
                    tags = state.tags,
                )

                updateTaskInProject(task)
            }
        }
    }

    private fun addTaskToProject(projectId: String, task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isLoading = true)
            val result = addTaskToProjectUseCase(projectId, task)
            when (result) {
                is Resource.Loading -> {
                    state = state.copy(isLoading = true)
                }

                is Resource.Success -> {
                    state = state.copy(isLoading = false, isTaskAddedToProject = true)
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

    private fun getUserByEmail(email: String) {
        getUserByEmailUseCase(email).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    state = state.copy(
                        isSearchingUser = true,
                        searchingUserError = ""
                    )
                }

                is Resource.Success -> {
                    val user = result.data
                    if (user != null) {
                        val updatedMembers = state.responsiblePersons + user
                        state = state.copy(
                            responsiblePersons = updatedMembers,
                            isSearchingUser = false,
                            isModified = true
                        )
                    } else {
                        state = state.copy(
                            isSearchingUser = false,
                            searchingUserError = "User not found"
                        )
                    }
                }

                is Resource.Error -> {
                    state = state.copy(
                        isSearchingUser = false,
                        searchingUserError = result.message ?: "Unknown error"
                    )
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun getTaskById(taskId: String) {

        getTaskByIdUseCase(taskId).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    state = state.copy(isLoading = true)
                }

                is Resource.Success -> {
                    val taskToEdit = result.data!!
                    state = state.copy(
                        isEditing = true,
                        taskName = taskToEdit.title,
                        description = taskToEdit.description,
                        deadline = taskToEdit.deadline,
                        priority = taskToEdit.priority,
                        responsiblePersons = taskToEdit.assignedTo,
                        tags = taskToEdit.tags,
                        editingTask = taskToEdit,
                    )
                    state = state.copy(isLoading = false)
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

    private fun updateTaskInProject(task: Task) {
        updateTaskUseCase(task).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    state = state.copy(isLoading = true)
                }
                is Resource.Success -> {
                    state = state.copy(isLoading = false, isTaskAddedToProject = true)
                }

                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = result.message ?: "Unknown error"
                    )
                }

                else -> Unit
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}