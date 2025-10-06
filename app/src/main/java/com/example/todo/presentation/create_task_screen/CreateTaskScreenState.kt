package com.example.todo.presentation.create_task_screen

import com.example.core.domain.model.User
import com.example.todo.domain.model.Task
import com.example.todo.presentation.tasks_screen.components.Priority
import com.example.todo.presentation.tasks_screen.components.Status
import com.google.firebase.Timestamp

data class CreateTaskScreenState(
    val currentUser: User? = null,
    val isEditing: Boolean = false,
    val taskName: String = "",
    val description: String = "",
    val deadline: Timestamp? = null,
    val priority: Priority = Priority.NONE,
    val responsiblePersons: List<User> = emptyList(),
    val tags: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = "",
    val searchingUserError: String = "",
    val isSearchingUser: Boolean = false,
    val tag: String = "",
    val isTaskAddedToProject: Boolean = false,
    val isModified: Boolean = false,
    // Keep original task when editing to preserve owner/id and other immutable fields
    val editingTask: Task? = null,
)
