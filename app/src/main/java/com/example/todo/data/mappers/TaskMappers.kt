package com.example.todo.data.mappers

import com.example.core.domain.model.User
import com.example.todo.data.dto.TaskDto
import com.example.todo.domain.model.Task
import com.example.todo.presentation.tasks_screen.components.Priority
import com.example.todo.presentation.tasks_screen.components.Status

fun TaskDto.toDomain(
    owner: User,
    assignedTo: List<User>

): Task {
    return Task(
        id = id,
        projectId = projectId,
        owner = owner,
        title = title,
        description = description,
        deadline = deadline,
        status = Status.valueOf(status ?: Status.TODO.name),
        priority = Priority.valueOf(priority ?: Priority.NONE.name),
        createdAt = createdAt,
        updatedAt = updatedAt,
        tags = tags,
        assignedTo = assignedTo
    )
}

fun Task.toDto(): TaskDto {
    return TaskDto(
        id = id,
        projectId = projectId,
        userId = owner.id,
        title = title,
        description = description,
        startDate = createdAt,
        deadline = deadline,
        status = status.name,
        priority = priority.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        tags = tags,
        assignedTo = assignedTo.map{it.id}
    )
}

