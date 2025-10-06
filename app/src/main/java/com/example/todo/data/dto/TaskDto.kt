package com.example.todo.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class TaskDto(
    @DocumentId
    val id: String = "",
    val projectId: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val startDate: Timestamp? = null,
    val deadline: Timestamp? = null,
    val status: String? = null,
    val priority: String? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null,
    val tags: List<String> = emptyList(),
    val assignedTo: List<String> = emptyList()
)