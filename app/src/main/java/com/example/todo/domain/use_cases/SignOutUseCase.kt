package com.example.todo.domain.use_cases

import com.example.todo.domain.TodoRepository

class SignOutUseCase(
    private val repository: TodoRepository
) {
    operator fun invoke() {
        repository.signOut()
    }
}