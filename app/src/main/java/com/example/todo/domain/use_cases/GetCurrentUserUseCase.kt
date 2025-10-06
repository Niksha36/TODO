package com.example.todo.domain.use_cases

import com.example.todo.domain.TodoRepository

class GetCurrentUserUseCase(private val repository: TodoRepository) {
    operator fun invoke() = repository.getCurrentUser()
}