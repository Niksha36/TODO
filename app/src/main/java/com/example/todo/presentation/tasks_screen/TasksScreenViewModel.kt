package com.example.todo.presentation.tasks_screen

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
import com.example.todo.domain.use_cases.GetProjectByIdUseCase
import com.example.todo.domain.use_cases.RemoveTaskFromProjectUseCase
import com.example.todo.domain.use_cases.UpdateTaskUseCase
import com.example.todo.presentation.tasks_screen.components.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TasksScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getUserProjectUseCase: GetProjectByIdUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val removeTaskFromProjectUseCase: RemoveTaskFromProjectUseCase,
) : ViewModel() {

    var state by mutableStateOf(TasksScreenState())
    val projectId: String = savedStateHandle.toRoute<Destination.TasksScreen>().projectId

    init {
        val statusToOpen: Status = savedStateHandle.toRoute<Destination.TasksScreen>().taskStatus
        state = state.copy(statusTabToOpen = statusToOpen)
        observeProject(projectId)
    }

    fun onEvent(event: TasksScreenEvent) {
        when (event) {
            is TasksScreenEvent.ChangeTaskStatus -> {
                val updatedTask = when (event.task.status) {
                    Status.TODO -> event.task.copy(status = Status.IN_PROGRESS)
                    Status.IN_PROGRESS -> event.task.copy(status = Status.COMPLETED)
                    Status.COMPLETED -> event.task
                }

                state.project?.let { project ->
                    val updatedTasks =
                        project.tasks.map { if (it.id == updatedTask.id) updatedTask else it }
                    val updatedProject = project.copy(tasks = updatedTasks)
                    state = state.copy(
                        project = updatedProject,
                        projectsByStatus = updatedTasks.groupBy { it.status }
                    )
                }


                updateTaskInProject(updatedTask)
            }

            is TasksScreenEvent.OpenTaskDetailsBottomSheet -> {
                state = state.copy(
                    taskDetailsToShow = event.task
                )
            }

            is TasksScreenEvent.CloseTaskDetailsBottomSheet -> {
                state = state.copy(
                    taskDetailsToShow = null
                )
            }

            is TasksScreenEvent.DeleteTask -> {
                removeTaskFromProject(
                    projectId = projectId,
                    taskId = event.taskId
                )
            }

        }
    }

    private fun observeProject(projectId: String) {
        getUserProjectUseCase(projectId)
            .distinctUntilChanged()
            .onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    state = state.copy(isLoading = true)
                }

                is Resource.Success -> {
                    val project = result.data
                    state = state.copy(
                        isLoading = false,
                        project = project,
                        projectsByStatus = project?.tasks?.groupBy { it.status } ?: emptyMap()
                    )
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
                is Resource.Success -> {
                    state = state.copy(isLoading = false)
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

    private fun removeTaskFromProject(projectId: String, taskId: String) {
        removeTaskFromProjectUseCase(projectId, taskId).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    state = state.copy(
                        error = result.message ?: "Unknown error"
                    )
                }

                else -> Unit
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

}