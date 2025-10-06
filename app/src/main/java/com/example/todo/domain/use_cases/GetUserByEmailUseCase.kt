package com.example.todo.domain.use_cases

import com.example.core.utils.Resource
import com.example.todo.domain.TodoRepository
import kotlinx.coroutines.flow.flow

class GetUserByEmailUseCase(
    private val repository: TodoRepository
) {
    operator fun invoke(email: String) = flow {
        emit(Resource.Loading())
        try {
            val user = repository.getUserByEmail(email)
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}