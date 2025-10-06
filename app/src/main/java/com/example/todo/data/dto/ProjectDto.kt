package com.example.todo.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class ProjectDto(
    @DocumentId
    val id: String = "",
    val ownerId: String = "",
    val users: List<String> = emptyList(),
    val tasksIds: List<String> = emptyList(),
    val name: String = "",
    val createdAt: Timestamp = Timestamp.now()
)