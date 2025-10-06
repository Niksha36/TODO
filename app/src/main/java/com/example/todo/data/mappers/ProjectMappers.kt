package com.example.todo.data.mappers

import com.example.core.domain.model.User
import com.example.todo.data.dto.ProjectDto
import com.example.todo.domain.model.Project
import com.example.todo.domain.model.Task

fun ProjectDto.toDomain(owner: User, users: List<User>, tasks: List<Task>): Project {
    return Project(
        id = id,
        owner = owner,
        users = users,
        tasks = tasks,
        name = name,
        createdAt = createdAt
    )
}

fun Project.toDto(): ProjectDto {
    return ProjectDto(
        id = id,
        ownerId = owner.id,
        users = users.map { it.id },
        tasksIds = tasks.map { it.id },
        name = this.name,
        createdAt = this.createdAt
    )
}


