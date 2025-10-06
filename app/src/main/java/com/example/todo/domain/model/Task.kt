package com.example.todo.domain.model

import com.example.core.domain.model.User
import com.example.todo.presentation.tasks_screen.components.Priority
import com.example.todo.presentation.tasks_screen.components.Status
import com.google.firebase.Timestamp

data class Task(
    val id: String,
    val projectId: String,
    val owner: User,
    val title: String,
    val description: String,
    val createdAt: Timestamp = Timestamp.Companion.now(),
    val deadline: Timestamp?,
    val status: Status,
    val priority: Priority,
    val updatedAt: Timestamp? = null,
    val tags: List<String>,
    val assignedTo: List<User>
)