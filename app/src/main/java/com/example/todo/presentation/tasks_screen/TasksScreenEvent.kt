package com.example.todo.presentation.tasks_screen

import com.example.todo.domain.model.Task

sealed interface TasksScreenEvent {
    data class OpenTaskDetailsBottomSheet(val task: Task) : TasksScreenEvent
    data object CloseTaskDetailsBottomSheet : TasksScreenEvent
    data class ChangeTaskStatus(val task: Task) : TasksScreenEvent
    data class DeleteTask(val taskId: String) : TasksScreenEvent
}