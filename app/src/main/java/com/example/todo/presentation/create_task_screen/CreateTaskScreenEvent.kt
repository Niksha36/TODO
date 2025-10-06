package com.example.todo.presentation.create_task_screen

import com.example.core.domain.model.User
import com.example.todo.presentation.tasks_screen.components.Priority
import com.google.firebase.Timestamp

sealed interface CreateTaskScreenEvent {
    data class OnTaskNameChange(val name: String) : CreateTaskScreenEvent
    data class OnDescriptionChange(val description: String) : CreateTaskScreenEvent
    data class OnPriorityChange(val priority: Priority) : CreateTaskScreenEvent
    data class OnResponsiblePersonAdd(val email: String) : CreateTaskScreenEvent
    data class OnResponsiblePersonRemove(val user: User) : CreateTaskScreenEvent
    data object OnTagAdd : CreateTaskScreenEvent
    data object UpdateTask : CreateTaskScreenEvent
    data class OnTagChange(val tag: String) : CreateTaskScreenEvent
    data class OnTagRemove(val tag: String) : CreateTaskScreenEvent
    data class OnDeadlineChange(val deadline: Timestamp) : CreateTaskScreenEvent
    data object SubmitTask : CreateTaskScreenEvent
}