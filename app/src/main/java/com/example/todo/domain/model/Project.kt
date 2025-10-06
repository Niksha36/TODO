package com.example.todo.domain.model

import com.example.core.domain.model.User
import com.example.todo.data.dto.TaskDto
import com.google.firebase.Timestamp

data class Project(
    val id: String,
    val owner: User,
    val users: List<User>,
    val tasks: List<Task>,
    val name: String,
    val createdAt: Timestamp = Timestamp.Companion.now()
)