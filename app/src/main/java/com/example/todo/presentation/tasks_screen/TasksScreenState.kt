package com.example.todo.presentation.tasks_screen

import com.example.todo.domain.model.Project
import com.example.todo.domain.model.Task
import com.example.todo.presentation.tasks_screen.components.Status

data class TasksScreenState(
    val project: Project? = null,
    val taskDetailsToShow: Task? = null,
    val projectsByStatus: Map<Status, List<Task>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String = "",
    val statusTabToOpen: Status = Status.TODO,
)
